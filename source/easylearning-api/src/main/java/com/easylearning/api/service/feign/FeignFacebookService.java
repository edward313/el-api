package com.easylearning.api.service.feign;

import com.easylearning.api.config.CustomFeignConfig;
import com.easylearning.api.dto.facebook.FacebookUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "lifeuni-fb", url = "${feign.lifeuni.fb.url}", configuration = CustomFeignConfig.class)
public interface FeignFacebookService {

    @GetMapping(value = "/me")
    FacebookUserInfo getUserInfo(@RequestParam("access_token") String accessToken, @RequestParam("fields") String fields);
    @GetMapping(value = "/v18.0/me/albums")
    String getAvatarUrl(@RequestParam("access_token") String accessToken, @RequestParam("fields") String fields);
}