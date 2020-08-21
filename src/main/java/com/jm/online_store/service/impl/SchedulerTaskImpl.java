package com.jm.online_store.service.impl;

import com.jm.online_store.model.SchedulerTasks;
import com.jm.online_store.repository.SchedulerTasksRepository;
import com.jm.online_store.service.interf.SchedulerTaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SchedulerTaskImpl implements SchedulerTaskService {

    private final SchedulerTasksRepository schedulerTasksRepository;

    @Override
    public List<SchedulerTasks> findAllTasks() {
        return schedulerTasksRepository.findAll();
    }

    @Override
    public SchedulerTasks findTaskById(Long id) {
        return schedulerTasksRepository.getOne(id);
    }

    @Override
    public SchedulerTasks findTaskByName(String name) {
        return schedulerTasksRepository.findByTaskName(name);
    }

    @Override
    public void addTask(SchedulerTasks task) {

    }

    @Override
    public void deleteTaskById(Long id) {

    }
}
