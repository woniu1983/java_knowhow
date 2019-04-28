/**
 * 
 */
package test;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;

import cn.woniu.encrypt.MD5;
import cn.woniu.http.FileTrunkIterator;
import cn.woniu.http.FileTrunkStreamBody;
import cn.woniu.http.HttpRestClient;

/**
 * @author woniu
 *
 */
public class TestHTTPMultipart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String host = "192.168.1.1";
		int port = 80;
		String scheme = "http";

		HashMap<String, File> files = new HashMap<String, File>();
		HashMap<String, String> params = new HashMap<String, String>();

		HttpRestClient client = null;
		try {
			client = new HttpRestClient(scheme, host, port);

			// 1: 获取AccessToken
			String user = "demo";
			String pwd = "demo";
			String path = "/?user/loginSubmit&isAjax=1&getToken=1&name=" + user + "&password=" + pwd;
			
			// 2： 免密token
			/* http://server/?user/loginSubmit&isAjax=1&getToken=1&login_token=[计算得到的login_token] */
			path = "/?user/loginSubmit&isAjax=1&getToken=1&login_token=";
			String apiLoginToken = "ritstestkod";
//			System.out.println("@@@@@@" + new String(Base64.encodeBase64(user.getBytes("UTF-8"))));
//			System.out.println("@@@@@@" + Base64.encodeBase64String(user.getBytes("UTF-8")));
//			System.out.println("@@@@@@MD5@@" + MD5.MD5Encode(user + apiLoginToken));
			String login_token = new String(Base64.encodeBase64(user.getBytes("UTF-8"))) + "|" + MD5.MD5Encode(user + apiLoginToken);
			System.out.println("login_token=" + login_token);
			path = path + URLEncoder.encode(login_token);
			
			

			String url = "http://" + host + ":" + port + path; 
			// 获取新Token
			HttpGet httpGet = new HttpGet(url);
			String resContent = client.execute(httpGet);

			JSONObject json = parseKDORes(resContent);
			boolean code = json.optBoolean("code", false);
			if(code) {
				System.out.println("-----Start Upload--------");
				
				String token = json.getString("data");
				
				String destPath = "/我的文档/"; // "/demo/"
				System.out.println(destPath);
				
				String sourcePath = "\\user\\hdd\\新建文件夹\\14348.txt";
				File sourceFile = new File(sourcePath);
				String fileName = sourceFile.getName();
				
				String pathUpload = "/?explorer/fileUpload&accessToken=" + token;
				String urlUpload = "http://" + host + ":" + port + pathUpload; 
				HttpPost httpPost = new HttpPost(urlUpload);

				// 参数列表
				params.put("name", fileName); // KDO此参数并不会用于最终命名
				params.put("upload_to", destPath);  

				
				
//				// 文件列表(一次只能传一个文件  KDO DMS)
//				files.put("File1", sourceFile);
//				// 上传--目前只支持一次上传一个文件
//				String upRes = client.executeUpload(httpPost, files, params);
				
				FileTrunkIterator iterator = new FileTrunkIterator(sourceFile, 10 * 1024 * 1024);
				while(iterator.hasNext()) {
					FileTrunkStreamBody trunkBody = iterator.next();
					System.out.println(">>>>Upload File Trunk:::: " + trunkBody.getTrunkIndex());
					
					String upRes = client.executeChunkUpload(httpPost, trunkBody, params);
					
					JSONObject upJson = parseKDORes(upRes);
					boolean upCode = upJson.optBoolean("code", false);
					if(upCode) {
						System.out.println(upJson.optString("data", "上传 分片" + trunkBody.getTrunkIndex() + "成功"));
					} else {
						System.out.println(upJson.optString("data", "上传" + trunkBody.getTrunkIndex() + "失败"));
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		//		catch (URISyntaxException e) {
		//			e.printStackTrace();
		//		}
		catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static JSONObject parseKDORes(String resContent) {
		JSONObject json = new JSONObject(resContent);
//		String code = json.getString("code");
//		String use_time = json.getString("use_time");
//		String data = json.getString("data");
		return json;
	}

}
