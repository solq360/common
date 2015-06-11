* 链式编/解码
* 链路层链式处理
* 管道管理socket
* 多协议处理非常方便
* 仿netty NioEventLoop 单线程串行处理

========
侍加功能 :
* 自动化编/解码
* rpc 接口增强使用


源码实现过程

========
链式编/解码

* 由 多个 ICoder 输入/输出转换处理
* CoderParser 类组装多个 ICoder
* 编/码处理器 注意优先级 <br>
* nio read -> packageCoder -> link coders -> handle <br>
* handle write -> link coders -> packageCoder -> nio write <br>
* 由 ICoderParserManager 管理调用处理

```
public interface ICoderParserManager {

    /**
     * 解码处理
     * 
     * @return CoderResult
     * */
    CoderResult decode(ByteBuffer buffer, ICoderCtx ctx);

    /**
     * 编码处理
     * */
    ByteBuffer encode(Object message, ICoderCtx ctx);

    void error(ByteBuffer buffer, ICoderCtx ctx);

    /** 注册 编/码处理器 */
    void register(CoderParser coderParser);
}
```

其中核心
decode
encode
```
  @Override
    public CoderResult decode(ByteBuffer buffer, ICoderCtx ctx) {
	final SocketChannelCtx socketChannelCtx = (SocketChannelCtx) ctx;
	final ClientSocket clientSocket = socketChannelCtx.getClientSocket();

	for (CoderParser coderParser : coderParsers.values()) {
	    final IPackageCoder packageCoder = coderParser.getPackageCoder();
	    final ICoder<?, ?>[] linkCoders = coderParser.getCoders();
	    final IHandle handle = coderParser.getHandle();
	    Object value = null;
	    synchronized (buffer) {
		// 已解析完
		if (socketChannelCtx.getCurrPackageIndex() >= buffer.limit()) {
		    return CoderResult.valueOf(ResultValue.UNFINISHED);
		}
		// 包协议处理
		if (!packageCoder.verify(buffer, ctx)) {
		    continue;
		}
		// 包解析
		value = packageCoder.decode(buffer, ctx);
		if (value == null) {
		    // 包未读完整
		    return CoderResult.valueOf(ResultValue.UNFINISHED);
		}
	    }
	    // 链式处理
	    if (linkCoders != null) {
		for (ICoder coder : linkCoders) {
		    value = coder.decode(value, ctx);
		    if (value == null) {
			throw new CoderException("解码出错 : " + coder.getClass());
		    }
		}
	    }
	    // 业务解码处理
	    value = handle.decode(value, ctx);
	    clientSocket.readBefore(socketChannelCtx, value);
	    handle.handle(value, ctx);
	    clientSocket.readAfter(socketChannelCtx, value);

	    return CoderResult.valueOf(ResultValue.SUCCEED);
	}
	return CoderResult.valueOf(ResultValue.NOT_FIND_CODER);
    }

    @Override
    public ByteBuffer encode(Object message, ICoderCtx ctx) {

	for (CoderParser coderParser : coderParsers.values()) {
	    final IPackageCoder packageCoder = coderParser.getPackageCoder();
	    final ICoder<?, ?>[] linkCoders = coderParser.getCoders();
	    final IHandle handle = coderParser.getHandle();
	    // 业务检查
	    if (!handle.verify(message, ctx)) {
		continue;
	    }
	    // 业务编码处理
	    Object value = handle.encode(message, ctx);
	    // 链式处理
	    if (linkCoders != null) {
		for (int i = linkCoders.length - 1; i >= 0; i--) {
		    ICoder coder = linkCoders[i];
		    value = coder.encode(value, ctx);
		    if (value == null) {
			throw new CoderException("编码出错 : " + coder.getClass());
		    }
		}
	    }
	    // 打包消息处理
	    value = packageCoder.encode(value, ctx);
	    if (value != null) {
		return (ByteBuffer) value;
	    }
	    throw new CoderException("编码出错  :" + packageCoder.getClass());
	}

	throw new CoderException("未找到编/解码处理器 ");
   }   

```

* 半包/帖包处理 : AbstractISocketChannel doRead方法摘要,根据解码返回的状态做处理。
* 半包:当不是完成状态时，继续解码，从最后一次包索引开始处理
* 帖包:当完成包解码移动包索引,等侍下轮解码处理
```
   boolean run = true;
	    // 粘包处理
	    while (run) {
		ByteBuffer cpbuffer = socketChannelCtx.coderBegin();
		cpbuffer.mark();
		CoderResult coderResult = coderParserManager.decode(cpbuffer, socketChannelCtx);
		switch (coderResult.getValue()) {
		case SUCCEED:
		    break;
		case NOT_FIND_CODER:
		    final int readySize = socketChannelCtx.getWriteIndex() - socketChannelCtx.getCurrPackageIndex();
		    final int headLimit = 255;
		    if (readySize >= headLimit) {
			throw new CoderException("未找到编/解码处理器 ");
		    }
		    run = false;
		    break;
		case UNFINISHED:
		case UNKNOWN:
		case ERROR:
		default:
		    run = false;
		    // TODO throw
		    break;
		}
	  }
```