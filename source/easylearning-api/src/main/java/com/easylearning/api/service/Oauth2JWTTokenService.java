package com.easylearning.api.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.config.CustomTokenEnhancer;
import com.easylearning.api.config.SecurityConstant;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.OauthClientDetailsDto;
import com.easylearning.api.service.impl.UserServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class Oauth2JWTTokenService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DefaultTokenServices tokenServices;
    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;
    @Autowired
    private UserServiceImpl userService;
    @Value("${oauth2.jwt.token.service.client.id}")
    private String clientId;

    public OAuth2AccessToken generateAccessToken(UserDetails userPrincipal, OauthClientDetailsDto oauthClientDetailsDto,String grantType ) {
        try {
            OAuth2Authentication authentication = convertAuthentication(userPrincipal, oauthClientDetailsDto,grantType);
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new CustomTokenEnhancer(jdbcTemplate, objectMapper), accessTokenConverter));
            tokenServices.setTokenEnhancer(tokenEnhancerChain);
            tokenServices.setReuseRefreshToken(false);
            tokenServices.setSupportRefreshToken(true);
            tokenServices.setAccessTokenValiditySeconds(oauthClientDetailsDto.getAccessTokenValidInSeconds());
            tokenServices.setRefreshTokenValiditySeconds(oauthClientDetailsDto.getRefreshTokenValidInSeconds());
            OAuth2AccessToken token = tokenServices.createAccessToken(authentication);
            return token;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
    private OAuth2Authentication convertAuthentication(UserDetails userDetails, OauthClientDetailsDto oauthClientDetailsDto, String grantType) {
        Set<String> scope = new HashSet<>();
        String[] scopeArray = oauthClientDetailsDto.getScope().split(",");
        for (String c : scopeArray) {
            scope.add(c);
        }
        Map<String,String> grantTypeMapper = Collections.singletonMap("grantType", grantType);
        OAuth2Request request = new OAuth2Request(grantTypeMapper, oauthClientDetailsDto.getClientId(), null, true, scope, null,
                null, null, null);
        return new OAuth2Authentication(request, new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities()));
    }

    public OauthClientDetailsDto getOauthClientDetails(String clientId){
        try {
            String query = "SELECT client_id, scope, authorized_grant_types, access_token_validity, refresh_token_validity " +
                    "FROM oauth_client_details WHERE client_id = '" + clientId + "' LIMIT 1";
            OauthClientDetailsDto oauthClientDetailsDto = jdbcTemplate.queryForObject(query,
                    (resultSet, rowNum) -> new OauthClientDetailsDto(resultSet.getString("client_id"),
                            resultSet.getString("scope"), resultSet.getString("authorized_grant_types"),
                            resultSet.getInt("access_token_validity"), resultSet.getInt("refresh_token_validity")));
            return oauthClientDetailsDto;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public OAuth2AccessToken getAccessTokenForStudent(String phone){
        OauthClientDetailsDto oauthClientDetailsDto = getOauthClientDetails(clientId);
        if(oauthClientDetailsDto == null){
            throw new RuntimeException("Not found clientId");
        }
        if (!checkStudentGrantType(oauthClientDetailsDto)) {
            throw new RuntimeException("Client not contain student grant type");
        }
        UserDetails userDetails = userService.loadUserByPhoneAndKind(phone, LifeUniConstant.USER_KIND_STUDENT);
        return generateAccessToken(userDetails, oauthClientDetailsDto, SecurityConstant.GRANT_TYPE_STUDENT);
    }
    public OAuth2AccessToken getInternalAccessToken(String name){
        OauthClientDetailsDto oauthClientDetailsDto = getOauthClientDetails(clientId);
        if(oauthClientDetailsDto == null){
            throw new RuntimeException("Not found clientId");
        }
        UserDetails userDetails = userService.loadUserByUsername(name);
        return generateAccessToken(userDetails, oauthClientDetailsDto,SecurityConstant.GRANT_TYPE_PASSWORD);
    }

    public boolean checkStudentGrantType(OauthClientDetailsDto oauthClientDetailsDto) {
        try {
            String[] grantTypesArray = oauthClientDetailsDto.getAuthorizedGrantTypes().split(",");
            for (String grantType : grantTypesArray) {
                if (grantType.trim().equalsIgnoreCase("student")) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

}