package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.TaskNotFoundException;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.service.interf.StockMailDistributionTask;
import com.jm.online_store.service.interf.TaskSchedulingService;
import com.jm.online_store.service.interf.TaskSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/manager/scheduling")
@AllArgsConstructor
public class SchedulingTaskController {
    private final TaskSchedulingService schedulingService;
    private final TaskSettingsService taskSettingsService;
    private final StockMailDistributionTask stockMailDistributionTask;

    @ExceptionHandler({TaskNotFoundException.class})
    public ResponseEntity<String> taskNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/stockMailDistribution/start")
    public ResponseEntity<TaskSettings> startMailDistributionTask(@RequestBody TaskSettings taskSettings) {
        taskSettingsService.updateTask(taskSettings);
        schedulingService.addTaskToScheduler(taskSettings, stockMailDistributionTask);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stockMailDistribution/stop")
    public ResponseEntity<TaskSettings> stopMailDistributionTask(@RequestBody TaskSettings taskSettings) {
        taskSettingsService.updateTask(taskSettings);
        schedulingService.removeTaskFromScheduler(taskSettings.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{taskName}")
    public ResponseEntity<TaskSettings> getMailDistributionSettings(@PathVariable String taskName) {
        return ResponseEntity.ok(taskSettingsService.findTaskByName(taskName));
    }
}
