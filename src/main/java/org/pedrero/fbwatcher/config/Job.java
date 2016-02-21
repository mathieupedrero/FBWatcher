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

	private final String pageId;
	private String eventFilter;
	private Profile subscriber;

	public Job(String pageId) {
		super();
		this.pageId = pageId;
	}

	@XmlAttribute
	public String getPageId() {
		return pageId;
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
		return Objects.equals(pageId, ((Job) obj).getPageId());
	}

}
