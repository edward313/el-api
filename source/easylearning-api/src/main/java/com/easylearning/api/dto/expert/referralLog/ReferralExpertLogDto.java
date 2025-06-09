package com.easylearning.api.dto.expert.referralLog;


import com.easylearning.api.dto.expert.ExpertDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ReferralExpertLogDto {

    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "expert")
    private ExpertDto expert;

    @ApiModelProperty(name = "refExpert")
    private ExpertDto refExpert;

    @ApiModelProperty(name = "usedTime ")
    private Date usedTime ;
}
