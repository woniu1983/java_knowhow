/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	
	
	
}
