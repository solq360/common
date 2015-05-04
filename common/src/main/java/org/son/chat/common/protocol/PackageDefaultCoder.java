package org.son.chat.common.protocol;

import java.nio.ByteBuffer;

import org.son.chat.common.net.core.coder.IPackageCoder;
import org.son.chat.common.net.core.coder.IcoderCtx;
import org.son.chat.common.net.core.socket.impl.SocketChannelCtx;
import org.son.chat.common.net.util.ByteHelper;

/**
 * 包封装 [包头标识] + [包长度] + [包内容] 校验值暂时不处理
 * @author solq
 */
public class PackageDefaultCoder implements IPackageCoder<byte[], ByteBuffer> {

	/** 包头标识 **/
	private byte[] HEAD_MARK = { 1, 0 };
	/** 请求包最大长度 **/
	private int MAX_REQUEST_LENGTH = 1024 * 1024 * 5;

	// //////////////////////静态构造//////////////////////////////
	public static PackageDefaultCoder valueOf() {
		PackageDefaultCoder result = new PackageDefaultCoder();
		return result;
	}

	public static PackageDefaultCoder valueOf(int maxRequestLength, byte[] headMak) {
		PackageDefaultCoder result = new PackageDefaultCoder();
		result.HEAD_MARK = headMak;
		result.MAX_REQUEST_LENGTH = maxRequestLength;
		return result;
	}

	/**
	 * 将消息打包给NIO 发送
	 */
	@Override
	public ByteBuffer encode(byte[] value, IcoderCtx ctx) {
		final int bufSize = HEAD_MARK.length + 4 + value.length;
		ByteBuffer bb = ByteBuffer.allocate(bufSize);
		bb.put(HEAD_MARK);
		bb.putInt(value.length);
		bb.put(value);
		return bb;
	}

	/**
	 * 把包提取出消息给下一次解码器处理
	 */
	@Override
	public byte[] decode(ByteBuffer value, IcoderCtx ctx) {
 		SocketChannelCtx socketChannelCtx = (SocketChannelCtx) ctx;
		value.reset();
		// 包长度判断
		final int readSize = value.remaining() - HEAD_MARK.length;
		//半包
		if(readSize <=0){
			return null;
		}
 
		// 草，getInt 不会移动索引
		final int bodyLen = value.getInt(value.position()+HEAD_MARK.length);
		// 半包
		if (bodyLen > readSize) {
			System.out.println("半包");
  			return null;
		}
		// TODO
		if (bodyLen > MAX_REQUEST_LENGTH) {
			// TODO 非法请求
		}

		byte[] result = new byte[bodyLen];
		// 不同 ByteBuffer getBytes 的时候不会移动当前索引 真 恶心
		final int bodyPosition = value.position() + HEAD_MARK.length + 4;
		value.position(bodyPosition);
		value.get(result);

		final int used = HEAD_MARK.length + 4 + bodyLen;
  		// 更新已读索引
 		socketChannelCtx.nextPackageIndex(used);
		return result;
	}

	@Override
	public boolean verify(ByteBuffer value, IcoderCtx ctx) {
		value.reset();
		// 包长度判断
		final int readSize = value.remaining() - HEAD_MARK.length;
		//半包
		if(readSize <=0){
			return false;
		}
 		byte[] headMark = new byte[HEAD_MARK.length];
		value.get(headMark);
		// 头标识判断
		final boolean isRight = ByteHelper.contains(headMark, HEAD_MARK);
		return isRight;
	}
	
}
