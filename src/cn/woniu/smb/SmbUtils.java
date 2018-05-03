/** 
 * Copyright (c) 2018, RITS All Rights Reserved. 
 * 
 */ 
package cn.woniu.smb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import cn.woniu.log.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/** 
 * @ClassName: SmbUtils <br/> 
 * @Description: 共享目录访问， 目前只支持SMBv1  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月2日 上午9:08:26 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class SmbUtils {

	private static final String FORWARD = "\\";
	private static final String BACKWARD = "/";

	private static final String UNC_START = "//";

	static {
		// System.setProperty("jcifs.smb.client.dfs.disabled", "true");
		jcifs.Config.setProperty("jcifs.smb.client.dfs.disabled", "true");
		jcifs.Config.setProperty("jcifs.smb.client.connTimeout", "20000");
		jcifs.Config.setProperty("jcifs.smb.client.responseTimeout", "5000"); // 30000
																				// default
		// jcifs.Config.setProperty("jcifs.smb.client.soTimeout", "10000"); //
		// 35000 default
		jcifs.Config.setProperty("jcifs.netbios.retryCount ", "1");
	}

	public static void updateSmbESConfig(boolean smbes) {
		if (smbes) {
			jcifs.Config.setProperty("jcifs.smb.client.useExtendedSecurity",
					"true");
			jcifs.Config.setProperty("jcifs.smb.lmcompatibility", "3");
		} else {
			jcifs.Config.setProperty("jcifs.smb.client.useExtendedSecurity",
					"false");
			jcifs.Config.setProperty("jcifs.smb.lmcompatibility", "0");

		}
	}

	public static String normalizeSmbPath(String path, boolean isDirectory) {
		// Clear space in the beginning and ending
		path = path.trim();

		// Replace "\\" to "/"
		String normalizePath = StringUtils.replace(path, FORWARD, BACKWARD);

		// Remove "//" in the beginning
		if (normalizePath.startsWith(UNC_START)) {
			normalizePath = new String(normalizePath.substring(2));
		}

		// Append "/" in the ending for Smb Directory
		if (isDirectory) {
			// Folder
			if (!normalizePath.endsWith(BACKWARD)) {
				normalizePath = normalizePath + BACKWARD;
			}
		} else {
			// File
			if (normalizePath.endsWith(BACKWARD)) {
				normalizePath = new String(normalizePath.substring(0,
						normalizePath.length() - 1));
			}
		}

		return normalizePath;
	}

	public static String createSmbPath(String path, boolean isDirectory) {
		String normailzePath = normalizeSmbPath(path, isDirectory);

		StringBuilder builder = new StringBuilder();
		builder.append("smb://");

		if (normailzePath.startsWith("//")) {
			builder.append(new String(normailzePath.substring(2)));
		} else {
			builder.append(normailzePath);
		}

		Logger.Debug("SMB Path=<" + builder.toString() + ">");

		return builder.toString();

	}

	public static NtlmPasswordAuthentication getSmbNtlmAuth(String username,
			String password) {
		String domain = "";
		if (username != null && username.trim().length() > 0) {
			username = username.trim();
			int index = -1;
			if (username.contains("\\")) {
				index = username.indexOf("\\");
			} else if (username.contains("/")) {
				index = username.indexOf("/");
			}
			if (index > -1) {
				domain = username.substring(0, index);
				if ((index + 1 + 1) < username.length()) {
					username = username.substring(index + 1);
				} else {
					username = "";
				}
			}
		}

		Logger.Debug("Domain=<" + domain + "> username=<" + username + ">");
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
				domain, username, password);
		return auth;
	}

	public static String getDomain(String username) {
		if (username.contains("\\")) {
			return new String(username.substring(0, username.indexOf("\\")));
		} else if (username.contains("/")) {
			return new String(username.substring(0, username.indexOf("/")));
		} else {
			return "";
		}
	}

	public static String getUsername(String username) {
		if (username.contains("\\")) {
			return new String(username.substring(username.indexOf("\\") + 1));
		} else if (username.contains("/")) {
			return new String(username.substring(username.indexOf("/") + 1));
		} else {
			return username;
		}

	}

	public static boolean testSMBConnect(String path, String username,
			String password, boolean isDir) throws Exception {
		boolean connected = false;
		long t1 = System.currentTimeMillis();
		long t2 = 0L;
		long t3 = 0L;
		long t4 = 0L;
		long t5 = 0L;
		try {
			String smbPath = SmbUtils.createSmbPath(path, isDir);
			NtlmPasswordAuthentication auth = SmbUtils.getSmbNtlmAuth(username,
					password);
			t2 = System.currentTimeMillis();
			SmbFile directory = new SmbFile(smbPath, auth);
			t3 = System.currentTimeMillis();
			directory.connect();
			t4 = System.currentTimeMillis();
			if (directory.exists()) {
				connected = true;
			} else {
				connected = false;
			}
		} catch (Exception ex) {
			t5 = System.currentTimeMillis();
			Logger.Error("Fail to connect, Cost time [" + (t5 - t1) + " -- "
					+ "] " + ex.getMessage());
			throw ex;
		}
		Logger.Debug("Cost time [" + (t2 - t1) + " -- " + (t3 - t2) + " -- "
				+ (t4 - t3) + " -- " + "] ");
		return connected;
	}

	/** User Abort this transfer(Send/Upload) */
	public static boolean user_abort = false;

	/** User Abort this Download(Get/Download) */
	public static boolean user_abort_dump = false;

	/**
	 * 
	 * @Title: sendFile
	 * @Description: Copy(Overwrite) local file to remote share folder(SMB)
	 * @param targetPath
	 *            String : Destination File path
	 * @param isDir
	 *            boolean: Destination is Folder or File
	 * @param username
	 *            String : SMB Auth UserName
	 * @param password
	 *            String : SMB Auth Password
	 * @param sourcePath
	 *            String : Source File Absolute Path
	 * @return
	 * @throws Exception
	 * @return boolean
	 */
	public static boolean sendFile(String targetPath, boolean isDir,
			String username, String password, String sourcePath)
			throws Exception {

		if (sourcePath == null || sourcePath.trim().isEmpty()) {
			throw new Exception("Source File is not specified.");
		}
		File file = new File(sourcePath);
		String fileName = file.getName();
		if (fileName == null || fileName.trim().isEmpty()) {
			throw new Exception("Source File is not specified.");
		}

		String data = String.valueOf(System.currentTimeMillis());
		if (isDir) {
			fileName = data + "_" + fileName;
			targetPath = targetPath + File.separator + fileName;
			Logger.Debug("SmbUtils targetPath:"+targetPath);
		}

		return sendFile(targetPath, username, password, sourcePath);
	}

	/**
	 * 
	 * @Title: sendFile
	 * @Description: Copy(Overwrite) local file to remote share folder(SMB)
	 * @param targetPath
	 *            String : Destination File path
	 * @param username
	 *            String : SMB Auth UserName
	 * @param password
	 *            String : SMB Auth Password
	 * @param sourcePath
	 *            String : Source File Absolute Path
	 * @throws Exception
	 * @return boolean
	 */
	public static boolean sendFile(String targetPath, String username,
			String password, String sourcePath) throws Exception {
		Logger.Debug("Copy File: from sourcePath=<" + sourcePath + ">"
				+ " to targetPath=<" + targetPath + ">");// TODO
		boolean result = false;
		user_abort = false;
		FileInputStream fin = null;
		File source = null;

		SmbFile smbFile = null;
		SmbFileOutputStream fout = null;
		try {

			String smbPath = SmbUtils.createSmbPath(targetPath, false);
			NtlmPasswordAuthentication auth = SmbUtils.getSmbNtlmAuth(username,
					password);
			smbFile = new SmbFile(smbPath, auth);

			byte[] buff = new byte[1024 * 4];
			source = new File(sourcePath);
			fin = new FileInputStream(source);

			fout = new SmbFileOutputStream(smbFile);
			int len = fin.read(buff);
			// Logger.Debug("Doing: Read len = " + len); //TODO
			while (len != -1) {
				if (user_abort) {
					// User Cancel => Quit Transfering
					Logger.Debug("Doing: User cancel file transfor");
					break;
				} else {
					// Logger.Debug("Copy File: len=" + len);//TODO
					fout.write(buff, 0, len);
					len = fin.read(buff);
				}
			}

			result = true;
		} catch (Exception ex) {
			Logger.Error("Fail to Send File:" + sourcePath + " : "
					+ ex.getMessage());
			throw ex;
		} finally {
			IOUtils.closeQuietly(fin);
			IOUtils.closeQuietly(fout);
			// FileDeleteStrategy.FORCE.deleteQuietly(source);

			if (user_abort) {
				Logger.Debug("Doing: Delete File due to User cancel file transfor.");
				smbFile.delete();
			}
		}
		return result;
	}

	public static boolean createFile(String targetPath, String username,
			String password, boolean isDir) throws Exception {

		boolean result = false;
		user_abort = false;

		SmbFile smbFile = null;
		try {

			String smbPath = SmbUtils.createSmbPath(targetPath, isDir);
			NtlmPasswordAuthentication auth = SmbUtils.getSmbNtlmAuth(username,
					password);
			smbFile = new SmbFile(smbPath, auth);

			if (isDir) {
				// Create Folder
				if (!smbFile.exists()) {
					smbFile.mkdirs();
				}
			} else {
				// Create File
				if (!smbFile.exists()) {
					smbFile.createNewFile();
				}
			}

			result = true;
		} catch (Exception ex) {
			Logger.Error("Fail to create File/Folder:" + targetPath + " : "
					+ ex.getMessage());
			throw ex;
		} finally {
		}
		return result;

	}

	/**
	 * 
	 * @Title: getFile
	 * @Description: Download(Overwrite) file from remote share folder to
	 *               local(SMB)
	 * @param targetPath
	 *            String : Destination Folder/File
	 * @param isDir
	 *            boolean: Destination is Folder or File
	 * @param username
	 *            String : SMB Auth UserName
	 * @param password
	 *            String : SMB Auth Password
	 * @param sourcePath
	 *            String : Source File Absolute Path
	 * @throws Exception
	 * @return boolean
	 */
	public static boolean getFile(String targetPath, boolean isDir,
			String username, String password, String sourcePath)
			throws Exception {

		if (sourcePath == null || sourcePath.trim().isEmpty()) {
			throw new Exception("Source File is not specified.");
		}
		File file = new File(sourcePath);
		String fileName = file.getName();
		if (fileName == null || fileName.trim().isEmpty()) {
			throw new Exception("Source File is not specified.");
		}

		if (isDir) {
			targetPath = targetPath + File.separator + fileName;
		}

		boolean result = false;
		user_abort_dump = false;

		FileOutputStream fout = null;

		SmbFile smbFile = null;
		SmbFileInputStream fin = null;
		try {

			String smbPath = SmbUtils.createSmbPath(sourcePath, false);
			NtlmPasswordAuthentication auth = SmbUtils.getSmbNtlmAuth(username,
					password);
			smbFile = new SmbFile(smbPath, auth);

			byte[] buff = new byte[1024 * 4];

			fin = new SmbFileInputStream(smbFile);
			fout = new FileOutputStream(targetPath);
			int len = fin.read(buff);
			while (len != -1) {
				if (user_abort_dump) {
					// User Cancel => Quit Transfering
					Logger.Debug("Doing: User cancel file transfor");
					break;
				} else {
					fout.write(buff, 0, len);
					len = fin.read(buff);
				}
			}

			result = true;
		} catch (Exception ex) {
			Logger.Error("Fail to Download File:" + sourcePath + " : "
					+ ex.getMessage());
			throw ex;
		} finally {
			IOUtils.closeQuietly(fin);
			IOUtils.closeQuietly(fout);

		}
		return result;
	}
	
}
