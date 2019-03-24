package de.greyshine.urm.test;

import java.net.URL;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import de.greyshine.urm.UrmService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Check: https://spring.io/guides/gs/spring-boot/
 */
@RunWith( SpringRunner.class )
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UrmTests {
	
	private static final Logger LOG = LoggerFactory.getLogger( UrmTests.class );
	
	@Autowired
    private MockMvc mvc;
	
	@LocalServerPort
    private int port;
	
	private URL baseUrl;
	
	@Before
    public void setUp() throws Exception {
        this.baseUrl = new URL("http://localhost:" + port + "/");
        LOG.info( "base url: {}", this.baseUrl );
    }
	
	@Test
	public void testHelloWorld() throws Exception {
		mvc.perform( MockMvcRequestBuilders.get( "/" ).accept( MediaType.TEXT_PLAIN ) )
		   .andExpect( status().isOk() )
		   .andExpect( content().string( "helloworld" ));
	}
	
	public boolean login(String user) throws Exception {
		
		final MvcResult mvcResult = mvc.perform( MockMvcRequestBuilders.post( "/login" )
													.accept( MediaType.TEXT_PLAIN )
													.param( "user" , user == null ? "" : user)
												)
									.andReturn();

		final HttpSession httpSession = mvcResult.getRequest().getSession();
		if ( httpSession == null || !(httpSession.getAttribute( UrmService.HTTPSESSIONKEY_USERNAME ) instanceof String)  ) {
			return false;
		}
		
		return mvcResult.getResponse().getStatus() == 200;
	}
	
	@Test
	public void testLogin() throws Exception {
		
		boolean login = login( "root" );
		Assert.assertFalse( login );
		
		login = login( "admin" );
		Assert.assertTrue( login );
		
		mvc.perform( MockMvcRequestBuilders.get( "/login" ).accept( MediaType.TEXT_PLAIN ).sessionAttr( UrmService.HTTPSESSIONKEY_USERNAME, "admin") )
		   .andExpect( status().isOk() )
		   .andExpect( content().string( "admin" ));
	}
	
	@Test
	public void testGetOnlyRoot() throws Exception {
		
		String user = "admin";
		Assert.assertTrue( login( user ) ); 
		
		mvc.perform( MockMvcRequestBuilders.get( "/getOnlyRoot" ).accept( MediaType.TEXT_PLAIN ).sessionAttr( UrmService.HTTPSESSIONKEY_USERNAME, user) )
		   .andExpect( status().isOk() );
		
	}

}
