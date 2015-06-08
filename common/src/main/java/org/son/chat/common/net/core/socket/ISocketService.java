package org.son.chat.common.net.core.socket;

/**
 * socket 服务接口
 * 
 * @author solq
 */
public interface ISocketService {
    public void init();

    public void start();

    public void stop();
    
    public void sync();
}
