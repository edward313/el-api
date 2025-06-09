package com.easylearning.api.dto.settings;

import com.easylearning.api.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class SettingsDto extends ABasicAdminDto {
    private String settingKey;
    private String settingValue;
    private String groupName;
    private String description;
    private Integer isSystem;
    private Integer isEditable;
    private String dataType;
}
