package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jm.online_store.enums.ConfirmReceiveEmail;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.model.SharedNews;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Spring Security wrapper for class {@link User}.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

public class JwtUser implements UserDetails {

//    private Long id;
//    private String email;
//    private String password;
//    private boolean isAccountNonBlockedStatus = true;
//    private String passwordConfirm;
//    private String firstName;
//    private String lastName;
//    private String phoneNumber;
//    private User.Gender userGender;
//    private LocalDate birthdayDate;
//    private LocalDate registerDate;
//    private String profilePicture = "";
//    private ConfirmReceiveEmail confirmReceiveEmail = ConfirmReceiveEmail.NO_ACTIONS;
//    private Set<Role> roles;
//    private Set<Product> favouritesGoods = new HashSet<>();
//    private Set<FavouritesGroup> favouritesGroups = new HashSet<>();
//    private Set<Address> userAddresses = new HashSet<>();
//    private List<SubBasket> userBasket = new ArrayList<>();
//    private Set<Order> orders;
//    private Set<SharedStock> sharedStocks;
//    private Set<SharedNews> sharedNews;
//    private Set<SentStock> sentStocks;
//    private Set<Feedback> feedbacks;

    private final Long id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final boolean enabled;
//    private final Date lastPasswordResetDate;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUser(
            Long id,
            String username,
            String firstName,
            String lastName,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            boolean enabled
//            Date lastPasswordResetDate
    ) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
//        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getFirstname() {
        return firstName;
    }

    public String getLastname() {
        return lastName;
    }


    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

//    @JsonIgnore
//    public Date getLastPasswordResetDate() {
//        return lastPasswordResetDate;
//    }
}
