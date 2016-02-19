package org.pedrero.fbwatcher;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.pedrero.fbwatcher.communication.CommunicationService;
import org.pedrero.fbwatcher.facebook.FacebookContext;
import org.pedrero.fbwatcher.facebook.FacebookUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Event;
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

	@Autowired
	private FacebookContext facebookKeeper;

	@Value("${facebook.app.id}")
	private String appId;

	@Value("${facebook.app.secret}")
	private String appSecret;

	@Value("${facebook.redirect.uri}")
	private String redirectUri;

	@RequestMapping(method = RequestMethod.GET)
	public String helloFacebook(Model model) {
		if (facebookKeeper.getFacebookByToken().isEmpty()) {
			return MessageFormat.format("redirect:{0}", CommunicationService
					.computeUrl(loginUrl, appId, redirectUri, requiredScopes));
		}

		Facebook facebook = facebookKeeper.getFacebookByToken().entrySet()
				.iterator().next().getValue();

		model.addAttribute(facebook.userOperations().getUserProfile());

		facebookKeeper.setApplicationFacebook(facebook);

		List<Event> events = facebook
				.fetchConnections(pageToWatchID, "events", Event.class)
				.stream()
				.filter(e -> e.getStartTime().after(new java.util.Date()))
				.collect(Collectors.toList());

		model.addAttribute("event", events);

		return "hello";
	}

	@RequestMapping(value = "/myFacebook", method = RequestMethod.GET, params = "code")
	public RedirectView oauth2Callback(NativeWebRequest request) {
		String code = request.getParameter("code");
		LOGGER.info("Code={}", code);
		facebookKeeper.addFacebookForCode(code);
		return new RedirectView("/");
	}
}
