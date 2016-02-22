package org.pedrero.fbwatcher.job;

import java.util.HashSet;
import java.util.Set;

import org.springframework.social.facebook.api.Facebook;

public class RuntimeJobData {
	private String pageName;

	private String userName;

	private Facebook userFacebook;

	private boolean firstWatch = true;

	private final Set<String> allreadyNotifiedEvents = new HashSet<>();

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Facebook getUserFacebook() {
		return userFacebook;
	}

	public void setUserFacebook(Facebook userFacebook) {
		this.userFacebook = userFacebook;
	}

	public boolean isFirstWatch() {
		return firstWatch;
	}

	public void setFirstWatch(boolean firstWatch) {
		this.firstWatch = firstWatch;
	}

	public Set<String> getAllreadyNotifiedEvents() {
		return allreadyNotifiedEvents;
	}

}
