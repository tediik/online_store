package com.jm.online_store.service.impl;

import com.jm.online_store.exception.TemplatesMailingSettingsNotFoundException;
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
     * The method adds the TemplatesMailingSettings entity to the database
     * @param setting - entity TemplatesMailingSettings
     * @return TemplatesMailingSettings
     */
    @Override
    public TemplatesMailingSettings addSetting(TemplatesMailingSettings setting) {
        return templatesMailingSettingsRepository.save(setting);
    }

    /**
     * method gets row in TemplatesMailingSettings table.
     * if there is no such line, it throws
     * TemplatesMailingSettingsNotFoundException
     * @param name - setting name
     * @return TemplatesMailingSettings
     */
    @Override
    public TemplatesMailingSettings getSettingByName(String name) {
        return templatesMailingSettingsRepository.findBySettingName(name)
                .orElseThrow(() -> new TemplatesMailingSettingsNotFoundException(name));
    }

    /**
     * method update row in TemplatesMailingSettings table.
     * if repository returns int 1, that means that 1 row was updated.
     * if it will be 0 that means that there is no row with such name
     * and method throws {@link TemplatesMailingSettingsNotFoundException}
     * @param settings - settings to be updated
     */
    @Transactional
    @Override
    public void updateTextValue(TemplatesMailingSettings settings) {
        if (templatesMailingSettingsRepository.updateTextValue(settings.getTextValue(), settings.getSettingName()) != true) {
            throw new TemplatesMailingSettingsNotFoundException("Template" + settings.getSettingName() + "not found");
        }
    }
}
