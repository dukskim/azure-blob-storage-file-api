package com.abs.utility.common.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileCtlUtil {
	
	/**
	 * 압축을 해제한다.
	 * 
	 * @param fileName
	 * @param targetDir
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void unzip(String fileName, String targetDir) throws IOException {
		File tmp_dir = new File(targetDir); // 압축을 해제할 임시 파일
		if (!tmp_dir.exists()) {
			tmp_dir.mkdirs();
		}
		FileOutputStream fos = null;
		InputStream in = null;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(fileName);
			Enumeration enu = zipFile.entries();
			byte[] bytes = new byte[4096];
			int index = -1;
			while (enu.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) enu.nextElement();
				String name = entry.getName();
				
				// 파일이 디렉토리 안에 포함되어 있는 경우 디렉토리를 별도로 만들어주어야 한다.
				index = name.lastIndexOf("/");
				if (index != -1) {
					File mFile = new File(tmp_dir, name.substring(0, index));
					if (!mFile.exists()) {
						mFile.mkdirs();
					}
				}
				
				in = zipFile.getInputStream(entry);
				fos = new FileOutputStream(new File(tmp_dir, name));
				int red = -1;
				while (true) {
					red = in.read(bytes);
					if (red < 0) {
						break;
					}
					fos.write(bytes, 0, red);
				}
				fos.flush();
				fos.close();
			}
			in.close();
			zipFile.close();
		} catch (IOException e) {
			throw new IOException();
		} finally {
			if (fos != null) {
				fos.flush();
				fos.close();
			}
			if (in != null) {
				in.close();
			}
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}
	
	/**
	 * 파일이름에서 확장자를 얻어온다
	 * ie
	 * 
	 * <pre>
	 * foo.txt   --> "txt"
	 * a\b\c.jpg --> "jpg"
	 * a\b\c     --> ""
	 * </pre>
	 * 
	 * @param filename
	 * @return String
	 */
	public static String getExtension(final String filename) {
		final int index = filename.lastIndexOf('.');
		
		if (-1 == index) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}
	
	/**
	 * 파일 확장자를 제외한 부분을 돌려준다
	 * ie
	 * 
	 * <pre>
	 * foo.txt   --> foo
	 * a\b\c.jpg --> a\b\c
	 * a\b\c     --> a\b\c
	 * </pre>
	 * 
	 * @param filename
	 * @return String
	 */
	public static String removeExtension(final String filename) {
		final int index = filename.lastIndexOf('.');
		
		if (-1 == index) {
			return filename;
		} else {
			return filename.substring(0, index);
		}
	}
	
	/**
	 * 파일이름에서 확장자를 제거한 파일이름만을 돌려준다.
	 * ie.
	 * 
	 * <pre>
	 * a/b/c.txt --> a/b
	 * a.txt     --> ""
	 * </pre>
	 * 
	 * @param filepath
	 * @return String
	 */
	public static String getPath(final String filepath) {
		return getPath(filepath, File.separatorChar);
	}
	
	/**
	 * 파일이름에서 확장자을 제거한 파일이름만을 돌려준다
	 * ie.
	 * 
	 * <pre>
	 * a/b/c.txt --> a/b
	 * a.txt     --> ""
	 * </pre>
	 * 
	 * @param filepath
	 * @param fileSeparatorChar - 분리자( Unix :/ 윈도우:\)
	 * @return 확장자가 제거된 파일 이름
	 */
	public static String getPath(final String filepath, final char fileSeparatorChar) {
		final int index = filepath.lastIndexOf(fileSeparatorChar);
		if (-1 == index) {
			return "";
		} else {
			return filepath.substring(0, index);
		}
	}
	
	/**
	 * 파일을 삭제함.
	 * 
	 * @param filePath - 파일위치 정보
	 */
	public static boolean Hdelete(String filePath) {
		File f = null;
		try {
			f = new File(filePath);
			if (f.isFile() == false) {
				return false;
			}
			f.delete();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 디렉토리를 생성한다.
	 */
	public static void createDirectory(final File directory) throws IOException {
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}
	
	/**
	 * 디렉토리와 하위디렉토리 그리고 모든 파일을 지운다.
	 */
	public static void deleteDirectory(final String directory) throws IOException {
		deleteDirectory(new File(directory));
	}
	
	/**
	 * 디렉토리와 하위디렉토리 그리고 모든 파일을 지운다.
	 */
	public static void deleteDirectory(final File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}
		
		cleanDirectory(directory);
		
		if (false == directory.delete()) {
			throw new IOException("Directory " + directory + " unable to be deleted.");
		}
	}
	
	/**
	 * 자체 디렉토리를 제외한 모든 것을 삭제한다.
	 */
	public static void cleanDirectory(final String directory) throws IOException {
		cleanDirectory(new File(directory));
	}
	
	/**
	 * Clean a directory without deleting it.
	 */
	public static void cleanDirectory(final File directory) throws IOException {
		if (!directory.exists()) {
			throw new IllegalArgumentException(directory + " does not exist");
		}
		
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory + " is not a directory");
		}
		
		final File[] files = directory.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			final File file = files[i];
			FileCtlUtil.forceDelete(file);
		}
	}
	
	/**
	 * 파일이라면 그 파일을 지우고, 디렉토리라면 그 디렉토리를 지운다
	 */
	public static void forceDelete(final String file) throws IOException {
		forceDelete(new File(file));
	}
	
	/**
	 * 파일이라면 그 파일을 지우고, 디렉토리라면 그 디렉토리를 지운다.
	 */
	public static void forceDelete(final File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (false == file.delete()) {
				throw new IOException("File " + file + " unable to be deleted.");
			}
		}
	}
	
	/**
	 * 텍스트 파일을 생성한다.
	 * 
	 * @param file
	 *            파일이 저장될 경로. 시스템의 절대 경로를 넣어준다. (ex. "/home/hsboy/text.txt")
	 * @param contents
	 *            저장될 파일의 내용
	 */
	public static void writeFile(String file, String contents) throws IOException {
		FileWriter filew = null;
		try {
			filew = new FileWriter(new File(file));
			filew.write(contents, 0, contents.length());
		} catch (IOException e) {
		} finally {
			filew.close();
		}
	}
	
	/**
	 * 해당 디렉토리 내 파일 존재유무
	 * @param path
	 * @return
	 */
	public static boolean isExistFileInDirectory(String path){
		File f1 = new File(path);
		if(f1.isDirectory()){
			// 폴더의 하위 요소들을 File[]로 받기
			File[] ar = f1.listFiles();
			if (ar.length > 0) {
				return true;
			}
		} 
		return false;
	}

}
