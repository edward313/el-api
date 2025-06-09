package com.easylearning.api.service.feign;


import com.easylearning.api.config.CustomFeignConfig;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.MoveVideoFileDto;
import com.easylearning.api.dto.UploadFileDto;
import com.easylearning.api.form.UploadFileFromUrlForm;
import com.easylearning.api.form.course.DeleteCourseFolderForm;
import com.easylearning.api.form.expert.DeleteExpertFolderForm;
import com.easylearning.api.form.lesson.DeleteFolderLessonForm;
import com.easylearning.api.form.lesson.DeleteFolderLessonVersioningForm;
import com.easylearning.api.form.lesson.MoveVideoFileForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(name = "lifeuni-media", url = "${feign.lifeuni.media.url}", configuration = CustomFeignConfig.class)
public interface FeignLifeUniMediaService {

    @PostMapping(value = "/v1/file/delete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void deleteFiles(@RequestPart("urls") String urls, @RequestHeader("Authorization") String authorizationToken);

    @PostMapping(value = "/v1/file/delete-folder-lesson")
    void deleteLessonFolder(@RequestBody DeleteFolderLessonForm deleteFolderLessonForm, @RequestHeader("Authorization") String authorizationToken);
    @PostMapping(value = "/v1/file/delete-folder-lesson-versioning")
    void deleteLessonVersioningFolder(@RequestBody DeleteFolderLessonVersioningForm deleteFolderLessonVersioningForm, @RequestHeader("Authorization") String authorizationToken);

    @PostMapping(value = "/v1/file/delete-expert-folder")
    void deleteExpertFolder(@RequestBody DeleteExpertFolderForm deleteExpertFolderForm, @RequestHeader("Authorization") String authorizationToken);

    @PostMapping(value = "/v1/file/delete-course-folder")
    void deleteCourseFolder(@RequestBody DeleteCourseFolderForm deleteCourseFolderForm, @RequestHeader("Authorization") String authorizationToken);

    @PostMapping(value = "/v1/file/upload-image-from-url")
    ApiMessageDto<UploadFileDto> uploadImageFromUrl(
            @RequestBody UploadFileFromUrlForm uploadFileFromUrlForm,
            @RequestHeader("Authorization") String authorizationToken
    );

    @PostMapping(value = "/v1/file/move-video-file")
    ApiMessageDto<MoveVideoFileDto> moveVideoFile(@RequestBody MoveVideoFileForm moveVideoFileForm,
                                                  @RequestHeader("Authorization") String authorizationToken);
}
