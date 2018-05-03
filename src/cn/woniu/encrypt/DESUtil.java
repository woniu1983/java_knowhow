/** 
 * Copyright (c) 2018, RITS All Rights Reserved. 
 * 
 */ 
package cn.woniu.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/** 
 * @ClassName: DESUtil <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午6:30:37 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class DESUtil {

	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };
	
	public static final String DEFAULT_KEY = "test.any"; //目前只支持8位的key

	public static String encryptDES(String encryptString, String encryptKey)
			throws Exception {
		if (encryptKey == null){
			encryptKey = DEFAULT_KEY;
		}
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return Base64.encode(encryptedData);
	}
	
	public static String decryptDES(String decryptString, String decryptKey)
	throws Exception {
		if (decryptKey == null){
			decryptKey = DEFAULT_KEY;
		}
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);

		return new String(decryptedData);
	}
	
	/**
	 * @param buf 
	 * @return  String
	 */  
	public static String parseByte2HexStr(byte buf[]) {  
		StringBuffer sb = new StringBuffer();  
		for (int i = 0; i < buf.length; i++) {  
			String hex = Integer.toHexString(buf[i] & 0xFF);  
			if (hex.length() == 1) {  
				hex = '0' + hex;  
			}  
			sb.append(hex.toUpperCase());  
		}  
		return sb.toString();  
	}}
