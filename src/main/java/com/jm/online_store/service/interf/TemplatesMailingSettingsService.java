package com.jm.online_store.service.interf;

import com.jm.online_store.model.TemplatesMailingSettings;

public interface TemplatesMailingSettingsService {
    TemplatesMailingSettings addSetting(TemplatesMailingSettings setting);

    TemplatesMailingSettings getSettingByName(String name);

    void updateTextValue(TemplatesMailingSettings settings);
}
