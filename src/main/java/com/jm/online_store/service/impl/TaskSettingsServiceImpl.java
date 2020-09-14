package com.jm.online_store.service.impl;

import com.jm.online_store.exception.TaskNotFoundException;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.repository.TaskSettingsRepository;
import com.jm.online_store.service.interf.TaskSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskSettingsServiceImpl implements TaskSettingsService {

    private final TaskSettingsRepository taskSettingsRepository;

    @Override
    public TaskSettings addNewTaskSetting(TaskSettings taskSettings) {
        return taskSettingsRepository.save(taskSettings);
    }

    @Override
    public TaskSettings findTaskById(Long id) {
        return taskSettingsRepository.findById(id).orElseThrow(TaskNotFoundException::new);
    }

    @Override
    public TaskSettings findTaskByName(String name) {
        return taskSettingsRepository.findByTaskName(name).orElseThrow(TaskNotFoundException::new);
    }

    @Override
    public TaskSettings updateTask(TaskSettings taskSettings) {
        TaskSettings taskSettingsToUpdate = taskSettingsRepository
                .findByTaskName(taskSettings.getTaskName())
                .orElseThrow(TaskNotFoundException::new);
        taskSettingsToUpdate.setActive(taskSettings.isActive());
        taskSettingsToUpdate.setStartTime(taskSettings.getStartTime());
        return taskSettingsRepository.save(taskSettingsToUpdate);
    }
}
