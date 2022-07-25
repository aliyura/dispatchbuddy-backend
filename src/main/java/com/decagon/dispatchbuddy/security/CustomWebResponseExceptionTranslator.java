package com.decagon.dispatchbuddy.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

@Component
public class CustomWebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {

    /**
     * Modify OAuth2.0 Error Response
     * @param e
     * @return ResponseEntity<OAuth2Exception>
     * @throws Exception
     */

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        ResponseEntity responseEntity = super.translate(e);
        OAuth2Exception auth2Exception = (OAuth2Exception)responseEntity.getBody();
        if (auth2Exception != null) {
//            auth2Exception.addAdditionalInformation("data", null);
//            auth2Exception.addAdditionalInformation("message", auth2Exception.getMessage());
//            auth2Exception.addAdditionalInformation("statusCode", String.valueOf(auth2Exception.getHttpErrorCode()));
//            e.printStackTrace();
        }

        return new ResponseEntity<OAuth2Exception>(auth2Exception, responseEntity.getHeaders(), responseEntity.getStatusCode());
    }
}