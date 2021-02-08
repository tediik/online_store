package com.jm.online_store.controller.rest.serviceWorker;

import com.itextpdf.text.DocumentException;
import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.service.interf.RepairOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Рест контроллер для управления заявками на ремонт
 */
@AllArgsConstructor
@RestController
@Api(description = "Rest controller for manage repair orders")
@RequestMapping("/api/service")
public class RepairOrderRestController {

    private final RepairOrderService repairOrderService;

    /**
     * Метод возвращает заказ по id
     *
     * @param id идентификатор RepairOrder
     * @return RepairOrder заказ на ремонт
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get order by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<RepairOrder> getRepairOrderById(@PathVariable Long id) {
        RepairOrder repairOrder = repairOrderService.findById(id);
        return ResponseEntity.ok(repairOrder);
    }

    /**
     * Метод сохраняет заказ на ремонт
     *
     * @param repairOrder сущность для сохранения в базу данных
     * @return ResponseEntity<RepairOrder> возвращает добавленный заказ на ремонт
     * со статусом ответа
     */
    @PostMapping("/addRepairOrder")
    @ApiOperation(value = "Add a new repair order",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<RepairOrder> addRepairOrder(@RequestBody RepairOrder repairOrder) {
        return ResponseEntity.ok(repairOrderService.save(repairOrder));
    }

    /**
     * Метод возвращает заказ на ремонт по номеру заказа и номеру телефону клиента
     *
     * @param repairOrder тело содержит номер заказа и телефон
     * @return ResponseEntity<RepairOrder> заказ на ремонт
     */
    @PostMapping("/checkStatus")
    @ApiOperation(value = "Get repair order by order ID and telephone number of client",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Repair odred was not found")
    public ResponseEntity<RepairOrder> getCurrentRepairOrder(@RequestBody RepairOrder repairOrder) {
        try {
            RepairOrder repairOrderCheck = repairOrderService.findByOrderNumberAndTelephoneNumber(
                    repairOrder.getOrderNumber(), repairOrder.getTelephoneNumber());
            return ResponseEntity.ok(repairOrderCheck);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Метод удаляет заказ по id
     *
     * @param id идентификатор RepairOrder
     * @return ResponseEntity<String> статус запроса
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete repair order by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<String> deleteRepairOrderById(@PathVariable Long id) {
        repairOrderService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод обновления заказа на ремонт
     *
     * @param repairOrder принимает заявку на ремонт
     * @return ResponseEntity.ok(repairOrder) возвращает статус запроса и заявку на ремонт
     */
    @PutMapping("/updateRepairOrder")
    @ApiOperation(value = "Update repair order",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<RepairOrder> updateRepairOrder(@RequestBody RepairOrder repairOrder) {
        repairOrderService.update(repairOrder);
        return ResponseEntity.ok(repairOrder);
    }

    /**
     * Метод возвращает все заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/getAllRepairOrder")
    @ApiOperation(value = "Get list of all repair orders",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getAllRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.findAll();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает вск заказы на ремонт КРОМЕ ОТМЕНЕННЫХ.
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/findAllWithoutCanceled")
    @ApiOperation(value = "Get list of all repair orders without cancelled",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getAllWithoutCanceled() {
        List<RepairOrder> repairOrderList = repairOrderService.findAllWithoutCanceled();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все принятые заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/getAcceptedRepairOrder")
    @ApiOperation(value = "Get list of accepted repair orders",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getAcceptedRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllAccepted();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе диагностики
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/getDiagnosticsRepairOrder")
    @ApiOperation(value = "Get list of all repair orders on diagnostic",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getDiagnosticsRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllDiagnostics();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе ремонта
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/getIn_WorkRepairOrder")
    @ApiOperation(value = "Get list of all repair orders in repair process",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getIn_WorkRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllIn_Work();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, ремонт по которым выполнен
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/getCompleteRepairOrder")
    @ApiOperation(value = "Get list of complete repair orders",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getCompleteRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllComplete();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все архивные заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/getArchiveRepairOrder")
    @ApiOperation(value = "Get list of archive repair orders",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getArchiveRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllArchive();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все отмененные заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/getCanceledRepairOrder")
    @ApiOperation(value = "Get list of cancelled repair orders",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrder>> getCanceledRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllCanceled();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает список статусов заказа на ремонт
     *
     * @return ResponseEntity<List < RepairOrderType>> список статусов
     */
    @GetMapping("/getAllRepairOrderType")
    @ApiOperation(value = "Get list of repair orders status",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<RepairOrderType>> getAllRepairOrderType() {
        List<RepairOrderType> orderTypeList = repairOrderService.findAllRepairOrderType();
        return ResponseEntity.ok().body(orderTypeList);
    }

    /**
     * Метод генерирует и возвращает заказ-наряд
     *
     * @param repairOrder Заказ на ремонт
     * @param response    запрос
     * @return файл заказ-наряда
     */
    @PostMapping("/getWorkOrder")
    @ApiOperation(value = "Generate and get list of work order",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Repair order was not found")
    public ResponseEntity<FileSystemResource> getWorkOrder(@RequestBody RepairOrder repairOrder, HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            repairOrderService.createPdfWorkOrder(repairOrder, response);
            return ResponseEntity.ok().build();
        } catch (DocumentException | IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
