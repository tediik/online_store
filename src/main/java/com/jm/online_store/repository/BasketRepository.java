package com.jm.online_store.repository;

import com.jm.online_store.model.SubBasket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * интерфейс BasketRepository основа для слоя сервиса для сущности SubBasket.
 */
@Repository
public interface BasketRepository extends JpaRepository<SubBasket, Long> {

}
