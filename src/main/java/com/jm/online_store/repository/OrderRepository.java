package com.jm.online_store.repository;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.dto.SalesReportDto;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAll();

    List<Order> findAllByUserId(Long userId);

    List<Order> findAllByUserIdAndStatus(Long userId, Order.Status status);

    Optional<Order> findById(Long id);

    List<Order> findAllByStatusEqualsAndDateTimeBetween(Order.@NonNull Status status, @NonNull LocalDateTime dateTime, @NonNull LocalDateTime dateTime2);

//    @Query("SELECT new com.jm.online_store.model.dto.SalesReportDto(o.id, o.user.email, o.user.firstName, o.user.lastName, o.dateTime, o.amount, o.productInOrders, o.orderPrice) FROM Order o " +
//            "WHERE o.status = Order.Status.COMPLETED " +
//            "AND o.dateTime >= :startPeriod " +
//            "AND o.dateTime <= :endPeriod")
//    List<SalesReportDto> findAllSalesForReport(@Param("startPeriod") LocalDate startPeriod,
//                                               @Param("endPeriod") LocalDate endPeriod);
}
