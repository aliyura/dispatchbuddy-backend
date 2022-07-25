package com.decagon.dispatchbuddy.security;
import com.decagon.dispatchbuddy.interfaceimpl.GoogleOauthProvider;
import com.decagon.dispatchbuddy.util.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import java.util.Map;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private GoogleOauthProvider googleOauthProvider;
    @Autowired
    private  MessageSource messageSource;
    @Autowired
    private App app;


    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {

        String thirdPartyOauthToken = "";
        if (!(auth.getDetails() instanceof WebAuthenticationDetails)) {
            Map<String, String> map = (Map<String, String>) auth.getDetails();
            thirdPartyOauthToken = map.get("oauth_token");
        }

        this.logger.info(String.format("token is : %s",thirdPartyOauthToken));
        String username = auth.getPrincipal() == null ? "NONE_PROVIDED" : auth.getName();
        app.print("username:"+username);
        boolean cacheWasUsed = true;
        FutureDAOUserDetails user = (FutureDAOUserDetails)this.getUserCache().getUserFromCache(username);

        app.print(this.getUserCache().getUserFromCache(username));
        if(user == null) {
            cacheWasUsed =false;

            app.print(auth.getName());
            user = (FutureDAOUserDetails) this.retrieveUser(auth.getName(),
                    (UsernamePasswordAuthenticationToken) auth);
        }
        app.print("Login user object:");
        app.print(user);
        this.getPreAuthenticationChecks().check(user);

        if(user.getPassword()!=null) {
            if(auth.getCredentials() == null && thirdPartyOauthToken != null)
                throw new BadCredentialsException(this.messages.getMessage("wrong.authprovider.email.error", "Bad credentials"));
            additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) auth);
        }

        if(user.getPassword()==null) {
            if(thirdPartyOauthToken == null)
                throw new BadCredentialsException(this.messages.getMessage("wrong.authprovider.google.error", "Bad credentials"));
            this.logger.info("Using google");
            googleOauthProvider.authentcate(thirdPartyOauthToken);
        }

        if (!cacheWasUsed) {
            this.getUserCache().putUserInCache(user);
        }
        this.getPostAuthenticationChecks().check(user);

        Object principalToReturn = user;
        if (this.isForcePrincipalAsString()) {
            principalToReturn = user.getUsername();
        }
        return this.createSuccessAuthentication(principalToReturn, auth, user);
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (!this.getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Authentication failed: password does not match stored value");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }
    }

}
