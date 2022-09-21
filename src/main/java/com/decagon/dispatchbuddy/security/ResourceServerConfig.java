package com.decagon.dispatchbuddy.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("" +
//								"/swagger-resources/**","/swagger-resources/**", "/swagger-ui.html**",
						"/api/v1/security/**", "/webjars/**", "/login", "/oauth/token", "/oauth/authorize/**","/api/order/status/update/**","/api/order/add",
						"favicon.ico", "/api/ping","/api/sms/test", "/api/test","/api/user/signup","/api/user/signin", "/api/user/verify",
						"/api/user/validate","/api/user/password-reset","/api/rider/search/**","/api/rider/request", "/api/user/sendmail").permitAll()
//				.antMatchers(AUTH_WHITELIST).permitAll()
				.and().requestMatchers().antMatchers("/api/**")
				.and().authorizeRequests()
				.antMatchers("/api/**").authenticated();
	}
}