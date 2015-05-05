package org.son.chat.common.net.util;

import java.net.SocketAddress;
import java.util.regex.Pattern;

/**
 * @author solq
 * */
public abstract class IpUtil {
    private static final int INDEX_NOT_FOUND = -1;

    /**
     * 获取会话的IP地址
     */
    public static String getIp(SocketAddress address) {
	if (address == null) {
	    return null;
	}
	String ip = address.toString();
	return substringBetween(ip, "/", ":");
    }

    public static String getAddress(SocketAddress address) {
	if (address == null) {
	    return null;
	}
	String ip = address.toString();
	if (ip == null) {
	    return null;
	}
	int start = ip.indexOf("/");
	if (start != INDEX_NOT_FOUND) {
	    return ip.substring(start + 1);
	}
	return null;
    }

    /*** ip地址正则配置 **/
    public final static Pattern ipPattern = Pattern.compile("^([0-9\\*]+\\.){3}[0-9\\*]+$");

    /**
     * 验证是否合法的IP
     */
    public static boolean isValidIp(String ip) {
	return ipPattern.matcher(ip).matches();
    }

    public static String substringBetween(String str, String open, String close) {
	if (str == null || open == null || close == null) {
	    return null;
	}
	int start = str.indexOf(open);
	if (start != INDEX_NOT_FOUND) {
	    int end = str.indexOf(close, start + open.length());
	    if (end != INDEX_NOT_FOUND) {
		return str.substring(start + open.length(), end);
	    }
	}
	return null;
    }
}
