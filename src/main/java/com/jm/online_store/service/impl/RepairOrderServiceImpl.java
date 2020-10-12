package com.jm.online_store.service.impl;

import com.jm.online_store.exception.RepairOrderNotFoundException;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.repository.RepairOrderRepository;
import com.jm.online_store.service.interf.RepairOrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис класс, имплементация интерфейса {@link RepairOrderService}
 * Содержит бизнес логику, использует методы репозитория {@link RepairOrderRepository}
 */
@AllArgsConstructor
@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;


    @Override
    public List<RepairOrder> findAll() {
        return repairOrderRepository.findAll();
    }

    @Override
    public List<RepairOrder> getAllAccepted() {
        return null;
    }

    @Override
    public List<RepairOrder> getAllDiagnostics() {
        return null;
    }

    @Override
    public List<RepairOrder> getAllIn_Work() {
        return null;
    }

    @Override
    public List<RepairOrder> getAllComplete() {
        return null;
    }

    @Override
    public List<RepairOrder> getAllArchive() {
        return null;
    }

    @Override
    public void save(RepairOrder repairOrder) {
        repairOrderRepository.save(repairOrder);
    }

    @Override
    public RepairOrder findById(long id) {
        return repairOrderRepository.findById(id).orElseThrow(RepairOrderNotFoundException::new);
    }

    @Override
    public boolean existsById(Long id) {
        return repairOrderRepository.existsById(id);
    }

    @Override
    public RepairOrder update(RepairOrder repairOrder) {
        return repairOrderRepository.saveAndFlush(repairOrder);
    }

    @Override
    public void deleteById(Long id) {
        repairOrderRepository.deleteById(id);
    }
}
