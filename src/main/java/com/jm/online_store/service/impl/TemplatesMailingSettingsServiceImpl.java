package com.jm.online_store.service.impl;

import com.jm.online_store.exception.CommonSettingsNotFoundException;
import com.jm.online_store.exception.TemplatesMailingSettingsNotFoundException;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.TemplatesMailingSettings;
import com.jm.online_store.repository.TemplatesMailingSettingsRepository;
import com.jm.online_store.service.interf.TemplatesMailingSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class TemplatesMailingSettingsServiceImpl implements TemplatesMailingSettingsService {

    private final TemplatesMailingSettingsRepository templatesMailingSettingsRepository;

    /**
     * method gets row in TemplatesMailingSettings table.
     * если такая строка отсутствует то кидает TemplatesMailingSettingsNotFoundException}
     * @param name - settings to be updated
     * @return TemplatesMailingSettings
     */
    @Override
    public TemplatesMailingSettings getSettingByName(String name) {
        return templatesMailingSettingsRepository.findBySettingName(name).orElseThrow(TemplatesMailingSettingsNotFoundException::new);
    }

    /**
     * method update row in TemplatesMailingSettings table.
     * if repository returns int 1, that means that 1 row was updated.
     * if it will be 0 that means that there is no row with such name
     * and method throws {@link TemplatesMailingSettingsNotFoundException}
     *
     * @param settings - settings to be updated
     */
    @Transactional
    @Override
    public void updateTextValue(TemplatesMailingSettings settings) {
        if (templatesMailingSettingsRepository.updateTextValue(settings.getTextValue(), settings.getSettingName()) != 1) {
            throw new TemplatesMailingSettingsNotFoundException();
        }
    }

}
