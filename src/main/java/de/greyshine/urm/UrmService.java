package de.greyshine.urm;

import java.util.Set;

import javax.servlet.http.HttpSession;

public interface UrmService {
	
	String HTTPSESSIONKEY_USERNAME = "de.greyshine.urm:User";
	
	boolean isUser(String user);
	Set<String> getRights(String user);
	boolean isUserAnyRight(String user, String... rights);
	boolean isUserRight(String user, String right);
	void setUserInSession(HttpSession httpSession, String user);
	void logout(HttpSession httpSession);
	
	static void defaultSetUser(HttpSession httpSession, String user) {
		
		if ( httpSession == null ) { return; }
		
		String userInSession = null;
		try { 
			userInSession = (String)httpSession.getAttribute( HTTPSESSIONKEY_USERNAME );
		} catch(Exception e) {
			httpSession.removeAttribute( HTTPSESSIONKEY_USERNAME );
			httpSession.invalidate();
			throw new IllegalStateException("Wrong value in session at key '"+ HTTPSESSIONKEY_USERNAME +"'. Killing session.");
		}
		
		if ( userInSession != null && !userInSession.trim().isEmpty() && !userInSession.equals( user ) ) {
			httpSession.removeAttribute( HTTPSESSIONKEY_USERNAME );
			httpSession.invalidate();
			throw new IllegalStateException("Wrong user in session. Killing session.");
		}
		
		httpSession.setAttribute( HTTPSESSIONKEY_USERNAME, user);
	}
	
	static void defaultLogout(HttpSession httpSession) {
	
		if ( httpSession == null ) { return; }
		
		httpSession.removeAttribute( HTTPSESSIONKEY_USERNAME );
		httpSession.invalidate();
	}
	
}
