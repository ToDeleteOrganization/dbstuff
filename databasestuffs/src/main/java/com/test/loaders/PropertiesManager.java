package com.test.loaders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class PropertiesManager {

	private final String fileName;

	public PropertiesManager(String fileName) {
		this.fileName = fileName;
	}

	public Properties loadProperties() {
		InputStream stream = null;
		Properties properties = null;

		try {
			stream = getInputStream(fileName);
			if (stream != null) {
				properties = new Properties();
				properties.load(stream);
			}
		} catch (Exception e) {
			// TODO: add logger here
			System.out.println(e);
		} finally {
			closeInputStream(stream);
		}

		return properties;
	}

	public Properties writePropertyToFile(final String fileName, final Map<String, String> props) {
		Properties properties = null;
		try {
			OutputStream output = getOutputStream(fileName);

			properties = new Properties();
			properties.putAll(props);
			properties.store(output, "");
		} catch (FileNotFoundException e) {
			// TODO: add logger here
			System.out.println(e);
		} catch (IOException e) {
			// TODO: add logger here
			System.out.println(e);
		}
		return properties;
	}

	public Properties writePropertyToFile(final String fileName, final String key, final String value) {
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(key, value);
		return writePropertyToFile(fileName, properties);
	}

	public String getPropertiesFile() {
		return fileName;
	}

	protected void closeInputStream(final InputStream inputStream) {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO: add logger here
				System.out.println(e);
			}
		}
	}

	protected abstract InputStream getInputStream(String fileName) throws FileNotFoundException;

	protected abstract OutputStream getOutputStream(String fileName) throws FileNotFoundException;

}
