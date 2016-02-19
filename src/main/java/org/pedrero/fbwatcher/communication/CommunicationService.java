package org.pedrero.fbwatcher.communication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommunicationService {
	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private JavaMailSender mailSender;

	private static String urlEncode(Object toEncode) {
		try {
			return URLEncoder.encode(toEncode.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("error while encoding url", e);
		}
	}

	public static String computeUrl(String urlPattern, Object... params) {
		List<String> escapedParams = Arrays.asList(params).stream()
				.map(CommunicationService::urlEncode)
				.collect(Collectors.toList());
		return MessageFormat.format(urlPattern, escapedParams.toArray());
	}

	public <T> T getForObject(String uriPattern, Class<T> clazz,
			Object... params) {
		return restTemplate.getForObject(uriPattern, clazz, params);
	}

	public void simpleGet(String uriPattern, Object... params) {
		restTemplate.execute(uriPattern, HttpMethod.GET, null, null, params);
	}

	public void sendMail(String from, String to, String subject, String message) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(to);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);
		mailSender.send(mailMessage);
	}

}
