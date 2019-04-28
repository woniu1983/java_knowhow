/**
 * 
 */
package cn.woniu.http;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author woniu
 *
 */
public class FileTrunkIterator implements Iterator<FileTrunkStreamBody> {
	
	/**
	 * 默认Trunk分块大小 1MB
	 */
	private static long DEFAULT_TRUNK_SIZE = 1024 * 1024;
	
	/**
	 * 最小 4KB
	 */
	private static long MIN_TRUNK_SIZE = 4096;
	
	private File targetFile;
	
	private long trunkSize = DEFAULT_TRUNK_SIZE;
	
	private int trunkTotalNum = 0;
	
	private int lastTrunkIndex = -1;
	
	public FileTrunkIterator(File targetFile) throws IOException {
		this(targetFile, DEFAULT_TRUNK_SIZE);
	}
	
	public FileTrunkIterator(File targetFile, long trunkSize) throws IOException {
		super();
		if (trunkSize < MIN_TRUNK_SIZE) {
			trunkSize = MIN_TRUNK_SIZE;
		}
		this.trunkSize = trunkSize;
		this.targetFile = targetFile;		
		if(this.targetFile == null) {
			throw new IOException("Invalid File paremeters.");
		}
		if(!this.targetFile.exists() || !this.targetFile.isFile()) {
			throw new IOException("File not exists or not a file!");
		}
		
		long fileSize = this.targetFile.length();
		this.trunkTotalNum = (int) Math.ceil(fileSize * 1.0d / this.trunkSize);
		this.lastTrunkIndex = -1;
		System.out.println(">>>>>this.trunkTotalNum=" + this.trunkTotalNum);
	}

	@Override
	public boolean hasNext() {
		if(this.lastTrunkIndex < (this.trunkTotalNum - 1)) {
			return true;
		}
		return false;
	}

	@Override
	public FileTrunkStreamBody next() {
		if (hasNext() == false) {
            throw new NoSuchElementException();
        }
		this.lastTrunkIndex++;
		return getFromIndex(this.lastTrunkIndex);
	}
	
	public FileTrunkStreamBody getFromIndex(int index) {
		FileTrunkStreamBody body = null;
		if(index < (this.trunkTotalNum - 1)) {
			// 前面的完整chunk，大小固定
			body = new FileTrunkStreamBody(this.targetFile, this.trunkTotalNum, index, this.trunkSize);
			return body;
		}  else if (index == (this.trunkTotalNum - 1)){
			// 最后一个， 块大小非固定
			// 或者有且仅有一个块时
//			long chunkLen = this.targetFile.length() - (index * this.trunkSize);
			body = new FileTrunkStreamBody(this.targetFile, this.trunkTotalNum, index, this.trunkSize);
			return body;
		} else {
			// 找不到
			
			throw new NoSuchElementException();
		}
        
	}

}
