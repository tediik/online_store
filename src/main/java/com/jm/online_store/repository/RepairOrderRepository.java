package com.jm.online_store.repository;

import com.jm.online_store.model.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    Optional<List<RepairOrder>> findAllRepairOrders();
}
