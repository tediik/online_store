package com.jm.online_store.service.interf;

import com.jm.online_store.model.TaskSettings;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.support.CronTrigger;

import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

public interface TaskSchedulingService {
//    void addTaskToScheduler(long id, Runnable task);
    void addTaskToScheduler(TaskSettings taskSettings);

    void removeTaskFromScheduler(Long id);

    //TODO доделать рефреш заданий из бд при перезапуске приложения
    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent();
}
