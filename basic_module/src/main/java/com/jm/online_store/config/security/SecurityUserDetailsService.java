package com.jm.online_store.config.security;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Если такго пользователся не существует throws UsernameNotFoundException
     * Если Юзер - кастомер проверяем :
     * 1) если заблокмрован но срок данный ему на восстановление не прошел throw new LockedException
     * выводим сообщение с сылкой на восстановление
     * 2) если заблокирован и срок данный ему на восстановление прошел throw new CredentialsExpiredException
     * удаляем его и сообщаем ему об этом
     * эти экспшены обрабатываются в классе LoginFailureHandler, дальнейшую логику смотри там
     *
     * @param email email
     * @return user for security
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User Not Found with -> username or email: " + email));
        if (user.getClass().equals(Customer.class)) {
            Customer customer = (Customer) user;
            if (!customer.isAccountNonLocked() && customer.getAnchorForDelete().isAfter(LocalDateTime.now().minusDays(30))) {
                log.warn("Пользователь с почтой " + user.getEmail() + " заблокирован");
                throw new LockedException("Аккаунт заблокирован !!!");
            } else if (!customer.isAccountNonLocked() &&
                    !customer.getAnchorForDelete().isAfter(LocalDateTime.now().minusDays(30))) {
                userRepository.deleteById(customer.getId());
                log.info("Говорим пользователю " + customer.getEmail() + " что его аккаунт был удален, т.к. 30 дней истекло");
                throw new CredentialsExpiredException("Аккаунт был удален");
            }
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));

    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getAuthority())).collect(Collectors.toList());
    }
}