package com.jm.online_store.service.impl;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.exception.RepairOrderNotFoundException;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.repository.RepairOrderRepository;
import com.jm.online_store.service.interf.RepairOrderService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Сервисный класс, имплементация интерфейса {@link RepairOrderService}
 * Содержит бизнес логику, использует методы репозитория {@link RepairOrderRepository}
 */
@AllArgsConstructor
@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;

    @Override
    public List<RepairOrder> findAll() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAll();
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    @Override
    public List<RepairOrder> getAllAccepted() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ACCEPTED);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    @Override
    public List<RepairOrder> getAllDiagnostics() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.DIAGNOSTICS);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    @Override
    public List<RepairOrder> getAllIn_Work() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.IN_WORK);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    @Override
    public List<RepairOrder> getAllComplete() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.COMPLETE);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    @Override
    public List<RepairOrder> getAllArchive() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ARCHIVED);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    @Override
    public void save(RepairOrder repairOrder) {
        repairOrder.setRepairOrderType(RepairOrderType.ACCEPTED);
        repairOrder.setAcceptanceDate(LocalDate.now());
        if (!ValidationUtils.isValidTelephoneNumber(repairOrder.getTelephoneNumber())) {
            throw new IllegalArgumentException();
        }
        repairOrderRepository.save(repairOrder);
    }

    @Override
    public RepairOrder update(RepairOrder repairOrder) {
        repairOrder.setModifiedDate(LocalDate.now());
        if (!ValidationUtils.isValidTelephoneNumber(repairOrder.getTelephoneNumber())) {
            throw new IllegalArgumentException();
        }
        return repairOrderRepository.save(repairOrder);
    }

    @Override
    public RepairOrder findById(long id) {
        return repairOrderRepository.findById(id).orElseThrow(RepairOrderNotFoundException::new);
    }

    @Override
    public RepairOrder findRepairOrderByIdAndTelephoneNumber(Long id, String telephoneNumber) {
        return repairOrderRepository.findByIdAndTelephoneNumber(id, telephoneNumber).orElseThrow(RepairOrderNotFoundException::new);
    }

    @Override
    public boolean existsById(Long id) {
        return repairOrderRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        repairOrderRepository.deleteById(id);
    }

    @Override
    public List<RepairOrderType> findAllRepairOrderType() {
        List<RepairOrderType> orderTypeList = new ArrayList<>();
        Collections.addAll(orderTypeList, RepairOrderType.values());
        return orderTypeList;
    }
}
