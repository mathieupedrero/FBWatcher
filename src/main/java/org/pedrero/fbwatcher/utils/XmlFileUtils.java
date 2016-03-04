package org.pedrero.fbwatcher.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlFileUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(XmlFileUtils.class);

	private XmlFileUtils() {
		super();
	}

	public static <T> void writeToFile(T properties, String filePath) {
		File existingFile = new File(filePath);
		if (existingFile.exists()) {
			existingFile.delete();
		}
		try {
			existingFile.getParentFile().mkdirs();
			existingFile.createNewFile();
			JAXBContext context = JAXBContext.newInstance(properties.getClass()); // 1
			Marshaller marshaller = context.createMarshaller();
			marshaller.setAdapter(LocalDateTimeAdapter.class, new LocalDateTimeAdapter());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(properties, existingFile);
		} catch (JAXBException e) {
			LOGGER.error("Jaxb error", e);
		} catch (IOException e) {
			LOGGER.error("FS error", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> readFromFile(String filePath, Class<T> configClass) {
		LOGGER.info("Reading application configuration from [{}]", filePath);
		File existingFile = new File(filePath);
		if (!existingFile.exists()) {
			return Optional.empty();
		}
		T configuration;
		try {
			JAXBContext context = JAXBContext.newInstance(configClass);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setAdapter(LocalDateTimeAdapter.class, new LocalDateTimeAdapter());
			configuration = (T) unmarshaller.unmarshal(new FileReader(existingFile));
		} catch (FileNotFoundException e) {
			LOGGER.error("File not found error", e);
			return Optional.empty();
		} catch (JAXBException e) {
			LOGGER.error("Jaxb error", e);
			return Optional.empty();
		}
		return Optional.of(configuration);
	}

}
