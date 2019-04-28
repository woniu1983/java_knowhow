/**
 * 
 */
package cn.woniu.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

/**
 * FileTrunkStreamBody
 * 大文件分块上传
 * @author woniu
 *
 */
public class FileTrunkStreamBody extends AbstractContentBody {

	private File targetFile;

	private int trunkTotalNum = 0;

	private int trunkIndex = 0;

	private long trunkSize = 0L;
	
	private long contentLen = 0L;
	
	/**
	 * 文件总大小: bytes
	 */
	private long totalSize = 0L;

	private FileTrunkStreamBody(ContentType contentType) {
		super(contentType);
	}

	public FileTrunkStreamBody(File targetFile, int trunkNum, int trunkIndex, long trunkSize) {
		this(ContentType.APPLICATION_OCTET_STREAM);
		this.targetFile = targetFile;
		this.trunkTotalNum = trunkNum;
		this.trunkIndex = trunkIndex;
		this.trunkSize = trunkSize;
		this.totalSize = this.targetFile.length();
		
		// 计算文件chunk长度
		if (this.trunkIndex < (this.trunkTotalNum - 1)) {
			// 非最后一个块
			this.contentLen = this.trunkSize;
		} else {
			// 最后一个块的长度
			long chunkLen = this.targetFile.length() - (this.trunkIndex * this.trunkSize);
			this.contentLen =  chunkLen;
		}
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		RandomAccessFile  ranAccessFile = null;
		try {
			ranAccessFile = new RandomAccessFile(this.targetFile, "r");
			int buffLen = 4096;
			byte[] buff = new byte[buffLen];
			System.out.println("Current Chunk Index=" + this.trunkIndex);
			if(this.trunkIndex < (this.trunkTotalNum - 1)) {
				// 不是最后一块
				// 跳过前面的块，开始读取
				ranAccessFile.seek(this.trunkSize * this.trunkIndex);
				int length = 0;
				long chunkReadLen = 0L;

				// 防止读去Chunk文件块时， 读取过头, 预留  (-buffLen)
				while(chunkReadLen <= (this.trunkSize - buffLen)) {
					length = ranAccessFile.read(buff);
					out.write(buff, 0, length);
					out.flush();
					chunkReadLen += length;
				}
				if(chunkReadLen < this.trunkSize) {
					length = ranAccessFile.read(buff, 0, (int)(this.trunkSize - chunkReadLen));
					out.write(buff, 0, length);
					out.flush();
					chunkReadLen += length;
				}

			} else {
				// 最后一块
				// 跳过前面的块，开始读取
				System.out.println("!!!!Last Trunk Start=" + this.trunkIndex);
				ranAccessFile.seek(this.trunkSize * this.trunkIndex);
				int length = 0;
				long chunkReadLen = 0L;
				while((length = ranAccessFile.read(buff)) != -1) {
					out.write(buff, 0, length);
					out.flush();
					chunkReadLen += length;
				}
				System.out.println("!!!!Last Trunk END=" + this.trunkIndex);
				System.out.println("!!!!Last Trunk END=chunkReadLen=" + chunkReadLen);

			}
		} catch(IOException e) {
			throw e;
		} finally {
			if (ranAccessFile != null) {
				ranAccessFile.close();
			}
		}

	}

	@Override
	public String getFilename() {
		return targetFile.getName();
	}

	@Override
	public String getTransferEncoding() {
		return MIME.ENC_BINARY;
	}

	@Override
	public long getContentLength() {
		System.out.println("#####################this.trunkSize=" + this.trunkSize
				+ " this.contentLen=" + this.contentLen);
		return this.contentLen;
	}

	public int getTrunkTotalNum() {
		return trunkTotalNum;
	}

	public int getTrunkIndex() {
		return trunkIndex;
	}

	public long getTotalSize() {
		return totalSize;
	}
	
}
