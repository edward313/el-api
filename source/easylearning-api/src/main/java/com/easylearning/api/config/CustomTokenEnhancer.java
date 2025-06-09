package com.easylearning.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.AccountForTokenDto;
import com.easylearning.api.model.Permission;
import com.easylearning.api.utils.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {
    private JdbcTemplate jdbcTemplate;

    public CustomTokenEnhancer(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        String username = authentication.getName();
        if(authentication.getOAuth2Request().getGrantType() != null){
            additionalInfo = getAdditionalInfo(username, authentication.getOAuth2Request().getGrantType());
        }else {
            String grantType = authentication.getOAuth2Request().getRequestParameters().get("grantType");
            if(grantType.equals(SecurityConstant.GRANT_TYPE_STUDENT) ||
                    grantType.equals(SecurityConstant.GRANT_TYPE_EXPERT)){
                additionalInfo = getAdditionalInfoCustom(username, grantType);
            }else if(grantType.equals(SecurityConstant.GRANT_TYPE_PASSWORD)) {
                additionalInfo = getAdditionalInfo(username, grantType);
            }
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }

    private Map<String, Object> getAdditionalInfoCustom(String phone, String grantType) {
        Map<String, Object> additionalInfo = new HashMap<>();
        Integer kind;
        if (grantType.equals(SecurityConstant.GRANT_TYPE_STUDENT)) {
            kind = LifeUniConstant.USER_KIND_STUDENT;
        } else {
            kind = LifeUniConstant.USER_KIND_EXPERT;
        }
        AccountForTokenDto a = getUserByPhoneAndKind(phone,kind);

        Boolean isSeller = false;
        Boolean isSystem = false;
        String referralCode = null;
        if(grantType.equalsIgnoreCase(SecurityConstant.GRANT_TYPE_STUDENT)) {
            //get isSeller
            isSeller = getIsSeller(a.getId());
            referralCode = getReferralCodeSeller(a.getId());
        }else {
            // set referralCode & isSystem for Expert
            AccountForTokenDto account = getReferralCodeAndIsSystemExpert(a.getId());
            if(account != null){
                if(account.getIsSystem() != null){
                    isSystem = getReferralCodeAndIsSystemExpert(a.getId()).getIsSystem();
                }
                referralCode = getReferralCodeAndIsSystemExpert(a.getId()).getReferralCode();
            }
        }
        if (a != null) {
            Long accountId = a.getId();
            Long storeId = -1L;
            Long deviceId = -1L;// id cua thiet bi, lưu ở table device để get firebase url..
            String pemission = "<>";//empty string
            Integer userKind = a.getKind(); //loại user là admin hay là gì
            Integer tabletKind = -1;
            Long orderId = -1L;
            Boolean isSuperAdmin = a.getIsSuperAdmin();
            String tenantId = "";
            additionalInfo.put("referral_code", referralCode);
            additionalInfo.put("user_id", accountId);
            additionalInfo.put("user_kind", kind);
            additionalInfo.put("grant_type", grantType);
            additionalInfo.put("tenant_info", tenantId);
            if(grantType.equalsIgnoreCase(SecurityConstant.GRANT_TYPE_STUDENT)) {
                additionalInfo.put("is_seller", isSeller);
            }else if(grantType.equalsIgnoreCase(SecurityConstant.GRANT_TYPE_EXPERT)) {
                additionalInfo.put("is_system",isSystem);
            }
            String DELIM = "|";
            String additionalInfoStr = ZipUtils.zipString(accountId + DELIM
                    + storeId + DELIM
                    + kind + DELIM
                    + pemission + DELIM
                    + deviceId + DELIM
                    + userKind + DELIM
                    + phone + DELIM
                    + tabletKind + DELIM
                    + orderId + DELIM
                    + isSuperAdmin + DELIM
                    + tenantId);
            additionalInfo.put("additional_info", additionalInfoStr);
        }
        return additionalInfo;
    }

    private Map<String, Object> getAdditionalInfo(String username, String grantType) {
        Map<String, Object> additionalInfo = new HashMap<>();
        AccountForTokenDto a = getAccountByUsername(username);

        if (a != null) {
            Long accountId = a.getId();
            Long storeId = -1L;
            String kind = a.getKind() + "";//token kind
            Long deviceId = -1L;// id cua thiet bi, lưu ở table device để get firebase url..
            String pemission = "<>";//empty string
            Integer userKind = a.getKind(); //loại user là admin hay là gì
            Integer tabletKind = -1;
            Long orderId = -1L;
            Boolean isSuperAdmin = a.getIsSuperAdmin();
            String tenantId = "";
            additionalInfo.put("user_id", accountId);
            additionalInfo.put("user_kind", a.getKind());
            additionalInfo.put("grant_type", grantType == null ? SecurityConstant.GRANT_TYPE_PASSWORD : grantType);
            additionalInfo.put("tenant_info", tenantId);
            String DELIM = "|";
            String additionalInfoStr = ZipUtils.zipString(accountId + DELIM
                    + storeId + DELIM
                    + kind + DELIM
                    + pemission + DELIM
                    + deviceId + DELIM
                    + userKind + DELIM
                    + username + DELIM
                    + tabletKind + DELIM
                    + orderId + DELIM
                    + isSuperAdmin + DELIM
                    + tenantId);
            additionalInfo.put("additional_info", additionalInfoStr);
        }
        return additionalInfo;
    }

    public AccountForTokenDto getAccountByUsername(String username) {
        try {
            String query = "SELECT id, kind, username, email, full_name, is_super_admin " +
                    "FROM db_el_account WHERE username = ? and status = 1 limit 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{username},  new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (dto.size() > 0)return dto.get(0);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountForTokenDto getUserByPhoneAndKind(String phone, Integer kind) {
        try {
            String query = "SELECT id, kind, username, email, full_name, is_super_admin " +
                    "FROM db_el_account WHERE phone = ? AND status = 1 AND kind = ? LIMIT 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{phone, kind}, new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (!dto.isEmpty()) return dto.get(0);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean getIsSeller(Long accountId) {
        try {
            String query = "SELECT is_seller " +
                    "FROM db_el_student WHERE account_id = ? and status = 1 limit 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{accountId},  new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (!dto.isEmpty())return dto.get(0).getIsSeller() != null ? dto.get(0).getIsSeller() : false;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getReferralCodeSeller(Long accountId) {
        try {
            String query = "SELECT referral_code " +
                    "FROM db_el_student WHERE account_id = ? and status = 1 limit 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{accountId},  new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (!dto.isEmpty())return dto.get(0).getReferralCode();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountForTokenDto getReferralCodeAndIsSystemExpert(Long accountId) {
        try {
            String query = "SELECT referral_code, is_system_expert as is_system " +
                    "FROM db_el_expert WHERE account_id = ? and status = 1 limit 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{accountId},  new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (!dto.isEmpty()){
                return dto.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Permission> getListPermissionByUserKind(Integer kind) {
        try{
            String query = "select * " +
                    "from db_el_permission p " +
                    "join db_el_permission_group g on p.id = g.permission_id " +
                    "join db_el_account a on a.group_id = g.group_id " +
                    "where a.kind = ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Permission.class), kind);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
