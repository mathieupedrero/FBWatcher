package org.pedrero.fbwatcher.job;


public class JobDescription {

	private String jobId;
	private String pageName;
	private String eventFilter;
	private String subscriberId;
	private String subscriberName;
	private boolean shouldMail;
	private boolean shouldSms;
	private boolean shouldAttend;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getEventFilter() {
		return eventFilter;
	}

	public void setEventFilter(String eventFilter) {
		this.eventFilter = eventFilter;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	public boolean isShouldMail() {
		return shouldMail;
	}

	public void setShouldMail(boolean shouldMail) {
		this.shouldMail = shouldMail;
	}

	public boolean isShouldSms() {
		return shouldSms;
	}

	public void setShouldSms(boolean shouldSms) {
		this.shouldSms = shouldSms;
	}

	public boolean isShouldAttend() {
		return shouldAttend;
	}

	public void setShouldAttend(boolean shouldAttend) {
		this.shouldAttend = shouldAttend;
	}

}
