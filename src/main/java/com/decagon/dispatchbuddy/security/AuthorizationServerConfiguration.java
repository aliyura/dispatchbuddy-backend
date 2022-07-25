package com.decagon.dispatchbuddy.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter{


	   @Value("${jwt.signing_key}")
	   private String jwtSigningKey;
	   private final AuthenticationManager authenticationManager;
	   private final PasswordEncoder passwordEncoder;
	   
	   
	   @Override
	   public void configure (ClientDetailsServiceConfigurer clients) throws Exception {
	   clients.inMemory ()
	       .withClient ("web-client")
	               .authorizedGrantTypes ("password", "authorization_code", "refresh_token", "implicit")
	               .authorities ("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "USER")
	               .scopes ("read", "write")
			       .accessTokenValiditySeconds(0)
	               .autoApprove (true)
	               .secret (passwordEncoder.encode ("password"));
	   }
	   
	    @Override
	    public void configure(AuthorizationServerSecurityConfigurer security)  {
	        security.tokenKeyAccess("permitAll()")
	        .checkTokenAccess("isAuthenticated()");

	    }
	   

	   
	   @Bean
	   @Primary
	   public DefaultTokenServices tokenServices()  {
	       DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
	       defaultTokenServices.setTokenStore(tokenStore());
	       defaultTokenServices.setSupportRefreshToken(true);
	       return defaultTokenServices;
	   }

	   
	    @Override
	    public void configure (AuthorizationServerEndpointsConfigurer endpoints) {
			TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new CustomTokenEnhancer(),accessTokenConverter()));
			endpoints.exceptionTranslator(new CustomWebResponseExceptionTranslator());
			endpoints.authenticationManager (authenticationManager).tokenEnhancer(tokenEnhancerChain)
					.accessTokenConverter(accessTokenConverter())
					.tokenStore(tokenStore ());
	    }


		@Bean
		public JwtTokenStore tokenStore() {
			return new JwtTokenStore(accessTokenConverter());
		}


		@Bean
		JwtAccessTokenConverter accessTokenConverter() {
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			converter.setAccessTokenConverter(new CustomAccessTokenConverter());
			converter.setSigningKey(jwtSigningKey);
			return converter;
		}
	    
	
}
