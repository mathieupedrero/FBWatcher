package org.pedrero.fbwatcher.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RootConfiguration {
	private final List<Job> jobs = new ArrayList<>();

	private final List<Profile> profiles = new ArrayList<>();

	@XmlElement
	public List<Job> getJobs() {
		return jobs;
	}

	@XmlElement
	public List<Profile> getProfiles() {
		return profiles;
	}

}
