/**
 * 
 */
package cn.woniu.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 * @author woniu
 *
 */
public class HttpRestClient {

	/* HttpClient */
	private HttpClient client;
	
	private String scheme = "http"; // HTTP ? HTTPs
	
	private String host = "";
	
	private int port	= 80; // HTTP=80  HTTPS=443

	public HttpRestClient(String scheme, String host, int port) {
		this.scheme = scheme;
		this.host = host;
		this.port = port;
//		CusProxy.getInstance().setProxy();
		this.client = getHttpClient();
	}
	


	/**
	 * getHttpClient
	 * @return HttpClient
	 */
	private HttpClient getHttpClient() {

		if("HTTP".equalsIgnoreCase(scheme)){
			// HTTP
			return HttpClients.createDefault();
		} else {

			return HttpClients.createDefault();
		}
	}

	/**
	 * 以Multipart/form的方式上传文件到服务器， 且能够回传上传进度
	 * @param request
	 * @param files
	 * @param params
	 * @param progressListener
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedOperationException 
	 */
	public String executeUpload(HttpPost request, HashMap<String, File> files, HashMap<String, String> params) 
			throws UnsupportedOperationException, Exception {

		System.out.println("[executeUpload] Start");
		String result = null;
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.setCharset(Charset.forName("UTF-8"));
		
		/* ContentType在传输中文参数(addTextBody)时很重要， 默认是ASCII解码  */
		ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));
		
		if(params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				builder.addTextBody(key, params.get(key), strContent); //TODO
			}
		}

		if (files != null && files.size() > 0) {
			for (String key : files.keySet()) {
//				builder.addBinaryBody(key, files.get(key), 
//						ContentType.APPLICATION_OCTET_STREAM, 
//						files.get(key).getName());
				builder.addBinaryBody("file", files.get(key), 
						ContentType.APPLICATION_OCTET_STREAM, 
						files.get(key).getName());
			}
		}

		if(!(request instanceof HttpEntityEnclosingRequestBase)) {
			throw new IllegalStateException("This method execute only Post/Put method");
		}

		HttpEntity entity = builder.build();

		long totalSize = entity.getContentLength();
		System.out.println("[executeUpload] totalSize=" + totalSize);

		ProgressEntity progressEntity = new ProgressEntity(entity, null, totalSize);
		request.setEntity(progressEntity);

//		HttpParams httpParams = client.getParams();
//		HttpConnectionParams.setConnectionTimeout(httpParams, 10*1000);
//		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);

		try {
			HttpResponse httpResponse = client.execute(request);
			StatusLine statusLine = httpResponse.getStatusLine();
			int code = statusLine.getStatusCode();
			String reason = statusLine.getReasonPhrase();
			System.out.println(">>>>code = " + code + " reason=" + reason);
			
			HttpEntity resEntity = httpResponse.getEntity();
			result = inputStream2String(resEntity.getContent());
			System.out.println("resContent>>>>" + result);
		} finally {
			if (request != null) {
				request.releaseConnection();
			}
		}
		return result;

	}

	/**
	 * 以Multipart/form + 分片的方式上传文件到服务器， 且能够回传上传进度
	 * @param request
	 * @param files
	 * @param params
	 * @param progressListener
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedOperationException 
	 */
	public String executeChunkUpload(HttpPost request, FileTrunkStreamBody trunkBody, HashMap<String, String> params) 
			throws UnsupportedOperationException, Exception {

		System.out.println("[executeChunkUpload] Start");
		String result = null;
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.setCharset(Charset.forName("UTF-8"));
		
		/* ContentType在传输中文参数(addTextBody)时很重要， 默认是ASCII解码  */
		ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));
		
		if(params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				builder.addTextBody(key, params.get(key), strContent); //TODO
			}
		}

		if (trunkBody != null) {
			builder.addTextBody("chunk", ""+trunkBody.getTrunkIndex(), strContent);
			builder.addTextBody("chunks", ""+trunkBody.getTrunkTotalNum(), strContent);
			builder.addTextBody("size", ""+trunkBody.getTotalSize(), strContent);
			
			builder.addPart("file", trunkBody);
		}

		if(!(request instanceof HttpEntityEnclosingRequestBase)) {
			throw new IllegalStateException("This method execute only Post/Put method");
		}

		HttpEntity entity = builder.build();

		long totalSize = entity.getContentLength();
		System.out.println("[executeUpload] totalSize=" + totalSize);

//		ProgressEntity progressEntity = new ProgressEntity(entity, null, totalSize); //TODO
		request.setEntity(entity);

//		HttpParams httpParams = client.getParams();
//		HttpConnectionParams.setConnectionTimeout(httpParams, 10*1000);
//		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);

		try {
			HttpResponse httpResponse = client.execute(request);
			StatusLine statusLine = httpResponse.getStatusLine();
			int code = statusLine.getStatusCode();
			String reason = statusLine.getReasonPhrase();
			System.out.println(">>>>code = " + code + " reason=" + reason);
			
			HttpEntity resEntity = httpResponse.getEntity();
			result = inputStream2String(resEntity.getContent());
			System.out.println("resContent>>>>" + result);
		} finally {
			if (request != null) {
				request.releaseConnection();
			}
		}
		return result;

	}

	public String execute(HttpRequestBase request) throws UnsupportedOperationException, Exception {
		String result = null;
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append("request: ");
			sb.append(request.getMethod());
			sb.append(" ");
			sb.append(request.getURI().toString());
			System.out.println("<<<<" + sb.toString()); //TODO


			HttpResponse res =  client.execute(request);

			sb = new StringBuilder();
			String reqPath = request.getURI().getPath();
			sb.append("response: reqPath: " + reqPath);
			sb.append(", ");
			sb.append(res.getStatusLine().getStatusCode());
			sb.append(" ");
			sb.append(res.getStatusLine().getReasonPhrase());
			HttpEntity entity = res.getEntity();
			sb.append(" " + entity.toString());
			
			System.out.println(">>>>" + sb.toString());
			
			result = inputStream2String(entity.getContent());
			System.out.println("resContent>>>>" + result);

		} finally {
			if (request != null) {
				request.releaseConnection();
			}
		}
		return result;
	}
	
	public String inputStream2String(InputStream inputStream) throws Exception {
		ByteArrayOutputStream baos = null;
		byte[] buffer = new byte[1024];
		int length = 0;
		try {
			baos = new ByteArrayOutputStream();
			while ((length = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
			return baos.toString("UTF-8");
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		} finally {
			if (baos != null) {
				try {
					baos.close();
					inputStream.close();
					baos = null;
					inputStream = null;
				} catch (IOException e) {
					System.out.println("HttpRestClient#inputStream2String#" + e.getMessage());
				}
			}
		}
	}

}
