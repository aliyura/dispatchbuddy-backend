package com.decagon.dispatchbuddy.interfaceimpl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.decagon.dispatchbuddy.interfaces.ThirdPartyOauthProvider;
import com.decagon.dispatchbuddy.pojos.ThirdPartyOauthResponse;
import com.decagon.dispatchbuddy.util.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleOauthProvider implements ThirdPartyOauthProvider {

    private final Logger logger = LoggerFactory.getLogger(GoogleOauthProvider.class);
    private static final JacksonFactory jacksonFactory = new JacksonFactory();

    @Value("${google.client_id}")
    private String googleClientId;
    @Autowired
    private App app;
    @Override
    public ThirdPartyOauthResponse authentcate(String idToken)  {

        GoogleIdToken googleIdToken = null;
        logger.info("Starting : {}",googleClientId);


        app.print("before");
        try {
            app.print("in try before");
            googleIdToken = getGoogleIdToken(idToken,googleClientId);
            app.print("in try");
        } catch (Exception e) {
            app.print("in catch");
            logger.info("Google Auth error: {}",e.getLocalizedMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
        logger.info("Here : {}",idToken);
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        ThirdPartyOauthResponse thirdPartyOauthResponse = new ThirdPartyOauthResponse();
        thirdPartyOauthResponse.setEmail( payload.getEmail());
        thirdPartyOauthResponse.setFirstName((String) payload.get("given_name"));
        thirdPartyOauthResponse.setLastName((String) payload.get("family_name"));
        thirdPartyOauthResponse.setImage((String) payload.get("picture"));

        logger.info("Google Auth response: {}", thirdPartyOauthResponse);
        //Check if user exist
        return thirdPartyOauthResponse;
    }

    private GoogleIdToken getGoogleIdToken(String googleIdTokenString, String clientId) throws GeneralSecurityException, IOException {
        app.print("token:"+googleIdTokenString);
        app.print("clientId:"+clientId);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jacksonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(clientId))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
            return verifier.verify(googleIdTokenString);
    }
}
