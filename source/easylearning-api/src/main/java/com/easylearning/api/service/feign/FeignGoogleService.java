package com.easylearning.api.service.feign;

import com.easylearning.api.config.CustomFeignConfig;
import com.easylearning.api.form.GoogleUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "easylearning-gg", url = "${feign.easylearning.gg.url}", configuration = CustomFeignConfig.class)
public interface FeignGoogleService {

    @GetMapping(value = "/oauth2/v1/userinfo")
    GoogleUserInfo getUserInfo(@RequestParam("access_token") String accessToken);
}