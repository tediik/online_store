package com.jm.online_store.repository;

import com.jm.online_store.model.CommonSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommonSettingsRepository extends JpaRepository<CommonSettings, Long> {
    Optional<CommonSettings> findBySettingName(String settingName);
}
