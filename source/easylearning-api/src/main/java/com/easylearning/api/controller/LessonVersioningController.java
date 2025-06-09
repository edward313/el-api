package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.lesson.LessonAdminDto;
import com.easylearning.api.dto.lesson.LessonDetailDto;
import com.easylearning.api.dto.lessonVersioning.LessonVersioningDetailDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.LessonVersioningMapper;
import com.easylearning.api.model.Lesson;
import com.easylearning.api.model.LessonVersioning;
import com.easylearning.api.model.Version;
import com.easylearning.api.model.criteria.LessonCriteria;
import com.easylearning.api.model.criteria.LessonVersioningCriteria;
import com.easylearning.api.repository.LessonVersioningRepository;
import com.easylearning.api.repository.VersionRepository;
import com.easylearning.api.service.hls.HlsService;
import io.swagger.models.auth.In;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/lesson-versioning")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Log4j2
public class LessonVersioningController extends ABasicController{
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    private LessonVersioningMapper lessonVersioningMapper;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private HlsService hlsService;
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('LV_L')")
    public ApiMessageDto<ResponseListDto<List<LessonVersioningDetailDto>>> list(LessonVersioningCriteria lessonVersioningCriteria,Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<LessonVersioningDetailDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<LessonVersioning> lessonVersionings;
        if(lessonVersioningCriteria.getVersionId()!= null){
            Version version = versionRepository.findById(lessonVersioningCriteria.getVersionId()).orElse(null);
            if(version == null){
                throw new BadRequestException("Version not found", ErrorCode.VERSION_ERROR_NOT_FOUND);
            }
            lessonVersioningCriteria.setCourseId(version.getCourseId());
            lessonVersioningCriteria.setVersionCode(version.getVersionCode());
            lessonVersionings = lessonVersioningRepository.findAll(lessonVersioningCriteria.getSpecification(),pageable);
            Map<Long, LessonVersioning> maxVersionCodes = new HashMap<>();
            for (LessonVersioning lv : lessonVersionings.getContent()) {
                Long lessonId = lv.getVisualId();
                Integer versionCode = lv.getVersion().getVersionCode();
                // Kiểm tra xem lessonId đã tồn tại trong Map chưa và versionCode lớn hơn versionCode đã lưu
                if (!maxVersionCodes.containsKey(lessonId) || versionCode > maxVersionCodes.get(lessonId).getVersion().getVersionCode()) {
                    maxVersionCodes.put(lessonId, lv);
                }
            }
            List<LessonVersioning> maxVersionLessonVersionings = new ArrayList<>(maxVersionCodes.values());
            lessonVersionings = new PageImpl<>(maxVersionLessonVersionings, pageable, maxVersionLessonVersionings.size());
        }
        else {
            lessonVersionings = lessonVersioningRepository.findAll(lessonVersioningCriteria.getSpecification(),pageable);
        }
        ResponseListDto<List<LessonVersioningDetailDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(lessonVersioningMapper.fromEntityToLessonVersioningDetailDtoList(lessonVersionings.getContent()));
        responseListObj.setTotalPages(lessonVersionings.getTotalPages());
        responseListObj.setTotalElements(lessonVersionings.getTotalElements());


        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get lesson versioning list success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('LV_V')")
    public ApiMessageDto<LessonVersioningDetailDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<LessonVersioningDetailDto> apiMessageDto = new ApiMessageDto<>();
        LessonVersioning lessonVersioning;
        if(isExpert()){
            lessonVersioning = lessonVersioningRepository.findByIdAndExpertId(id, getCurrentUser()).orElse(null);
        }else {
            lessonVersioning = lessonVersioningRepository.findById(id).orElse(null);
        }
        if (lessonVersioning == null) {
            throw new NotFoundException("Lesson versioning is not found",ErrorCode.LESSON_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(lessonVersioningMapper.fromEntityToLessonVersioningDetailDto(lessonVersioning));
        if(lessonVersioning.getKind().equals(LifeUniConstant.LESSON_KIND_VIDEO)){
            apiMessageDto.getData().setVideoUrl(hlsService.getVideoUrl(lessonVersioning.getContent()));
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get lesson success.");
        return apiMessageDto;
    }
}
