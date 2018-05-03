/** 
 * Copyright (c) 2018, RITS All Rights Reserved. 
 * 
 */ 
package cn.woniu.common;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

/** 
 * @ClassName: StringUtils <br/> 
 * @Description: 字符串常用操作  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午6:24:25 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断字符串是否为null或长度为0
     *
     */
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     */
    public static boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 判断两字符串是否相等
     *
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两字符串忽略大小写是否相等
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        return (a == b) || (b != null) && (a.length() == b.length()) && a.regionMatches(true, 0, b, 0, b.length());
    }

    /**
     * null转为长度为0的字符串
     */
    public static String null2Length0(String s) {
        return s == null ? "" : s;
    }

    /**
     * 返回字符串长度
     */
    public static int length(CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * 首字母大写
     *
     */
    public static String upperFirstLetter(String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     *
     */
    public static String lowerFirstLetter(String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     */
    public static String reverse(String s) {
        int len = length(s);
        if (len <= 1) return s;
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 转化为半角字符
     *
     */
    public static String toDBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     */
    public static String toSBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 
     * @Title: fill  
     * @Description: 返回指定个数的char组成的字符串  
     *
     * @param size
     * @param pattern
     * @return
     */
	public static String fill(int size, char pattern) {
		if (size == 0) {
			return "";
		}

		String str = "";
		for (int i = 0; i < size; i++) {
			str += pattern;
		}
		return str;
	}

	/**
	 * 
	 * @Title: paddingIPaddress  
	 * @Description: 将IP地址格式化为 000.000.000.000 
	 *
	 * @param strIPAddress
	 * @return
	 */
	public static String paddingIPaddress(String strIPAddress) {
		if (strIPAddress == null || strIPAddress.equals("")) {
			return strIPAddress;
		}

		DecimalFormat ipFormat = new DecimalFormat("000");
		String strDelimitter = ".";

		StringBuffer buff = new StringBuffer();
		StringTokenizer st = new StringTokenizer(strIPAddress, strDelimitter);

		try {
			while (st.hasMoreTokens()) {
				String strIpToken = st.nextToken();
				String strIP = ipFormat.format(new Integer(strIpToken));
				strIP += strDelimitter;
				buff.append(strIP);
			}
			buff.deleteCharAt(buff.length() - 1);
			return buff.toString();
		} catch (NumberFormatException e) {
			return strIPAddress;
		}
	}

	/**
	 * 
	 * @Title: isNumberString  
	 * @Description: 判断字符串是不是数字  
	 *
	 * @param str
	 * @return boolean
	 */
	public static boolean isNumberString(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @Title: parseBoolean  
	 * @Description: 判断字符串是不是Boolean[true], 否则全部为false  
	 *
	 * @param str
	 * @return
	 */
	public static boolean parseBoolean(String str) {
		if (str == null || str.length() == 0) {
			return false;
		} else if (str.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @Title: isIncludedChar  
	 * @Description: 判断字符串str中是否包含特定的字符数组(char[])  
	 *
	 * @param str
	 * @param compChars
	 * @return
	 */
	public static boolean isIncludedChar(String str, char[] compChars) {
		if (str == null || str.length() == 0) {
			return false;
		}
		if (compChars == null || compChars.length == 0) {
			return false;
		}
		char[] temp = str.toCharArray();
		for (int i = 0; i < temp.length; i++){
			for (int j = 0; j < compChars.length; j++) {
				if (temp[i] == compChars[j]){
					return true;
				}
			}
		}
		return false;
	}    
}
