package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * сущность для связи продукта и заказа и хранения кол-ва конкретного продукта в заказе
 */
@Entity
@NoArgsConstructor
@Data
@RequiredArgsConstructor
@Table(name = "order_product")
@ApiModel(description =  "Сущность ProductInOrder для связи продукта и заказа и хранения кол-ва конкретного продукта в заказе" +
        ", связана с Order")
public class ProductInOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    private Product product;

    @NonNull
    @ManyToOne
    //TODO @JsonManagedReference пока не удаляю, возможно придется менять обратно
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private Order order;

    @NonNull
    private int amount;

    @NonNull
    private double buyPrice;

    public ProductInOrder(Product product, int count) {
        this.product = product;
        this.amount = count;
    }
}
