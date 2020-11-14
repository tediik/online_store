package com.jm.online_store.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.exception.InvalidTelephoneNumberException;
import com.jm.online_store.exception.RepairOrderNotFoundException;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.repository.RepairOrderRepository;
import com.jm.online_store.service.interf.RepairOrderService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Сервисный класс для работы с заказами на ремонт, имплементация интерфейса {@link RepairOrderService}
 * Содержит бизнес логику, использует методы репозитория {@link RepairOrderRepository}
 */
@AllArgsConstructor
@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;

    /**
     * Метод возвращает все заказы на ремонт, сортирует по дате, от нового заказа к старому
     *
     * @return лист заказов на ремонт
     */
    @Override
    public List<RepairOrder> findAll() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAll();
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    /**
     * Метод возвращает все принятые заказы на ремонт, сортирует по дате, от нового заказа к старому
     *
     * @return лист заказов на ремонт
     */
    @Override
    public List<RepairOrder> getAllAccepted() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ACCEPTED);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе диагностики,
     * сортирует по дате, от нового заказа к старому
     *
     * @return лист заказов на ремонт
     */
    @Override
    public List<RepairOrder> getAllDiagnostics() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.DIAGNOSTICS);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    /**
     * Метод возвращает все заказы на ремонт, которые находятся на этапе ремонта,
     * сортирует по дате, от нового заказа к старому
     *
     * @return лист заказов на ремонт
     */
    @Override
    public List<RepairOrder> getAllIn_Work() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.IN_WORK);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    /**
     * Метод возвращает все заказы на ремонт, ремонт по которым выполнен,
     * сортирует по дате, от нового заказа к старому
     *
     * @return лист заказов на ремонт
     */
    @Override
    public List<RepairOrder> getAllComplete() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.COMPLETE);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    /**
     * Метод возвращает все архивные заказы на ремонт, сортирует по дате, от нового заказа к старому
     *
     * @return лист заказов на ремонт
     */
    @Override
    public List<RepairOrder> getAllArchive() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.ARCHIVED);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    /**
     * Метод возвращает все отмененные заказы на ремонт, сортирует по дате, от нового заказа к старому
     *
     * @return лист заказов на ремонт
     */
    @Override
    public List<RepairOrder> getAllCanceled() {
        List<RepairOrder> repairOrderList = repairOrderRepository.findAllByRepairOrderTypeEquals(RepairOrderType.CANCELED);
        repairOrderList.sort((a, b) -> b.getAcceptanceDate().compareTo(a.getAcceptanceDate()));
        return repairOrderList;
    }

    /**
     * Метод сохраняет заявку на ремонт, устанавливает дату создания заявки и ставит статус Accepted.
     * Проверяет валидность введенного номера телефона, в случае его невалидности
     * бросает исключение {@throws InvalidTelephoneNumberException}
     *
     * @param repairOrder принимает в качестве параметра заказ на ремонт
     */
    @Override
    public void save(RepairOrder repairOrder) {
        repairOrder.setRepairOrderType(RepairOrderType.ACCEPTED);
        repairOrder.setAcceptanceDate(LocalDate.now());
        if (!ValidationUtils.isValidTelephoneNumber(repairOrder.getTelephoneNumber())) {
            throw new InvalidTelephoneNumberException();
        }
        repairOrderRepository.save(repairOrder);
    }

    /**
     * Метод создает PDF документ и помещает в поток response
     * @param repairOrder заказ на ремонт
     * @param response    изменяемый response с формы заказа на ремонт
     * @throws IOException       ошибка получения потока из response
     * @throws DocumentException ошибка добавления информации в файл
     */
    @Override
    public void createPdfWorkOrder(RepairOrder repairOrder, HttpServletResponse response) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        Font companyNameFont = FontFactory.getFont("fonts/HelveticaRegular.ttf", BaseFont.IDENTITY_H, true, 18, Font.BOLD);
        Font infoFont = FontFactory.getFont("fonts/HelveticaRegular.ttf", BaseFont.IDENTITY_H, true, 12);
        Font titleFont = FontFactory.getFont("fonts/HelveticaRegular.ttf", BaseFont.IDENTITY_H, true, 16, Font.BOLD);
        document.open();
        document.addTitle("Заказ-наряд " + repairOrder.getOrderNumber());
        Paragraph paragraph = new Paragraph("ООО «ONLINE STORE»", companyNameFont);
        paragraph.add(new Paragraph("125130, г. Москва, ул. Нарвская, д. 1А", infoFont));
        paragraph.add(new Paragraph("ИНН 7724457832, КПП 841689725, ОГРН 1176713648274", infoFont));
        document.add(paragraph);
        paragraph = new Paragraph("ЗАКАЗ-НАРЯД № " + repairOrder.getOrderNumber(), titleFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        paragraph = new Paragraph("От " + DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now()),
                infoFont);
        paragraph.add(new Paragraph("Заказчик " + repairOrder.getFullNameClient(), infoFont));
        paragraph.add(new Paragraph("Номер телефона " + repairOrder.getTelephoneNumber(), infoFont));
        paragraph.add(new Paragraph("Название устройства " + repairOrder.getNameDevice(), infoFont));
        paragraph.add(new Paragraph("Гарантия " + (repairOrder.isGuarantee() ? "да" : "нет"), infoFont));
        paragraph.add(new Paragraph("Описание проблемы " + repairOrder.getFullTextProblem()
                .substring(3, repairOrder.getFullTextProblem().length() - 4), infoFont));
        paragraph.add(new Paragraph("Исполнитель ____________________  Заказчик ____________________", infoFont));
        paragraph.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph);
        document.close();
    }

    /**
     * Метод обновляет заявку на ремонт, устанавливает дату редактирования заявки.
     * Проверяет валидность введенного номера телефона, в случае его невалидности
     * бросает исключение {@throws InvalidTelephoneNumberException}
     *
     * @param repairOrder принимает в качестве параметра заказ на ремонт
     * @return отредактированную заявку на ремонт
     */
    @Override
    public RepairOrder update(RepairOrder repairOrder) {
        repairOrder.setModifiedDate(LocalDate.now());
        if (!ValidationUtils.isValidTelephoneNumber(repairOrder.getTelephoneNumber())) {
            throw new InvalidTelephoneNumberException();
        }
        return repairOrderRepository.save(repairOrder);
    }

    /**
     * Метод находит заказ по {@param id}, в случае если его нет,
     * бросается исключение {@throws RepairOrderNotFoundException}
     *
     * @param id идентификатор заказа на ремонт
     * @return RepairOrder заказ на ремонт
     */
    @Override
    public RepairOrder findById(Long id) {
        return repairOrderRepository.findById(id).orElseThrow(RepairOrderNotFoundException::new);
    }

    /**
     * Метод находит заказ по {@param id} и {@param telephoneNumber}, в случае если его нет,
     * бросается исключение {@throws RepairOrderNotFoundException}
     *
     * @param id              идентификатор заказа на ремонт
     * @param telephoneNumber номер телефона клиента
     * @return RepairOrder заказ на ремонт
     */
    @Override
    public RepairOrder findRepairOrderByIdAndTelephoneNumber(Long id, String telephoneNumber) {
        return repairOrderRepository.findByIdAndTelephoneNumber(id, telephoneNumber).orElseThrow(RepairOrderNotFoundException::new);
    }

    /**
     * Метод проверяет существует ли заказ на ремонт по {@param id}
     *
     * @param id идентификатор заказа на ремонт
     * @return возвращает true если существует заказ, иначе false
     */
    @Override
    public boolean existsById(Long id) {
        return repairOrderRepository.existsById(id);
    }

    /**
     * Удаляет заказ на ремонт по {@param id}
     *
     * @param id идентификатор заказа на ремонт
     */
    @Override
    public void deleteById(Long id) {
        repairOrderRepository.deleteById(id);
    }

    /**
     * Метод возвращает список всех возможных статусов заказа на ремонт
     *
     * @return возвращает список статусов заказа на ремонт
     */
    @Override
    public List<RepairOrderType> findAllRepairOrderType() {
        List<RepairOrderType> orderTypeList = new ArrayList<>();
        Collections.addAll(orderTypeList, RepairOrderType.values());
        return orderTypeList;
    }

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        return repairOrderRepository.existsByOrderNumber(orderNumber);
    }

    @Override
    public RepairOrder findByOrderNumber(String orderNumber) {
        return repairOrderRepository.findByOrderNumber(orderNumber);
    }
}