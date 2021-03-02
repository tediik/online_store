package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jm.online_store.enums.ConfirmReceiveEmail;
import io.swagger.annotations.ApiModel;
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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(description =  "Сущность User, расширяет UserDetails, связана с Role, Product, " +
        "FavouritesGroup и Address")
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    @Email
    @NotBlank
    private String email;

    @Column(name = "password")
    @NotBlank
    private String password;

    private Boolean isAccountNonBlockedStatus = true;

    private Boolean isAccountNonExpiredStatus = true;

    @Transient
    @NotBlank
    private String passwordConfirm;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender userGender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdayDate;

    private LocalDate registerDate;

    private String profilePicture = "";

    /**
     * Согласие пользователя на рассылки (об изменении цен)
     */
    @Column
    @Enumerated(EnumType.STRING)
    private ConfirmReceiveEmail confirmReceiveEmail = ConfirmReceiveEmail.NO_ACTIONS;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "user_product",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> favouritesGoods = new HashSet<>();

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<FavouritesGroup> favouritesGroups = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_adresses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private Set<Address> userAddresses = new HashSet<>();

    /**
     * "Корзина клиента" состоит из подкорзин "SubBasket", состоящих в свою очередь
     * из сущности "Product" и количества данного "Product" в "SubBasket".
     * Данная схема необходима, чтобы можно было хранить необходимое количество товара
     * для заказа пользователя и сам товар, как экземпляр класса "Product".
     * Для оформления заказа, необходимо пройти по всем "SubBasket" и получить из сущности "Product",
     * который находится в "SubBasket" актуальную цену, из объекта "SubBasket" получить количество товара "Product".
     * Для добавления товара в корзину, необходимо пройти по всем "SubBasket" и проверить на наличие данного "Product"
     * в корзине. При наличии совпадений, необходимо проверить количество (наличие) данного "Product" в БД
     * и увеличить на "1" в данном "SubBasket".
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_basket",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "basket_id"))
    private List<SubBasket> userBasket = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<Order> orders;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference(value = "user-sharedStock")
    private Set<SharedStock> sharedStocks;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference(value = "user-sharedNews")
    private Set<SharedNews> sharedNews;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference(value = "user-sentStock")
    private Set<SentStock> sentStocks;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonIgnore
    private Set<Feedback> feedbacks;

    public User() {
        registerDate = LocalDate.now();
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        registerDate = LocalDate.now();
    }

    public User(String email, String password, String firstName, String lastName, Set<Role> roleSet) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roleSet;
    }

    public User(String password, String firstName, String lastName, Set<Role> roleSet) {
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roleSet;
    }

    /**
     * Конструктор для поиска подписчиков из CustomerRepository (метод findSubscriberByEmail())
     * @param id - поле id
     * @param email - поле email
     */
    public User(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpiredStatus;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonBlockedStatus;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum Gender {
        MAN,
        WOMAN
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password);
    }
}
