package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalTime;

/**
 * Class which contains settings of tasks such as:
 *  - task name
 *  - active
 *  - start time
 *  This class is used for scheduling.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description =  "Сущность TaskSettings, содержит настройки для задач")
public class TaskSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", unique = true)
    private String taskName;

    @Column(name = "active")
    private boolean active;

    @Column(name = "start_time")
    private LocalTime startTime;

}
