/** 
 * Copyright (c) 2018, RITS All Rights Reserved. 
 * 
 */ 
package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.woniu.file.FileUtil;

/** 
 * @ClassName: TestFileSearch <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu 
 * @date: 2018年5月7日 下午1:09:01 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class TestFileSearch {
	
	public static void main(String[] args) {
		File srcFile = new File("F:\\New folder\\large1.pdf");
		File destFile = new File("F:\\New folder\\large1_filter.pdf");
		String key = "%PDF-";
		testSearchFirst(srcFile, key, destFile);
	}
	
	private static boolean testSearchFirst(File srcFile, String key, File destFile) {
		boolean result = false;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(srcFile));
			out = new BufferedOutputStream(new FileOutputStream(destFile));
			
			byte[] buffer = new byte[4096];
			int i = 0;
			boolean firstRead = true;
			while ( (i = in.read(buffer)) > 0) {
				if (firstRead) {
					firstRead = false;
					int index = FileUtil.search(buffer, 0, i, key.getBytes());
					out.write(buffer, index, i - index);					
				} else {
					out.write(buffer, 0, i);
				}
			}
			out.flush();
			result = true;
			
		} catch (IOException e) {
			
			result = false;
			System.out.println("<FileUtil>#copyFile -- " +  
					"Copy file[" + srcFile.getAbsolutePath() + "] to [" + destFile.getAbsolutePath() + "] failed.");
			
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
