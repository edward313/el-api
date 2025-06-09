package com.easylearning.api.service.hls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class HlsService {

    @Value("${hls.expire}")
    private String expireTime;

    @Value("${hls.secretkey}")
    private String secretKey;

    @Value("${hls.server}")
    private String hlsServer;

    public HlsSecureObject getObjectKey(){
        try {
            Date date = new Date(System.currentTimeMillis());
            long epoch = (date.getTime() + (Long.parseLong(expireTime) * 60 * 1000))/1000; // thêm n phút
            String input =  epoch+ " "+ secretKey;

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] enc = md.digest();
            String md5Sum = Base64.getEncoder().encodeToString(enc);
            String hash = md5Sum.substring( 0,md5Sum.length() -2).replaceAll("/", "_");
            return new HlsSecureObject(epoch,md5Sum);

        }catch (Exception e){
            return null;
        }
    }

    public String getVideoUrl(String filePath){
        try {
            Date date = new Date(System.currentTimeMillis());
            long epoch = (date.getTime() + (Long.parseLong(expireTime) * 60 * 1000))/1000; // thêm n phút
            String input =  epoch+ " "+ secretKey;

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] enc = md.digest();
            String md5Sum = Base64.getEncoder().encodeToString(enc);
            String hash = md5Sum.substring( 0,md5Sum.length() -2)
                    .replaceAll("/", "_")
                    .replaceAll("\\+", "-");

            String content = filePath;
            //https://lf-video.developteam.net/hls/media/general/123456789(1)/COURSE/123(2)/1234(3)/{token}(4)/{expired}(5)/livestream.m3u8
            //https://lf-video.developteam.net/hls/media/general/123456789/COURSE/123/1234/htLIUws0a9jfJZXovXwhZg/1702870092/livestream.m3u8
            return hlsServer + "/hls/media/general"+content;
        }catch (Exception e){
            return null;
        }
    }
}
