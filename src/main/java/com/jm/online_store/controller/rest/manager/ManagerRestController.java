package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.exception.OrdersNotFoundException;
import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.SalesReportDto;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.OrderService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.core.io.FileSystemResource;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

/**
 * Рест контроллер для управления новостями из кабинете менеджера, а также публикации новостей
 * на странице новостей
 */
@AllArgsConstructor
@RestController
@RequestMapping(value = "api/manager")
@Slf4j
@Api(description = "Rest controller for manage and publish news from manager page")
public class ManagerRestController {


    private final NewsService newsService;
    private final OrderService orderService;
    private final ModelMapper modelMapper = new ModelMapper();
    /**
     * Метод возвращающий всписок всех новостей
     *
     * @return List<News> возвращает список всех новстей из базы данных
     */
    @GetMapping("/news")
    @ApiOperation(value = "Get list of all news",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllNews() {
        Type listType = new TypeToken<List<NewsDto>>() {}.getType();
        List<NewsDto> returnValue = modelMapper.map(newsService.findAll(), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод сохраняет новости в базу данных
     *
     * @param news сущность для сохранения в базе данных
     * @return возвращает заполненную сущность клиенту
     */
    @PostMapping("/news/post")
    @ApiOperation(value = "Method for save news in database",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<NewsDto>> newsPost(@RequestBody News news) { //changed

        if (news.getPostingDate() == null || news.getPostingDate().isBefore(LocalDate.now())) {
            news.setPostingDate(LocalDate.now());
        }
        newsService.save(news);
        NewsDto returnValue = modelMapper.map(news, NewsDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue, null));
    }

    /**
     * Метод обновляет сущность в базе данных
     *
     * @param news сущность для сохранения в базе данных
     * @return возвращает обновленную сущность клиенту
     */
    @PutMapping("/news/update")
    @ApiOperation(value = "Method for update news in database",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<String>> newsUpdate(@RequestBody News news) {

        if (news.getPostingDate() == null || news.getPostingDate().isBefore(LocalDate.now())) {
            news.setPostingDate(LocalDate.now());
        }
        newsService.save(news);
        return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.HAS_BEEN_UPDATED.name(), null));
    }

    /**
     * Метод удаляет сушность из базы данных по уникальному идентификатору
     *
     * @param id уникальный идентификатор
     * @return возвращает ответ в виде строки с описанием результата
     */
    @DeleteMapping("/news/{id}/delete")
    @ApiOperation(value = "Method for delete news in database by ID",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<String>> newsDelete(@PathVariable Long id) {
        newsService.deleteById(id);
        return ResponseEntity.ok(new ResponseDto<>(true ,
                String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id),
                ResponseOperation.NO_ERROR.getMessage()));
    }

    /**
     * Get mapping for get request to response with sales during the custom date range
     *
     * @param stringStartDate - start of custom date range
     * @param stringEndDate   - end of custom date range
     * @return - {@link ResponseEntity} with list of Orders with status complete
     */
    @GetMapping("/sales")
    @ApiOperation(value = "Get mapping for get request to response with sales during the custom date range",
            authorizations = { @Authorization(value="jwtToken") })
    @ApiResponse(code = 404, message = "Sales was not found")
    public ResponseEntity<ResponseDto<List<SalesReportDto>>> getSalesForCustomRange(@RequestParam String stringStartDate, @RequestParam String stringEndDate) {
        LocalDate startDate = LocalDate.parse(stringStartDate);
        LocalDate endDate = LocalDate.parse(stringEndDate);

        return ResponseEntity.ok(new ResponseDto<>(true, orderService.findAllSalesBetween(startDate, endDate)));
    }

    /**
     * Mapping for csv export.
     *
     * @param stringStartDate - beginning of the period that receives from frontend in as String
     * @param stringEndDate   - end of the period that receives from frontend in as String
     * @param response        - response to write back stream with csv
     * @return - ResponseEntity
     */
    @GetMapping("/sales/exportCSV")
    @ApiOperation(value = "Mapping for csv export",
            authorizations = { @Authorization(value="jwtToken") })
    @ApiResponse(code = 400, message = "Problem with writing csv file")
    public ResponseEntity<FileSystemResource> getSalesForCustomRangeAndExportToCSV(@RequestParam String stringStartDate, @RequestParam String stringEndDate, HttpServletResponse response) {
        LocalDate startDate = LocalDate.parse(stringStartDate);
        LocalDate endDate = LocalDate.parse(stringEndDate);
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            StatefulBeanToCsv<SalesReportDto> writer = new StatefulBeanToCsvBuilder<SalesReportDto>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(';')
                    .withOrderedResults(true)
                    .build();
            writer.write(orderService.findAllSalesBetween(startDate, endDate));
            return ResponseEntity.ok().build();
        } catch (OrdersNotFoundException e) {
            log.debug("csv file was successfully sent");
            return ResponseEntity.notFound().build();
        } catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
            log.debug("Problem with writing csv file");
            return ResponseEntity.badRequest().build();
        }
    }
}
