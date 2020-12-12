package com.jm.online_store.repository;

import com.jm.online_store.model.CommonSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommonSettingsRepository extends JpaRepository<CommonSettings, Long> {
    Optional<CommonSettings> findBySettingName(String settingName);

    @Modifying
    @Query("UPDATE CommonSettings set textValue = :textValue, status = :status WHERE settingName = :settingName")
    int updateTextValue(@Param("textValue") String textValue, @Param("status") String status, @Param("settingName") String settingName);
}
