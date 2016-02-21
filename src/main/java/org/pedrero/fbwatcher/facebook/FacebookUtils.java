package org.pedrero.fbwatcher.facebook;

import java.text.MessageFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.pedrero.fbwatcher.communication.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Singleton
public class FacebookUtils {

	@Autowired
	private CommunicationService communicationService;

	@Value("${facebook.redirect.uri}")
	private String redirectUri;

	@Value("${facebook.get_token_url}")
	private String getTokenUrl;

	@Value("${facebook.app.id}")
	private String appId;

	@Value("${facebook.app.secret}")
	private String appSecret;

	private Facebook applicationFacebook;

	@PostConstruct
	private void postConstruct() {
		applicationFacebook = new FacebookTemplate(MessageFormat.format(
				"{0}|{1}", appId, appSecret));
	}

	public Facebook buildFacebookForToken(String token) {
		return new FacebookTemplate(token);
	}

	public FacebookAccessToken refreshToken(String token) {
		FacebookAccessToken facebookAccessToken = communicationService
				.getForObject(getTokenUrl, FacebookAccessToken.class, appId,
						redirectUri, appSecret, token);
		return facebookAccessToken;
	}

	public FacebookAccessToken retrieveTokenFor(String code) {
		FacebookAccessToken facebookAccessToken = communicationService
				.getForObject(getTokenUrl, FacebookAccessToken.class, appId,
						redirectUri, appSecret, code);
		return facebookAccessToken;
	}

	public static Facebook buildFor(String token) {
		return new FacebookTemplate(token);
	}

	public static class FacebookAccessToken {
		private final Date requestDate = new Date();

		@JsonProperty("access_token")
		private String accessToken;

		@JsonProperty("token_type")
		private String tokenType;

		@JsonProperty("expires_in")
		private Long expiresIn;

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public String getTokenType() {
			return tokenType;
		}

		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}

		public Long getExpiresIn() {
			return expiresIn;
		}

		public void setExpiresIn(Long expiresIn) {
			this.expiresIn = expiresIn;
		}

		public Date getRequestDate() {
			return requestDate;
		}
	}

	public Facebook getApplicationFacebook() {
		return applicationFacebook;
	}
}
