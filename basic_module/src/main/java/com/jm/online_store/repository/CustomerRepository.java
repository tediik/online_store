package com.jm.online_store.repository;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select customer from Customer customer where customer.anchorForDelete < :nowMinusThirtyDays")
    List<Customer> findAllByAnchorForDeleteThirtyDays(
            @Param("nowMinusThirtyDays") LocalDateTime time);

    @Override
    void deleteAll(Iterable<? extends Customer> iterable);

    Optional<Customer> findByEmail(String username);

    List<Customer> findByDayOfWeekForStockSend(DayOfWeekForStockSend dayOfWeekForStockSend);
}
