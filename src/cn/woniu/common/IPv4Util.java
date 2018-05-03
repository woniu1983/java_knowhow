/** 
 * Copyright (c) 2018, RITS All Rights Reserved. 
 * 
 */ 
package cn.woniu.common;

import java.util.regex.Pattern;

/** 
 * @ClassName: IPv4Util <br/> 
 * @Description: Ipv4格式化和检查  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午7:10:48 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class IPv4Util {

	private static Pattern ptipv4;

	static {

		// ipv4 校验

		/* 数字前不能带0 */
		// ptipv4 =
		// Pattern.compile("^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$");

		/* 数字前能带0 */
		ptipv4 = Pattern.compile(
				"^(\\d|0\\d{1,2}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|0\\d{1,2}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$");
	}

	public static void main(String[] args) {
		System.out.println("---------------------");
		System.out.println(ptipv4.matcher("1.1.0.1").matches());
		System.out.println(ptipv4.matcher("123.1.0.19").matches());
		System.out.println(ptipv4.matcher("255.255.255.255").matches());
		System.out.println(ptipv4.matcher("0.0.0.0").matches());
		System.out.println(ptipv4.matcher("072.001.002.000").matches());

		System.out.println("---------------------");
		System.out.println(ptipv4.matcher("-1.1.0.1").matches());
		System.out.println(ptipv4.matcher("1.1b.0.1").matches());
		System.out.println(ptipv4.matcher("1.01.0.1").matches());
		System.out.println(ptipv4.matcher("1.1.300.1").matches());
		System.out.println(ptipv4.matcher("1.1..1").matches());
		System.out.println(ptipv4.matcher("0.0.0.0000").matches());
		System.out.println(ptipv4.matcher("0.0.0000.00").matches());

		System.out.println("---------------------");
		try {
			System.out.println(formatIPv4("1.1.0.1"));
			System.out.println(formatIPv4("123.1.0.19"));
			System.out.println(formatIPv4("255.255.255.255"));
			System.out.println(formatIPv4("0.0.0.0"));
			System.out.println(formatIPv4("072.001.002.000"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断是否为合法的IPv4地址
	 * 
	 * @Title: isValidIPv4
	 * @Description: TODO
	 *
	 * @param ipv4
	 * @return
	 */
	public static boolean isValidIPv4(String ipv4) {

		if (ipv4 == null || ipv4.trim().isEmpty()) {
			return false;
		}

		return ptipv4.matcher(ipv4).matches();
	}

	// 将毫秒数格式化
	public static String formatTime(long elapsed) {
		int hour, minute, second, milli;

		milli = (int) (elapsed % 1000);
		elapsed = elapsed / 1000;

		second = (int) (elapsed % 60);
		elapsed = elapsed / 60;

		minute = (int) (elapsed % 60);
		elapsed = elapsed / 60;

		hour = (int) (elapsed % 60);

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	/**
	 * 将IPv4地址格式化为 ： 去零化
	 * 
	 * @Title: formatIPv4
	 * @Description: 172.025.073.109 --> 172.25.73.109
	 *
	 * @param ipv4
	 * @return
	 * @throws Exception
	 */
	public static String formatIPv4(String ipv4) throws Exception {
		if (ipv4 == null || ipv4.trim().isEmpty()) {
			throw new Exception("Ipv4 address is empty");
		}

		boolean valid = isValidIPv4(ipv4);
		if (!valid) {
			throw new Exception("Invalid Ipv4 address");
		}

		String[] ipNums = ipv4.split("\\.");
		StringBuilder builder = new StringBuilder();

		// 将IPv4地址格式化为 ：x.x.x.x
		for (int i = 0; i < ipNums.length; i++) {
			int num = Integer.valueOf(ipNums[i]);
			builder.append(num);

			if (i < (ipNums.length - 1)) {
				builder.append(".");
			}
		}

		return builder.toString();

	}

	/**
	 * 将IPv4地址格式化为 ： 补零化
	 * 
	 * @Title: formatIPv4
	 * @Description: 172.025.073.109 --> 172.025.073.109
	 *
	 * @param ipv4
	 * @return
	 * @throws Exception
	 */
	public static String formatIPv4WithZero(String ipv4) throws Exception {
		if (ipv4 == null || ipv4.trim().isEmpty()) {
			throw new Exception("Ipv4 address is empty");
		}

		boolean valid = isValidIPv4(ipv4);
		if (!valid) {
			throw new Exception("Invalid Ipv4 address");
		}

		String[] ipNums = ipv4.split("\\.");
		StringBuilder builder = new StringBuilder();

		// 将IPv4地址格式化为 ： 000.000.000.000 不支持， 访问WIM时会出错
		for (int i = 0; i < ipNums.length; i++) {
			builder.append(String.format("%03d", Integer.valueOf(ipNums[i])));

			if (i < (ipNums.length - 1)) {
				builder.append(".");
			}
		}

		return builder.toString();

	}


}
