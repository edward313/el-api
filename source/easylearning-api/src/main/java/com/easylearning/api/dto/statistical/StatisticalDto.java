package com.easylearning.api.dto.statistical;

import com.easylearning.api.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class StatisticalDto extends ABasicAdminDto {
    private String statisticalKey;
    private String statisticalValue;
}