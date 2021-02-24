package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jm.online_store.enums.DayOfWeekForStockSend;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность расширяющая {@link User}.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ApiModel(description =  "Сущность Customer расширяющая User")
public class Customer extends User {

    /**
     * Отсчет времени удаления клиента(+30 дней).
     * По уолчанию значение null.Когда клиент , в своем кабинете нажимает кнопку
     * "удалить профиль " мы присваем этому полю текущую дату .
     */
    @Column(name = "anchorForDelete")
    private LocalDateTime anchorForDelete;

    /**
     * День недели для получения рассылок.
     */
    @Column(name = "day_of_week_for_stock_send")
    @Enumerated(EnumType.STRING)
    private DayOfWeekForStockSend dayOfWeekForStockSend;

    @ManyToMany
    @JoinTable(
            name = "customer_product",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> favouritesGoods = new HashSet<>();


    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<FavouritesGroup> favouritesGroups = new HashSet<>();

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
            name = "customer_basket",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "basket_id"))
    private List<SubBasket> userBasket = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Set<Order> orders;


    @OneToMany(mappedBy = "customer", orphanRemoval = true)
    @JsonManagedReference(value = "customer-sharedStock")
    private Set<SharedStock> sharedStocks;


    @OneToMany(mappedBy = "customer", orphanRemoval = true)
    @JsonManagedReference(value = "customer-sentStock")
    private Set<SentStock> sentStocks;

    public Customer(String email, String password) {
        super(email, password);
    }

    public Customer(@Email @NotBlank String email, DayOfWeekForStockSend dayOfWeekForStockSend, String password) {
        super(email, password);
        this.dayOfWeekForStockSend = dayOfWeekForStockSend;
    }

    /**
     * Конструктор для поиска подписчиков из CustomerRepository (метод findSubscriberByEmail())
     *
     * @param id                    - поле id
     * @param email                 - поле email
     * @param dayOfWeekForStockSend - поле dayOfWeekForStockSend
     */
    public Customer(Long id, String email, DayOfWeekForStockSend dayOfWeekForStockSend) {
        super(id, email);
        this.dayOfWeekForStockSend = dayOfWeekForStockSend;
    }
}
