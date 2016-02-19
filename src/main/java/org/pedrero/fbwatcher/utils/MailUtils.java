package org.pedrero.fbwatcher.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public final class MailUtils {
	public final static MailUtils INSTANCE = new MailUtils();
	private final static String MAILER_VERSION = "Java";

	private MailUtils() {
		super();
	}

	public static MailUtils getInstance() {
		return INSTANCE;
	}

	public boolean sendMail(String serveur, String from, String to, String subject, String content) {
		boolean result = false;
		try {
			Properties prop = System.getProperties();
			prop.put("mail.smtp.host", serveur);
			prop.put("mail.from", from);
			prop.put("mail.smtp.port", "25");

			Session session = Session.getInstance(prop);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			InternetAddress[] internetAddresses = new InternetAddress[1];
			internetAddresses[0] = new InternetAddress(to);
			message.setRecipients(Message.RecipientType.TO, internetAddresses);
			message.setSubject(subject);
			message.setText(content);
			message.setHeader("X-Mailer", MAILER_VERSION);
			message.setSentDate(new Date());
			session.setDebug(false);
			Transport.send(message);
			result = true;
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return result;
	}

}
