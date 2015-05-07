package org.son.chat.common.net.core.session;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话对象
 * 
 * @author solq
 * */
public class Session implements ISession {

    public static Session valueOf(String id) {
	Session result = new Session();
	result.id = id;
	return result;
    }

    private String id;

    private Map<String, Object> attr = new ConcurrentHashMap<>();

    @Override
    public String getId() {
	return id;
    }

    @Override
    public void replace(ISession session) {
	for(Entry<String, Object> entry : session.getAttr().entrySet()){
	    attr.put(entry.getKey(), entry.getValue());
	}
     }

    @Override
    public ISession setAttr(String key, Object value) {
	attr.put(key, value);
	return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttr(String key) {
	return (T) attr.get(key);
    }

    @Override
    public final Map<String, Object> getAttr() {
	return attr;
    }

    @Override
    public ISession removeAttr(String key) {
	attr.remove(key);
	return this;
    }
}
