package com.easylearning.api.service.hls;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HlsSecureObject {
    private Long expiredTime;
    private String md5;
}
