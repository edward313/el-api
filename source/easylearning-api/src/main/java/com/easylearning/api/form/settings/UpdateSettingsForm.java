package com.easylearning.api.form.settings;

import com.easylearning.api.validation.DataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateSettingsForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @NotEmpty(message = "settingValue cant not be null")
    @ApiModelProperty(name = "settingValue", required = true)
    private String settingValue;

    @NotEmpty(message = "settingKey cant not be null")
    @ApiModelProperty(name = "settingKey", required = true)
    private String settingKey;

    @ApiModelProperty(name = "groupName")
    private String groupName;

    @ApiModelProperty(name = "description")
    private String description;

    @NotNull(message = "status cant not be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;

    @NotNull(message = "dataType can not be null")
    @DataType
    @ApiModelProperty(required = true)
    private String dataType;
}
