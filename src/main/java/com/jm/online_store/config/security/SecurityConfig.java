package com.jm.online_store.config.security;

import com.jm.online_store.config.facebook.FacebookOAuth2UserService;
import com.jm.online_store.config.handler.LoginSuccessHandler;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginSuccessHandler successHandler;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private FacebookOAuth2UserService facebookOAuth2UserService;


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2Login()
//                .userInfoEndpoint().userService(facebookOAuth2UserService);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()


        // указываем страницу с формой логина
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
                .logoutSuccessUrl("/login?logout")
                .and()
                .csrf().disable();
        //выклчаем кроссдоменную секьюрность (на этапе обучения неважна)


        http
                // делаем страницу регистрации недоступной для авторизированных пользователей
                .authorizeRequests()

                //страницы аутентификаци доступна всем
                .antMatchers("/login").permitAll()

                .and()

                .authorizeRequests()

                .and()

                .oauth2Login().userInfoEndpoint().userService(facebookOAuth2UserService).and()

                .and()

                .authorizeRequests()




//                .antMatchers("/user").access("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")

                .antMatchers("/api/users").access("hasAnyRole('ROLE_ADMIN')")

                .antMatchers("/manager").access("hasAnyRole('ROLE_MANAGER')")

                .antMatchers("/admin/**").access("hasAnyRole('ROLE_ADMIN')").anyRequest().authenticated()

                .and()

                .exceptionHandling().accessDeniedPage("/denied");

    }




}
