package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.service.interf.RepairOrderService;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * Метод сохраняет заказ на ремонт
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
     * Метод возвращает заказ на ремонт по номеру заказа и номеру телефону клиента
     *
     * @param repairOrder тело содержит номер заказа и телефон
     * @return ResponseEntity<RepairOrder> заказ на ремонт
     */
    @PostMapping("/api/checkStatus")
    public ResponseEntity<RepairOrder> getCurrentRepairOrder(@RequestBody RepairOrder repairOrder) {
        RepairOrder repairOrderCheck = repairOrderService.findRepairOrderByIdAndTelephoneNumber(repairOrder.getId(), repairOrder.getTelephoneNumber());
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

    /**
     * Метод обновления заказа на ремонт
     *
     * @param repairOrder принимает заявку на ремонт
     * @return ResponseEntity.ok(repairOrder) возвращает статус запроса и заявку на ремонт
     */
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
    public ResponseEntity<List<RepairOrder>> getAllRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.findAll();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все принятые заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getAcceptedRepairOrder")
    public ResponseEntity<List<RepairOrder>> getAcceptedRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllAccepted();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе диагностики
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getDiagnosticsRepairOrder")
    public ResponseEntity<List<RepairOrder>> getDiagnosticsRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllDiagnostics();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе ремонта
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getIn_WorkRepairOrder")
    public ResponseEntity<List<RepairOrder>> getIn_WorkRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllIn_Work();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все заказы на ремонт, ремонт по которым выполнен
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getCompleteRepairOrder")
    public ResponseEntity<List<RepairOrder>> getCompleteRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllComplete();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все архивные заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getArchiveRepairOrder")
    public ResponseEntity<List<RepairOrder>> getArchiveRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllArchive();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает все отмененные заказы на ремонт
     *
     * @return ResponseEntity<String> возвращает статус запроса и лист заявок на ремонт
     */
    @GetMapping("/service/getCanceledRepairOrder")
    public ResponseEntity<List<RepairOrder>> getCanceledRepairOrder() {
        List<RepairOrder> repairOrderList = repairOrderService.getAllCanceled();
        return ResponseEntity.ok(repairOrderList);
    }

    /**
     * Метод возвращает список статусов заказа на ремонт
     *
     * @return ResponseEntity<List < RepairOrderType>> список статусов
     */
    @GetMapping("/service/getAllRepairOrderType")
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
    @PostMapping("/service/getWorkOrder")
    public ResponseEntity<FileSystemResource> getWorkOrder(@RequestBody RepairOrder repairOrder, HttpServletResponse response) {
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            XWPFDocument document = new XWPFDocument();

            XWPFParagraph companyName = document.createParagraph();
            companyName.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun companyNameRun = companyName.createRun();
            companyNameRun.setText("ООО «ONLINE STORE»");
            companyNameRun.setBold(true);
            companyNameRun.setFontFamily("Arial");
            companyNameRun.setFontSize(18);

            XWPFParagraph companyInfo = document.createParagraph();
            companyInfo.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun companyInfoRun = companyInfo.createRun();
            companyInfoRun.setText("125130, г. Москва, ул. Нарвская, д. 1А");
            companyInfoRun.addBreak();
            companyInfoRun.setText("ИНН 7724457832, КПП 841689725, ОГРН 1176713648274");
            companyInfoRun.setFontFamily("Arial");
            companyInfoRun.setFontSize(12);

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("ЗАКАЗ-НАРЯД № " + repairOrder.getId().toString());
            titleRun.setBold(true);
            titleRun.setFontFamily("Arial");
            titleRun.setFontSize(16);

            XWPFParagraph userInfo = document.createParagraph();
            userInfo.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun userInfoRun = userInfo.createRun();
            userInfoRun.setText("От " + repairOrder.getAcceptanceDate().toString());
            userInfoRun.addBreak();
            userInfoRun.setText("Заказчик " + repairOrder.getFullNameClient());
            userInfoRun.addBreak();
            userInfoRun.setText("Номер телефона " + repairOrder.getTelephoneNumber());
            userInfoRun.addBreak();
            userInfoRun.setText("Название устройства " + repairOrder.getNameDevice());
            userInfoRun.addBreak();
            userInfoRun.setText("Гарантия " + (repairOrder.isGuarantee() ? "да" : "нет"));
            userInfoRun.addBreak();
            userInfoRun.setText("Описание проблемы " + repairOrder.getFullTextProblem());
            userInfoRun.addBreak();
            userInfoRun.setText("Исполнитель ____________________  Заказчик ____________________");
            userInfoRun.setFontFamily("Arial");
            userInfoRun.setFontSize(12);

            PdfOptions options = PdfOptions.create().fontEncoding("utf-8");
            PdfConverter.getInstance().convert(document, response.getOutputStream(), options);

            return ResponseEntity.ok().build();
        } catch (NullPointerException | IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}