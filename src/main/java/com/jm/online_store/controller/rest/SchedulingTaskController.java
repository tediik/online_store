package com.jm.online_store.controller.rest;

import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.service.interf.StockMailDistributionTask;
import com.jm.online_store.service.interf.TaskSchedulingService;
import com.jm.online_store.service.interf.TaskSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/stockMailDistribution/start")
    public ResponseEntity<TaskSettings> startMailDistributionTask(@RequestBody TaskSettings taskSettings) {
        TaskSettings updatedTaskSettings = taskSettingsService.updateTask(taskSettings);
        schedulingService.addTaskToScheduler(updatedTaskSettings, stockMailDistributionTask);
        return ResponseEntity.ok(updatedTaskSettings);
    }

    @PostMapping("/stockMailDistribution/stop")
    public ResponseEntity<TaskSettings> stopMailDistributionTask(@RequestBody TaskSettings taskSettings) {
        TaskSettings updatedTaskSettings = taskSettingsService.updateTask(taskSettings);
        schedulingService.removeTaskFromScheduler(updatedTaskSettings.getId());
        return ResponseEntity.ok(updatedTaskSettings);
    }

    @GetMapping("/{taskName}")
    public ResponseEntity<TaskSettings> getMailDistributionSettings(@PathVariable String taskName) {
        TaskSettings taskSettings = taskSettingsService.findTaskByName(taskName);
        return ResponseEntity.ok(taskSettings);
    }
}
