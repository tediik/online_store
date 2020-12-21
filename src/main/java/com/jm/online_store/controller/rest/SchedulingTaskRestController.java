package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.TaskNotFoundException;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.service.interf.PriceListService;
import com.jm.online_store.service.interf.StockMailDistributionTask;
import com.jm.online_store.service.interf.TaskSchedulingService;
import com.jm.online_store.service.interf.TaskSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(description = "Rest controller for scheduling task")
public class SchedulingTaskRestController {
    private final TaskSchedulingService schedulingService;
    private final TaskSettingsService taskSettingsService;
    private final StockMailDistributionTask stockMailDistributionTask;
    private final PriceListService priceListService;

    /**
     * Exception handler method that catches all {@link TaskNotFoundException}
     * in current class and return ResponseEntity with not found status
     * @return - {@link ResponseEntity<String>}
     */
    @ExceptionHandler({TaskNotFoundException.class})
    public ResponseEntity<String> taskNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/stockMailDistribution/start")
    @ApiOperation(value = "Start of mail distribution task")
    public ResponseEntity<TaskSettings> startMailDistributionTask(@RequestBody TaskSettings taskSettings) {
        taskSettingsService.updateTask(taskSettings);
        schedulingService.addTaskToScheduler(taskSettings, stockMailDistributionTask);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stockMailDistribution/stop")
    @ApiOperation(value = "Stop of mail distribution task")
    public ResponseEntity<TaskSettings> stopMailDistributionTask(@RequestBody TaskSettings taskSettings) {
        taskSettingsService.updateTask(taskSettings);
        schedulingService.removeTaskFromScheduler(taskSettings.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dailyPriceCreate")
    @ApiOperation(value = "Changing of daily price task")
    public ResponseEntity<TaskSettings> changeDailyPriceTask(@RequestBody TaskSettings taskSettings) {
        taskSettingsService.updateTask(taskSettings);
        schedulingService.addTaskToScheduler(taskSettings, priceListService);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{taskName}")
    @ApiOperation(value = "Get mail distribution settings by task name")
    public ResponseEntity<TaskSettings> getMailDistributionSettings(@PathVariable String taskName) {
        return ResponseEntity.ok(taskSettingsService.findTaskByName(taskName));
    }
}