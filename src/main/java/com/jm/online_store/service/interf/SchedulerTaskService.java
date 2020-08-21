package com.jm.online_store.service.interf;

import com.jm.online_store.model.SchedulerTasks;

import java.util.List;

public interface SchedulerTaskService {
    List<SchedulerTasks> findAllTasks();

    SchedulerTasks findTaskById(Long id);

    SchedulerTasks findTaskByName(String name);

    SchedulerTasks findByNameOfDay(String nameOfDay);

    void addTask(SchedulerTasks task);

    void deleteTaskById(Long id);
}
