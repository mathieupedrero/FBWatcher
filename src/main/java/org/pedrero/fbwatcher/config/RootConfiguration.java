package org.pedrero.fbwatcher.config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RootConfiguration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2793110342968877462L;

	private final Set<Job> jobs = new HashSet<>();

	private final Set<Profile> profiles = new HashSet<>();

	@XmlElement
	public Set<Job> getJobs() {
		return jobs;
	}

	@XmlElement
	public Set<Profile> getProfiles() {
		return profiles;
	}

}
