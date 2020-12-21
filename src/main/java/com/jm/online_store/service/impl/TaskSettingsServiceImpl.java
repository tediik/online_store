package com.jm.online_store.service.impl;

import com.jm.online_store.exception.TaskNotFoundException;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.repository.TaskSettingsRepository;
import com.jm.online_store.service.interf.TaskSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

    /**
     * method updates row in task_settings
     * if repository returns int 1, that means that 1 row was updated.
     * if it will be 0 that means that there is no row with such name
     * and method throws {@link TaskNotFoundException}
     * @param taskSettings - settings to be updated
     */
    @Transactional
    @Override
    public void updateTask(TaskSettings taskSettings) {
        if (taskSettingsRepository.updateTaskStatusAndStartTime(taskSettings.getTaskName(),
                taskSettings.isActive(),
                taskSettings.getStartTime()) != 1) {
            throw new TaskNotFoundException();
        }
    }
}
