package com.yuxuejian.generator.util;

import java.io.File;

public class FileUtil {
	
	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					// noinspection ResultOfMethodCallIgnored
					file.delete();
				}
			}
		}
		return (directory.delete());
	}
	
	public static File createEmptyDir(String path) {
		File tempDir = new File(path);
		if (tempDir.exists() && !tempDir.isDirectory()) {
			throw new RuntimeException("file:" + path + " already exists.");
		}
		if (tempDir.exists()) {
			if (!deleteDirectory(tempDir)) {
				throw new RuntimeException("can not delete old dir:" + path);
			}
		}
		if (!tempDir.mkdirs()) {
			throw new RuntimeException("can not create dir:" + path);
		}
		return tempDir;
	}
	
	public static void main(String[] args) {
		String a = "cn.inovance.upms.rpc.dao";
		System.out.println(a.substring(0,a.lastIndexOf(".")));
		System.out.println(a.replace(".", "/"));
	}
}
