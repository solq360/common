package org.son.chat.common.net.core.session;

/**
 * session attr 托管 
 * @author solq
 * */
public class Key<T> {
    private String key;

    public Key(String key) {
	this.key = key;
    }

    public Key<T> setAttr(ISession session, T value) {
	session.setAttr(key, value);
	return this;
    }
    
    public T getAttr(ISession session) {
	return session.getAttr(key);
    }
}