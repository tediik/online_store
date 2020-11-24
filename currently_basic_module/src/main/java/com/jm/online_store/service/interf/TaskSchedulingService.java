package com.jm.online_store.service.interf;

import com.jm.online_store.model.TaskSettings;

public interface TaskSchedulingService {
    void addTaskToScheduler(TaskSettings taskSettings, Runnable task);

    void removeTaskFromScheduler(Long id);
}
