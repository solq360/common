package org.son.chat.common;

import org.junit.Test;
import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.session.ISessionFactory;
import org.son.chat.common.net.core.session.SessionFactory;
import org.son.chat.common.net.core.socket.impl.ServerSocket;
import org.son.chat.common.protocol.PackageDefaultCoder;

/**
 * @author solq
 */
public class TestNioServer {
    @Test
    public void normal() {
	ISessionFactory sessionFactory = new SessionFactory();

	ICoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("server chat", PackageDefaultCoder.valueOf(), new ChatTestServerHandle()));
	ServerSocket serverSocket=ServerSocket.valueOf(SocketChannelConfig.valueOf(6969), 10,20,coderParserManager, sessionFactory);
	serverSocket.start();
	serverSocket.sync();
	serverSocket.stop();
    }

}
