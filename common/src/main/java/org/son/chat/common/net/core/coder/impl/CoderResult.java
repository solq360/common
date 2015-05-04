package org.son.chat.common.net.core.coder.impl;

/**
 * @author solq
 */
public class CoderResult {

    public static enum ResultValue {
	/** 成功 **/
	SUCCEED,
	/** 未完成 半包/帖包状态 **/
	UNFINISHED,
	/** 非法数据 **/
	ERROR,
	/** 未知错误 **/
	UNKNOWN,
	/** 未找到编/解码 **/
	NOT_FIND_CODER;
    }

    /** 处理后返回内容 **/
    private Object content;

    /** 处理结果 **/
    private ResultValue value;

    // getter

    public Object getContent() {
	return content;
    }

    public ResultValue getValue() {
	return value;
    }

    public static CoderResult valueOf(ResultValue resule) {
	CoderResult result = new CoderResult();
	result.value = resule;
	return result;
    }

}
