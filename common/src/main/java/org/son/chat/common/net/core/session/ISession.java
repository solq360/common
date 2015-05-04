package org.son.chat.common.net.core.session;

import java.util.Map;

/**
 * @author solq
 * */
public interface ISession {
    public String getId();
    public void replace(ISession session);
    
    public ISession setAttr(String key,Object value);
    public <T> T getAttr(String key);
    public Map<String,Object> getAttr();
}
