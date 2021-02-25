package com.jm.online_store.service.impl;

import com.jm.online_store.exception.CommonSettingsNotFoundException;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.repository.CommonSettingsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CommonSettingsServiceImpl implements CommonSettingsService {

    private final CommonSettingsRepository commonSettingsRepository;

    @Override
    public CommonSettings getSettingById(Long id) {
        return commonSettingsRepository.findById(id).orElseThrow(CommonSettingsNotFoundException::new);
    }

    @Override
    public CommonSettings getSettingByName(String name) {
        return commonSettingsRepository.findBySettingName(name).orElseThrow(CommonSettingsNotFoundException::new);
    }

    @Override
    public CommonSettings addSetting(CommonSettings setting) {
        return commonSettingsRepository.save(setting);
    }

    /**
     * method update row in common_settings table.
     * if repository returns int 1, that means that 1 row was updated.
     * if it will be 0 that means that there is no row with such name
     * and method throws {@link CommonSettingsNotFoundException}
     *
     * @param settings - settings to be updated
     */
    @Transactional
    @Override
    public void updateTextValue(CommonSettings settings) {
        if (commonSettingsRepository.updateTextValue(settings.getTextValue(), settings.getSettingName()) != 1) {
            throw new CommonSettingsNotFoundException();
        }
    }

    /**
     * method update row in common_settings table with taking into account boolean field "status".
     * if repository returns int 1, that means that 1 row was updated.
     * if it will be 0 that means that there is no row with such name
     * and method throws {@link CommonSettingsNotFoundException}
     *
     * @param settings - settings to be updated
     */
    @Transactional
    @Override
    public void updateMaintenanceMode(CommonSettings settings) {
        if (commonSettingsRepository.updateMaintenanceMode(settings.getTextValue(), settings.isStatus(), settings.getSettingName()) != 1) {
            throw new CommonSettingsNotFoundException();
        }

    }
}
