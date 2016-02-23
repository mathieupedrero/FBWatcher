package org.pedrero.fbwatcher.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RootConfiguration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2793110342968877462L;

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
