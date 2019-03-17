package de.greyshine.urm.filedatasource;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greyshine.urm.UrmService;

public class FileDatasourceUrmService implements UrmService {
	
	private static final Logger LOG = LoggerFactory.getLogger( FileDatasourceUrmService.class );
	
	private final File file;
	
	public FileDatasourceUrmService(String file) {
		this( file == null ? null : new File(file) );
	}
	
	public FileDatasourceUrmService(File file) {
		
		if ( file == null ) { throw new IllegalArgumentException("given file is null"); }
		if ( !file.isFile() || !file.canRead() ) { throw new IllegalArgumentException("given file is not accessible"); }
	
		this.file = file;
	}
	
	@Override
	public void setUserInSession(HttpSession httpSession, String user) {
		UrmService.defaultSetUser(httpSession, user);
	}

	public boolean isUser(String user) {
		
		if ( user == null || user.trim().isEmpty() ) { return false; }
		
		BufferedReader r = null;
		
		try { 
		
			r = new BufferedReader( new FileReader( file ) );

			while( r.ready() ) {
				
				final String line = r.readLine().trim();
				if ( line.startsWith( "#" ) || line.trim().isEmpty() || line.indexOf(':') < 1 ) { continue; }

				final String u = line.substring( 0, line.indexOf(':') );
				if ( u.trim().equals( user ) ) { 
					return true;
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("IO Access problem: "+ e, e);
		} finally {
			close( r );
		}
		
		return false;
	}

	public Set<String> getRights(String user) {
		return Collections.emptySet();
	}

	@Override
	public boolean isUserAnyRight(String user, String... rights) {
		
		if ( user == null || rights == null || rights.length == 0 ) { return false; }
		
		final Set<String> userRights = new HashSet<>();
		
		BufferedReader r = null;
		
		try { 
		
			r = new BufferedReader( new FileReader( file ) );

			while( r.ready() ) {
				
				final String line = r.readLine().trim();
				if ( line.startsWith( "#" ) || line.trim().isEmpty() || line.indexOf(':') < 1 ) { continue; }

				final String u = line.substring( 0, line.indexOf(':') );
				if ( u.trim().equals( user ) ) {
					
					for(String aRight : line.substring( line.indexOf( ':' )+1 ).split("\\,", -1) ) {
						if ( (aRight = aRight.trim()).isEmpty() ) { continue; }
						userRights.add( aRight );
					}
					
					break;
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("IO Access problem: "+ e, e);
		} finally {
			close( r );
		}
		
		for( String aRight : rights ) {
			if ( aRight == null || (aRight=aRight.trim()).isEmpty() ) { continue; }
			if ( userRights.contains( aRight ) ) { return true; }
		}
		
		return false;
	}

	public boolean isUserRight(String user, String right) {
		return getRights(user).contains( right );
	}
	
	private void close(Closeable c) {
		try { ((Flushable)c).flush(); } catch(Exception e) { /*intended ignore*/ }
		try { c.close(); } catch(Exception e) { /*intended ignore*/ }
	} 
	
	

}
