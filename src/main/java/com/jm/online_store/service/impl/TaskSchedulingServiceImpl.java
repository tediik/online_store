package com.jm.online_store.service.impl;

import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.repository.TaskSettingsRepository;
import com.jm.online_store.service.interf.PriceListService;
import com.jm.online_store.service.interf.StockMailDistributionTask;
import com.jm.online_store.service.interf.TaskSchedulingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@AllArgsConstructor
@Service
@Slf4j
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    private final TaskScheduler scheduler;
    private final TaskSettingsRepository taskSettingsRepository;
    private final StockMailDistributionTask stockMailDistributionTask;
    private final PriceListService priceListService;
    private final Map<Long, ScheduledFuture<?>> jobsMap;

    /**
     * Method add {@link Runnable} tasks to {@link ScheduledFuture}
     *
     * @param taskSettings - task settings from db such as LocalTime, and task id
     * @param task         - task which are needed to schedule implements {@link Runnable}
     */
    @Override
    public void addTaskToScheduler(TaskSettings taskSettings, Runnable task) {
        String cronExpression = DateTimeToCron(taskSettings.getStartTime().minusHours(2));
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task,
                new CronTrigger(cronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        jobsMap.put(taskSettings.getId(), scheduledTask);
    }

    /**
     * Method cancels scheduled task
     *
     * @param id - task id from db
     */
    @Override
    public void removeTaskFromScheduler(Long id) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(id);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            jobsMap.put(id, null);
        }
    }

    /**
     * Method checks db for active tasks after context refresh
     */
    @EventListener({ContextRefreshedEvent.class})
    public void contextRefreshedEvent() {
        List<TaskSettings> allTasks = taskSettingsRepository.findAll();
        addTaskToScheduler(allTasks.get(0), stockMailDistributionTask);
        addTaskToScheduler(allTasks.get(1), priceListService);
    }

    /**
     * Util method which convert {@link LocalTime} to cron expression
     *
     * @param time - {@link LocalTime}
     * @return - String with cron expression
     */
    private String DateTimeToCron(LocalTime time) {
        return String.format("0 %S %S */1 * *", time.getMinute(), time.getHour());
    }
}
