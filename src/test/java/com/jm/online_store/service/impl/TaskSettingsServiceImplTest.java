package com.jm.online_store.service.impl;

import com.jm.online_store.exception.TaskNotFoundException;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.repository.TaskSettingsRepository;
import com.jm.online_store.service.interf.TaskSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class TaskSettingsServiceImplTest {
    @MockBean
    private TaskSettingsRepository taskSettingsRepository;
    @Autowired
    private TaskSettingsService taskSettingsService;

    private TaskSettings taskSettings;

    @BeforeEach
    void init() {
        taskSettings = TaskSettings.builder()
                .id(1L)
                .taskName("stockMailDistribution")
                .startTime(LocalTime.now())
                .active(false)
                .build();
    }

    @Test
    void findTaskById() {
        when(taskSettingsRepository.findById(2L)).thenReturn(Optional.empty());
        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> taskSettingsService.findTaskById(2L), "Expected exception doesn't math actual");
        assertNotNull(thrown.getMessage(), "Expected exception message is empty");
    }

    @Test
    void findTaskByName() {
        when(taskSettingsRepository.findByTaskName("some task name")).thenReturn(Optional.empty());
        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> taskSettingsService.findTaskByName("some task name"), "Expected exception doesn't math actual");
        assertNotNull(thrown.getMessage(), "Expected exception message is empty");
    }

}