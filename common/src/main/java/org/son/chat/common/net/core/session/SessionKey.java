package org.son.chat.common.net.core.session;

import java.util.Date;
import java.util.concurrent.Future;

/***
 * 会话属性 key
 * 
 * @author solq
 * */
public interface SessionKey {

    /*** 心跳时间 **/
    public final static String HEART_TIME = "HEART_TIME";

    /*** 最后发送数据时间 **/
    public final static String SEND_TIME = "SEND_TIME";

    /*** 最后读取数据时间 **/
    public final static String READ_TIME = "READ_TIME";

    /*** 最后连接数据时间 **/
    public final static String CONNECT_TIME = "CONNECT_TIME";

    public final static Key<Date> KEY_HEART_TIME = new Key<>(HEART_TIME);
    public final static Key<Date> KEY_SEND_TIME = new Key<>(SEND_TIME);
    public final static Key<Date> KEY_READ_TIME = new Key<>(READ_TIME);
    public final static Key<Date> KEY_CONNECT_TIME = new Key<>(CONNECT_TIME);
    public final static Key<Future<?>> KEY_HEART_TASK = new Key<>("KEY_HEART_TASK");
        
}
