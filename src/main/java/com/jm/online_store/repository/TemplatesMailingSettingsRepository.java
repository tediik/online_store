package com.jm.online_store.repository;

import com.jm.online_store.model.TemplatesMailingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemplatesMailingSettingsRepository extends JpaRepository<TemplatesMailingSettings, Long> {

    Optional<TemplatesMailingSettings> findBySettingName(String settingName);

    @Modifying
    @Query("UPDATE TemplatesMailingSettings set textValue = :textValue WHERE settingName = :settingName")
    int updateTextValue(@Param("textValue") String textValue, @Param("settingName") String settingName);
}
