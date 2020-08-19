package com.jm.online_store.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * основная сущность проекта - USER.
 */
@Entity
@Getter
@Setter
public class User implements UserDetails {
    /**
     * уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * адрес электронной почты пользователя.
     */
    @Column(name = "email")
    @Email
    @NotBlank
    private String email;
    /**
     * пароль пользователя.
     */
    @Column(name = "password")
    @NotBlank
    private String password;
    /**
     * ?????
     */
    @Transient
    @NotBlank
    private String passwordConfirm;
    /**
     * имя пользователя.
     */
    private String firstName;
    /**
     * фамилия ползователя.
     */
    private String lastName;
    /**
     * пол пользователя(мужской, женский).
     */
    @Enumerated(EnumType.STRING)
    private Gender userGender;
    /**
     * дата дня рождения пользователя.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date birthdayDate;
    /**
     * дата регистрации пользователя.
     */
    @Temporal(TemporalType.DATE)
    private Date registerDate;
    /**
     * роли пользователя - основа для разделения полномочий.
     */
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    /**
     * список избранных товаров пользователя.
     * подгружается "ленивым" способом.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_product",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> favouritesGoods;

    /**
     * конструктор по умолчанию.
     */
    public User() {
        registerDate = new Date();
    }

    /**
     * конструктор с основными полями пользвателя.
     * @param email адрес электронной почты.
     * @param password пароль
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        registerDate = new Date();
    }

    /**
     * конструктор.
     * @param email адрес электронной почты.
     * @param password пароль
     * @param firstName имя
     * @param lastName фамилия
     * @param roleSet список ролей
     */
    public User(String email, String password, String firstName, String lastName, Set<Role> roleSet) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roleSet;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * ????
     */
    private enum Gender {
        MAN,
        WOMAN
    }
}