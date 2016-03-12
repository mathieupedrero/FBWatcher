package org.pedrero.fbwatcher.job;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pedrero.fbwatcher.communication.RecipientsNotifier;
import org.pedrero.fbwatcher.config.FBWatcherConfiguration;
import org.pedrero.fbwatcher.config.Job;
import org.pedrero.fbwatcher.config.Profile;
import org.pedrero.fbwatcher.config.Token;
import org.pedrero.fbwatcher.facebook.FacebookUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.facebook.api.Event;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;

@Controller
public class JobExecutor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JobExecutor.class);

	@Autowired
	private RecipientsNotifier notifier;

	@Autowired
	private FacebookUtils facebookUtils;

	@Autowired
	private FBWatcherConfiguration configuration;

	private final Map<Job, RuntimeJobData> runtimeData = new HashMap<>();

	@Value("${facebook.events_to_attend_filter}")
	private String eventFilter;

	@Value("${facebook.page_to_watch}")
	private String pageWatchedId;

	@Value("${messages.beginning.title}")
	private String beginningTitle;

	@Value("${messages.beginning.message}")
	private String beginningMessage;

	@Value("${messages.new_event.title}")
	private String newEventTitle;

	@Value("${messages.new_event.message}")
	private String newEventMessage;

	@Value("${messages.new_event_filtered.title}")
	private String newFilteredTitle;

	@Value("${messages.new_event_filtered.message}")
	private String newFilteredMessage;

	@Value("${messages.end.title}")
	private String endTitle;

	@Value("${messages.end.message}")
	private String endMessage;

	@Scheduled(cron = "*/5 * * * * ?")
	public synchronized void demoServiceMethod() {
		for (Job job : configuration.retrieveJobs()) {
			final RuntimeJobData runtime = runtimeData.getOrDefault(job,
					new RuntimeJobData());
			if (!runtimeData.containsValue(job)) {
				feedRuntimeForJob(runtime, job);
				runtimeData.put(job, runtime);
			}
			if (runtime.isFirstWatch()) {
				notifyBeginning(runtime.getPageName(), job);
			}
			if (runtime.getUserFacebook() != null) {
				Facebook userFacebook = runtime.getUserFacebook();
				if (userFacebook.isAuthorized()) {

					List<Event> newFutureEvents = userFacebook
							.fetchConnections(pageWatchedId, "events",
									Event.class)
							.stream()
							.filter(e -> e.getStartTime().after(
									new java.util.Date()))
							.filter(e -> !runtime.getAllreadyNotifiedEvents()
									.contains(e.getId()))
							.collect(Collectors.toList());

					newFutureEvents.forEach(e -> runtime
							.getAllreadyNotifiedEvents().add(e.getId()));

					List<Event> newFutureFilteredEvents = newFutureEvents
							.stream().filter(e -> eventNameMatchesFilter(e))
							.collect(Collectors.toList());

					if (!runtime.isFirstWatch()) {
						for (Event event : newFutureEvents) {
							if (newFutureFilteredEvents.contains(event)) {
								if (job.isShouldAttend()) {
									userFacebook.eventOperations()
											.acceptInvitation(event.getId());
								}
								notifyFilteredEvent(runtime.getPageName(),
										event.getName(), event.getStartTime(),
										runtime.getUserName(), job);
							} else {
								notifyNewEvent(runtime.getPageName(),
										event.getName(), event.getStartTime(),
										job);
							}
						}
					}

				}
			}
			runtime.setFirstWatch(false);
		}
	}

	private RuntimeJobData feedRuntimeForJob(RuntimeJobData runtime, Job job) {
		Profile subscriber = job.getSubscriber();
		if (subscriber != null) {
			Token token = subscriber.getToken();
			if (token != null) {
				Facebook userFacebook = FacebookUtils
						.buildFor(token.getToken());
				runtime.setUserFacebook(userFacebook);
				runtime.setPageName(userFacebook.pageOperations()
						.getPage(job.getPageId()).getName());
				runtime.setUserName(userFacebook.userOperations()
						.getUserProfile().getName());
			} else {
				LOGGER.warn("no valid token associated to user [{}]",
						subscriber.getId());
			}
			return runtime;
		} else {
			String msg = "subscriber shouldn't be null";
			LOGGER.error(msg);
			throw new RuntimeException(msg);
		}
	}

	private boolean eventNameMatchesFilter(Event e) {
		return e.getName().toLowerCase().contains(eventFilter.toLowerCase());
	}

	private void notifyBeginning(String pageName, Job job) {
		String title = MessageFormat.format(beginningTitle, pageName);
		String message = MessageFormat.format(beginningMessage, pageName);
		notify(title, message, job);
	}

	private void notifyNewEvent(String pageName, String eventName,
			Date eventDate, Job job) {
		String title = MessageFormat.format(newEventTitle, eventName);
		String message = MessageFormat.format(newEventMessage, pageName,
				eventName, eventDate);
		notify(title, message, job);
	}

	private void notifyFilteredEvent(String pageName, String eventName,
			Date eventDate, String user, Job job) {
		String title = MessageFormat.format(newFilteredTitle, eventFilter,
				eventName);
		String message = MessageFormat.format(newFilteredMessage, pageName,
				eventFilter, eventName, eventDate, user);
		notify(title, message, job);
	}

	protected void notifyEnd(String pageName, Job job) {
		String title = MessageFormat.format(endTitle, pageName);
		String message = MessageFormat.format(endMessage, pageName);
		notify(title, message, job);
	}

	private void notify(String title, String message, Job job) {
		LOGGER.info(message);
		Profile subscriber = job.getSubscriber();
		if (job.isShouldMail()) {
			notifier.notifyMail(title, message, subscriber.getMailAddresses());
		}
		if (job.isShouldSms()) {
			notifier.notifyRest(message, subscriber.getFreeRestAddresses());
		}
	}

	public synchronized List<JobDescription> gecDescriptionsOfCurrentJobs() {
		return configuration
				.retrieveJobs()
				.stream()
				.map(JobDescriptionGenerator.getForFacebook(facebookUtils
						.getApplicationFacebook()))
				.collect(Collectors.toList());
	}
}
