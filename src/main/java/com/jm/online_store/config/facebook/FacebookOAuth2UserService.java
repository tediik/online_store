package com.jm.online_store.config.facebook;

import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class FacebookOAuth2UserService extends DefaultOAuth2UserService {


    private static final Log log = LogFactory.getLog(FacebookOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;




    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oath2User = super.loadUser(userRequest);
        return buildPrincipal(oath2User);
    }

    /**
     * Builds the security principal from the given userReqest.
     * Registers the user if not already reqistered
     */
    public OAuth2User buildPrincipal(OAuth2User oath2User) {
        FacebookUserInfo facebookUserInfo = new FacebookUserInfo(oath2User.getAttributes());
        String email = facebookUserInfo.getEmail();

        User user = userRepository.findByEmail(email).orElseGet(()  -> {

            Set<Role> roleSet = new HashSet<>();
            Optional<Role> fbDefaultRole = roleRepository.findByName("ROLE_CUSTOMER");
            roleSet.add(fbDefaultRole.get());

            // register a new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode("1"));
            newUser.setRoles(roleSet);
            userRepository.save(newUser);
            return newUser;
        });


        return oath2User;
    }
}

