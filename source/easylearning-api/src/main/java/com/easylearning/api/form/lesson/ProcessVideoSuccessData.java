package com.easylearning.api.form.lesson;

import lombok.Data;

import java.util.List;

@Data
public class ProcessVideoSuccessData {
    private Long lessonId;
    private String thumbnail;
    private Boolean isFail;
    private String contentPath;
    private Long videoDuration;
}
