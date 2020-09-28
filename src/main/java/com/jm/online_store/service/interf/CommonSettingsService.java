package com.jm.online_store.service.interf;

import com.jm.online_store.model.CommonSettings;

public interface CommonSettingsService {
    CommonSettings getSettingById(Long id);

    CommonSettings getSettingByName(String name);

    void updateTextValue(CommonSettings settings);

    CommonSettings addSetting(CommonSettings setting);
}
