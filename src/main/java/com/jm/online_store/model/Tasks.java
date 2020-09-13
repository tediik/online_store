package com.jm.online_store.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalTime;

@Entity
@Data
public class Tasks {

    @Id
    private Long id;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "active")
    private boolean active;

    @Column(name = "start_time")
    private LocalTime startTime;
}
