package org.pedrero.fbwatcher.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Job {
	private String pageId;
	private String eventFilter;
	private Profile subscriber;

	@XmlElement
	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	@XmlElement
	public String getEventFilter() {
		return eventFilter;
	}

	public void setEventFilter(String eventFilter) {
		this.eventFilter = eventFilter;
	}

	@XmlIDREF
	@XmlElement
	public Profile getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Profile subscriber) {
		this.subscriber = subscriber;
	}

}
