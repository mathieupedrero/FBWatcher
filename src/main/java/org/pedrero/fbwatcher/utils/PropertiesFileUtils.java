package org.pedrero.fbwatcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFileUtils {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(PropertiesFileUtils.class);

	private PropertiesFileUtils() {
		super();
	}

	public static void writePropertiesToFile(Properties properties,
			String filePath) {
		File existingFile = new File(filePath);
		if (existingFile.exists()) {
			existingFile.delete();
		}
		try (OutputStream output = new FileOutputStream(filePath)) {
			properties.store(output, null);
		} catch (IOException io) {
			LOGGER.error("Error while saving properties file {}", filePath, io);
		}
	}

	public static Optional<Properties> readPropertiesFromFile(String filePath) {
		File existingFile = new File(filePath);
		if (!existingFile.exists()) {
			return Optional.empty();
		}
		try (InputStream input = new FileInputStream(filePath)) {
			Properties properties = new Properties();
			properties.load(input);
			return Optional.of(properties);
		} catch (IOException io) {
			LOGGER.error("Error while saving properties file {}", filePath, io);
		}
		return Optional.empty();
	}

}
