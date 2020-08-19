package com.jm.online_store.config.security;

import com.jm.online_store.config.security.oauth2userinfo.OAuth2UserInfo;
import com.jm.online_store.config.security.oauth2userinfo.OAuth2UserInfoFactory;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final Log log = LogFactory.getLog(OAuth2UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public OAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oath2User = super.loadUser(userRequest);
        return buildPrincipal(oath2User, userRequest);
    }

    /**
     * Builds the security principal from the given userReqest.
     * Registers the user if not already reqistered
     */
    public OAuth2User buildPrincipal(OAuth2User oauth2User, OAuth2UserRequest oAuth2UserRequest) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oauth2User.getAttributes());
        String email = oAuth2UserInfo.getEmail();
        String fullName = oAuth2UserInfo.getName();
        String lastName = fullName.split(" ")[fullName.split(" ").length - 1];
        String firstName = fullName.substring(0, fullName.length() - lastName.length());
        Set<Role> roleSet = new HashSet<>();
        Optional<Role> fbDefaultRole = roleRepository.findByName("ROLE_CUSTOMER");
        roleSet.add(fbDefaultRole.get());
        User newUser = new User(email, passwordEncoder.encode("1"), firstName, lastName, roleSet);
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // register a new user
            userRepository.save(newUser);
            return newUser;
        });
        return oauth2User;
    }
}