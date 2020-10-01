package com.jm.online_store.config.security;

import com.jm.online_store.config.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoginSuccessHandler successHandler;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    @Setter
    private OAuth2UserService OAuth2UserService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login().loginPage("/login").userInfoEndpoint().userService(OAuth2UserService).and().and().authorizeRequests().antMatchers("/customer").hasRole("CUSTOMER");

        http.formLogin()
                // указываем страницу с формой логина
                .loginPage("/login")
                //указываем логику обработки при логине
                .successHandler(successHandler)
                // указываем action с формы логина
                .loginProcessingUrl("/login")
                // Указываем параметры логина и пароля с формы логина
                .usernameParameter("login_username")
                .passwordParameter("login_password")
                // даем доступ к форме логина всем
                .permitAll();

        http.logout()
                // разрешаем делать логаут всем
                .permitAll()
                // указываем URL логаута

                // указываем URL при удачном логауте
                .logoutSuccessUrl("/")
                .and()
                .csrf().disable();
        http
                // делаем страницу регистрации недоступной для авторизированных пользователей
                .authorizeRequests()
                .antMatchers("/auth-vk").permitAll()
                .antMatchers("/api/comments/**").permitAll()
                .antMatchers("/oauth/**", "/oauthTwitter/**", "/TwitterRegistrationPage/**").permitAll()
                .antMatchers("/", "/login", "/news/**", "/registration", "/css/**", "api/sharedStock","/global/**","/stocks/**").permitAll()
                .antMatchers("/api/products/productChangeMonitor").access("hasAnyRole('ROLE_MANAGER')")
                .antMatchers("/api/categories/**", "/api/products/**").permitAll()
                .antMatchers("/categories/**", "/products/**").permitAll()
                .antMatchers("/uploads/images/**").permitAll()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/js/**", "/images/**", "/static/**", "/activate/**", "/404").permitAll()
                .antMatchers("/customer/**").access("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")
                .antMatchers("/authority/**", "/api/commonSettings/**").access("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
                .antMatchers("/api/users/**").access("hasAnyRole('ROLE_ADMIN')")
                .antMatchers("/manager/**").access("hasAnyRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasAnyRole('ROLE_ADMIN')").anyRequest().authenticated()
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
