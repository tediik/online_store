package com.jm.online_store.repository;

import com.jm.online_store.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * интерфейс BasketRepository основа для слоя сервиса для сущности Basket.
 */
@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
}
