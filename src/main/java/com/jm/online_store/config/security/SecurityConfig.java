package com.jm.online_store.config.security;

import com.jm.online_store.config.filters.CorsFilter;
import com.jm.online_store.config.handler.LoginFailureHandler;
import com.jm.online_store.config.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableGlobalMethodSecurity( prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoginSuccessHandler successHandler;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final LoginFailureHandler loginFailureHandler;

    @Autowired
    @Setter
    private OAuth2UserService OAuth2UserService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(securityUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);

        http.oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(OAuth2UserService)
                .and().and()
                .authorizeRequests()
                .antMatchers("/customer").hasRole("CUSTOMER");

        http.formLogin()
                .loginPage("/login") // указываем страницу с формой логина
                .successHandler(successHandler) //указываем логику обработки при логине
                .loginProcessingUrl("/login")// указываем action с формы логина
                .usernameParameter("login_username") // Указываем параметры логина и пароля с формы логина
                .passwordParameter("login_password")
                .failureHandler(loginFailureHandler)
                .permitAll(); // даем доступ к форме логина всем

        http.logout().permitAll() // разрешаем делать логаут всем
                .logoutSuccessUrl("/") // указываем URL при удачном логауте
                .and().csrf().disable();

        http.authorizeRequests()
                .antMatchers("/api/feedback/").access("hasAnyRole('ROLE_CUSTOMER', 'ROLE_MANAGER')")
                .antMatchers("/api/products/productChangeMonitor").access("hasRole('ROLE_MANAGER')")
                .antMatchers("/customer/**").access("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")
                .antMatchers("/service/**").access("hasAnyRole('ROLE_SERVICE','ROLE_ADMIN')")
                .antMatchers("/authority/**", "/api/commonSettings/**")
                .access("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_SERVICE', 'ROLE_MODERATOR')")
                .antMatchers("/admin/**", "/api/users/**").access("hasAnyRole('ROLE_ADMIN')")
                .antMatchers("/moderator/**").access("hasRole('ROLE_MODERATOR')")
                .antMatchers("/**", "/api/allUser/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/denied")
                .and()
                .rememberMe();
    }

    @Bean
    public AuthenticationManager authManager() throws Exception {
        return this.authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
