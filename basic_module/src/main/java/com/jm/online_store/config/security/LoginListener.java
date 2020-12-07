package com.jm.online_store.config.security;

import com.jm.online_store.config.security.oauth2userinfo.OAuth2UserInfo;
import com.jm.online_store.config.security.oauth2userinfo.OAuth2UserInfoFactory;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
/**
 * Класс выводящий в лог информацию об авторизации .
 */
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final UserService userService;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Current authentication of user" + authentication.getName() + " is");

        //Checking if Authentication coming from OAuth
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            log.debug("Authentication request coming from OAuth with token details: " + oauthToken);

            OAuth2AuthenticatedPrincipal principal = ((OAuth2AuthenticationToken) authentication).getPrincipal();

            //Sending data to retrieve proper UserInfo
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(clientRegistrationId, principal.getAttributes());
            log.debug("Client registration Id from OAuth token is: " + clientRegistrationId);
            User userPrincipalFromDB = userService.findByEmail(oAuth2UserInfo.getEmail()).get();
            Authentication newCustomAuthentication = new UsernamePasswordAuthenticationToken(userPrincipalFromDB, null, userPrincipalFromDB.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newCustomAuthentication);

        } else if (authentication.getClass().isAssignableFrom(UsernamePasswordAuthenticationToken.class)) {
            log.debug("Form based manual authorization");
        }
        Authentication oAuth2Authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
    }
}

