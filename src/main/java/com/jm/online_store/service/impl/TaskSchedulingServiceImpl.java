package com.jm.online_store.service.impl;

import com.jm.online_store.exception.TaskNotFoundException;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.repository.TaskSettingsRepository;
import com.jm.online_store.service.interf.StockMailSendingTask;
import com.jm.online_store.service.interf.TaskSchedulingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@AllArgsConstructor
@Service
@Slf4j
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    private final TaskScheduler scheduler;
    private final TaskSettingsRepository taskSettingsRepository;
    private final StockMailSendingTask stockMailSendingTask;

    Map<Long, ScheduledFuture<?>> jobsMap;

    @Override
    public void addTaskToScheduler(TaskSettings taskSettings) {
//        TaskSettings taskSettingsToSchedule = taskSettingsRepository.findByTaskName(taskSettings.getTaskName()).orElseThrow(TaskNotFoundException::new);
//        taskSettingsToSchedule.setStartTime(taskSettings.getStartTime());
        String cronExpression = DateTimeToCron(taskSettings.getStartTime());
        ScheduledFuture<?> scheduledTask = scheduler.schedule(stockMailSendingTask,
                new CronTrigger(cronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        jobsMap.put(taskSettings.getId(), scheduledTask);
    }

    @Override
    public void removeTaskFromScheduler(Long id) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(id);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            jobsMap.put(id, null);
        }
    }

    //TODO доделать рефреш заданий из бд при перезапуске приложения
    @Override
    @EventListener({ContextRefreshedEvent.class})
    public void contextRefreshedEvent() {
    }

    private String DateTimeToCron(LocalTime time) {
        return String.format("0 %S %S */1 * *", time.getMinute(), time.getHour());
    }
}
