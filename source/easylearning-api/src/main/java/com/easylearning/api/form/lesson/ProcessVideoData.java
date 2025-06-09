package com.easylearning.api.form.lesson;

import lombok.Data;

@Data
public class ProcessVideoData {
    private String url;
    private Long lessonId;
    private Integer versionCode;
}
