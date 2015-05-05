package org.son.chat.common.net.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.son.chat.common.net.util.IpUtil;

/**
 * @author solq
 */
public class SocketChannelConfig {
    private InetSocketAddress address;

    public static SocketChannelConfig valueOf(int remotePort) {
	SocketChannelConfig result = new SocketChannelConfig();
	result.address = new InetSocketAddress(remotePort);
	return result;
    }

    public static SocketChannelConfig valueOf(String remoteHost, int remotePort) {
	SocketChannelConfig result = new SocketChannelConfig();
	result.address = new InetSocketAddress(remoteHost, remotePort);
	return result;
    }

    public static SocketChannelConfig valueOf(SocketAddress remoteAddress) {
	SocketChannelConfig result = new SocketChannelConfig();
	result.address = (InetSocketAddress) remoteAddress;
	return result;
    }

    public String toIp() {
	return IpUtil.getIp(address);
    }

    // getter

    public InetSocketAddress getAddress() {
	return address;
    }

}
