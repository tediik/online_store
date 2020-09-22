package com.jm.online_store.repository;

import com.jm.online_store.model.TaskSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Optional;

public interface TaskSettingsRepository extends JpaRepository<TaskSettings, Long> {
    Optional<TaskSettings> findByTaskName(String name);

    @Modifying
    @Query("UPDATE TaskSettings SET active = :isActive, startTime = :startTime WHERE taskName = :taskName")
    int updateTaskStatusAndStartTime(@Param("taskName") String taskName,
                                      @Param("isActive") boolean isActive,
                                      @Param("startTime") LocalTime startTime);
}
