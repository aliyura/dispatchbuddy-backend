package com.decagon.dispatchbuddy.interfaces;

import com.decagon.dispatchbuddy.pojos.ThirdPartyOauthResponse;

public interface ThirdPartyOauthProvider {

    public ThirdPartyOauthResponse authentcate(String idToken);

}
