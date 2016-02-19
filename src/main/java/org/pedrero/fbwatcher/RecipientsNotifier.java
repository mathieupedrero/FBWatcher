package org.pedrero.fbwatcher;

import java.util.List;

import org.pedrero.fbwatcher.communication.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecipientsNotifier {

	@Value("#{'${notification.clients.rest}'.split(',')}")
	private List<String> restClientsToNotify;

	@Value("#{'${notification.clients.mail}'.split(',')}")
	private List<String> mailAddressesToNotify;

	@Value("${notification.mail_sender}")
	private String from;

	@Autowired
	private CommunicationService communicationService;

	public void notify(String title, String message) {
		notifyMail(title, message);
		notifyRest(message);
	}

	private void notifyMail(String title, String message) {
		for (String mailRecipient : mailAddressesToNotify) {
			communicationService.sendMail(from, mailRecipient, title, message);
		}
	}

	private void notifyRest(String message) {
		for (String restUri : restClientsToNotify) {
			communicationService.simpleGet(restUri, message);
		}
	}

}
