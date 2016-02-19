package org.pedrero.fbwatcher;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.pedrero.fbwatcher.facebook.FacebookContext;
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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TimerExecutor.class);

	@Autowired
	private FacebookContext facebookKeeper;

	@Autowired
	private RecipientsNotifier notifier;

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

	private final Set<String> allreadyNotifiedEvents = new HashSet<>();

	private String loggedUser;
	private String pageWatched;

	@Scheduled(cron = "*/5 * * * * ?")
	public void demoServiceMethod() {
		if (!facebookKeeper.getFacebookByToken().isEmpty()) {
			Facebook facebook = facebookKeeper.getFacebookByToken().entrySet()
					.iterator().next().getValue();
			if (facebook.isAuthorized()) {
				boolean isFirstLoop = false;
				if (loggedUser == null) {
					loggedUser = facebook.userOperations().getUserProfile()
							.getName();
					isFirstLoop = true;
				}
				if (pageWatched == null) {
					pageWatched = facebook.pageOperations()
							.getPage(pageWatchedId).getName();
					notifyBeginning(pageWatched);
				}

				List<Event> newFutureEvents = facebook
						.fetchConnections(pageWatchedId, "events", Event.class)
						.stream()
						.filter(e -> e.getStartTime().after(
								new java.util.Date()))
						.filter(e -> !allreadyNotifiedEvents.contains(e.getId()))
						.collect(Collectors.toList());

				newFutureEvents.forEach(e -> allreadyNotifiedEvents.add(e
						.getId()));

				List<Event> newFutureFilteredEvents = newFutureEvents.stream()
						.filter(e -> eventNameMatchesFilter(e))
						.collect(Collectors.toList());

				for (Event event : newFutureEvents) {
					if (newFutureFilteredEvents.contains(event)) {
						facebook.eventOperations().acceptInvitation(
								event.getId());
						if (!isFirstLoop) {
							notifyFilteredEvent(pageWatched, event.getName(),
									event.getStartTime(), loggedUser);
						}
					} else {
						if (!isFirstLoop) {
							notifyNewEvent(pageWatched, event.getName(),
									event.getStartTime());
						}
					}
				}
			}
		}
	}

	private boolean eventNameMatchesFilter(Event e) {
		return e.getName().toLowerCase().contains(eventFilter.toLowerCase());
	}

	private void notifyBeginning(String pageName) {
		String title = MessageFormat.format(beginningTitle, pageName);
		String message = MessageFormat.format(beginningMessage, pageName);
		notify(title, message);
	}

	private void notifyNewEvent(String pageName, String eventName,
			Date eventDate) {
		String title = MessageFormat.format(newEventTitle, eventName);
		String message = MessageFormat.format(newEventMessage, pageName,
				eventName, eventDate);
		notify(title, message);
	}

	private void notifyFilteredEvent(String pageName, String eventName,
			Date eventDate, String user) {
		String title = MessageFormat.format(newFilteredTitle, eventFilter,
				eventName);
		String message = MessageFormat.format(newFilteredMessage, pageName,
				eventFilter, eventName, eventDate, user);
		notify(title, message);
	}

	protected void notifyEnd(String pageName) {
		String title = MessageFormat.format(endTitle, pageName);
		String message = MessageFormat.format(endMessage, pageName);
		notify(title, message);
	}

	private void notify(String title, String message) {
		LOGGER.info(message);
		notifier.notify(title, message);
	}
}
