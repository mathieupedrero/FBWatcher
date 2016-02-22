package org.pedrero.fbwatcher.config;

import groovy.lang.Singleton;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.pedrero.fbwatcher.utils.XmlFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.SerializationUtils;

@Singleton
public class FBWatcherConfiguration {

	private RootConfiguration configuration;

	@Value("${config.file_path_from_home}")
	private String homeConfigFilePath;

	private String fullConfigFilePath;

	@PostConstruct
	private void postConstruct() {
		String homePath = System.getProperty("user.home");
		fullConfigFilePath = MessageFormat.format("{0}/{1}", homePath, fullConfigFilePath);
		configuration = XmlFileUtils.readFromFile(fullConfigFilePath, RootConfiguration.class).orElse(
				new RootConfiguration());
	}

	public synchronized void addProfile(String id, List<String> mailAddresses, List<String> freeRestAddresses) {
		Profile profile = new Profile(id);
		profile.getMailAddresses().addAll(mailAddresses);
		profile.getFreeRestAddresses().addAll(freeRestAddresses);

		configuration.getProfiles().add(profile);
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

	public synchronized void addTokenToProfile(String token, Date expiracy, String profileId) {
		Token tokenObject = new Token(token);
		tokenObject.setExpiracy(expiracy);

		configuration.getProfiles().stream().filter(p -> Objects.equals(p.getId(), profileId)).findFirst()
				.ifPresent(p -> p.setToken(tokenObject));
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

	public synchronized void revokeToken(String tokenToRevoke) {
		configuration.getProfiles().stream()
				.filter(p -> p.getToken() != null && Objects.equals(p.getToken().getToken(), tokenToRevoke))
				.forEach(p -> p.setToken(null));
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

	public synchronized void addJob(String pageId, String eventFilter, String profileId) {
		Job job = new Job(pageId);
		job.setPageId(pageId);
		job.setEventFilter(eventFilter);

		job.setSubscriber(configuration.getProfiles().stream().filter(p -> Objects.equals(p.getId(), profileId))
				.findFirst().orElse(null));

		configuration.getJobs().add(job);
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

	@SuppressWarnings("unchecked")
	public synchronized List<Profile> retrieveProfiles() {
		return (List<Profile>) SerializationUtils
				.deserialize(SerializationUtils.serialize(configuration.getProfiles()));
	}

	@SuppressWarnings("unchecked")
	public synchronized List<Job> retrieveJobs() {
		return (List<Job>) SerializationUtils.deserialize(SerializationUtils.serialize(configuration.getJobs()));
	}

}
