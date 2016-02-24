package org.pedrero.fbwatcher.config;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Job implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6180752165457843151L;

	private String id;
	private String pageId;
	private String eventFilter;
	private boolean shouldMail;
	private boolean shouldSms;
	private boolean shouldAttend;
	private Profile subscriber;

	public Job(String jobId) {
		super();
		this.id = jobId;
	}

	public Job() {
		super();
	}

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	@XmlElement
	public boolean isShouldMail() {
		return shouldMail;
	}

	public void setShouldMail(boolean shouldMail) {
		this.shouldMail = shouldMail;
	}

	@XmlElement
	public boolean isShouldSms() {
		return shouldSms;
	}

	public void setShouldSms(boolean shouldSms) {
		this.shouldSms = shouldSms;
	}

	@XmlElement
	public boolean isShouldAttend() {
		return shouldAttend;
	}

	public void setShouldAttend(boolean shouldAttend) {
		this.shouldAttend = shouldAttend;
	}

	@XmlIDREF
	@XmlElement
	public Profile getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Profile subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pageId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return Objects.equals(id, ((Job) obj).getId());
	}

}
