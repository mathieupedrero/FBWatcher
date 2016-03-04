package org.pedrero.fbwatcher.config;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.pedrero.fbwatcher.utils.XmlFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
public class FBWatcherConfiguration {

	private RootConfiguration configuration;

	@Value("${config.file_path_from_home}")
	private String homeConfigFilePath;

	private String fullConfigFilePath;

	@PostConstruct
	private void postConstruct() {
		String homePath = System.getProperty("user.home");
		fullConfigFilePath = MessageFormat.format("{0}/{1}", homePath, homeConfigFilePath);
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

	public synchronized void addTokenToProfile(String token, LocalDateTime expiracy, String profileId) {
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

	public synchronized void addJob(String id, String eventFilter, boolean shouldMail, boolean shouldSms,
			boolean shouldAttend, String profileId) {
		Job job = new Job(id);
		job.setPageId(id);
		job.setEventFilter(eventFilter);

		job.setSubscriber(configuration.getProfiles().stream().filter(p -> Objects.equals(p.getId(), profileId))
				.findFirst().orElse(null));

		configuration.getJobs().add(job);
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

	@SuppressWarnings("unchecked")
	public synchronized Set<Profile> retrieveProfiles() {
		return (Set<Profile>) SerializationUtils.deserialize(SerializationUtils.serialize(configuration.getProfiles()));
	}

	@SuppressWarnings("unchecked")
	public synchronized Set<Job> retrieveJobs() {
		return (Set<Job>) SerializationUtils.deserialize(SerializationUtils.serialize(configuration.getJobs()));
	}

	public synchronized void defineShouldMail(String jobId, boolean shouldMail) {
		configuration.getJobs().stream().filter(j -> Objects.equals(j.getId(), jobId)).findFirst()
				.ifPresent(j -> j.setShouldMail(shouldMail));
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

	public synchronized void defineShouldAttend(String jobId, boolean shouldAttend) {
		configuration.getJobs().stream().filter(j -> Objects.equals(j.getId(), jobId)).findFirst()
				.ifPresent(j -> j.setShouldAttend(shouldAttend));
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

	public synchronized void defineShouldSms(String jobId, boolean shouldSms) {
		configuration.getJobs().stream().filter(j -> Objects.equals(j.getId(), jobId)).findFirst()
				.ifPresent(j -> j.setShouldSms(shouldSms));
		XmlFileUtils.writeToFile(configuration, fullConfigFilePath);
	}

}
