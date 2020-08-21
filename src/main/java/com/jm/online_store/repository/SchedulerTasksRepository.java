package com.jm.online_store.repository;

import com.jm.online_store.model.SchedulerTasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SchedulerTasksRepository extends JpaRepository<SchedulerTasks, Long> {

    SchedulerTasks findByTaskName(String name);

    SchedulerTasks findByExecutionDay(LocalDate day);
}
