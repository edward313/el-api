package com.easylearning.api.dto.expert;

import lombok.Data;

@Data
public class MyStudentDto {
    private Long studentId;
    private String fullName;
    private String phone;
    private String email;
    private String avatarPath;
    private Long amountOfCourse;

    public MyStudentDto(Long studentId, String fullName, String phone, String email, String avatarPath, Long amountOfCourse) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.avatarPath = avatarPath;
        this.amountOfCourse = amountOfCourse;
    }
}
