package org.son.chat.common.net.util;

import java.util.List;

/**
 * @author solq
 */
public abstract class ByteHelper {
	public static byte[] int32Tobyte(int value) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((value >>> 24) & 0xff);
		bytes[1] = (byte) ((value >>> 16) & 0xff);
		bytes[2] = (byte) ((value >>> 8) & 0xff);
		bytes[3] = (byte) (value & 0xff);
		return bytes;
	}

	public static int byteToint32(byte[] bytes) {
		int result = (bytes[0] & 0xff) << 24;
		result |= (bytes[1] & 0xff) << 16;
		result |= (bytes[2] & 0xff) << 8;
		result |= bytes[3] & 0xff;
		return result;
	}

	public static byte[] int16Tobyte(int value) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) ((value >>> 8) & 0xff);
		bytes[1] = (byte) (value & 0xff);
		return bytes;
	}

	public static int byteToint16(byte[] bytes) {
		int result = (bytes[0] & 0xff) << 8;
		result |= bytes[1] & 0xff;
		return result;
	}

	public static byte[] readBytes(byte[] bytes, int start, int offSet) {
		byte[] result = new byte[offSet];
		System.arraycopy(bytes, start, result, 0, offSet);
		return result;
	}

	public static byte[] merge(List<byte[]> array) {
		int size = 0;
		int len = array.size();
		for (int i = 0; i < len; i++) {
			size += array.get(i).length;
		}
		byte[] result = new byte[size];
		int count = 0;
		for (int i = 0; i < len; i++) {
			System.arraycopy(array.get(i), 0, result, count, array.get(i).length);
			count += array.get(i).length;
		}
		return result;
	}

	public static byte[] merge(byte[]... array) {
		int size = 0;
		for (int i = 0; i < array.length; i++) {
			size += array[i].length;
		}
		byte[] result = new byte[size];
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			System.arraycopy(array[i], 0, result, count, array[i].length);
			count += array[i].length;
		}
		return result;
	}

	public static final String bytesToHexString(byte[] bArray, int begin, int end) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = begin; i < end; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * @param ip ip的字节数组形式
	 * @return 字符串形式的ip
	 */
	public static String byteToIp(byte[] ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(ip[0] & 0xFF);
		sb.append('.');
		sb.append(ip[1] & 0xFF);
		sb.append('.');
		sb.append(ip[2] & 0xFF);
		sb.append('.');
		sb.append(ip[3] & 0xFF);
		return sb.toString();
	}

	/**
	 * A 是否包含 B
	 */
	public static boolean contains(byte[] A, byte[] B) {
		return indexOf(A, B) > -1;
	}

	public static int indexOf(byte[] A, byte[] B) {
		if (B.length > A.length) {
			return -1;
		}
		int result = 0;
		TO:
		for (int lastIndex = 0; lastIndex < A.length; lastIndex++) {
			result = lastIndex;
			if (B[0] == A[lastIndex]) {
				// next
				for (int n = 1; n < B.length; n++) {
					lastIndex++;
					if (lastIndex >= A.length) {
						return -1;
					}
 					if (B[n] != A[lastIndex]) {
						continue TO;
					}
				}
				return result;
			}
		}
		return -1;
	}

}
