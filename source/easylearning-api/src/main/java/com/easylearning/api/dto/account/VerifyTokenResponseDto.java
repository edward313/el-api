package com.easylearning.api.dto.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Data
public class VerifyTokenResponseDto {
    @ApiModelProperty(name = "platform")
    private Integer platform;
    @ApiModelProperty(name = "platformUserId")
    private String platformUserId;
    @ApiModelProperty(name = "code")
    private String code;
    @ApiModelProperty(name = "email")
    private String email;
    @ApiModelProperty(name = "accessToken")
    private OAuth2AccessToken accessToken;
}
