package cn.woniu.http;

public class ProgressListener {

	protected long contentLen = 0;

	public void onFinished(String msg) {
		System.out.println(msg);
	}
	
	public void onFailed(Exception e) {
		System.out.println(e.getMessage());
	}

	public void onProgressUpdate(long progress) {
		System.out.println("onProgress: " + progress);
	}

	public long getContentLen() {
		return contentLen;
	}

	public void setContentLen(long contentLen) {
		this.contentLen = contentLen;
	}
}
