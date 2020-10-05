package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private long id;
    @NonNull
    @Column(name = "product", nullable = false)
    private String product;
    @NonNull
    @Column(name = "price", nullable = false)
    private Double price;
    @NonNull
    private Integer amount;
    @NonNull
    private Double rating;
    @OneToOne(cascade = CascadeType.ALL)
    private Description descriptions;
    @NonNull
    private String productType;
    @NonNull
    private boolean deleted;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    private List<ProductInOrder> productInOrders;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    /**
     *поле для возможности отслеживания изменения цены на Product.
     * при изменении цены добавлять элемент данной коллекции.
     */
    @ElementCollection
    @CollectionTable(name = "product_price_mapping",
            joinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "price_dateChange")
    @Column(name = "price")
    private Map<LocalDateTime, Double> changePriceHistory = new LinkedHashMap<>();

    /**
     *поле для хранения почтовых адресов для рассылки информации об уменьшения цены на товар
     */
    @ElementCollection
    @CollectionTable(name = "product_subscribers_mails",
            joinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")})
    @Column(name = "email")
    private Set<String> priceChangeSubscribers = new HashSet<>();


    public Product(@NonNull String product, @NonNull Double price, @NonNull Integer amount, @NonNull Double rating, @NonNull String productType) {
        this.product = product;
        this.price = price;
        this.amount = amount;
        this.rating = rating;
        this.productType = productType;
    }

    public Product(@NonNull String product, @NonNull Double price, @NonNull int amount, @NonNull Double rating) {
        this.product = product;
        this.price = price;
        this.amount = amount;
        this.rating = rating;
    }

    public Product(String product, double price, int amount) {
        this.product = product;
        this.price = price;
        this.amount = amount;
    }

    public @NonNull boolean getDeleteStatus(){
        return this.deleted;
    }
}
