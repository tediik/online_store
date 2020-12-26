package com.jm.online_store.service.impl;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.exception.InvalidTelephoneNumberException;
import com.jm.online_store.exception.RepairOrderNotFoundException;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.repository.RepairOrderRepository;
import com.jm.online_store.service.interf.RepairOrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RepairOrderServiceImpl.class)
public class RepairOrderServiceImplTest {

    @MockBean
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private RepairOrderService repairOrderService;

    List<RepairOrder> repairOrderList;
    RepairOrder repairOrder1;
    RepairOrder repairOrder2;
    RepairOrder repairOrder3;
    RepairOrder repairOrder4;
    RepairOrder repairOrder5;
    RepairOrder repairOrder6;
    List<RepairOrder> listAccepted;
    List<RepairOrder> listDiagnostics;
    List<RepairOrder> listInWork;
    List<RepairOrder> listComplete;
    List<RepairOrder> listArchived;
    List<RepairOrder> listCanceled;

    @BeforeEach
    public void init() {
        repairOrder1 = new RepairOrder();
        repairOrder2 = new RepairOrder();
        repairOrder3 = new RepairOrder();
        repairOrder4 = new RepairOrder();
        repairOrder5 = new RepairOrder();
        repairOrder6 = new RepairOrder();

        repairOrder1.setId(1L);
        repairOrder1.setFullNameClient("Иванов Иван Иванович");
        repairOrder1.setTelephoneNumber("+7 (111) 111 11 11");
        repairOrder1.setAcceptanceDate(LocalDate.now());
        repairOrder1.setRepairOrderType(RepairOrderType.ACCEPTED);
        listAccepted = new ArrayList<>();
        listAccepted.add(repairOrder1);

        repairOrder2.setId(2L);
        repairOrder2.setFullNameClient("Зирюкин Павел Андреевич");
        repairOrder2.setTelephoneNumber("+7 (222) 222 22 22");
        repairOrder2.setAcceptanceDate(LocalDate.now());
        repairOrder2.setRepairOrderType(RepairOrderType.DIAGNOSTICS);
        listDiagnostics = new ArrayList<>();
        listDiagnostics.add(repairOrder2);

        repairOrder3.setId(3L);
        repairOrder3.setFullNameClient("Азимов Исаак Юдович");
        repairOrder3.setTelephoneNumber("+7 (333) 333 33 33");
        repairOrder3.setAcceptanceDate(LocalDate.now());
        repairOrder3.setRepairOrderType(RepairOrderType.IN_WORK);
        listInWork = new ArrayList<>();
        listInWork.add(repairOrder3);

        repairOrder4.setId(4L);
        repairOrder4.setFullNameClient("Хайнлайн Роберт Энсон");
        repairOrder4.setTelephoneNumber("+7 (444) 444 44 44");
        repairOrder4.setAcceptanceDate(LocalDate.now());
        repairOrder4.setRepairOrderType(RepairOrderType.COMPLETE);
        listComplete = new ArrayList<>();
        listComplete.add(repairOrder4);

        repairOrder5.setId(5L);
        repairOrder5.setFullNameClient("Перумов Николай Даниилович");
        repairOrder5.setTelephoneNumber("+7 (555) 555 55 55");
        repairOrder5.setAcceptanceDate(LocalDate.now());
        repairOrder5.setRepairOrderType(RepairOrderType.ARCHIVED);
        listArchived = new ArrayList<>();
        listArchived.add(repairOrder5);

        repairOrder6.setId(6L);
        repairOrder6.setFullNameClient("Лукьяненко Сергей Васильевич");
        repairOrder6.setTelephoneNumber("+7 (666) 666 66 66");
        repairOrder6.setAcceptanceDate(LocalDate.now());
        repairOrder6.setRepairOrderType(RepairOrderType.CANCELED);
        listCanceled = new ArrayList<>();
        listCanceled.add(repairOrder6);

        repairOrderList = new ArrayList<>();
        repairOrderList.add(repairOrder1);
        repairOrderList.add(repairOrder2);
        repairOrderList.add(repairOrder3);
        repairOrderList.add(repairOrder4);
        repairOrderList.add(repairOrder5);
        repairOrderList.add(repairOrder6);
    }

    @Test
    void findAllTest() {
        when(repairOrderRepository.findAll()).thenReturn(repairOrderList);
        List<RepairOrder> list = repairOrderService.findAll();
        assertEquals(6, list.size());
        verify(repairOrderRepository, times(1)).findAll();
    }

    @Test
    void getAllAcceptedTest() {
        when(repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ACCEPTED))
                .thenReturn(listAccepted);
        List<RepairOrder> listAccepted = repairOrderService.getAllAccepted();
        assertEquals(1, listAccepted.size());
        verify(repairOrderRepository, times(1))
                .findAllByRepairOrderTypeEquals(RepairOrderType.ACCEPTED);
    }

    @Test
    void getAllDiagnosticsTest() {
        when(repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.DIAGNOSTICS))
                .thenReturn(listDiagnostics);
        List<RepairOrder> listDiagnostics = repairOrderService.getAllDiagnostics();
        assertEquals(1, listDiagnostics.size());
        verify(repairOrderRepository, times(1))
                .findAllByRepairOrderTypeEquals(RepairOrderType.DIAGNOSTICS);
    }

    @Test
    void getAllIn_WorkTest() {
        when(repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.IN_WORK))
                .thenReturn(listInWork);
        List<RepairOrder> listInWork = repairOrderService.getAllIn_Work();
        assertEquals(1, listInWork.size());
        verify(repairOrderRepository, times(1))
                .findAllByRepairOrderTypeEquals(RepairOrderType.IN_WORK);
    }

    @Test
    void getAllCompleteTest() {
        when(repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.COMPLETE))
                .thenReturn(listComplete);
        List<RepairOrder> listComplete = repairOrderService.getAllComplete();
        assertEquals(1, listComplete.size());
        verify(repairOrderRepository, times(1))
                .findAllByRepairOrderTypeEquals(RepairOrderType.COMPLETE);
    }

    @Test
    void getAllArchiveTest() {
        when(repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ARCHIVED))
                .thenReturn(listArchived);
        List<RepairOrder> listArchived = repairOrderService.getAllArchive();
        assertEquals(1, listArchived.size());
        verify(repairOrderRepository, times(1))
                .findAllByRepairOrderTypeEquals(RepairOrderType.ARCHIVED);
    }

    @Test
    void getAllCanceledTest() {
        when(repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.CANCELED))
                .thenReturn(listCanceled);
        List<RepairOrder> listCanceled = repairOrderService.getAllCanceled();
        assertEquals(1, listCanceled.size());
        verify(repairOrderRepository, times(1))
                .findAllByRepairOrderTypeEquals(RepairOrderType.CANCELED);
    }

    @Test
    void saveTest() {
        when(repairOrderRepository.save(repairOrder2)).thenReturn(repairOrder2);
        repairOrderService.save(repairOrder2);
        verify(repairOrderRepository, times(1)).save(repairOrder2);
    }

    @Test
    void saveTestThrowException() {
        when(repairOrderRepository.save(repairOrder2)).thenReturn(repairOrder2);
        repairOrder2.setTelephoneNumber("+38 123 12");
        assertThrows(InvalidTelephoneNumberException.class, () -> repairOrderService.save(repairOrder2));
    }

    @Test
    void updateTest() {
        when(repairOrderRepository.save(repairOrder1)).thenReturn(repairOrder1);
        RepairOrder repairOrder = repairOrderService.update(repairOrder1);
        assertNotNull(repairOrder);
        assertEquals(repairOrder, repairOrder1);
        verify(repairOrderRepository, times(1)).save(repairOrder1);
    }

    @Test
    void updateTestThrowException() {
        when(repairOrderRepository.save(repairOrder1)).thenReturn(repairOrder1);
        repairOrder1.setTelephoneNumber("НомерТелефона");
        assertThrows(InvalidTelephoneNumberException.class, () -> repairOrderService.update(repairOrder1));
    }

    @Test
    void findByIdTest() {
        when(repairOrderRepository.findById(3L)).thenReturn(Optional.ofNullable(repairOrder3));
        RepairOrder repairOrder = repairOrderService.findById(3L);
        assertNotNull(repairOrder);
        assertEquals(repairOrder3, repairOrder);
        verify(repairOrderRepository, times(1)).findById(3L);
    }

    @Test
    void findByIdTestThrowException() {
        when(repairOrderRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RepairOrderNotFoundException.class, () -> repairOrderService.findById(3L));
    }

    @Test
    void findRepairOrderByIdAndTelephoneNumberTest() {
        when(repairOrderRepository.findByIdAndTelephoneNumber(4L, repairOrder4.getTelephoneNumber()))
                .thenReturn(Optional.ofNullable(repairOrder4));
        RepairOrder repairOrder =
                repairOrderService.findRepairOrderByIdAndTelephoneNumber(4L, repairOrder4.getTelephoneNumber());
        assertNotNull(repairOrder);
        assertEquals(repairOrder4, repairOrder);
        verify(repairOrderRepository, times(1))
                .findByIdAndTelephoneNumber(4L, repairOrder.getTelephoneNumber());
    }

    @Test
    void findRepairOrderByIdAndTelephoneNumberTestThrowException() {
        when(repairOrderRepository.findByIdAndTelephoneNumber(4L, repairOrder4.getTelephoneNumber()))
                .thenReturn(Optional.empty());
        assertThrows(RepairOrderNotFoundException.class,
                () -> repairOrderService.findRepairOrderByIdAndTelephoneNumber(4L, repairOrder4.getTelephoneNumber()));
    }

    @Test
    void existsByIdTest() {
        when(repairOrderRepository.existsById(5L)).thenReturn(true);
        boolean checkExist = repairOrderService.existsById(5L);
        assertTrue(checkExist);
        verify(repairOrderRepository, times(1)).existsById(5L);
    }

    @Test
    void deleteByIdTest() {
        doNothing().when(repairOrderRepository).deleteById(6L);
        repairOrderService.deleteById(6L);
        verify(repairOrderRepository, times(1)).deleteById(6L);
    }

    @Test
    void findAllRepairOrderTypeTest() {
        List<RepairOrderType> repairOrderTypeList = repairOrderService.findAllRepairOrderType();
        assertEquals(7, repairOrderTypeList.size());
    }

    @AfterEach
    public void after() {
        repairOrderList.clear();
        repairOrder1 = null;
        repairOrder2 = null;
        repairOrder3 = null;
        repairOrder4 = null;
        listAccepted.clear();
        listDiagnostics.clear();
        listInWork.clear();
        listComplete.clear();
        listArchived.clear();
        listCanceled.clear();
    }
}