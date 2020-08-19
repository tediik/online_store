package com.jm.online_store.config.security.facebook;

import com.jm.online_store.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jm.online_store.service.interf.UserService;
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
@Slf4j
@RequiredArgsConstructor
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final UserService userService;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current authentication is: " + authentication);

        //Checking if Authentication coming from OAuth
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            log.info("Authentication request coming from OAuth with token details: " + oauthToken);

            //Checking if OAuth authentication Token is coming from Facebook
            if (clientRegistrationId.equals("facebook")) {
                log.info("Client registration Id from OAuth token is: " + clientRegistrationId);
                OAuth2AuthenticatedPrincipal principal = ((OAuth2AuthenticationToken) authentication).getPrincipal();
                FacebookUserInfo facebookUserInfo = new FacebookUserInfo(principal.getAttributes());
                User userPrincipalFromDB = userService.findByEmail(facebookUserInfo.getEmail()).get();
                Authentication newCustomAuthentication = new UsernamePasswordAuthenticationToken(userPrincipalFromDB, null, userPrincipalFromDB.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newCustomAuthentication);
            }
        } else if (authentication.getClass().isAssignableFrom(UsernamePasswordAuthenticationToken.class)) {
            log.info("Form based manual authorization");
        }
    }
}

