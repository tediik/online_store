package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.service.impl.StockMailSendingTaskImpl;
import com.jm.online_store.service.interf.StockMailSendingTask;
import com.jm.online_store.service.interf.TaskSchedulingService;
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
//    private final StockMailSendingTask stockMailSendingTask;

    @PostMapping("/start")
    public ResponseEntity<String> startTask(@RequestBody TaskSettings taskSettings) {
        schedulingService.addTaskToScheduler(taskSettings);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/start")
    public ResponseEntity<String> startTestTask() {


//        schedulingService.addTaskToScheduler(1L, test);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stop")
    public ResponseEntity<String> stopTestTask() {
        schedulingService.removeTaskFromScheduler(1L);
        return ResponseEntity.ok().build();
    }
}
