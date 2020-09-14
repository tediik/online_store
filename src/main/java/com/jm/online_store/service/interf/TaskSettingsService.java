package com.jm.online_store.service.interf;

import com.jm.online_store.model.TaskSettings;

public interface TaskSettingsService {
    TaskSettings findTaskById(Long id);

    TaskSettings findTaskByName(String name);

    TaskSettings updateTask(TaskSettings taskSettings);
}
