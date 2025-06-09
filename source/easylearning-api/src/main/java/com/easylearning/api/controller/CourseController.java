package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.lesson.LessonDetailDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.course.*;
import com.easylearning.api.mapper.*;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.CourseCriteria;
import com.easylearning.api.model.criteria.CourseVersioningCriteria;
import com.easylearning.api.model.elastic.ElasticCourse;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.elastic.ElasticCourseService;
import com.easylearning.api.service.rabbitMQ.SyncDataElasticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/v1/course")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CourseController extends ABasicController{

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private CourseComboDetailRepository courseComboDetailRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private LessonMapper lessonMapper;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;
    @Autowired
    private CourseRetailRepository courseRetailRepository;
    @Autowired
    private CompletionRepository completionRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private ElasticCourseService elasticCourseService;
    @Autowired
    private CourseVersioningMapper courseVersioningMapper;
    @Autowired
    private CourseComboDetailMapper courseComboDetailMapper;
    @Autowired
    private CourseVersioningRepository courseVersioningRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private CourseComboDetailVersionRepository courseComboDetailVersionRepository;
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private ElasticCourseRepository elasticCourseRepository;
    @Autowired
    private CategoryHomeRepository categoryHomeRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CourseReviewHistoryRepository courseReviewHistoryRepository;
    @Value("${elastic.index.course}")
    private String courseIndex;
    @Autowired
    private SyncDataElasticService syncDataElasticService;

    @PostMapping("/lock-course")
    @PreAuthorize("hasRole('COU_U')")
    public ApiMessageDto<CourseDto> lockCourse(@Valid @RequestBody LockCourseForm lockCourseForm){
        ApiMessageDto<CourseDto> apiMessageDto = new ApiMessageDto<>();

        Course course = courseRepository.findByIdAndStatus(lockCourseForm.getId(), LifeUniConstant.STATUS_ACTIVE).orElse(null);
        if (course == null || (isExpert() && !course.getExpert().getId().equals(getCurrentUser()))) {
            throw new NotFoundException("Course not found");
        }
        course.setStatus(LifeUniConstant.STATUS_LOCK);
        courseRepository.save(course);
        syncDataElasticService.sendElasticDataMessage(course.getId());
        apiMessageDto.setData(courseMapper.fromEntityToCourseDtoAutoComplete(course));
        apiMessageDto.setMessage("Lock course successfully");
        return apiMessageDto;
    }

    @PostMapping("/unlock-course")
    @PreAuthorize("hasRole('COU_U')")
    public ApiMessageDto<CourseDto> unlockCourse(@Valid @RequestBody UnLockCourseForm form){
        ApiMessageDto<CourseDto> apiMessageDto = new ApiMessageDto<>();

        Course course = courseRepository.findByIdAndStatus(form.getId(), LifeUniConstant.STATUS_LOCK).orElse(null);
        if (course == null || (isExpert() && !course.getExpert().getId().equals(getCurrentUser()))) {
            throw new NotFoundException("Course not found");
        }
        course.setStatus(LifeUniConstant.STATUS_ACTIVE);
        courseRepository.save(course);
        syncDataElasticService.sendElasticDataMessage(course.getId());
        apiMessageDto.setData(courseMapper.fromEntityToCourseDtoAutoComplete(course));
        apiMessageDto.setMessage("unlock course successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_V')")
    public ApiMessageDto<CourseDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<CourseDto> apiMessageDto = new ApiMessageDto<>();
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            throw new NotFoundException("Course is not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        CourseDto courseDto = courseMapper.fromEntityToCourseDto(course);

        Version version = versionRepository.findHighestVersionByCourseId(courseDto.getId());
        courseDto.setVersion(versionMapper.fromEntityToVersionDtoForMyCourse(version));

        apiMessageDto.setData(getCourseDetail(courseDto));
        apiMessageDto.setData(courseDto);
        apiMessageDto.setMessage("Get course  success");
        return apiMessageDto;
    }
    @GetMapping(value = "/course-detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<CourseDto> getCourseDetail(@PathVariable("id") Long id, @RequestParam(value = "sellCode",required = false) String sellCode) {
        ApiMessageDto<CourseDto> apiMessageDto = new ApiMessageDto<>();
        Course course = courseRepository.findByIdAndStatus(id,LifeUniConstant.STATUS_ACTIVE).orElse(null);
        if (course == null) {
            throw new NotFoundException("Course is not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        CourseDto courseDto = courseMapper.fromEntityToCourseDtoForClient(course);
        courseDto.setIsSystemCourse(course.getExpert().getIsSystemExpert());

        apiMessageDto.setData(getCourseDetail(courseDto));
        apiMessageDto.setMessage("Get course detail success");
        return apiMessageDto;
    }
    private CourseDto getCourseDetail(CourseDto courseDto){

        List<Lesson> lessonList = lessonRepository.findAllByCourseIdAndStatusOrderByOrdering(courseDto.getId(), LifeUniConstant.STATUS_ACTIVE);
        List<LessonDetailDto> lessonDetailDtos = new ArrayList<>();
        Boolean checkBuyCourse = checkBuyCourse(courseDto.getId(),getCurrentUser());
        Boolean checkIsProcessing = checkIsProcessing(courseDto.getId(), getCurrentUser());
        courseDto.setIsBuy(checkBuyCourse);
        courseDto.setIsProcessing(checkIsProcessing);
        if(lessonList!=null) {
            for (Lesson lesson : lessonList) {
                LessonDetailDto lessonDetailDto = lessonMapper.fromEntityToLessonDtoForCourseDetail(lesson);
                if (checkBuyCourse) {
                    // find completion in completion table
                    Completion completion = completionRepository.findFirstByStudentIdAndCourseIdAndLessonIdAndExcludeLessonKind(getCurrentUser(), courseDto.getId(), lessonDetailDto.getId(), LifeUniConstant.LESSON_KIND_SECTION);
                    if(completion!=null){
                        lessonDetailDto.setIsDone(completion.getIsFinished()!=null && completion.getIsFinished());
                    }
                }
                lessonDetailDtos.add(lessonDetailDto);
            }
        }
        courseDto.setLessons(lessonDetailDtos);
        // set combo cho course
        if(Objects.equals(courseDto.getKind(), LifeUniConstant.COURSE_KIND_COMBO)){
            List<Course> coursesListChildren = courseRepository.findAllCourseByComboId(courseDto.getId());
            courseDto.setComboList(courseComboDetailMapper.fromEntityToComboDtoForComboList(coursesListChildren));
        }
        return courseDto;
    }

    private Boolean checkIsProcessing(Long courseId, Long studentId) {
        CourseTransaction courseTransaction = courseTransactionRepository.findFirstByCourseIdInAndStudentIdAndBookingState(courseId,studentId,LifeUniConstant.BOOKING_STATE_UNPAID);
        return courseTransaction != null;
    }
    private Boolean checkBuyCourse(Long courseId, Long studentId){
        Registration registration = registrationRepository.findFirstByCourseIdAndStudentId(courseId,studentId);
        return registration != null;
    }
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_L')")
    public ApiMessageDto<ResponseListDto<List<CourseDto>>> listVersioning(CourseVersioningCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CourseDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<CourseDto>> responseListObj = new ResponseListDto<>();
        criteria.setIgnoreVisualStatus(criteria.getIgnoreStatus());
        Page<CourseVersioning> courseVersionings = courseVersioningRepository.findAll(criteria.getSpecification(), pageable);
        //set maxVersion
        for (CourseVersioning courseVersioning: courseVersionings){
            Version version = versionRepository.findHighestVersionByCourseId(courseVersioning.getVisualId());
            if(version != null){
                if(version.getState().equals(LifeUniConstant.VERSION_STATE_INIT) && !Boolean.TRUE.equals(criteria.getIsMaxCourseVersioning())){
                    version = versionRepository.findHighestVersionByCourseIdIgnoreId(courseVersioning.getVisualId(),version.getId());
                }
                if(version != null){
                    courseVersioning.setVersion(version);
                }
            }
            if(Objects.equals(criteria.getStatus(),LifeUniConstant.STATUS_DELETE) || Objects.equals(criteria.getStatus(),LifeUniConstant.STATUS_LOCK)){
                courseVersioning.setStatus(criteria.getStatus());
            }
        }
        List<CourseDto> courseDtoList = courseMapper.fromCourseVersioningToCourseDtoList(courseVersionings.getContent());
        responseListObj.setContent(courseDtoList);
        responseListObj.setTotalPages(courseVersionings.getTotalPages());
        responseListObj.setTotalElements(courseVersionings.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course success");
        return responseListObjApiMessageDto;
    }
    @ApiIgnore
    @GetMapping(value = "/sync-elastic-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> runPayoutPeriod() {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed to run.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        try {
            elasticCourseService.resetIndex(courseIndex,ElasticCourse.class);
            List<Course> courseList = courseRepository.findAll();
            List<ElasticCourse> elasticCourses = courseMapper.fromEntityToElasticCourseList(courseList);
            elasticCourseRepository.saveAll(elasticCourses);
            log.error("Elastic search data is successfully synchronized");
        }catch (Exception e){
            log.error("Error occurred with sync:" + e.getMessage());
        }
        apiMessageDto.setMessage("sync course data success");
        return apiMessageDto;
    }
    @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CourseDto>>> clientList(CourseCriteria courseCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CourseDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        courseCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        ResponseListDto<List<CourseDto>> responseListObj = new ResponseListDto<>();
        List<CourseDto> courseDtoList = new ArrayList<>();
        int totalPages;
        long totalElements;
        if(courseCriteria.getQuery()!=null){
            Page<ElasticCourse> courseList = elasticCourseService.findAll(courseCriteria, pageable);
            for(ElasticCourse course: courseList){
                CourseDto courseDto = courseMapper.fromElasticEntityToCourseDtoForClient(course);
                courseDto.setIsBuy(checkBuyCourse(courseDto.getId(),getCurrentUser()));
                courseDtoList.add(courseDto);
            }
            totalPages = courseList.getTotalPages();
            totalElements = courseList.getTotalElements();
        }
        else {
            if(courseCriteria.getCategoryIds()!=null){
                pageable = PageRequest.of(0,10);
            }
            Page<Course> coursePages;
            if(courseCriteria.getCategoryId()!=null){
                coursePages = getCoursePagesAfterSort(courseCriteria,pageable);
            }else {
                coursePages = courseRepository.findAll(courseCriteria.getSpecification(), pageable);
            }
            courseDtoList = courseMapper.fromEntityToCourseDtoListForClient(coursePages.getContent());
            totalPages = coursePages.getTotalPages();
            totalElements = coursePages.getTotalElements();
        }
        responseListObj.setContent(courseDtoList);
        responseListObj.setTotalPages(totalPages);
        responseListObj.setTotalElements(totalElements);
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course success");
        return responseListObjApiMessageDto;
    }
    private Page<Course> getCoursePagesAfterSort(CourseCriteria courseCriteria,Pageable pageable){
        Long categoryId = courseCriteria.getCategoryId();

        List<Course> courseCategoryHomePages = courseRepository.findAll(courseCriteria.getSpecification(), sortCourseByKind(categoryId));

        List<Long> courseIdsFromCategoryHomePages = courseCategoryHomePages
                .stream()
                .map(Course::getId)
                .collect(Collectors.toList());

        if(!isKindTopNew(courseCriteria.getCategoryId())){
            courseCriteria.setFieldId(courseCriteria.getCategoryId());
        }
        courseCriteria.setCategoryId(null);
        courseCriteria.setCourseDuplicateIds(courseIdsFromCategoryHomePages);

        List<Course> courseCategoryPages = courseRepository.findAll(courseCriteria.getSpecification(), sortCourseByKind(categoryId));
        List<Course> courses = Stream.concat(
                        courseCategoryHomePages.stream(),
                        courseCategoryPages.stream()
                )
                .collect(Collectors.toList());

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int fromIndex = Math.min(pageNumber * pageSize, courses.size());
        int toIndex = Math.min(fromIndex + pageSize, courses.size());
        List<Course> pagedCourses = courses.subList(fromIndex, toIndex);

        return new PageImpl<>(
                pagedCourses,
                PageRequest.of(pageNumber, pageSize),
                courses.size()
        );
    }
    Boolean isKindTopNew(Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElse(null);
        return category != null && Objects.equals(category.getKind(), LifeUniConstant.CATEGORY_KIND_TOP_NEW);
    }
    private Sort sortCourseByKind(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            if (Objects.equals(category.getKind(), LifeUniConstant.CATEGORY_KIND_TOP_FREE)
                    || Objects.equals(category.getKind(), LifeUniConstant.CATEGORY_KIND_TOP_CHARGE)) {
                return Sort.by(Sort.Direction.DESC, "soldQuantity");
            } else {
                return Sort.by(Sort.Direction.DESC, "createdDate");
            }
        }
        return Sort.unsorted();
    }
    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CourseDto>>> autoComplete(CourseCriteria courseCriteria) {
        ApiMessageDto<ResponseListDto<List<CourseDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();

        Pageable pageable = PageRequest.of(0,10);
        courseCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);

        Page<Course> courseList = courseRepository.findAll(courseCriteria.getSpecification(), pageable);
        ResponseListDto<List<CourseDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(courseMapper.fromEntityToCourseDtoAutoCompleteList(courseList.getContent()));
        responseListObj.setTotalPages(courseList.getTotalPages());
        responseListObj.setTotalElements(courseList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course auto-complete success");
        return responseListObjApiMessageDto;
    }
    @Transactional
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateCourseForm createCourseForm, BindingResult bindingResult) {
        if (!isExpert()) {
            throw new UnauthorizationException("Not allowed create");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(courseVersioningRepository.countCourseVersioningWithSameName(createCourseForm.getName(),null,LifeUniConstant.STATUS_DELETE,getCurrentUser())>0){
            throw new NotFoundException("Course is exist",ErrorCode.COURSE_ERROR_EXIST);
        }
        Course existCourseName = courseRepository.findFirstByNameAndExpertIdAndStatus(createCourseForm.getName(),getCurrentUser(),LifeUniConstant.STATUS_ACTIVE);
        if(existCourseName != null){
            throw new NotFoundException("Course is exist",ErrorCode.COURSE_ERROR_EXIST);
        }
        Category field = categoryRepository.findById(createCourseForm.getFieldId()).orElse(null);
        if (field == null) {
            throw new NotFoundException("Category is not found",ErrorCode.CATEGORY_ERROR_NOT_FOUND);
        }
        checkCategoryKind(field,LifeUniConstant.CATEGORY_KIND_SPECIALIZED);
        Expert expert = expertRepository.findById(getCurrentUser()).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert is not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        if (Objects.equals(createCourseForm.getKind(), LifeUniConstant.COURSE_KIND_SINGLE) && createCourseForm.getChildrenDetails() != null) {
            throw new BadRequestException("Course kind single cannot have a children",ErrorCode.COURSE_ERROR_NOT_TRUE_KIND);
        }
        if(createCourseForm.getKind().equals(LifeUniConstant.COURSE_KIND_COMBO)
                && (createCourseForm.getChildrenDetails() == null || createCourseForm.getChildrenDetails().length == 0)){
            throw new BadRequestException("Course combo must contain children course",ErrorCode.COURSE_ERROR_NOT_CONTAIN_CHILDREN_COURSE);
        }
        if(Objects.equals(createCourseForm.getPrice(),0.0)){
            createCourseForm.setSaleOff(0);
        }
        Course course = courseMapper.fromCreateCourseFormToEntity(createCourseForm);
        if (createCourseForm.getIsSellerCourse() != null && createCourseForm.getIsSellerCourse()) {
            if(expert.getIsSystemExpert() == null || !expert.getIsSystemExpert()){
                throw new BadRequestException("Just allow set isSystemCourse with System Expert",ErrorCode.COURSE_ERROR_NOT_ALLOW_CREATE_IS_SYSTEM_COURSE);
            }
            course.setIsSellerCourse(true);
        }
        course.setStatus(LifeUniConstant.STATUS_PENDING);
        course.setField(field);
        course.setExpert(expert);
        courseRepository.save(course);

        // create course versioning for course not combo
        Version version = new Version();
        version.setCourseId(course.getId());
        version.setDate(new Date());
        version.setState(LifeUniConstant.VERSION_STATE_INIT);
        version.setVersionCode(LifeUniConstant.VERSION_CODE_INIT);
        versionRepository.save(version);

        course.setVersion(version);
        courseRepository.save(course);

        CourseVersioning courseVersioning = courseVersioningMapper.fromCourseEntityToCourseVersioning(course);
        courseVersioning.setField(course.getField());
        courseVersioning.setExpert(course.getExpert());
        courseVersioning.setVersion(version);
        courseVersioning.setVisualId(course.getId());
        courseVersioningRepository.save(courseVersioning);
        // create combo
        if (course.getKind().equals(LifeUniConstant.COURSE_KIND_COMBO) && createCourseForm.getChildrenDetails() != null) {
            List<Long> resolvedIds = new ArrayList<>();
            for (ChildrenDetailForm parentDetail : createCourseForm.getChildrenDetails()) {
                if(!resolvedIds.contains(parentDetail.getChildrenId())){
                    createComboAndComboVersion(course,courseVersioning,parentDetail.getChildrenId());
                    resolvedIds.add(parentDetail.getChildrenId());
                }
            }
            if(resolvedIds.size() < 2){
                throw new BadRequestException("Course combo must contain at least 2 children course",ErrorCode.COURSE_ERROR_NOT_CONTAIN_CHILDREN_COURSE);
            }
        }
        apiMessageDto.setMessage("Create course success");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateCourseForm updateCourseForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if (!isExpert() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed update");
        }
        if (isExpert()) {
            Course course = courseRepository.findFirstByIdAndStatusNot(updateCourseForm.getId(), LifeUniConstant.STATUS_DELETE).orElse(null);
            if (course == null) {
                throw new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND);
            }
            if (courseVersioningRepository.countCourseVersioningWithSameName(updateCourseForm.getName(), updateCourseForm.getId(), LifeUniConstant.STATUS_DELETE, course.getExpert().getId()) > 0) {
                throw new NotFoundException("Course is exist", ErrorCode.COURSE_ERROR_EXIST);
            }
            Course existCourseName = courseRepository.findFirstByNameAndExpertIdAndStatus(updateCourseForm.getName(), course.getExpert().getId(), LifeUniConstant.STATUS_ACTIVE);
            if (existCourseName != null && !Objects.equals(course.getId(), existCourseName.getId())) {
                throw new NotFoundException("Course is exist", ErrorCode.COURSE_ERROR_EXIST);
            }

            CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(updateCourseForm.getId());
            if (courseVersioning == null) {
                throw new NotFoundException("Course is not found", ErrorCode.COURSE_ERROR_NOT_FOUND);
            }
            if (courseVersioning.getKind().equals(LifeUniConstant.COURSE_KIND_COMBO) &&
                    (updateCourseForm.getChildrenDetails() == null || updateCourseForm.getChildrenDetails().length == 0)) {
                throw new BadRequestException("Course combo must contain children course", ErrorCode.COURSE_ERROR_NOT_CONTAIN_CHILDREN_COURSE);
            }
            Version version = getInitVersion(updateCourseForm.getId());
            if (isExpert() && !Objects.equals(courseVersioning.getExpert().getId(), getCurrentUser())) {
                throw new NotFoundException("Can not update course that are not yours", ErrorCode.COURSE_ERROR_EXPERT_ONLY_MODIFY_THEIR_COURSE);
            }
            if (!courseVersioning.getVersion().getId().equals(version.getId())) {
                courseVersioning = createNewCourseVersioningFromOldCourseVersioning(courseVersioning.getVisualId(), version);
            }
            // create course combo
            if (courseVersioning.getKind().equals(LifeUniConstant.COURSE_KIND_COMBO) && updateCourseForm.getChildrenDetails() != null) {
                // lấy danh sách unique ids trong form
                List<Long> ids = new ArrayList<>();
                for (ChildrenDetailForm childrenDetail : updateCourseForm.getChildrenDetails()) {
                    if (!ids.contains(childrenDetail.getChildrenId())) {
                        ids.add(childrenDetail.getChildrenId());
                    }
                }
                if (ids.size() < 2) {
                    throw new BadRequestException("Course combo must contain at least 2 children course", ErrorCode.COURSE_ERROR_NOT_CONTAIN_CHILDREN_COURSE);
                }
                courseComboDetailVersionRepository.deleteByComboIdAndNotInCourseIds(ids, courseVersioning.getId());
                for (Long id : ids) {
                    if (!courseComboDetailVersionRepository.existsByComboIdAndCourseId(courseVersioning.getId(), id)) {
                        createComboVersion(courseVersioning, id);
                    }
                }
            }
            Category field = categoryRepository.findById(updateCourseForm.getFieldId()).orElse(null);
            if (field == null) {
                throw new NotFoundException("Category is not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            }
            checkCategoryKind(field, LifeUniConstant.CATEGORY_KIND_SPECIALIZED);
            if (Boolean.TRUE.equals(updateCourseForm.getIsSellerCourse()) && !courseVersioning.getExpert().getIsSystemExpert()) {
                throw new BadRequestException("Just allow set isSystemCourse with System Expert", ErrorCode.COURSE_ERROR_NOT_ALLOW_CREATE_IS_SYSTEM_COURSE);
            }
            if (Objects.equals(updateCourseForm.getPrice(), 0.0)) {
                updateCourseForm.setSaleOff(0);
            }
            courseVersioningMapper.fromUpdateCourseFormToEntity(updateCourseForm, courseVersioning);
            courseVersioning.setField(field);
            courseVersioningRepository.save(courseVersioning);
        }else if (updateCourseForm.getStatus() != null) {
            Course course = courseRepository.findById(updateCourseForm.getId()).orElse(null);
            if (course == null) {
                throw new BadRequestException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND);
            }
            if (course.getStatus() != updateCourseForm.getStatus()) {
                course.setStatus(updateCourseForm.getStatus());
                courseRepository.save(course);
            }
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Update course success.");
        return apiMessageDto;
    }
    void createComboAndComboVersion(Course comboCourse, CourseVersioning comboCourseVersion, Long childrenCourseId){
        Course childrenCourse = courseRepository.findByIdAndKindAndExpertIdAndStatus(childrenCourseId, LifeUniConstant.COURSE_KIND_SINGLE,getCurrentUser(),LifeUniConstant.STATUS_ACTIVE).orElse(null);
        // Nếu childrenCourse tồn tại, thêm mới record
        if (childrenCourse != null) {
            // create ComboDetail
            CourseComboDetail newCombo = new CourseComboDetail();
            newCombo.setCombo(comboCourse);
            newCombo.setCourse(childrenCourse);
            courseComboDetailRepository.save(newCombo);
            // create ComboDetailVersion
            CourseComboDetailVersion newComboVersion = new CourseComboDetailVersion();
            newComboVersion.setCombo(comboCourseVersion);
            newComboVersion.setCourse(childrenCourse);
            courseComboDetailVersionRepository.save(newComboVersion);
        }
    }
    void createComboVersion(CourseVersioning comboCourse, Long childrenCourseId){
        Course childrenCourse = courseRepository.findByIdAndKindAndExpertIdAndStatus(childrenCourseId, LifeUniConstant.COURSE_KIND_SINGLE,getCurrentUser(),LifeUniConstant.STATUS_ACTIVE).orElse(null);
        if (childrenCourse != null) {
            CourseComboDetailVersion newCombo = new CourseComboDetailVersion();
            newCombo.setCombo(comboCourse);
            newCombo.setCourse(childrenCourse);
            courseComboDetailVersionRepository.save(newCombo);
        }
    }
    @Transactional
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Course course = courseRepository.findFirstByIdAndStatusNot(id,LifeUniConstant.STATUS_DELETE).orElse(null);
        if(course == null){
            throw new NotFoundException("Course is not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if(isExpert() && !Objects.equals(course.getExpert().getId(),getCurrentUser())){
            throw new BadRequestException("course is not yours",ErrorCode.COURSE_ERROR_COURSE_IS_NOT_YOURS);
        }
        cartItemRepository.deleteAllByCourseId(id);
        // cancel version submit or processing
        Version version = versionRepository.findHighestVersionByCourseId(id);
        if(version!=null){
            if(!version.getState().equals(LifeUniConstant.VERSION_STATE_INIT) && !version.getState().equals(LifeUniConstant.VERSION_STATE_APPROVE)){
                courseReviewHistoryRepository.deleteAllByVersionId(version.getId());
                version.setState(LifeUniConstant.VERSION_STATE_INIT);
                versionRepository.save(version);
            }
            CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(id);
            if (courseVersioning != null) {
                if(!courseVersioning.getVersion().getId().equals(version.getId())){
                    courseVersioning = createNewCourseVersioningFromOldCourseVersioning(courseVersioning.getVisualId(),version);
                }
                courseVersioning.setStatus(LifeUniConstant.STATUS_DELETE);
                courseVersioningRepository.save(courseVersioning);
            }
        }
        // course already approve
        if(Objects.equals(course.getStatus(),LifeUniConstant.STATUS_ACTIVE)){
            //set total course & total lesson time for expert
            Expert expert = course.getExpert();
            if(expert.getTotalCourse() == null){
                expert.setTotalCourse(0);
            }
            if(expert.getTotalLessonTime() == null){
                expert.setTotalLessonTime(0L);
            }
            if(course.getTotalStudyTime() == null){
                course.setTotalStudyTime(0L);
            }
            expert.setTotalCourse(expert.getTotalCourse() - 1);
            expert.setTotalLessonTime(expert.getTotalLessonTime() - course.getTotalStudyTime());
            if(expert.getTotalCourse() < 0){
                expert.setTotalCourse(0);
            }
            if(expert.getTotalLessonTime() < 0){
                expert.setTotalLessonTime(0L);
            }
            expertRepository.save(expert);
            elasticCourseRepository.deleteByCourseId(course.getId());
            categoryHomeRepository.deleteByCourse(id);
            course.setStatus(LifeUniConstant.STATUS_DELETE);
            courseRepository.save(course);
            updateAllCategoryHome();
        }else {
            course.setStatus(LifeUniConstant.STATUS_DELETE);
            courseRepository.save(course);
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete course success");
        return apiMessageDto;
    }
}
