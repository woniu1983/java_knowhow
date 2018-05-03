/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.common;

/** 
 * @ClassName: ByteUtility <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午7:01:57 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class ByteUtility {
	private static final int HEX_CODE_000000FF = 0x000000ff;

	private static final int HEX_CODE_FFFFFFF0 = 0xfffffff0;

	private static final int RADIX_HEX = 16;

	/**
	 * 
	 * @Title: convertByteToHexString  
	 * @Description: 将byte[]<String>转换为16进制显示的字符串  
	 *
	 * @param ch
	 * @return
	 */
	public static String convertByteToHexString(byte[] ch) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < ch.length; i++) {
			int hex = ch[i] & HEX_CODE_000000FF;
			if (0 != (hex & HEX_CODE_FFFFFFF0)) {
				buff.append(Integer.toHexString(hex));
			} else {
				buff.append("0" + Integer.toHexString(hex));
			}
		}
		return buff.toString();
	}

	/**
	 * 
	 * @Title: convertHexStringToByte  
	 * @Description: 将16进制显示的字符串转为对应的byte[] <String>  
	 *
	 * @param hexStr
	 * @return
	 */
	public static byte[] convertHexStringToByte(String hexStr) {
		int len;

		if (hexStr.length() == 0 || hexStr.length() % 2 != 0) {
			return null;
		}
		len = hexStr.length() / 2;

		byte[] byteArray = new byte[len];
		for (int i = 0; i < len; i++) {
			byteArray[i] = Integer.valueOf(hexStr.substring(i * 2, i * 2 + 2), RADIX_HEX)
					.byteValue();
		}

		return byteArray;
	}
}
