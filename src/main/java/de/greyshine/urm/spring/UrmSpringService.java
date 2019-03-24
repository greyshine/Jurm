package de.greyshine.urm.spring;

import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.greyshine.urm.UrmService;

@Service
public class UrmSpringService implements UrmService {

	@Autowired
	private UrmService urmService;

	public boolean isUser(String user) {
		return urmService.isUser(user);
	}

	@Override
	public Set<String> getRights(String user) {
		return urmService.getRights(user);
	}

	@Override
	public boolean isUserRight(String user, String right) {
		return urmService.isUserRight(user, right);
	}

	@Override
	public void setUserInSession(HttpSession httpSession, String user) {
		urmService.setUserInSession(httpSession, user);
	}
	
	@Override
	public boolean isUserAnyRight(String user, String... rights) {
		return urmService.isUserAnyRight(user, rights);
	}

	@Override
	public void logout(HttpSession httpSession) {
		urmService.logout(httpSession);
	}
		
	
}
