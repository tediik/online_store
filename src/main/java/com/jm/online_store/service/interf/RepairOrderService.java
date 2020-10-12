package com.jm.online_store.service.interf;

import com.jm.online_store.model.RepairOrder;

import java.util.List;

public interface RepairOrderService {
    List<RepairOrder> findAll();
    List<RepairOrder> getAllAccepted();
    List<RepairOrder> getAllDiagnostics();
    List<RepairOrder> getAllIn_Work();
    List<RepairOrder> getAllComplete();
    List<RepairOrder> getAllArchive();

    void save(RepairOrder repairOrder);
    RepairOrder findById(long id);
    boolean existsById(Long id);
    RepairOrder update(RepairOrder repairOrder);
    void deleteById(Long id);
}