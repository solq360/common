package org.son.chat.common.net.config;

import java.net.InetSocketAddress;

/**
 * @author solq
 */
public class SocketChannelConfig {
	private InetSocketAddress localAddress;
	private InetSocketAddress remoteAddress;

	// getter
	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public static SocketChannelConfig valueOf(int remotePort) {
		SocketChannelConfig result = new SocketChannelConfig();
		result.remoteAddress = new InetSocketAddress(remotePort);
		return result;
	}

	public static SocketChannelConfig valueOf(String remoteHost, int remotePort) {
		SocketChannelConfig result = new SocketChannelConfig();
		result.remoteAddress = new InetSocketAddress(remoteHost, remotePort);
		return result;
	}
}
