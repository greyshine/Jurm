package de.greyshine.urm.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	
	/**
	 * This method gets invoke by having defined the class level
	 * @EnableWebMvc and @Configuration
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor( urmInterceptor() );
	}
	
	@Bean
	public UrmInterceptor urmInterceptor() {
		return new UrmInterceptor();
	}
	
}
