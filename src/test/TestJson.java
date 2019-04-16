package test;

import org.json.JSONObject;

import cn.woniu.common.JSONUtils;

public class TestJson {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String jsonStr = "{\"name\": \"shawn1992\",\"address\":null}";
		
		JSONObject data = new JSONObject(jsonStr);
		String nameString = data.optString("name", "123");
		String addressString = data.optString("address", "123");
		System.out.println("name=" + nameString);
		System.out.println("address=" + addressString);

		System.out.println("-------------------------------");
		
		Object nameObj = data.opt("name");
		String addObj = data.optString("address");
		System.out.println("name=" + nameObj);
		System.out.println("address=" + addObj);

		System.out.println("-------------------------------");
		
		String name2String = JSONUtils.optString(data, "name", "123");
		String address2String = JSONUtils.optString(data, "address", "123");
		System.out.println("name=" + name2String);
		System.out.println("address=" + address2String);
	}

}
