/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.math;

/** 
 * @ClassName: MathUtils <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午6:40:03 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class MathUtils {

	/**
	 * 设置整数二进制后的某位为1
	 * @param src   源整数
	 * @param index 第几位（从最右边0位开始）
	 * @return
	 */
	public static int bitSet(int src, int index) {
		if (index >= 31) {
			// 最高位不能修改
			return src;
		}
		src = src | ((int)Math.pow(2, index));
		return src;
	}

	/**
	 * 整数转二进制数字符串
	 * @param src 源整数
	 * @return
	 */
	public static String int2BinStr(int src){
		String binStr = Integer.toBinaryString(src);
		String result = String.format("%32s", binStr);
		result = result.replaceAll("\\s","0");
		return result;
	}
	
	/**
	 * 
	 * @Title: checkBit  
	 * @Description: 查看整数的二进制模式下，某一位是0还是1  
	 *
	 * @param val
	 * @param pos
	 * @return
	 */
	private int checkBit(int val, int pos) {
		int com = (int)Math.pow(2, pos);
		if ((val & com) == 0) {
			return 0;
		} else {
			return 1;
		}
	}

}
