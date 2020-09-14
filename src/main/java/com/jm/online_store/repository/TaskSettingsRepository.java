package com.jm.online_store.repository;

import com.jm.online_store.model.TaskSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskSettingsRepository extends JpaRepository<TaskSettings, Long> {
    Optional<TaskSettings> findByTaskName(String name);
}
