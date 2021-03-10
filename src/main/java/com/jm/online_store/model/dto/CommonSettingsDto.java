package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ApiModel(description =  "Dto для CommonSettings для настройки задач")
public class CommonSettingsDto {
    private Long id;
    private String settingName;
    private String textValue;
    private boolean status;
}
