package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность проекта "Список избранных товаров"
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties("user")
@ApiModel(description =  "Сущность FavouritesGroup - список избранных товаров")
public class FavouritesGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Customer customer;

    @ManyToMany
    @JoinTable(
            name = "favourites_group_product",
            joinColumns = @JoinColumn(name = "favourites_group_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new HashSet<>();

    public FavouritesGroup(String name, Customer customer, Set<Product> products) {
        this.name = name;
        this.customer = customer;
        this.products = products;
    }
}
