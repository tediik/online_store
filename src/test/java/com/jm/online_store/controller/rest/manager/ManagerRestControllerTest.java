package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.NewsNotFoundException;
import com.jm.online_store.model.News;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.SalesReportDto;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.UserService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ManagerRestControllerTest {
    private NewsService newsService;
    private OrderService orderService;
    private UserService userService;
    private ModelMapper modelMapper;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private final static String END_POINT = "/api/manager";
    private User loggedUser;
    private List<News> allNews;
    private List<SalesReportDto> salesReport;

    @BeforeEach
    void setUp() {
        newsService = mock(NewsService.class);
        orderService = mock(OrderService.class);
        userService = mock(UserService.class);
        objectMapper = new ObjectMapper();
        modelMapper = new ModelMapper();
        mockMvc = MockMvcBuilders
                    .standaloneSetup(new ManagerRestController(newsService, orderService, userService, modelMapper))
                    .setControllerAdvice(new ExceptionsHandler())
                    .build();
        loggedUser = new User(1L, "customer1@mail.com");
        allNews = Arrays.asList(
                new News(1L, "ArchTitle1", "ArchAnons1", "ArchFullText1", LocalDate.now().plusDays(5), true),
                new News(2L, "ArchTitle2", "ArchAnons2", "ArchFullText2", LocalDate.now().plusDays(5), true),
                new News(3L, "ArchTitle3", "ArchAnons3", "ArchFullText3", LocalDate.now().minusDays(5), true));
        salesReport = Arrays.asList(
                new SalesReportDto(5L, "customer@mail.ru", "Покупатель", LocalDate.now().minusMonths(10), 3L, "Apple-10 - (3)", 3071.7),
                new SalesReportDto(2L, "customer@mail.ru", "Покупатель", LocalDate.now().minusMonths(1), 1L,  "ACER-543 - (1)", 399.9));
    }

    @Test
    @DisplayName("show authenticated user info")
    void showAuthUserInfo() throws Exception {
        when(userService.getCurrentLoggedInUser()).thenReturn(loggedUser);
        mockMvc.perform(get(END_POINT + "/authUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(loggedUser.getId()))
                .andExpect(jsonPath("$.data.email").value(loggedUser.getEmail()));
    }

    @Test
    @DisplayName("get all news")
    void getAllNews() throws Exception {
        when(newsService.findAll()).thenReturn(allNews);
        mockMvc.perform(get(END_POINT + "/news")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(allNews.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        allNews.get(0).getId().intValue(),
                        allNews.get(1).getId().intValue(),
                        allNews.get(2).getId().intValue())))
                .andExpect(jsonPath("$.data[*].title", containsInAnyOrder(
                        allNews.get(0).getTitle(),
                        allNews.get(1).getTitle(),
                        allNews.get(2).getTitle())))
                .andExpect(jsonPath("$.data[*].anons", containsInAnyOrder(
                        allNews.get(0).getAnons(),
                        allNews.get(1).getAnons(),
                        allNews.get(2).getAnons())));
    }

    @Test
    @DisplayName("create news post")
    void newsPost() throws Exception {
        when(newsService.save(any(News.class))).thenReturn(allNews.get(0));
        mockMvc.perform(post(END_POINT + "/news/post")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toNewsDto(allNews.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(allNews.get(0).getId()))
                .andExpect(jsonPath("$.data.title").value(allNews.get(0).getTitle()))
                .andExpect(jsonPath("$.data.anons").value(allNews.get(0).getAnons()));
    }

    @Test
    @DisplayName("update news post")
    void newsUpdate() throws Exception {
        when(newsService.update(allNews.get(1))).thenReturn(allNews.get(2));
        mockMvc.perform(put(END_POINT + "/news/update")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toNewsDto(allNews.get(1)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(allNews.get(2).getId()))
                .andExpect(jsonPath("$.data.title").value(allNews.get(2).getTitle()))
                .andExpect(jsonPath("$.data.anons").value(allNews.get(2).getAnons()));
    }

    @Test
    @DisplayName("delete news by id")
    void newsDelete() throws Exception {
        when(newsService.deleteById(anyLong())).thenReturn(true);
        mockMvc.perform(delete(END_POINT + "/news/{id}/delete", 11)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("get sales for custom range")
    void getSalesForCustomRange() throws Exception {
        when(orderService.findAllSalesBetween(LocalDate.now().minusMonths(10), LocalDate.now().minusDays(1))).thenReturn(salesReport);
        mockMvc.perform(get(END_POINT + "/sales")
                .param("stringStartDate", LocalDate.now().minusMonths(10).toString())
                .param("stringEndDate", LocalDate.now().minusDays(1).toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(salesReport.size())))
                .andExpect(jsonPath("$.data[*].orderNumber", containsInAnyOrder(
                        salesReport.get(0).getOrderNumber().intValue(),
                        salesReport.get(1).getOrderNumber().intValue())))
                .andExpect(jsonPath("$.data[*].userEmail", containsInAnyOrder(
                        salesReport.get(0).getUserEmail(),
                        salesReport.get(1).getUserEmail())))
                .andExpect(jsonPath("$.data[*].quantity", containsInAnyOrder(
                        salesReport.get(0).getQuantity().intValue(),
                        salesReport.get(1).getQuantity().intValue())))
                .andExpect(jsonPath("$.data[*].listOfProducts", containsInAnyOrder(
                        salesReport.get(0).getListOfProducts(),
                        salesReport.get(1).getListOfProducts())));
    }

    @Test
    @DisplayName("Mapping for csv export")
    void getSalesForCustomRangeAndExportToCSV() throws Exception {
        HttpServletResponse response = new MockHttpServletResponse();
        StatefulBeanToCsv<SalesReportDto> writer = null;
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            writer = new StatefulBeanToCsvBuilder<SalesReportDto>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(';')
                    .withOrderedResults(true)
                    .build();
            writer.write(salesReport);
        } catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
        when(orderService.exportOrdersByCSV(LocalDate.now().minusMonths(10), LocalDate.now().minusDays(1), response)).thenReturn(writer);
        mockMvc.perform(MockMvcRequestBuilders.get(END_POINT + "/sales/exportCSV")
                .param("stringStartDate", LocalDate.now().minusMonths(10).toString())
                .param("stringEndDate", LocalDate.now().minusDays(1).toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("get all news returns an empty list")
    void getAllNewsShouldReturnEmptyList() throws Exception {
        when(newsService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("delete news by id throws news not found exception")
    void newsDeleteThrowsNewsNotFoundException() throws Exception {
        when(newsService.deleteById(anyLong())).thenThrow(new NewsNotFoundException());
        mockMvc.perform(delete(END_POINT + "/news/{id}/delete", 11))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("get sales for custom range returns an empty list")
    void getSalesForCustomRangeReturnEmptyList() throws Exception {
        when(orderService.findAllSalesBetween(LocalDate.now().minusMonths(10), LocalDate.now().minusDays(1))).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/sales")
                .param("stringStartDate", LocalDate.now().minusMonths(10).toString())
                .param("stringEndDate", LocalDate.now().minusDays(1).toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk());
    }

    private NewsDto toNewsDto(News news) {
        return modelMapper.map(news, NewsDto.class);
    }
}
