package com.jm.online_store.config.security;

import com.jm.online_store.config.security.oauth2userinfo.OAuth2UserInfo;
import com.jm.online_store.config.security.oauth2userinfo.OAuth2UserInfoFactory;
import com.jm.online_store.model.User;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final UserService userService;

    @Autowired
    public LoginListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Checking if Authentication coming from OAuth
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();

            OAuth2AuthenticatedPrincipal principal = ((OAuth2AuthenticationToken) authentication).getPrincipal();

            //Sending data to retrieve proper UserInfo
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(clientRegistrationId, principal.getAttributes());

            User userPrincipalFromDB = userService.findByEmail(oAuth2UserInfo.getEmail()).get();
            Authentication newCustomAuthentication = new UsernamePasswordAuthenticationToken(userPrincipalFromDB, null, userPrincipalFromDB.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newCustomAuthentication);

        } else if (authentication.getClass().isAssignableFrom(UsernamePasswordAuthenticationToken.class)) {
            System.out.println("Form based manual authorization");
        }
        Authentication oAuth2Authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
    }
}

