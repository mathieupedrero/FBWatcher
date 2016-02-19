package org.pedrero.fbwatcher.facebook;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.pedrero.fbwatcher.config.FBWatcherConfiguration;
import org.pedrero.fbwatcher.config.Profile;
import org.pedrero.fbwatcher.facebook.FacebookUtils.FacebookAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;

@Service
public class FacebookContext {

	@Autowired
	private FacebookUtils facebookUtils;

	@Autowired
	private FBWatcherConfiguration configuration;

	@Value("${facebook.app.id}")
	private String appId;

	@Value("${facebook.app.secret}")
	private String appSecret;

	private Facebook applicationfacebook;

	private final Map<String, Facebook> facebookByToken = new HashMap<String, Facebook>();

	@PostConstruct
	private void postConstruct() {
		applicationfacebook = new FacebookTemplate(MessageFormat.format(
				"{0}|{1}", appId, appSecret));
		for (Profile profile : configuration.getConfiguration().getProfiles()) {
			if (profile.getToken() != null) {
				FacebookAccessToken refreshed = facebookUtils
						.refreshToken(profile.getToken().getToken());
				addFacebookForToken(refreshed);
			}
		}
	}

	public void addFacebookForToken(FacebookAccessToken token) {
		Facebook facebook = facebookUtils.buildFacebookForToken(token
				.getAccessToken());
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.add(Calendar.SECOND, token.getExpiresIn().intValue());
		facebookByToken.put(token.getAccessToken(), facebook);
		String userId = facebook.userOperations().getUserProfile().getId();
		configuration.addTokenToProfile(token.getAccessToken(),
				gregorianCalendar.getTime(), userId);
	}

	public void addFacebookForCode(String code) {
		FacebookAccessToken token = facebookUtils.retrieveTokenFor(code);
		addFacebookForToken(token);
	}

	public void revokeFacebook(Facebook facebook) {
		String tokenToRevoke = facebookByToken.entrySet().stream()
				.filter(e -> Objects.equals(e.getValue(), facebook))
				.map(e -> e.getKey()).findFirst().get();
		facebookByToken.remove(tokenToRevoke);
		configuration.revokeToken(tokenToRevoke);
	}

	public Facebook getApplicationFacebook() {
		return applicationfacebook;
	}

	public void setApplicationFacebook(Facebook facebook) {
		this.applicationfacebook = facebook;
	}

	public Map<String, Facebook> getFacebookByToken() {
		return facebookByToken;
	}

}
