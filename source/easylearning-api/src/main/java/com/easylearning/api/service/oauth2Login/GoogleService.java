package com.easylearning.api.service.oauth2Login;

import com.easylearning.api.form.GoogleUserInfo;
import com.easylearning.api.service.feign.FeignGoogleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class GoogleService {
    @Autowired
    FeignGoogleService feignGoogleService;

    public GoogleUserInfo getUserInfo(String accessToken){
        GoogleUserInfo googleUserInfo;
        try {
            googleUserInfo = feignGoogleService.getUserInfo(accessToken);
        }
        catch (Exception e){
            log.error("Get access token fail" + e);
            return null;
        }
        return googleUserInfo;
    }
}
