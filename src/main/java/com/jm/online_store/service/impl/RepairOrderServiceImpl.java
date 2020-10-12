package com.jm.online_store.service.impl;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.exception.RepairOrderNotFoundException;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.repository.RepairOrderRepository;
import com.jm.online_store.service.interf.RepairOrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ACCEPTED);
    }

    @Override
    public List<RepairOrder> getAllDiagnostics() {
        return repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.DIAGNOSTICS);
    }

    @Override
    public List<RepairOrder> getAllIn_Work() {
        return repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.IN_WORK);
    }

    @Override
    public List<RepairOrder> getAllComplete() {
        return repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.COMPLETE);
    }

    @Override
    public List<RepairOrder> getAllArchive() {
        return repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ARCHIVED);
    }

    @Override
    public void save(RepairOrder repairOrder) {
        repairOrder.setRepairOrderType(RepairOrderType.ACCEPTED);
        repairOrder.setAcceptanceDate(LocalDate.now());
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
