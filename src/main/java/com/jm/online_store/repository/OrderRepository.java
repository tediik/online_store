package com.jm.online_store.repository;

import com.jm.online_store.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAll();

    List<Order> findByUserId(Long id);

    Optional<Order> findById(Long id);

//    List<Order> findAllByStatus(String status);

    List<Order> findAllByStatusEquals(String status);
}
