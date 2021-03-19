package com.jm.online_store.repository;

import com.jm.online_store.model.Order;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAll();

    List<Order> findAllByCustomerId(Long customerId);

    List<Order> findAllByCustomerIdAndStatus(Long customerId , Order.Status status);

    Optional<Order> findById(Long id);

    List<Order> findAllByStatusEqualsAndDateTimeBetween(Order.@NonNull Status status, @NonNull LocalDateTime dateTime, @NonNull LocalDateTime dateTime2);
}
