package com.jm.online_store.config.security;

import com.jm.online_store.config.facebook.FacebookOAuth2UserService;
import com.jm.online_store.config.handler.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


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

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**","/assets/**","/fonts/**","/dis/**","/vendor1/**");
    }


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
                .antMatchers("/login", "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**","/assets/**","/fonts/**","/dis/**","/vendor1/**")




                .permitAll()

                .and()

                .authorizeRequests()

                .and()

                .oauth2Login().userInfoEndpoint().userService(facebookOAuth2UserService).and()

                .and()

                .authorizeRequests()

                .antMatchers("/api/users").access("hasAnyRole('ROLE_ADMIN')")

                .antMatchers("/manager").access("hasAnyRole('ROLE_MANAGER')")

                .antMatchers("/admin/**").access("hasAnyRole('ROLE_ADMIN')").anyRequest().authenticated()

                .and()

                .exceptionHandling().accessDeniedPage("/denied");

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
