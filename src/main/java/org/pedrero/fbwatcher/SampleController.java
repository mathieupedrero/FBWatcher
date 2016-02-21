package org.pedrero.fbwatcher;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.pedrero.fbwatcher.communication.CommunicationService;
import org.pedrero.fbwatcher.config.FBWatcherConfiguration;
import org.pedrero.fbwatcher.facebook.FacebookUtils;
import org.pedrero.fbwatcher.facebook.FacebookUtils.FacebookAccessToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
public class SampleController {

	private static final org.slf4j.Logger LOGGER = LoggerFactory
			.getLogger(SampleController.class);

	@Autowired
	private CommunicationService communicationService;

	@Autowired
	private FacebookUtils facebookUtils;

	@Autowired
	private FBWatcherConfiguration configuration;

	@Value("${facebook.required_scopes}")
	private String requiredScopes;

	@Value("${facebook.login_url}")
	private String loginUrl;

	@Value("${facebook.get_long_lived_token_url}")
	private String getLongTokenUrl;

	@Value("${facebook.debug_token_url}")
	private String debugTokenUrl;

	@Value("${facebook.page_to_watch}")
	private String pageToWatchID;

	@Value("${facebook.events_to_attend_filter}")
	private String eventFilter;

	@Value("${facebook.app.id}")
	private String appId;

	@Value("${facebook.app.secret}")
	private String appSecret;

	@Value("${facebook.redirect.uri}")
	private String redirectUri;

	@Value("#{'${notification.clients.rest}'.split(',')}")
	private List<String> restClientsToNotify;

	@Value("#{'${notification.clients.mail}'.split(',')}")
	private List<String> mailAddressesToNotify;

	@RequestMapping(method = RequestMethod.GET)
	public String helloFacebook(Model model) {
		if (configuration.retrieveJobs().isEmpty()) {
			return MessageFormat.format("redirect:{0}", CommunicationService
					.computeUrl(loginUrl, appId, redirectUri, requiredScopes));
		}

		return "hello";
	}

	@RequestMapping(value = "/myFacebook", method = RequestMethod.GET, params = "code")
	public RedirectView oauth2Callback(NativeWebRequest request) {
		String code = request.getParameter("code");
		LOGGER.info("Code={}", code);

		FacebookAccessToken token = facebookUtils.retrieveTokenFor(code);
		Facebook userFacebook = FacebookUtils.buildFor(token.getAccessToken());
		String userId = userFacebook.userOperations().getUserProfile().getId();
		configuration.addProfile(userId, mailAddressesToNotify,
				restClientsToNotify);
		Calendar predictedExpliracy = new GregorianCalendar();
		predictedExpliracy
				.add(Calendar.SECOND, token.getExpiresIn().intValue());
		configuration.addTokenToProfile(token.getAccessToken(),
				predictedExpliracy.getTime(), userId);
		configuration.addJob(pageToWatchID, eventFilter, userId);
		return new RedirectView("/");
	}
}
