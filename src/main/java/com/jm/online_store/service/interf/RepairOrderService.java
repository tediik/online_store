package com.jm.online_store.service.interf;

import com.itextpdf.text.DocumentException;
import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.RepairOrder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface RepairOrderService {

    List<RepairOrder> findAll();

    List<RepairOrder> getAllAccepted();

    List<RepairOrder> getAllDiagnostics();

    List<RepairOrder> getAllIn_Work();

    List<RepairOrder> getAllComplete();

    List<RepairOrder> getAllArchive();

    List<RepairOrder> getAllCanceled();

    void save(RepairOrder repairOrder);

    RepairOrder findById(Long id);

    boolean existsById(Long id);

    RepairOrder update(RepairOrder repairOrder);

    void deleteById(Long id);

    List<RepairOrderType> findAllRepairOrderType();

    RepairOrder findRepairOrderByIdAndTelephoneNumber(Long id, String telephoneNumber);

    void createPdfWorkOrder(RepairOrder repairOrder, HttpServletResponse response) throws IOException, DocumentException;
}