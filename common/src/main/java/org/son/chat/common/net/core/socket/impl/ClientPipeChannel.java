package org.son.chat.common.net.core.socket.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.son.chat.common.net.core.socket.IPipeChannel;

/**
 * 客房端管道
 * */
public class ClientPipeChannel  implements IPipeChannel<ClientSocket>{

	public final static String SERVER_CHANNEL="SERVER_CHANNEL";
	public final static String DEFAULT_CLIENT_CHANNEL="DEFAULT_CLIENT_CHANNEL";
	/** 已连接的客户端 */
	private Map<String, ClientSocket> ipMapClients = new HashMap<>();
 	private Map<String, Map<String, ClientSocket>> channelMapClients = new HashMap<>();
	
  	
	@Override
	public synchronized void join(String channelName, ClientSocket e) {
		Map<String, ClientSocket> channelClients = channelMapClients.get(channelName);
		if(channelClients==null){
			channelClients=new HashMap<String, ClientSocket>();
		}
 		final String ip = e.getSocketChannelConfig().getLocalAddress().getAddress().toString();
		ipMapClients.put(ip, e);
 		channelClients.put(ip, e);
	}

	@Override
	public synchronized void eixt(ClientSocket e) {
 		final String ip = e.getSocketChannelConfig().getLocalAddress().getAddress().toString();
		ipMapClients.remove(ip);
 		
	}

	
	public List<ClientSocket> getAllClinetSockets(){
		List<ClientSocket> result = new LinkedList<ClientSocket>();
		return result;
	}


	public List<ClientSocket> getChannelClinetSockets(String channelName){
		List<ClientSocket> result = new LinkedList<ClientSocket>();
		return result;
	}
}
