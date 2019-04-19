package de.greyshine.urm.spring;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import de.greyshine.urm.Urm;
import de.greyshine.urm.UrmService;

@Component
public class UrmInterceptor implements HandlerInterceptor {
	
	private static final Logger LOG = LoggerFactory.getLogger( UrmInterceptor.class );
	private static final Logger LOG_URM = LoggerFactory.getLogger( UrmInterceptor.class );
	
	private static final ThreadLocal<String> TL_USERID = new ThreadLocal<>();
	
	@Autowired
	private UrmService urmService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if ( !(handler instanceof HandlerMethod) ) {
			return HandlerInterceptor.super.preHandle(request, response, handler);
		}
		
		final HandlerMethod handlerMethod = (HandlerMethod) handler;
		final Method method = handlerMethod.getMethod();
		final Urm urmAnnotation = method.getDeclaredAnnotation( Urm.class );
		
		if ( urmAnnotation == null ) {
			return HandlerInterceptor.super.preHandle(request, response, handler);
		}
		
		final String user = getSessionUser( request );
		
		if ( user == null ) {
			LOG_URM.info( "not logged in tried to access: {}", requestToString(request) );
			response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
			return false;
		}
		
		if ( !urmService.isUserAnyRight(user, urmAnnotation.value()) ) {
			LOG_URM.info( "bad user rights tried to access: {}", requestToString(request) );
			response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
			return false;
		}
		
		LOG.debug("serving user: {} on {}", user, method);
		LOG_URM.info( "serving user: {} on {}", user, method );
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	private String requestToString(HttpServletRequest request) {
		return String.valueOf( request );
	}

	private String getSessionUser(HttpServletRequest request) {
		
		final HttpSession httpSession = request.getSession(false);
		if ( httpSession == null ) { return null; }
		
		final Object userName = httpSession.getAttribute( UrmService.HTTPSESSIONKEY_USERNAME );
		
		if ( !(userName instanceof String) || userName.toString().trim().isEmpty() ) { return null; }
		
		return userName.toString();
	}
	
	public void setThreadLocalUserId(String userId) {
		
		if ( userId == null || userId.trim().isEmpty() ) { 
			TL_USERID.remove();
			return;
		}
		
		TL_USERID.set( userId );
	}
	
	public String getThreadLocalUserId() {
		return TL_USERID.get();
	}
	
}
