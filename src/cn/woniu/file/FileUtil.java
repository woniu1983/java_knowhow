/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import cn.woniu.common.StringUtils;
import cn.woniu.log.Logger;

/** 
 * @ClassName: FileUtil <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午6:20:37 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class FileUtil {

    /**
     * 根据文件路径获取文件
     */
    public static File getFileByPath(String filePath) {
        return StringUtils.isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在
     *
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 重命名文件
     *
     */
    public static boolean rename(String filePath, String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * 重命名文件
     *
     */
    public static boolean rename(File file, String newName) {
        // 文件为空返回false
        if (file == null) return false;
        // 文件不存在返回false
        if (!file.exists()) return false;
        // 新的文件名为空返回false
        if (StringUtils.isSpace(newName)) return false;
        // 如果文件名没有改变返回true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回false
        return !newFile.exists()
                && file.renameTo(newFile);
    }


    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     */
    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     */
    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	/**
	 * Delete files under Directory: path
	 * @param path
	 * @return
	 */
	public static boolean deleteAll(String path) {
		if (path == null) {
			return true;
		}
		boolean result = true;
		Logger.Debug("<FileUtil>#deleteAll(" + path + ")");
		
		File dir = new File(path);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i =0; i < files.length; i++) {
				boolean perResult = files[i].delete();
				Logger.Debug("<FileUtil>#deleteAll" +  
						"===>Delete File [" + files[i].getAbsolutePath() + "] = " + perResult);
				result &= perResult;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @Title: copyFile  
	 * @Description: Copy source file to destination  
	 *
	 * @param srcFile  Source File
	 * @param destFile Destination File
	 * @return
	 */
	public static boolean copyFile(File srcFile, File destFile) {
		boolean result = false;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(srcFile));
			out = new BufferedOutputStream(new FileOutputStream(destFile));
			
			byte[] buffer = new byte[1024];
			int i = 0;
			while ( (i = in.read(buffer)) > 0) {
				out.write(buffer, 0, i);
			}
			out.flush();
			result = true;
			
		} catch (IOException e) {
			
			result = false;
			Logger.Error("<FileUtil>#copyFile -- " +  
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
	


	/**
	 * バイト配列内から一致するバイト配列を検索します。<br>
	 *
	 * @param buf 判定対象のバイト配列
	 * @param offset オフセット情報
	 * @param len 判定長
	 * @param compBytes 確認バイト配列
	 * @return 位置情報（ヒットしない場合は-1）
	 */
	public static int search(byte[] buf, int offset, int len, byte[] compBytes) {
		int max = buf.length - offset - compBytes.length;
		if (max <= 0) {
			return -1;
		}
		if (len < max) {
			max = len;
		}
		for (int i = 0; i < max; i++) {
			int j = 0;
			for (j = 0; j < compBytes.length; j++) {
				if (buf[offset + i + j] != compBytes[j]) {
					break;
				}
			}
			if (j == compBytes.length) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 读取文件内容，存储到String对象中，针对小文件
	 * 大文件切勿使用
	*/
	public static String readFromFile(String srcFilePath) {
		if (srcFilePath == null || srcFilePath.trim().isEmpty()) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		File logFile = new File(srcFilePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"));
			String line = "";
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\r\n");
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		
		return builder.toString();
	}
	
}
