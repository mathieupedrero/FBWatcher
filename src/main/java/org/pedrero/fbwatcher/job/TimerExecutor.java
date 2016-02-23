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
public class TimerExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimerExecutor.class);

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

	@Value("${facebook.should_attend}")
	private Boolean shouldAttend;

	@Value("${notification.should_mail}")
	private Boolean shouldMail;

	@Value("${notification.should_sms}")
	private Boolean shouldSms;

	@Scheduled(cron = "*/5 * * * * ?")
	public void demoServiceMethod() {
		for (Job job : configuration.retrieveJobs()) {
			Profile subscriber = job.getSubscriber();
			final RuntimeJobData runtime = runtimeData.getOrDefault(job, new RuntimeJobData());
			if (!runtimeData.containsValue(job)) {
				feedRuntimeForJob(runtime, job);
				runtimeData.put(job, runtime);
			}
			if (runtime.isFirstWatch()) {
				notifyBeginning(runtime.getPageName(), job.getSubscriber());
				runtime.setFirstWatch(false);
			}
			if (runtime.getUserFacebook() != null) {
				Facebook userFacebook = runtime.getUserFacebook();
				if (userFacebook.isAuthorized()) {

					List<Event> newFutureEvents = userFacebook.fetchConnections(pageWatchedId, "events", Event.class)
							.stream().filter(e -> e.getStartTime().after(new java.util.Date()))
							.filter(e -> !runtime.getAllreadyNotifiedEvents().contains(e.getId()))
							.collect(Collectors.toList());

					newFutureEvents.forEach(e -> runtime.getAllreadyNotifiedEvents().add(e.getId()));

					List<Event> newFutureFilteredEvents = newFutureEvents.stream()
							.filter(e -> eventNameMatchesFilter(e)).collect(Collectors.toList());

					for (Event event : newFutureEvents) {
						if (newFutureFilteredEvents.contains(event)) {
							if (shouldAttend) {
								userFacebook.eventOperations().acceptInvitation(event.getId());
							}
							notifyFilteredEvent(runtime.getPageName(), event.getName(), event.getStartTime(),
									runtime.getUserName(), subscriber);
						} else {
							notifyNewEvent(runtime.getPageName(), event.getName(), event.getStartTime(), subscriber);
						}
					}

				}
			}
		}
	}

	private RuntimeJobData feedRuntimeForJob(RuntimeJobData runtime, Job job) {
		Profile subscriber = job.getSubscriber();
		if (subscriber != null) {
			Token token = subscriber.getToken();
			if (token != null) {
				Facebook userFacebook = FacebookUtils.buildFor(token.getToken());
				runtime.setUserFacebook(userFacebook);
				runtime.setPageName(userFacebook.pageOperations().getPage(job.getPageId()).getName());
				runtime.setUserName(userFacebook.userOperations().getUserProfile().getName());
			} else {
				LOGGER.warn("no valid token associated to user [{}]", subscriber.getId());
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

	private void notifyBeginning(String pageName, Profile profile) {
		String title = MessageFormat.format(beginningTitle, pageName);
		String message = MessageFormat.format(beginningMessage, pageName);
		notify(title, message, profile);
	}

	private void notifyNewEvent(String pageName, String eventName, Date eventDate, Profile profile) {
		String title = MessageFormat.format(newEventTitle, eventName);
		String message = MessageFormat.format(newEventMessage, pageName, eventName, eventDate);
		notify(title, message, profile);
	}

	private void notifyFilteredEvent(String pageName, String eventName, Date eventDate, String user, Profile profile) {
		String title = MessageFormat.format(newFilteredTitle, eventFilter, eventName);
		String message = MessageFormat.format(newFilteredMessage, pageName, eventFilter, eventName, eventDate, user);
		notify(title, message, profile);
	}

	protected void notifyEnd(String pageName, Profile profile) {
		String title = MessageFormat.format(endTitle, pageName);
		String message = MessageFormat.format(endMessage, pageName);
		notify(title, message, profile);
	}

	private void notify(String title, String message, Profile profile) {
		LOGGER.info(message);
		if (shouldMail) {
			notifier.notifyMail(title, message, profile.getMailAddresses());
		}
		if (shouldSms) {
			notifier.notifyRest(message, profile.getFreeRestAddresses());
		}
	}
}
