//package com.jm.online_store.config.security;
//
//import com.jm.online_store.config.security.jwt.JwtUserFactory;
//import com.jm.online_store.model.JwtUser;
//import com.jm.online_store.model.User;
//import com.jm.online_store.repository.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
///**
// * Implementation of {@link UserDetailsService} interface for {@link JwtUser}.
// *
// * @author Eugene Suleimanov
// * @version 1.0
// */
//
//@Service
//@Slf4j
//public class JwtUserDetailsService implements UserDetailsService {
//
//        private final UserRepository userRepository;
//
//    @Autowired
//    public JwtUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByFirstName(username);
//
//        if (user == null) {
//            throw new UsernameNotFoundException("User with username: " + username + " not found");
//        }
//
//        JwtUser jwtUser = JwtUserFactory.create(user);
//        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);
//        return jwtUser;
////        return user;
//    }
//}
