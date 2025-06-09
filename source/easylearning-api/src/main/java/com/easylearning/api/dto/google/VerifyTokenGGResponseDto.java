package com.easylearning.api.dto.google;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Data
public class VerifyTokenGGResponseDto {
    @ApiModelProperty(name = "platform")
    private Integer platform;
    @ApiModelProperty(name = "platformUserId")
    private String platformUserId;
    @ApiModelProperty(name = "code")
    private String code;
    @ApiModelProperty(name = "accessToken")
    private OAuth2AccessToken accessToken;
}
