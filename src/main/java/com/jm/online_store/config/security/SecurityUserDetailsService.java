package com.jm.online_store.config.security;

import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Если такого пользователся не существует - бросаем UsernameNotFoundException
     * Если Юзер - кастомер проверяем:
     * 1) если заблокирован, но срок, данный ему на восстановление не прошел - бросаем LockedException
     * и выводим сообщение с ссылкой на восстановление.
     * 2) если заблокирован и срок данный ему на восстановление прошел - бросаем CredentialsExpiredException,
     * удаляем и сообщаем ему об этом.
     * Экспшены обрабатываются в классе {@linkplain LoginFailureHandler}, дальнейшую логику смотри там.
     *
     * @param email - наш логин
     * @return user for security
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User Not Found with -> username or email: " + email));

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), mapRolesToAuthorities(user.getRoles()));
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getAuthority())).collect(Collectors.toList());
    }

}
