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

@SpringBootApplication
@Controller
public class WebController implements WebMvcConfigurer {
	
	private static final Logger LOG = LoggerFactory.getLogger( WebController.class );

	public static final String RESPONSE_OK = "ok";
	
	@Autowired
	private UrmSpringService urmService;
	
	@GetMapping( value="/", produces="text/plain" )
	@ResponseBody
	public String helloworld() {
		return "helloworld";
	}
	
	@GetMapping( value="/login", produces="text/plain" )
	@ResponseBody
	public String login(HttpSession httpSession) {
		return httpSession == null ? "" : (String)httpSession.getAttribute( UrmService.HTTPSESSIONKEY_USERNAME );
	}
	
	@PostMapping( value="/login", produces="text/plain" )
	@ResponseBody
	public String login(@RequestParam(name="user") String user, HttpServletRequest hsReq, HttpServletResponse hsRes) throws IOException {
		
		LOG.info( "urmSpringService: {}", urmService );
		
		user = user == null || user.trim().isEmpty() ? null : user.trim();
		
		if ( user == null ) {
			hsRes.sendError(500, "no user given");
			return null;
		}
		
		if ( urmService.isUser( user ) == false) {
			hsRes.sendError(403, "no such user: "+ user);
			return null;
		}
		
		urmService.setUserInSession(hsReq.getSession(true), user);
		
		HttpSession session = hsReq.getSession( true );
		LOG.info( "sessionId={}", session.getId() );
		
		return RESPONSE_OK; 
	}
	
	@GetMapping( value="/getOnlyRoot", produces="text/plain" )
	@Urm( "root" )
	public String onlyRoot () {
		return RESPONSE_OK;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(WebController.class, args);
	}
	
	@Bean
	public UrmSpringService urmSpringService() {
		return new UrmSpringService();
	}
	
	@Bean
	public UrmService urmService() {

		final String URM_SOURCE = "src/test/resources/rights.txt";
		
		// copy from resource to output path 
		final FileDatasourceUrmService fdsUrmService = new FileDatasourceUrmService( URM_SOURCE ); 
		return fdsUrmService;
	}
	
	@Bean
	public UrmInterceptor urmInterceptor() {
		return new UrmInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LOG.info( "{}",registry );
		registry.addInterceptor( urmInterceptor() );
	}
	

}
