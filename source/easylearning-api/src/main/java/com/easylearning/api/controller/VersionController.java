package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.courseVersioning.CourseVersioningDto;
import com.easylearning.api.dto.version.VersionDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.CourseComboDetailVersionMapper;
import com.easylearning.api.mapper.CourseVersioningMapper;
import com.easylearning.api.mapper.LessonVersioningMapper;
import com.easylearning.api.mapper.VersionMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.VersionCriteria;
import com.easylearning.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/version")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class VersionController extends ABasicController {

    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private CourseVersioningMapper courseVersioningMapper;
    @Autowired
    private CourseVersioningRepository courseVersioningRepository;
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    private LessonVersioningMapper lessonVersioningMapper;
    @Autowired
    private CourseComboDetailVersionRepository courseComboDetailVersionRepository;
    @Autowired
    private CourseComboDetailVersionMapper courseComboDetailVersionMapper;
    @Autowired
    private CourseRepository courseRepository;


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('V_L')")
    public ApiMessageDto<ResponseListDto<List<VersionDto>>> list(VersionCriteria versionCriteria, Pageable pageable) {
        if(isExpert()){
            versionCriteria.setExpertId(getCurrentUser());
        }
        ApiMessageDto<ResponseListDto<List<VersionDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Version> versions = versionRepository.findAll(versionCriteria.getSpecification(), pageable);
        ResponseListDto<List<VersionDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(versionMapper.fromEntityToAdminDtoList(versions.getContent()));
        responseListObj.setTotalPages(versions.getTotalPages());
        responseListObj.setTotalElements(versions.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get version list success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/preview-course-by-version/{versionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('V_GCBV')")
    public ApiMessageDto<CourseVersioningDto> previewCourseByVersion(@PathVariable("versionId") Long versionId) {
        ApiMessageDto<CourseVersioningDto> apiMessageDto = new ApiMessageDto<>();
        Version version = versionRepository.findById(versionId).orElse(null);
        if(version == null){
            throw new NotFoundException("Version not found", ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        Course course = courseRepository.findFirstByIdAndStatusNot(version.getCourseId(), LifeUniConstant.STATUS_DELETE).orElse(null);
        if(course == null){
            throw new NotFoundException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        Version latestVersion = versionRepository.findHighestVersionByCourseId(version.getCourseId());
        if(!Objects.equals(latestVersion.getId(), version.getId())){
            throw new BadRequestException("Version not is latest of course", ErrorCode.VERSION_ERROR_NOT_LATEST);
        }
        CourseVersioning courseVersioning;
        courseVersioning = courseVersioningRepository.findFirstByVersionId(versionId);
        if(courseVersioning == null){
            courseVersioning = courseVersioningRepository.findLatestCourseVersioningUpToVersionCode(version.getCourseId(),version.getVersionCode());
        }
        CourseVersioningDto courseVersioningDto = courseVersioningMapper.fromCourseVersioningToDto(courseVersioning);
        if(courseVersioning.getKind().equals(LifeUniConstant.COURSE_KIND_SINGLE)){
            List<LessonVersioning> lessonVersionings = lessonVersioningRepository.findAllLessonVersioningUpToVersionCodeAndNotDelete(courseVersioningDto.getVisualId(),version.getVersionCode(),LifeUniConstant.STATUS_DELETE);
            courseVersioningDto.setLessons(lessonVersioningMapper.fromEntityToLessonVersioningDetailDtoList(lessonVersionings));
        }else if(courseVersioning.getKind().equals(LifeUniConstant.COURSE_KIND_COMBO)){
            List<CourseComboDetailVersion> courseComboDetailVersions = courseComboDetailVersionRepository.findAllByComboId(courseVersioning.getId());
            courseVersioningDto.setComboList(courseComboDetailVersionMapper.fromEntityToComboDtoForComboList(courseComboDetailVersions));
        }
        courseVersioningDto.setVersion(versionMapper.fromEntityToDtoForVisual(version));
        apiMessageDto.setData((courseVersioningDto));
        apiMessageDto.setMessage("Get course success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get-course-by-version/{versionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('V_GCBV')")
    public ApiMessageDto<CourseVersioningDto> get(@PathVariable("versionId") Long versionId) {
        ApiMessageDto<CourseVersioningDto> apiMessageDto = new ApiMessageDto<>();
        Version version = versionRepository.findById(versionId).orElse(null);
        if(version == null){
            throw new NotFoundException("Version not found", ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        if(isExpert()){
            Course course = courseRepository.findFirstByIdAndStatusNot(version.getCourseId(),LifeUniConstant.STATUS_DELETE).orElse(null);
            if(course == null){
                throw new NotFoundException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
            }
        }
        CourseVersioning courseVersioning;
        courseVersioning = courseVersioningRepository.findFirstByVersionId(versionId);
        if(courseVersioning == null){
            courseVersioning = courseVersioningRepository.findLatestCourseVersioningUpToVersionCode(version.getCourseId(),version.getVersionCode());
        }
        CourseVersioningDto courseVersioningDto = courseVersioningMapper.fromCourseVersioningToDto(courseVersioning);
        if(courseVersioning.getKind().equals(LifeUniConstant.COURSE_KIND_SINGLE)){
            List<LessonVersioning> lessonVersionings;
            if(isExpert()){ //expert not get deleted lesson
                lessonVersionings = lessonVersioningRepository.findAllLessonVersioningUpToVersionCodeAndNotDelete(courseVersioningDto.getVisualId(),version.getVersionCode(),LifeUniConstant.STATUS_DELETE);
            }else {
                lessonVersionings = lessonVersioningRepository.findAllLessonVersioningUpToVersionCode(courseVersioningDto.getVisualId(),version.getVersionCode(),LifeUniConstant.STATUS_DELETE);
            }
            courseVersioningDto.setLessons(lessonVersioningMapper.fromEntityToLessonVersioningDetailDtoList(lessonVersionings));
        }else if(courseVersioning.getKind().equals(LifeUniConstant.COURSE_KIND_COMBO)){
            List<CourseComboDetailVersion> courseComboDetailVersions = courseComboDetailVersionRepository.findAllByComboId(courseVersioning.getId());
            courseVersioningDto.setComboList(courseComboDetailVersionMapper.fromEntityToComboDtoForComboList(courseComboDetailVersions));
        }
        courseVersioningDto.setVersion(versionMapper.fromEntityToDtoForVisual(version));
        apiMessageDto.setData((courseVersioningDto));
        apiMessageDto.setMessage("Get course success");
        return apiMessageDto;
    }
}
