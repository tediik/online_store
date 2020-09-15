package com.jm.online_store.service.impl;

import com.jm.online_store.exception.NewsNotFoundException;
import com.jm.online_store.model.News;
import com.jm.online_store.repository.NewsRepository;
import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class NewsServiceImplTest {
    @MockBean
    private NewsRepository newsRepository;
    @Autowired
    private NewsService newsService;

    private News publishedNews;
    private News notPublishedNews;
    private News archivedNews;
    private List<News> newsList;
    private List<News> emptyNewsList;

    @BeforeEach
    void init() {
        emptyNewsList = new ArrayList<>();
        newsList = new ArrayList<>();
        publishedNews = News.builder()
                .id(1L)
                .title("Title of test publishedNews")
                .anons("Anons of test publishedNews")
                .fullText("Full text of test publishedNews")
                .postingDate(LocalDate.now().minusDays(5))
                .archived(false)
                .build();
        notPublishedNews = News.builder()
                .id(2L)
                .title("Title of test notPublishedNews")
                .anons("Anons of test notPublishedNews")
                .fullText("Full text of test notPublishedNews")
                .postingDate(LocalDate.now().plusDays(4))
                .archived(false)
                .build();
        archivedNews = News.builder()
                .id(3L)
                .title("Title of test archivedNews")
                .anons("Anons of test archivedNews")
                .fullText("Full text of test archivedNews")
                .postingDate(LocalDate.now().minusDays(5))
                .archived(true)
                .build();
        newsList.add(publishedNews);
        newsList.add(notPublishedNews);
        newsList.add(archivedNews);

    }

    @Test
    void findAll() {
        when(newsRepository.findAll()).thenReturn(newsList);
        List<News> testNewsList = newsService.findAll();
        assertEquals(3, testNewsList.size(), "Expected size of list doesn't match actual size");
        assertFalse(testNewsList.isEmpty(), "findAll test not passed");
        verify(newsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Testing method findAll() in class NewsServiceImpl to throw Exception if returned List is empty")
    void throwExceptionIfFindAllIsEmpty() {
        when(newsRepository.findAll()).thenReturn(emptyNewsList);
        NewsNotFoundException thrown = assertThrows(NewsNotFoundException.class, newsService::findAll, "Expected exception doesnt match actual");
        assertNotNull(thrown.getMessage(), "Expected message is empty");
        verify(newsRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(newsRepository.findById(1L)).thenReturn(Optional.ofNullable(publishedNews));
        News newsTest = newsService.findById(1L);
        assertNotNull(newsTest);
        assertEquals(newsTest, publishedNews, "Found object doesn't equals expected");
        verify(newsRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Testing if findById() in NewsServiceImpl throws exception if returned Optional is empty ")
    void throwExceptionIfFindByIdReturnsOptionalEmpty() {
        when(newsRepository.findById(1L)).thenReturn(Optional.empty());
        NewsNotFoundException thrown = assertThrows(NewsNotFoundException.class, () -> newsService.findById(1), "Expected exception doesnt match actual");
        assertNotNull(thrown.getMessage(), "Expected exception message is empty");
        verify(newsRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(newsRepository);
    }

    @Test
    void existsById() {
        when(newsRepository.existsById(1L)).thenReturn(true);
        assertTrue(newsService.existsById(1L), "Expected return true if exists");
        verify(newsRepository, times(1)).existsById(1L);
    }

    @Test
    void update() {
        News updatedNews = publishedNews;
        updatedNews.setModifiedDate(LocalDate.now());
        when(newsRepository.save(publishedNews)).thenReturn(updatedNews);
        assertEquals(updatedNews.getModifiedDate(), newsService.update(publishedNews).getModifiedDate(), "Expected date doesn't match actual");
        verify(newsRepository, times(1)).save(publishedNews);
    }

    @Test
    void getAllPublished() {
        List<News> publishedNewsList = new ArrayList<>();
        publishedNewsList.add(publishedNews);
        when(newsRepository.findAllByPostingDateBeforeAndArchivedEquals(LocalDate.now(), false)).thenReturn(publishedNewsList);
        assertEquals(publishedNewsList, newsService.getAllPublished(), "Expected list doesnt match actual");
        assertEquals(publishedNewsList.size(), newsService.getAllPublished().size(), "Expected List size doesn't match actual size");
        verify(newsRepository, times(2)).findAllByPostingDateBeforeAndArchivedEquals(LocalDate.now(), false);
    }

    @Test
    @DisplayName("Testing method getAllPublished() in class NewsServiceImpl to throw Exception if returned List is empty")
    void throwExceptionIfGetAllPublishedReturnsEmptyList() {
        when(newsRepository.findAllByPostingDateBeforeAndArchivedEquals(LocalDate.now(), false)).thenReturn(emptyNewsList);
        NewsNotFoundException thrown = assertThrows(NewsNotFoundException.class, () -> newsService.getAllPublished(), "Expected exception doesnt match actual");
        assertNotNull(thrown.getMessage(), "Expected message is empty");
        verify(newsRepository, times(1)).findAllByPostingDateBeforeAndArchivedEquals(LocalDate.now(), false);
    }

    @Test
    void getAllUnpublished() {
        List<News> unpublishedNewsList = new ArrayList<>();
        unpublishedNewsList.add(notPublishedNews);
        when(newsRepository.findAllByPostingDateAfterAndArchivedEquals(LocalDate.now(), false)).thenReturn(unpublishedNewsList);
        List<News> unpublishedNewsListTest = newsService.getAllUnpublished();
        assertEquals(unpublishedNewsList, unpublishedNewsListTest, "Expected list doesnt match actual");
        assertEquals(unpublishedNewsList.size(), unpublishedNewsListTest.size(), "Expected List size doesn't match actual size");
        verify(newsRepository, times(1)).findAllByPostingDateAfterAndArchivedEquals(LocalDate.now(), false);
    }

    @Test
    @DisplayName("Testing method getAllUnpublished() in class NewsServiceImpl to throw Exception if returned List is empty")
    void throwExceptionIfGetAllUnpublishedReturnsEmptyList() {
        when(newsRepository.findAllByPostingDateAfterAndArchivedEquals(LocalDate.now(), false)).thenReturn(emptyNewsList);
        NewsNotFoundException thrown = assertThrows(NewsNotFoundException.class, () -> newsService.getAllUnpublished(), "Expected exception doesnt match actual");
        assertNotNull(thrown.getMessage(), "Expected message is empty");
        verify(newsRepository, times(1)).findAllByPostingDateAfterAndArchivedEquals(LocalDate.now(), false);
    }

    @Test
    void getAllArchivedNews() {
        List<News> archivedNewsList = new ArrayList<>();
        archivedNewsList.add(archivedNews);
        when(newsRepository.findAllByArchivedEquals(true)).thenReturn(archivedNewsList);
        List<News> archivedNewsListTest = newsService.getAllArchivedNews();
        assertEquals(archivedNewsList, archivedNewsListTest, "Expected list doesnt match actual");
        assertEquals(archivedNewsList.size(), archivedNewsListTest.size(), "Expected List size doesn't match actual size");
    }

    @Test
    @DisplayName("Testing method getAllArchivedNews() in class NewsServiceImpl to throw Exception if returned List is empty")
    void throwExceptionIfGetAllArchivedNewsReturnsEmptyList() {
        when(newsRepository.findAllByArchivedEquals(true)).thenReturn(emptyNewsList);
        NewsNotFoundException thrown = assertThrows(NewsNotFoundException.class, newsService::getAllArchivedNews, "Expected exception doesnt match actual");
        assertNotNull(thrown.getMessage(), "Expected message is empty");
        verify(newsRepository, times(1)).findAllByArchivedEquals(true);
    }
}
