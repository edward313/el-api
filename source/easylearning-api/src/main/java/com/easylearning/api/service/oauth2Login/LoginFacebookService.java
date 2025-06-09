package com.easylearning.api.service.oauth2Login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.dto.facebook.FacebookUserInfo;
import com.easylearning.api.service.feign.FeignFacebookService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Log4j2
public class LoginFacebookService {
    @Autowired
    FeignFacebookService feignFacebookService;
    public FacebookUserInfo getUserInfo(String accessToken){
        try {
            FacebookUserInfo facebookUserInfo = feignFacebookService.getUserInfo(accessToken,"id,name,email,birthday");
            log.error("FacebookUserInfo Response: " + facebookUserInfo);
            return facebookUserInfo;
        }
        catch (Exception e){
            log.error("get user info fail, invalid access token" + e);
            return null;
        }

    }
    public Date convertToDate(String birthday) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormat.parse(birthday);
        } catch (ParseException e) {
            log.error("Facebook: Failed to convert birthday to Date: {}", e.getMessage());
            return null;
        }
    }
    public String getFacebookAvatarUrl(String accessToken) {
        try {
            String jsonData = feignFacebookService.getAvatarUrl(accessToken, "type,picture");
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonNode = objectMapper.readTree(jsonData);
                JsonNode dataNode = jsonNode.get("data");
                for (JsonNode albumNode : dataNode) {
                    String type = albumNode.get("type").asText();
                    if ("profile".equals(type)) {
                        JsonNode pictureNode = albumNode.get("picture");
                        JsonNode pictureDataNode = pictureNode.get("data");
                        return pictureDataNode.get("url").asText();
                    }
                }
            } catch (Exception e) {
                log.error("Can not parse album id: " + e);
                return null;
            }
        } catch (Exception e) {
            log.error("Get album profile fail, invalid access token: " + e);
        }
        return null;
    }
}
