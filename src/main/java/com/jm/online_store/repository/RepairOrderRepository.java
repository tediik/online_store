package com.jm.online_store.repository;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.RepairOrder;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    List<RepairOrder> findAllByRepairOrderTypeEquals(RepairOrderType repairOrderType);

    Optional<RepairOrder> findByIdAndTelephoneNumber(Long id, @NonNull String telephoneNumber);

    boolean existsByOrderNumber(String orderNumber);

    RepairOrder findByOrderNumber(String orderNumber);
}
