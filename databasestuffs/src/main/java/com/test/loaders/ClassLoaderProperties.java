package com.test.loaders;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

// This is a comment to push from Intellij
public class ClassLoaderProperties extends PropertiesManager {

	public ClassLoaderProperties(String fileName) {
		super(fileName);
	}

	protected InputStream getInputStream(String fileName) throws FileNotFoundException {
		return getClass().getClassLoader().getResourceAsStream(fileName);
	}

	protected OutputStream getOutputStream(String fileName) throws FileNotFoundException {
		return new FileOutputStream(fileName);
	}

}
