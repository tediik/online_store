package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.NewsNotFoundException;
import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.service.interf.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ManagerNewsRestControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ModelMapper modelMapper;
    private NewsService newsService;
    private final static String END_POINT = "/api/manager/news";
    private List<News> archivedNews;
    private List<News> publishedNews;
    private List<News> unPublishedNews;
    private List<News> allNews;

    @BeforeEach
    void setUp(){
        newsService = mock(NewsService.class);
        modelMapper = new ModelMapper();
        mockMvc = MockMvcBuilders
                    .standaloneSetup(new ManagerNewsRestController(newsService, modelMapper))
                    .setControllerAdvice(new ExceptionsHandler())
                    .build();
        objectMapper = new ObjectMapper();
        allNews = new ArrayList<>();
        archivedNews = Arrays.asList(
                new News(1L, "ArchTitle1", "ArchAnons1", "ArchFullText1", LocalDate.now().minusDays(5), true),
                new News(2L, "ArchTitle2", "ArchAnons2", "ArchFullText2", LocalDate.now().minusDays(5), true),
                new News(3L, "ArchTitle3", "ArchAnons3", "ArchFullText3", LocalDate.now().minusDays(5), true));
        publishedNews = Arrays.asList(
                new News(4L, "PubTitle1", "PubAnons1","PubFullText1", LocalDate.now().minusDays(5), false),
                new News(5L, "PubTitle2", "PubAnons2","PubFullText2", LocalDate.now().minusDays(5), false),
                new News(6L, "PubTitle3", "PubAnons3","PubFullText3", LocalDate.now().minusDays(5), false));
        unPublishedNews = Arrays.asList(
                new News(7L, "unPubTitle1", "unPubAnons1","unPubFullText1", LocalDate.now().plusDays(5), false),
                new News(8L, "unPubTitle2", "unPubAnons2","unPubFullText2", LocalDate.now().plusDays(5), false),
                new News(9L, "unPubTitle3", "unPubAnons3","unPubFullText3", LocalDate.now().plusDays(5), false));
        allNews.addAll(publishedNews);
        allNews.addAll(unPublishedNews);
        allNews.addAll(archivedNews);
    }

    @Test
    @DisplayName("get news by id")
    void getNewsById() throws Exception {
        when(newsService.findById(anyLong())).thenReturn(allNews.get(0));
        mockMvc.perform(get(END_POINT + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(allNews.get(0).getId()))
                .andExpect(jsonPath("$.data.title").value(allNews.get(0).getTitle()))
                .andExpect(jsonPath("$.data.anons").value(allNews.get(0).getAnons()));
    }

    @Test
    @DisplayName("get all news")
    void getAllNews() throws Exception {
        when(newsService.findAll()).thenReturn(allNews);
        mockMvc.perform(get(END_POINT + "/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(allNews.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        allNews.get(0).getId().intValue(),
                        allNews.get(1).getId().intValue(),
                        allNews.get(2).getId().intValue(),
                        allNews.get(3).getId().intValue(),
                        allNews.get(4).getId().intValue(),
                        allNews.get(5).getId().intValue(),
                        allNews.get(6).getId().intValue(),
                        allNews.get(7).getId().intValue(),
                        allNews.get(8).getId().intValue())))
                .andExpect(jsonPath("$.data[*].title", containsInAnyOrder(
                        allNews.get(0).getTitle(),
                        allNews.get(1).getTitle(),
                        allNews.get(2).getTitle(),
                        allNews.get(3).getTitle(),
                        allNews.get(4).getTitle(),
                        allNews.get(5).getTitle(),
                        allNews.get(6).getTitle(),
                        allNews.get(7).getTitle(),
                        allNews.get(8).getTitle())))
                .andExpect(jsonPath("$.data[*].anons", containsInAnyOrder(
                        allNews.get(0).getAnons(),
                        allNews.get(1).getAnons(),
                        allNews.get(2).getAnons(),
                        allNews.get(3).getAnons(),
                        allNews.get(4).getAnons(),
                        allNews.get(5).getAnons(),
                        allNews.get(6).getAnons(),
                        allNews.get(7).getAnons(),
                        allNews.get(8).getAnons())));
    }

    @Test
    @DisplayName("get all archived news")
    void getAllArchivedNews() throws Exception {
        when(newsService.getAllArchivedNews()).thenReturn(archivedNews);
        mockMvc.perform(get(END_POINT + "/archived")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        archivedNews.get(0).getId().intValue(),
                        archivedNews.get(1).getId().intValue(),
                        archivedNews.get(2).getId().intValue())))
                .andExpect(jsonPath("$.data[*].archived", containsInAnyOrder(
                        archivedNews.get(0).isArchived(),
                        archivedNews.get(1).isArchived(),
                        archivedNews.get(2).isArchived())));
    }

    @Test
    @DisplayName("get all published news")
    void getAllPublishedNews() throws Exception {
        when(newsService.getAllPublished()).thenReturn(publishedNews);
        mockMvc.perform(get(END_POINT + "/published")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(publishedNews.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        publishedNews.get(0).getId().intValue(),
                        publishedNews.get(1).getId().intValue(),
                        publishedNews.get(2).getId().intValue())))
                .andExpect(jsonPath("$.data[*].archived", containsInAnyOrder(
                        publishedNews.get(0).isArchived(),
                        publishedNews.get(1).isArchived(),
                        publishedNews.get(2).isArchived())))
                .andExpect(jsonPath("$.data[*].postingDate", containsInAnyOrder(
                        publishedNews.get(0).getPostingDate().toString(),
                        publishedNews.get(1).getPostingDate().toString(),
                        publishedNews.get(2).getPostingDate().toString())));
    }

    @Test
    @DisplayName("get all unpublished news")
    void getAllUnPublishedNews() throws Exception {
        when(newsService.getAllUnpublished()).thenReturn(unPublishedNews);
        mockMvc.perform(get(END_POINT + "/unpublished")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(unPublishedNews.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        unPublishedNews.get(0).getId().intValue(),
                        unPublishedNews.get(1).getId().intValue(),
                        unPublishedNews.get(2).getId().intValue())))
                .andExpect(jsonPath("$.data[*].archived", containsInAnyOrder(
                        unPublishedNews.get(0).isArchived(),
                        unPublishedNews.get(1).isArchived(),
                        unPublishedNews.get(2).isArchived())))
                .andExpect(jsonPath("$.data[*].postingDate", containsInAnyOrder(
                        unPublishedNews.get(0).getPostingDate().toString(),
                        unPublishedNews.get(1).getPostingDate().toString(),
                        unPublishedNews.get(2).getPostingDate().toString())));
    }

    @Test
    @DisplayName("create news")
    void createNewsPost() throws Exception {
        when(newsService.save(any(News.class))).thenReturn(allNews.get(0));
        mockMvc.perform(post(END_POINT)
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
    @DisplayName("update news")
    void updateNewsPost() throws Exception {
        when(newsService.update(allNews.get(1))).thenReturn(allNews.get(2));
        mockMvc.perform(put(END_POINT)
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
    void deleteNewsById() throws Exception {
        when(newsService.deleteById(anyLong())).thenReturn(true);
        mockMvc.perform(delete(END_POINT + "/{id}", 11)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("unpublished, published, archived lists return empty data")
    void shouldReturnEmptyList() throws Exception {
        when(newsService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        when(newsService.getAllPublished()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        when(newsService.getAllUnpublished()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/unpublished"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        when(newsService.getAllArchivedNews()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/archived"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("throws news not found exception")
    void getNewsByIdThrowsNewsNotFoundException() throws Exception {
        when(newsService.findById(anyLong())).thenThrow(new NewsNotFoundException());
        mockMvc.perform(get(END_POINT + "/{id}", 11))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("throws news not found exception when try to delete news by id")
    void deleteNewsByIdThrowsNewsNotFoundException() throws Exception {
        when(newsService.deleteById(anyLong())).thenThrow(new NewsNotFoundException());
        mockMvc.perform(delete(END_POINT + "{id}", 11))
                .andExpect(status().isNotFound());
    }

    private static NewsDto toNewsDto(News news) {
        NewsDto newsDto = new NewsDto();
        newsDto.setId(news.getId());
        newsDto.setTitle(news.getTitle());
        newsDto.setAnons(news.getAnons());
        newsDto.setFullText(news.getFullText());
        newsDto.setArchived(news.isArchived());
        newsDto.setPostingDate(news.getPostingDate());
        return newsDto;
    }
}
