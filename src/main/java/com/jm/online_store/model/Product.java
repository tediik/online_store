package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModel;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
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
@ApiModel(description =  "Сущность Product, связана с ProductInOrder, Comment и Review")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;
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

    /**
     * Строковое описание товара
     */
    @Column(name = "description")
    private String description;
    @NonNull
    private String productType; //Что это за поле?
    @NonNull
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories categories;

    /**
     * поле для хранения адресов картинок для товара
     */
    @ElementCollection
    @CollectionTable(name = "product_picture_names")
    private List<String> productPictureNames;

    @Transient
    private List<String> productPictureShortNames;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    private List<ProductInOrder> productInOrders;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Review> reviews;

    /**
     * поле для возможности отслеживания изменения цены на Product.
     * при изменении цены добавлять элемент данной коллекции.
     */
    @ElementCollection
    @CollectionTable(name = "product_price_mapping",
            joinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "price_dateChange")
    @Column(name = "price")
    private Map<LocalDateTime, Double> changePriceHistory = new LinkedHashMap<>();

    /**
     * поле для хранения почтовых адресов для рассылки информации об уменьшения цены на товар
     */
    @ElementCollection
    @CollectionTable(name = "product_subscribers_mails",
            joinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")})
    @Column(name = "email")
    private Set<String> priceChangeSubscribers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<ProductCharacteristic> productCharacteristics;

    public Product(Long id, @NonNull String product, @NonNull Double price, @NonNull int amount, @NonNull Double rating, @NonNull String productType) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.amount = amount;
        this.rating = rating;
        this.productType = productType;
    }

    public Product(Long id, @NonNull String product, @NonNull Double price, @NonNull int amount, @NonNull Double rating, @NonNull String productType, @NonNull List<String> productPictureNames) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.amount = amount;
        this.rating = rating;
        this.productType = productType;
        this.productPictureNames = productPictureNames;
    }



    public Product(@NonNull String product, @NonNull Double price, @NonNull int amount, @NonNull Double rating, @NonNull String productType) {
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

    public Product(String product, double price, int amount, double rating, String productType, String description) {
        this.product = product;
        this.price = price;
        this.amount = amount;
        this.rating = rating;
        this.productType = productType;
        this.description = description;
    }

    public @NonNull boolean getDeleteStatus() {
        return this.deleted;
    }
}
