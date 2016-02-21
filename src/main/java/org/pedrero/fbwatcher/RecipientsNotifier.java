package org.pedrero.fbwatcher;

import java.util.List;

import org.pedrero.fbwatcher.communication.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecipientsNotifier {

	@Value("${notification.mail_sender}")
	private String from;

	@Autowired
	private CommunicationService communicationService;

	public void notifyMail(String title, String message,
			List<String> mailAddressesToNotify) {
		for (String mailRecipient : mailAddressesToNotify) {
			communicationService.sendMail(from, mailRecipient, title, message);
		}
	}

	public void notifyRest(String message, List<String> restClientsToNotify) {
		for (String restUri : restClientsToNotify) {
			communicationService.simpleGet(restUri, message);
		}
	}

}
