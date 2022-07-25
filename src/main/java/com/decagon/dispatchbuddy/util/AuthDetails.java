package com.decagon.dispatchbuddy.util;

import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthDetails {

    private final UserRepository userRepository;
    public User getAuthorizedUser(OAuth2Authentication authentication){
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        Map<String,Object> detailsMap = (Map<String, Object>) details.getDecodedDetails();
        String userImg = detailsMap.get("userImg") == null ? null : detailsMap.get("userImg").toString();
        return  userRepository.findByUuid(detailsMap.get("uuid").toString()).orElse(null);
    }
}
