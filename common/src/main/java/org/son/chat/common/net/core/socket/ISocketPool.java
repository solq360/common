package org.son.chat.common.net.core.socket;

import java.nio.channels.Selector;

/**
 * socket 逻辑处理池子
 * 
 * @author solq
 */
public interface ISocketPool {

    public void init();
    /** 执行任务 **/
    public void execute(Runnable task);

    /** 关闭处理 **/
    public void shutdown();
    
    /** 是否运行 **/
    public boolean isRun();

    /** 任务数 **/
    public int taskCount();

    /** select **/
    public Selector getSelector();

    /** 触发select **/
    public void select();
}
