package org.son.chat.common.net.core.socket;

/**
 * @author solq
 * */
public interface IPipeChannel<E> {

    /***
     * 加入管道
     * 
     * @param channelName
     *             管道名称
     * @param e
     *            元素
     * */
    void join(String channelName, E e);

    /**
     * 退出管道
     * 
     * @param e
     * */
    void eixt(E e);

}
