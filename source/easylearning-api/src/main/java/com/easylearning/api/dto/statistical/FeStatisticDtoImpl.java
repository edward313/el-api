package com.easylearning.api.dto.statistical;

import lombok.Data;

@Data
public class FeStatisticDtoImpl implements FeStatisticDto{
    private Integer totalExpert;
    private Integer totalStudent;
    private Integer totalCourse;
    private Integer totalCourseSell;
}
