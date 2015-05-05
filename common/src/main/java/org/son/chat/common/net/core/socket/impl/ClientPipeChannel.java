package org.son.chat.common.net.core.socket.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.son.chat.common.net.core.socket.IPipeChannel;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.IpUtil;

/**
 * 客房端管道
 * */
public class ClientPipeChannel implements IPipeChannel<ClientSocket> {

    public final static String DEFAULT_CLIENT_CHANNEL = "DEFAULT_CLIENT_CHANNEL";
    /** 已连接的客户端 */
    private ConcurrentHashMap<String, ClientSocket> ipMapClients = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ClientSocket>> channelMapClients = new ConcurrentHashMap<>();

    @Override
    public void join(String channelName, ClientSocket e) {
	e.buildAddress();
	final String ip = IpUtil.getAddress(e.getRemoteAddress());
	if (ip == null) {
	    throw new NetException("ClientSocket 远程IP 未找到");
	}

	if (null != e.getChannelName()) {
	    replace(channelName, e);
	    return;
	}

	e.setChannelName(channelName);
	Map<String, ClientSocket> channelClients = channelMapClients.get(channelName);
	if (channelClients == null) {
	    ConcurrentHashMap<String, ClientSocket> newChannelClients = new ConcurrentHashMap<String, ClientSocket>();
	    ConcurrentHashMap<String, ClientSocket> now = channelMapClients.putIfAbsent(channelName, newChannelClients);
	    channelClients = now != null ? now : newChannelClients;
	}

	ipMapClients.put(ip, e);
	channelClients.put(ip, e);

    }

    @Override
    public void eixt(ClientSocket e) {
	final String ip = IpUtil.getAddress(e.getRemoteAddress());
	if (ip == null) {
	    return;
	}
	ipMapClients.remove(ip);

	final String channle = e.getChannelName();
	if (null == channle) {
	    return;
	}

	ConcurrentHashMap<String, ClientSocket> channels = channelMapClients.get(channle);
	if (channels != null) {
	    channels.remove(ip);
	}
	e.setChannelName(null);
    }

    @Override
    public void replace(String newChannelName, ClientSocket e) {
	eixt(e);
	join(newChannelName, e);
    }

    public List<ClientSocket> getAllClinetSockets() {
	List<ClientSocket> result = new LinkedList<ClientSocket>();
	return result;
    }

    public List<ClientSocket> getChannelClinetSockets(String channelName) {
	List<ClientSocket> result = new LinkedList<ClientSocket>();
	return result;
    }

}
