package com.jm.online_store.service.impl;

import com.jm.online_store.exception.SettingsNotFound;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.repository.CommonSettingsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommonSettingsServiceImpl implements CommonSettingsService {

    private final CommonSettingsRepository commonSettingsRepository;

    @Override
    public CommonSettings getSettingById(Long id) {
        return commonSettingsRepository.findById(id).orElseThrow(SettingsNotFound::new);
    }

    @Override
    public CommonSettings getSettingByName(String name) {
        return commonSettingsRepository.findBySettingName(name).orElseThrow(SettingsNotFound::new);
    }

    @Override
    public CommonSettings updateSetting(CommonSettings setting) {
        CommonSettings settingToUpdate = commonSettingsRepository
                .findBySettingName(setting.getSettingName())
                .orElseThrow(SettingsNotFound::new);
        settingToUpdate.setTextValue(setting.getTextValue());
        return commonSettingsRepository.save(settingToUpdate);
    }

    @Override
    public CommonSettings addSetting(CommonSettings setting) {
        return commonSettingsRepository.save(setting);
    }

}
