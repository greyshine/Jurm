package de.greyshine.urm.test;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.greyshine.urm.Urm;
import de.greyshine.urm.UrmService;
import de.greyshine.urm.filedatasource.FileDatasourceUrmService;
import de.greyshine.urm.spring.UrmInterceptor;
import de.greyshine.urm.spring.UrmSpringService;

/**
 * Simple test server of the Spring application. See main function.
 * 
 * @author greyshine
 *
 */
@SpringBootApplication
@Controller
public class WebController implements WebMvcConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

	public static final String RESPONSE_OK = "ok";

	@Autowired
	private UrmSpringService urmService;

	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public String index() {
		return "index.html";
	}

	@GetMapping(value = "/helloworld", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String helloworld() {
		return "helloworld";
	}

	/**
	 * @param httpSession
	 * @return
	 */
	@GetMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String login(HttpSession httpSession) {
		
		String result = httpSession == null ? null : (String) httpSession.getAttribute(UrmService.HTTPSESSIONKEY_USERNAME); 
		result = result == null || result.trim().isEmpty() ? "" : result.trim();
		
		LOG.debug( "responding: '{}'", result );
		
		return result;
	}

	@PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String login(@RequestParam(name = "user") String user, HttpServletRequest hsReq, HttpServletResponse hsRes)
			throws IOException {

		LOG.info("urmSpringService: {}", urmService);

		user = user == null || user.trim().isEmpty() ? null : user.trim();

		if (user == null) {

			final HttpSession httpSession = hsReq.getSession(false);
			if (httpSession == null
					|| !(httpSession.getAttribute(UrmService.HTTPSESSIONKEY_USERNAME) instanceof String)) {
				hsRes.sendError(200, "no user given");
				return null;
			}

			user = String.valueOf(httpSession.getAttribute(UrmService.HTTPSESSIONKEY_USERNAME));
			urmService.logout(hsReq.getSession(false));
			
			LOG.info( "logout: {}", user );
			return RESPONSE_OK;
		}

		if (urmService.isUser(user) == false) {
			hsRes.sendError(403, "no such user: " + user);
			return null;
		}

		urmService.setUserInSession(hsReq.getSession(true), user);

		HttpSession session = hsReq.getSession(true);
		LOG.info("sessionId={}", session.getId());

		return RESPONSE_OK;
	}

	@GetMapping(value = "/handleRoot")
	// having 'produces' attribute declared will return '406' on bad requests:
	// @GetMapping( value="/handleRoot", produces=MediaType.TEXT_PLAIN_VALUE )
	@Urm("root")
	@ResponseBody
	public String onlyRoot() {
		return RESPONSE_OK;
	}

	@GetMapping(value = "/handleRight1")
	// having 'produces' attribute declared will return '406' on bad requests:
	// @GetMapping( value="/handleRoot", produces=MediaType.TEXT_PLAIN_VALUE )
	@Urm("right1")
	@ResponseBody
	public String right1() {
		return RESPONSE_OK;
	}

	@GetMapping(value = "/handleRight2")
	// having 'produces' attribute declared will return '406' on bad requests:
	// @GetMapping( value="/handleRoot", produces=MediaType.TEXT_PLAIN_VALUE )
	@Urm("right2")
	@ResponseBody
	public String right2() {
		return RESPONSE_OK;
	}

	@Bean
	public UrmSpringService urmSpringService() {
		return new UrmSpringService();
	}

	@Bean
	public UrmService urmService() {

		final String URM_SOURCE = "src/test/resources/rights.txt";

		// copy from resource to output path
		final FileDatasourceUrmService fdsUrmService = new FileDatasourceUrmService(URM_SOURCE);
		return fdsUrmService;
	}

	@Bean
	public UrmInterceptor urmInterceptor() {
		return new UrmInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LOG.info("{}", registry);
		registry.addInterceptor(urmInterceptor());
	}

	public static void main(String[] args) {
		SpringApplication.run(WebController.class, args);
	}

}
