package com.jm.online_store.service.interf;

import com.jm.online_store.model.TaskSettings;

public interface TaskSettingsService {
    TaskSettings addNewTaskSetting(TaskSettings taskSettings);

    TaskSettings findTaskById(Long id);

    TaskSettings findTaskByName(String name);

    void updateTask(TaskSettings taskSettings);
}
