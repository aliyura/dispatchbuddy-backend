package com.decagon.dispatchbuddy.security;

import com.decagon.dispatchbuddy.entities.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<>();

        additionalInfo.put("id", user.getId());
        additionalInfo.put("uuid", user.getUuid());
        additionalInfo.put("role", user.getRole());
        additionalInfo.put("accountType", user.getAccountType());
        additionalInfo.put("email", user.getEmail());
        additionalInfo.put("status", user.getStatus());
        additionalInfo.put("isEnabled", user.getIsEnabled());
        additionalInfo.put("dp", user.getDp());
        additionalInfo.put("city", user.getCity());
        additionalInfo.put("gender", user.getGender());
        additionalInfo.put("phoneNumber", user.getPhoneNumber());
        additionalInfo.put("name", user.getName());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }


}