package de.huebner.easynotes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class FileUtils {

	public String getFileContent(String filename) {
		File file = getFileFromFilename(filename);
		if (!checkFileExists(file)) {
			throw new RuntimeException("Required file " + filename
					+ " does not exist.");
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);

			byte[] reader = new byte[fis.available()];
			while (fis.read(reader) != -1) {
				// do nothing
			}

			return new String(reader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// swallow
				}
			}
		}
	}

	public boolean checkFileExists(File file) {
		return file.exists();
	}
	
	private File getFileFromFilename(String filename) {
		URL url = getClass().getResource(filename);
		String path;
		try {
			path = URLDecoder.decode(url.getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		File file = new File(path);
		
		return file;
	}
}
