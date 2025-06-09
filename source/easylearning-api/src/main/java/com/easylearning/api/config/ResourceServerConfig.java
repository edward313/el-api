package com.easylearning.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    JsonToUrlEncodedAuthenticationFilter jsonFilter;

    @Bean
    public DefaultTokenServices createTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        jwtAccessTokenConverter.setAccessTokenConverter(new CustomTokenConverter());
        defaultTokenServices.setTokenStore(new JwtTokenStore(jwtAccessTokenConverter));
        return defaultTokenServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(jsonFilter, BasicAuthenticationFilter.class)
                .requestMatchers()
                .and()
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/index", "/pub/**", "/api/token","/api/auth/pwd/verify-token",
                        "/api/auth/activate/resend", "/api/auth/pwd", "/api/auth/logout", "/actuator/**").permitAll()
                .antMatchers( "/v1/customer/register").permitAll()
                .antMatchers("/v1/service/detail/**").permitAll()
                .antMatchers("/v1/user/login","/v1/user/signup").permitAll()
                .antMatchers("/v1/student/signup").permitAll()
                .antMatchers("/v1/student/facebook/verify-token").permitAll()
                .antMatchers("/v1/student/facebook/register-profile").permitAll()
                .antMatchers("/v1/student/google/verify-token").permitAll()
                .antMatchers("/v1/student/google/register-profile").permitAll()
                .antMatchers("/v1/expert/signup").permitAll()
                .antMatchers("/v1/seller-code-tracking/create").permitAll()
                .antMatchers("/v1/course/course-detail/{id}").permitAll()
                .antMatchers("/v1/course/client-list").permitAll()
                .antMatchers("/v1/category-home/client-list").permitAll()
                .antMatchers("/v1/category/list").permitAll()
                .antMatchers("/v1/category/get/**").permitAll()
                .antMatchers("/v1/slideshow/get/**").permitAll()
                .antMatchers("/v1/slideshow/list").permitAll()
                .antMatchers("/v1/nation/auto-complete").permitAll()
                .antMatchers("/v1/nation/client-list").permitAll()
                .antMatchers("/v1/expert/client-get/**").permitAll()
                .antMatchers("/v1/expert/client-list").permitAll()
                .antMatchers("/v1/news/client-list").permitAll()
                .antMatchers("/v1/news/client-get/**").permitAll()
                .antMatchers("/v1/lesson/lesson-detail/{id}").permitAll()
                .antMatchers("/v1/expert-registration/registration").permitAll()
                .antMatchers("/v1/review/list-reviews/{courseId}").permitAll()
                .antMatchers("/v1/review/star/{courseId}").permitAll()
                .antMatchers("/v1/review/client-list").permitAll()
                .antMatchers("/v1/review/list-expert-reviews/{expertId}").permitAll()
                .antMatchers("/v1/review/star-expert/{expertId}").permitAll()
                .antMatchers("/v1/account/request_forget_password", "/v1/account/forget_password").permitAll()
                .antMatchers("/v1/statistical/get-fe-statistic").permitAll()
                .antMatchers("/v1/booking/payos-webhook").permitAll()
                .antMatchers("/**").authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("lifeuni-service");
    }
}