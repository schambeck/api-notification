package com.schambeck.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.http.HttpMethod.*;

@Profile("!test")
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and().authorizeRequests()
                .mvcMatchers(GET, "/notifications").authenticated()
                .mvcMatchers(POST, "/notifications").authenticated()
                .mvcMatchers(PUT, "/notifications").authenticated()
                .anyRequest().permitAll()
                .and().oauth2ResourceServer().jwt();
    }

}
