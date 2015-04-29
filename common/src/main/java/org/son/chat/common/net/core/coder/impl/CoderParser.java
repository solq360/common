package org.son.chat.common.net.core.coder.impl;

import org.son.chat.common.net.core.coder.ICoder;
import org.son.chat.common.net.core.coder.IHandle;
import org.son.chat.common.net.core.coder.IPackageCoder;

/**
 * 编/码处理器 注意优先级 <br>
 * nio read -> packageCoder -> link coders -> handle <br>
 * handle write -> link coders -> packageCoder -> nio write <br>
 * @author solq
 */
public class CoderParser {

	/** 业务处理分发 **/
	private IHandle<?, ?> handle;
	/** 网络包编/解码 **/
	private IPackageCoder<?, ?> packageCoder;
	/** 链式编/解码 **/
	private ICoder<?, ?>[] coders;
	/** 名称标识 **/
	private String name;

	public static CoderParser valueOf(String name, IPackageCoder<?, ?> packageCoder, IHandle<?, ?> handle, ICoder<?, ?>... coders) {
		CoderParser result = new CoderParser(name, packageCoder, handle, coders);
		return result;
	}

	private CoderParser(String name, IPackageCoder<?, ?> packageCoder, IHandle<?, ?> handle, ICoder<?, ?>... coders) {
		this.packageCoder = packageCoder;
		this.coders = coders;
		this.handle = handle;
		this.name = name;
	}

	// getter

	public ICoder<?, ?>[] getCoders() {
		return coders;
	}

	public String getName() {
		return name;
	}

	public IHandle<?, ?> getHandle() {
		return handle;
	}

	public IPackageCoder<?, ?> getPackageCoder() {
		return packageCoder;
	}

}
