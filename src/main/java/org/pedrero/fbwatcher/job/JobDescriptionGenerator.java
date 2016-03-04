package org.pedrero.fbwatcher.job;

import java.util.function.Function;

import org.pedrero.fbwatcher.config.Job;
import org.pedrero.fbwatcher.config.Profile;
import org.pedrero.fbwatcher.facebook.FacebookUtils;
import org.springframework.social.facebook.api.Facebook;

public class JobDescriptionGenerator {
	public static Function<Job, JobDescription> getForFacebook(Facebook applicationFacebook) {
		return job -> convert(job, applicationFacebook);
	}

	private JobDescriptionGenerator() {
	}

	public static JobDescription convert(Job job, Facebook applicationFacebook) {
		if (job == null) {
			return null;
		}
		JobDescription description = new JobDescription();
		description.setJobId(job.getId());
		description.setShouldAttend(job.isShouldAttend());
		description.setShouldSms(job.isShouldSms());
		description.setShouldMail(job.isShouldMail());
		description.setPageName(applicationFacebook.pageOperations().getPage(job.getPageId()).getName());
		description.setEventFilter(job.getEventFilter());
		Profile subscriber = job.getSubscriber();
		if (subscriber != null) {
			description.setSubscriberId(subscriber.getId());
			if (subscriber.getToken() != null) {
				description.setSubscriberName(FacebookUtils.buildFor(subscriber.getToken().getToken()).userOperations()
						.getUserProfile().getName());
				description.setTokenExpiracy(subscriber.getToken().getExpiration());
			}
		}
		return description;
	}
}
