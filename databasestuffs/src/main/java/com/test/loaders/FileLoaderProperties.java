package com.test.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileLoaderProperties extends PropertiesManager {

	private static final String FOLDER_ROOT = "src" + File.separator + "main";

	private static String RESOURCES_FOLDER = null;
	static {
		RESOURCES_FOLDER = FOLDER_ROOT + File.separator + "resources";
	}

	private static String SRC_FILE = null;
	static {
		SRC_FILE = FOLDER_ROOT + File.separator + "java";
	}

	private static final String[] FOLDERS_TO_SEARCH = { RESOURCES_FOLDER, SRC_FILE };

	public FileLoaderProperties(String fileName) {
		super(fileName);
	}

	protected InputStream getInputStream(final String fileName) throws FileNotFoundException {
		 String resourcePath = getCommandFileInputStream(fileName);
		 return new FileInputStream(resourcePath);
	}

	private String getCommandFileInputStream(String fileName) throws FileNotFoundException {
		String resourcePath = null;

		for (String folder : FOLDERS_TO_SEARCH) {
			resourcePath = findFullPath(folder, fileName);
			if (new File(resourcePath).exists()) {
				break;
			}
		}

		return resourcePath;
	}

	private String findFullPath(String folder, String fileName) {
		StringBuilder fullFilePath = new StringBuilder();
		fullFilePath.append(System.getProperty("user.dir"));
		fullFilePath.append(File.separator);
		fullFilePath.append(folder);
		fullFilePath.append(File.separator);
		fullFilePath.append(fileName);
		return fullFilePath.toString();
	}

	protected OutputStream getOutputStream(final String fileName) throws FileNotFoundException {
		return new FileOutputStream(fileName);
	}

}
