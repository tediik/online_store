package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * класс корзины (миникорзина из одного продукта).
 * "Корзина клиента" состоит из подкорзин "SubBasket", сотоящих в свою очередь
 *из сущности "Product" и количества данного "Product" в "SubBasket".
 *данная схема необходима, чтобы можно было хранить необходимое количество товара
 * для заказа пользователя и сам товар как экземпляр класса "Product".
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubBasket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Product product;
    private int count;
}
