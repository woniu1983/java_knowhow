/** 
 * Copyright (c) 2018, RITS All Rights Reserved. 
 * 
 */ 
package cn.woniu.math;

import java.math.BigDecimal;

/** 
 * @ClassName: BigDecimalMath <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午6:35:02 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class BigDecimalMath {
	
	private static final BigDecimal ONE = new BigDecimal("1");
	
	private BigDecimalMath() {
		
	}
	
    /**   
     *   提供精确的加法运算。   
     *   @param   v1   被加数   
     *   @param   v2   加数   
     *   @return   两个参数的和   
     */   
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		double result = b1.add(b2).doubleValue();
//		double result = b1.add(b2).divide(ONE, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
//		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
//		System.out.println(v1 + " + " + v2 + " = " + df.format(result));
		
		return result;
	}

    /**   
     *   提供精确的减法运算。   
     *   @param   v1   被减数   
     *   @param   v2   减数   
     *   @return   两个参数的差   
     */   
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		double result = b1.subtract(b2).doubleValue();
//		double result = b1.subtract(b2).divide(ONE, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
//		System.out.println(v1 + " - " + v2 + " = " + result);
		return result;
		
	}

    /**   
     *   提供精确的乘法运算。   
     *   @param   v1   被乘数   
     *   @param   v2   乘数   
     *   @return   两个参数的积   
     */   
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		double result = b1.multiply(b2).doubleValue();
//		double result = b1.multiply(b2).divide(ONE, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
//		System.out.println(v1 + " x " + v2 + " = " + result);
		return result;
	}

    /**   
     *   提供（相对）精确的除法运算，当发生除不尽的情况时，精确到   
     *   小数点以后10位，以后的数字四舍五入。   
     *   @param   v1   被除数   
     *   @param   v2   除数   
     *   @return   两个参数的商   
     */   
	public static double div(double v1, double v2) {
		return div(v1, v2, 10);
	}

    /**   
     *   提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指   
     *   定精度，以后的数字四舍五入。   
     *   @param   v1   被除数   
     *   @param   v2   除数   
     *   @param   scale   表示需要精确到小数点以后几位。   
     *   @return   两个参数的商   
     */  
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		double result = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println(v1 + " / " + v2 + " = " + result + ", scale = " + scale);
		return result;
	}
	
    /**   
     *   提供精确的小数位四舍五入处理。   
     *   @param   v   需要四舍五入的数字   
     *   @param   scale   小数点后保留几位   
     *   @return   四舍五入后的结果   
     */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		return b.divide(ONE, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
//	public static void main(String[] args) {
//		double i1 = 2.034;
//		double i2 = 0.5;
//		
//		add(i1, i2);
//		
//	}

	/**
	 *   提供精确的乘法运算。
	 *   @param   v1   被乘数
	 *   @param   v2   乘数
	 *   @return   两个参数的积
	 */
	public static int mulInt(double v1, int v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		int result = b1.multiply(b2).intValue();
		return result;
	}}
