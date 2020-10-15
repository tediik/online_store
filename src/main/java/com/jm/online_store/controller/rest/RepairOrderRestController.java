package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.service.interf.RepairOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер для управления заявками на ремонт
 */
@AllArgsConstructor
@RestController
public class RepairOrderRestController {

    private final RepairOrderService repairOrderService;

    /**
     * Метод возвращает заказ по id
     *
     * @param id идентификатор RepairOrder
     * @return RepairOrder заказ на ремонт
     */
    @GetMapping("/service/{id}")
    public ResponseEntity<RepairOrder> getRepairOrderById(@PathVariable Long id) {
        RepairOrder repairOrder = repairOrderService.findById(id);
        return ResponseEntity.ok(repairOrder);
    }

    /**
     * Метод сохраняет заявку на ремонт
     *
     * @param repairOrder сущность для сохранения в базу данных
     * @return ResponseEntity<String> возвращает статус запроса
     */
    @PostMapping("/service/addRepairOrder")
    public ResponseEntity<String> addRepairOrder(@RequestBody RepairOrder repairOrder) {
        repairOrderService.save(repairOrder);
        return ResponseEntity.ok().build();
    }

/*    @PostMapping("/api/checkStatus")
    public ResponseEntity<RepairOrder> addRepairOrder(@RequestParam(name = "idCheck", required = false) Long idCheck,
                                                      @RequestParam(name = "telCheck", required = false) String telCheck) {
        RepairOrder repairOrder = repairOrderService.findById(idCheck);
        return ResponseEntity.ok(repairOrder);
    }*/

    @PostMapping("/api/checkStatus")
    public ResponseEntity<RepairOrder> getCurrentRepairOrder(@RequestBody RepairOrder repairOrder) {
        System.out.println(repairOrder);
        RepairOrder repairOrderCheck = repairOrderService.findById(repairOrder.getId());
        return ResponseEntity.ok(repairOrderCheck);
    }

    /**
     * Метод удаляет заказ по id
     *
     * @param id идентификатор RepairOrder
     * @return ResponseEntity<String> статус запроса
     */
    @DeleteMapping("/service/{id}")
    public ResponseEntity<String> deleteRepairOrderById(@PathVariable Long id) {
        repairOrderService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/service/updateRepairOrder")
    public ResponseEntity<RepairOrder> updateRepairOrder(@RequestBody RepairOrder repairOrder) {
        repairOrderService.update(repairOrder);
        return ResponseEntity.ok(repairOrder);
    }

    /**
     * Метод возвращает все заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getAllRepairOrder")
    public ResponseEntity<List<RepairOrder>> getAllRepairOrder(){
        List<RepairOrder> repairOrderList = repairOrderService.findAll();
        return ResponseEntity.ok().body(repairOrderList);
    }

    /**
     * Метод возвращает все принятые заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getAcceptedRepairOrder")
    public ResponseEntity<List<RepairOrder>> getAcceptedRepairOrder(){
        List<RepairOrder> repairOrderList = repairOrderService.getAllAccepted();
        return ResponseEntity.ok().body(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе диагностики
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getDiagnosticsRepairOrder")
    public ResponseEntity<List<RepairOrder>> getDiagnosticsRepairOrder(){
        List<RepairOrder> repairOrderList = repairOrderService.getAllDiagnostics();
        return ResponseEntity.ok().body(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе ремонта
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getIn_WorkRepairOrder")
    public ResponseEntity<List<RepairOrder>> getIn_WorkRepairOrder(){
        List<RepairOrder> repairOrderList = repairOrderService.getAllIn_Work();
        return ResponseEntity.ok().body(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, ремонт по которым выполнен
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getCompleteRepairOrder")
    public ResponseEntity<List<RepairOrder>> getCompleteRepairOrder(){
        List<RepairOrder> repairOrderList = repairOrderService.getAllComplete();
        return ResponseEntity.ok().body(repairOrderList);
    }

    /**
     * Метод возвращает все архивные заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getArchiveRepairOrder")
    public ResponseEntity<List<RepairOrder>> getArchiveRepairOrder(){
        List<RepairOrder> repairOrderList = repairOrderService.getAllArchive();
        return ResponseEntity.ok().body(repairOrderList);
    }

    /**
     * Метод возвращает список статусов заказа на ремонт
     *
     * @return ResponseEntity<List<RepairOrderType>> список статусов
     */
    @GetMapping("/service/getAllRepairOrderType")
    public ResponseEntity<List<RepairOrderType>> getAllRepairOrderType(){
        List<RepairOrderType> orderTypeList = repairOrderService.findAllRepairOrderType();
        return ResponseEntity.ok().body(orderTypeList);
    }

}