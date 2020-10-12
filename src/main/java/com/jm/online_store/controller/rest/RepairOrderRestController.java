package com.jm.online_store.controller.rest;

import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.service.interf.RepairOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер для управления заявками на ремонт и для их добавления и просмотра
 */
@AllArgsConstructor
@RestController
public class RepairOrderRestController {

    private final RepairOrderService repairOrderService;

    /**
     * Метод сохраняет заявку на ремонт в базу данных
     *
     * @param repairOrder сущность для сохранения в базу данных
     * @return ResponseEntity<String> возвращает статус запроса
     */
    @PostMapping("/service/addRepairOrder")
    public ResponseEntity<String> addRepairOrder(@RequestBody RepairOrder repairOrder) {
        repairOrderService.save(repairOrder);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод возвращает все заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса
     */
    @GetMapping("/service/getAllRepairOrder")
    public ResponseEntity<List<RepairOrder>> getAllRepairOrder(){
        List<RepairOrder> repairOrderList = repairOrderService.findAll();
        return ResponseEntity.ok().body(repairOrderList);
    }
}