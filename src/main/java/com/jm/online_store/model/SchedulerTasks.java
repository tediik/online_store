package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "scheduler_tasks")
public class SchedulerTasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "task_name", length = 100)
    private String taskName;

    @NotBlank
    @Column(name = "task_status")
    private boolean taskStatus;

    @NotBlank
    @Column(name = "execution_day", length = 20)
    private String executionDayOfTheWeek;

    @NotBlank
    @Column(name = "task_start_time")
    private LocalTime taskStartTime;

    @OneToMany(mappedBy = "task")
    private Set<User> users;
}

