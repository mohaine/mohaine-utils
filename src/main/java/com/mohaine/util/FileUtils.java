package com.mohaine.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class FileUtils {
	public static String getFileExtension(String fileName) {
		StringBuilder sb = new StringBuilder();
		if (fileName != null) {
			int dotPos = fileName.lastIndexOf('.');
			for (int i = dotPos + 1; i < fileName.length(); i++) {
				char charAt = fileName.charAt(i);

				charAt = Character.toLowerCase(charAt);

				if (charAt >= 'a' && charAt <= 'z') {
					sb.append(charAt);
				} else if (charAt >= '0' && charAt <= '9') {
					sb.append(charAt);
				}
			}
		}
		return sb.toString();
	}

	public static String getFileNamePreExtension(String fileName) {
		int dotPos;
		dotPos = fileName.lastIndexOf('.');
		return fileName.substring(0, dotPos);
	}

	public static String readFromFile(File file) throws IOException {
		return new String(readFileAsByteArray(file));
	}

	public static String readFromGzipFile(File file) throws IOException {
		return new String(readGzipFileAsByteArray(file));
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {

		FileInputStream fis = new FileInputStream(srcFile);
		BufferedInputStream bis = new BufferedInputStream(fis);

		try {
			try {
				FileOutputStream fos = new FileOutputStream(destFile);
				try {
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					try {
						StreamUtils.writeStream(bis, bos);
					} finally {
						bos.close();
					}

				} finally {
					fos.close();
				}
			} finally {
				bis.close();
			}
		} finally {
			fis.close();
		}

	}

	public static byte[] readFileAsByteArray(File file, int length) throws FileNotFoundException, IOException {
		byte[] readStream = new byte[length];
		FileInputStream fis = new FileInputStream(file);
		try {
			fis.read(readStream);
		} finally {
			fis.close();
		}
		return readStream;
	}

	public static byte[] readFileAsByteArray(File file) throws FileNotFoundException, IOException {
		byte[] readStream;
		FileInputStream fis = new FileInputStream(file);
		try {
			readStream = StreamUtils.readStream(fis);
		} finally {
			fis.close();
		}
		return readStream;
	}

	public static byte[] readGzipFileAsByteArray(File file) throws FileNotFoundException, IOException {
		byte[] readStream;
		InputStream fis = new FileInputStream(file);
		InputStream gis = new GZIPInputStream(fis);
		try {
			readStream = StreamUtils.readStream(gis);
		} finally {
			StreamUtils.close(gis);
			StreamUtils.close(fis);
		}
		return readStream;
	}

	public static void writeToFile(InputStream is, File destFile) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(is);

		try {
			FileOutputStream fos = new FileOutputStream(destFile);
			try {
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				try {
					StreamUtils.writeStream(bis, bos);
				} finally {
					bos.close();
				}

			} finally {
				fos.close();
			}
		} finally {
			bis.close();
		}

	}

	public static void appendToFile(File file, String value) throws IOException {
		FileOutputStream fos = new FileOutputStream(file.getAbsolutePath(), true);
		try {
			fos.write(value.getBytes());
		} finally {
			fos.close();
		}
	}

	public static void writeToFile(byte[] bs, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			fos.write(bs);
		} finally {
			fos.close();
		}
	}

	public static void writeToFile(String value, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			fos.write(value.getBytes());
		} finally {
			fos.close();
		}
	}

	public static boolean deleteFile(File file) throws IOException {
		if (file.isDirectory()) {
			File[] listing = file.listFiles();
			for (int i = 0; i < listing.length; i++) {
				File subFile = listing[i];
				deleteFile(subFile);
			}
		}
		return file.delete();
	}

	public static boolean isPdf(File file) throws IOException {

		byte[] data = readFileAsByteArray(file, 5);
		if (data.length == 5) {
			return data[0] == 0x25 && data[1] == 0x50 && data[2] == 0x44 && data[3] == 0x46 && data[4] == 0x2d;
		}
		return false;
	}

	public static String getHumanFileLength(long length) {
		StringBuffer sb = new StringBuffer();
		if (length < 0) {
			return "-" + getHumanFileLength(-length);
		}

		if (length < 512) {
			sb.append(length);
		} else if (length < 1024 * 512) {
			double value = length / 1024.0;
			addValue(sb, value);
			sb.append('K');
		} else if (length < 1048576.0 * 512.0) {
			double value = length / 1048576.0;
			addValue(sb, value);
			sb.append('M');
		} else {
			double value = length / (1048576.0 * 1024.0);
			addValue(sb, value);
			sb.append('G');
		}
		return sb.toString();
	}

	private static void addValue(StringBuffer sb, double value) {
		value = Math.round(value * 10.0) / 10.0;
		if (value < 10) {
			sb.append(value);
		} else {
			int intVal = (int) Math.round(value);
			sb.append(intVal);
		}
	}
}
