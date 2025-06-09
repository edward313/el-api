package com.easylearning.api.controller;


import com.google.common.base.Objects;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.review.AmountReviewDetailDto;
import com.easylearning.api.dto.review.AmountReviewDto;
import com.easylearning.api.dto.review.ReviewAdminDto;
import com.easylearning.api.dto.review.ReviewDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.review.CreateReviewForm;
import com.easylearning.api.form.review.UpdateReviewForm;
import com.easylearning.api.mapper.ReviewMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.ReviewCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.rabbitMQ.SyncDataElasticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/review")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ReviewController extends ABasicController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SyncDataElasticService syncDataElasticService;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REV_V')")
    public ApiMessageDto<ReviewAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<ReviewAdminDto> apiMessageDto = new ApiMessageDto<>();
        Review review =  reviewRepository.findById(id).orElse(null);
        if (review == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.REVIEW_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        apiMessageDto.setData(reviewMapper.fromEntityToReviewAdminDto(review));
        apiMessageDto.setMessage("Get review success.");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REV_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateReviewForm createReviewForm, BindingResult bindingResult) {
        if (!isStudent() && !isAdmin()) {
            throw new UnauthorizationException("Not allowed create");
        }
        // check user kind  with review kind
        if (isAdmin()) {
            if (createReviewForm.getStudentId() == null) {
                throw new BadRequestException("Admin cannot create a review without specifying a student ID.", ErrorCode.REVIEW_ERROR_NULL_STUDENT_ID);
            }
            if (!LifeUniConstant.REVIEW_KIND_SYSTEM.equals(createReviewForm.getKind())) {
                throw new BadRequestException("Admin can only create system reviews.", ErrorCode.REVIEW_ERROR_KIND_NOT_ALLOW_FOR_CURRENT_USER);
            }
        }
        if (isStudent() && LifeUniConstant.REVIEW_KIND_SYSTEM.equals(createReviewForm.getKind())) {
            throw new BadRequestException("Only admins are allowed to create system reviews.", ErrorCode.REVIEW_ERROR_USER_NOT_ADMIN);
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student;
        Review review = reviewMapper.fromCreateFormToEntity(createReviewForm);
        // review course
        if(createReviewForm.getKind().equals(LifeUniConstant.REVIEW_KIND_COURSE)){
            if(createReviewForm.getCourseId() == null){
                throw new BadRequestException("courseId cannot null when create course review",ErrorCode.REVIEW_ERROR_NOT_ENTER_COURSE_ID);
            }
            Course course = courseRepository.findById(createReviewForm.getCourseId()).orElse(null);
            checkNotNullAndActiveCourse(course);
            if (reviewRepository.existsByCourseIdAndStudentIdAndKind(createReviewForm.getCourseId(),getCurrentUser(),createReviewForm.getKind())) {
                throw new BadRequestException("Student already review this course",ErrorCode.REVIEW_ERROR_STUDENT_REVIEWED);
            }
            Registration registration = registrationRepository.findFirstByCourseIdAndStudentId(createReviewForm.getCourseId(),getCurrentUser());
            if (registration == null) {
                throw new NotFoundException("Registration not found",ErrorCode.REGISTRATION_ERROR_NOT_FOUND);
            }
            student = registration.getStudent();
            checkNotNullAndActiveStudent(student);
            review.setCourse(course);
            // set course AverageStar & totalReview
            if(course.getAverageStar() == null){
                course.setAverageStar(0F);
            }
            if(course.getTotalReview() == null){
                course.setTotalReview(0);
            }
            course.setAverageStar((course.getAverageStar() * course.getTotalReview() + createReviewForm.getStar())/(course.getTotalReview()+1));
            course.setTotalReview(course.getTotalReview()+1);
            courseRepository.save(course);
            syncDataElasticService.sendElasticDataMessage(course.getId());
        } else if (createReviewForm.getKind().equals(LifeUniConstant.REVIEW_KIND_EXPERT)){ // review expert
            if(createReviewForm.getExpertId() == null){
                throw new BadRequestException("ExpertId cannot null when create expert review",ErrorCode.REVIEW_ERROR_NOT_ENTER_EXPERT_ID);
            }
            Expert expert = expertRepository.findById(createReviewForm.getExpertId()).orElse(null);
            if(expert == null){
                throw new BadRequestException("Expert not found", ErrorCode.EXPERT_ERROR_NOT_FOUND);
            }
            if (reviewRepository.existsByExpertIdAndStudentIdAndKind(createReviewForm.getExpertId(),getCurrentUser(),createReviewForm.getKind())) {
                throw new BadRequestException("Student already review this expert",ErrorCode.REVIEW_ERROR_STUDENT_REVIEWED);
            }
            Registration registration = registrationRepository.findFirstByExpertIdAndStudentId(createReviewForm.getExpertId(),getCurrentUser());
            if (registration == null) {
                throw new NotFoundException("Registration not found",ErrorCode.REGISTRATION_ERROR_NOT_FOUND);
            }
            student = registration.getStudent();
            checkNotNullAndActiveStudent(student);
            review.setExpert(expert);
        } else { // review system
            student = studentRepository.findById(createReviewForm.getStudentId()).orElse(null);
            checkNotNullAndActiveStudent(student);
            if (reviewRepository.existsByStudentIdAndKind(student.getId(),LifeUniConstant.REVIEW_KIND_SYSTEM)) {
                throw new BadRequestException("Student already review system",ErrorCode.REVIEW_ERROR_STUDENT_REVIEWED);
            }

            Course course = courseRepository.findById(createReviewForm.getCourseId()).orElse(null);
            checkNotNullAndActiveCourse(course);
            review.setCourse(course);
        }
        review.setStudent(student);
        reviewRepository.save(review);
        apiMessageDto.setMessage("Create service review success");
        return apiMessageDto;
    }


    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REV_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateReviewForm updateReviewForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Review review;
        if (isStudent()) {
            review = reviewRepository.findByIdAndStudentId(updateReviewForm.getId(),getCurrentUser()).orElse(null);
        }else {
            review = reviewRepository.findById(updateReviewForm.getId()).orElse(null);
        }
        if (review == null) {
            throw new NotFoundException("Review not found",ErrorCode.REVIEW_ERROR_NOT_FOUND);
        }
        if (isStudent() && !Objects.equal(review.getCourse().getStatus(), LifeUniConstant.STATUS_ACTIVE)) {
            throw new BadRequestException("Course not active",ErrorCode.COURSE_ERROR_NOT_ACTIVE);
        }
        if(java.util.Objects.equals(review.getKind(), LifeUniConstant.REVIEW_KIND_COURSE)){
            Course course = review.getCourse();
            if(course.getAverageStar() == null){
                course.setAverageStar(0F);
            }
            if(course.getTotalReview() == null){
                course.setTotalReview(0);
            }
            course.setAverageStar((course.getAverageStar() * course.getTotalReview() - review.getStar() + updateReviewForm.getStar())/(course.getTotalReview()));
            courseRepository.save(course);
            syncDataElasticService.sendElasticDataMessage(course.getId());
        }
        if(updateReviewForm.getCourseId() != null){
            if(!isAdmin() || !review.getKind().equals(LifeUniConstant.REVIEW_KIND_SYSTEM)){
                throw new BadRequestException("Just allow admin update course in system review",ErrorCode.REVIEW_ERROR_KIND_JUST_ALLOW_ADMIN_UPDATE_COURSE_IN_SYSTEM_REVIEW);
            }
            Course course = courseRepository.findById(updateReviewForm.getCourseId()).orElse(null);
            checkNotNullAndActiveCourse(course);
            review.setCourse(course);
        }
        reviewMapper.fromUpdateFormToEntity(updateReviewForm, review);
        reviewRepository.save(review);
        apiMessageDto.setMessage("Update service review success.");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REV_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Review review;
        if(isStudent()){
            review = reviewRepository.findByIdAndStudentId(id,getCurrentUser()).orElse(null);
        }
        else {
            review = reviewRepository.findById(id).orElse(null);
        }
        if (review == null) {
            throw new NotFoundException("Review not found",ErrorCode.REVIEW_ERROR_NOT_FOUND);
        }
        if (isStudent() && !Objects.equal(review.getCourse().getStatus(), LifeUniConstant.STATUS_ACTIVE)) {
            throw new BadRequestException("Course not active",ErrorCode.COURSE_ERROR_NOT_ACTIVE);
        }
        if(java.util.Objects.equals(review.getKind(), LifeUniConstant.REVIEW_KIND_COURSE)) {
            Course course = review.getCourse();
            if(course.getTotalReview() != 1){
                course.setAverageStar((course.getAverageStar() * course.getTotalReview() - review.getStar()) / (course.getTotalReview() - 1));
            }else {
                course.setAverageStar(0F);
            }
            if(course.getAverageStar() < 0){
                course.setAverageStar(0F);
            }
            if(course.getTotalReview() < 0){
                course.setTotalReview(0);
            }
            course.setTotalReview(course.getTotalReview() - 1);
            courseRepository.save(course);
            syncDataElasticService.sendElasticDataMessage(course.getId());
        }
        reviewRepository.deleteById(id);
        apiMessageDto.setMessage("Delete service review success.");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REV_L')")
    public ApiMessageDto<ResponseListDto<List<ReviewAdminDto>>> list(ReviewCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<ReviewAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Review> reviews = reviewRepository.findAll(criteria.getCriteria(), pageable);
        ResponseListDto<List<ReviewAdminDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(reviewMapper.fromEntityListToAdminDtoList(reviews.getContent()));
        responseListObj.setTotalPages(reviews.getTotalPages());
        responseListObj.setTotalElements(reviews.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list review success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ReviewDto>>> clientList(ReviewCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<ReviewDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        criteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<Review> reviews = reviewRepository.findAll(criteria.getCriteria(), pageable);
        ResponseListDto<List<ReviewDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(reviewMapper.fromEntityListToAutoCompleteList(reviews.getContent()));
        responseListObj.setTotalPages(reviews.getTotalPages());
        responseListObj.setTotalElements(reviews.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list review success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ReviewDto>>> autoComplete(ReviewCriteria criteria) {
        ApiMessageDto<ResponseListDto<List<ReviewDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Pageable pageable = PageRequest.of(0,10);
        criteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<Review> reviews = reviewRepository.findAll(criteria.getCriteria(), pageable);
        ResponseListDto<List<ReviewDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(reviewMapper.fromEntityListToAutoCompleteList(reviews.getContent()));
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list review success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/list-reviews/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ReviewDto>>> listReviews(@PathVariable Long courseId, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<ReviewDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        checkValidCourse(courseId);
        Page<Review> reviews = reviewRepository.findAllByCourseIdAndStatusAndKind(courseId,LifeUniConstant.STATUS_ACTIVE,LifeUniConstant.REVIEW_KIND_COURSE,pageable);
        ResponseListDto<List<ReviewDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(reviewMapper.fromEntityToGetMyReviewDtoList(reviews.getContent()));
        responseListObj.setTotalPages(reviews.getTotalPages());
        responseListObj.setTotalElements(reviews.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list review success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/list-expert-reviews/{expertId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ReviewDto>>> listExpertReview(@PathVariable Long expertId, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<ReviewDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Review> reviews = reviewRepository.findAllByExpertIdAndStatusAndKind(expertId,LifeUniConstant.STATUS_ACTIVE,LifeUniConstant.REVIEW_KIND_EXPERT,pageable);
        ResponseListDto<List<ReviewDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(reviewMapper.fromEntityToGetMyReviewDtoList(reviews.getContent()));
        responseListObj.setTotalPages(reviews.getTotalPages());
        responseListObj.setTotalElements(reviews.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list review success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/my-review", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ReviewDto>>> myReview(ReviewCriteria criteria, Pageable pageable) {
        if (!isStudent()) {
            throw new UnauthorizationException("Not allowed get.");
        }
        criteria.setStudentId(getCurrentUser());
        ApiMessageDto<ResponseListDto<List<ReviewDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Review> reviews = reviewRepository.findAll(criteria.getCriteria(), pageable);
        ResponseListDto<List<ReviewDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(reviewMapper.fromEntityToGetMyReviewDtoList(reviews.getContent()));
        responseListObj.setTotalPages(reviews.getTotalPages());
        responseListObj.setTotalElements(reviews.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get my review success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/star/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<AmountReviewDto> reviewCourseStar(@PathVariable Long courseId) {
        ApiMessageDto<AmountReviewDto> reviewDtoApiMessageDto = new ApiMessageDto<>();
        checkValidCourse(courseId);
        List<AmountReviewDetailDto> amountReviewDetailDtos = reviewRepository.groupByStar(courseId,LifeUniConstant.REVIEW_KIND_COURSE);
        getAmountReview(amountReviewDetailDtos);
        AmountReviewDto amountReviewDto = new AmountReviewDto();
        setAmountReviewDto(amountReviewDto,amountReviewDetailDtos,LifeUniConstant.REVIEW_KIND_COURSE);
        reviewDtoApiMessageDto.setData(amountReviewDto);
        reviewDtoApiMessageDto.setMessage("Get list amount review success");
        return reviewDtoApiMessageDto;
    }

    @GetMapping(value = "/star-expert/{expertId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<AmountReviewDto> reviewExpertStar(@PathVariable Long expertId) {
        ApiMessageDto<AmountReviewDto> reviewDtoApiMessageDto = new ApiMessageDto<>();
        checkValidExpert(expertId);
        List<AmountReviewDetailDto> amountReviewDetailDtos = reviewRepository.groupByExpertStar(expertId,LifeUniConstant.REVIEW_KIND_EXPERT);
        getAmountReview(amountReviewDetailDtos);
        AmountReviewDto amountReviewDto = new AmountReviewDto();
        setAmountReviewDto(amountReviewDto,amountReviewDetailDtos,LifeUniConstant.REVIEW_KIND_EXPERT);
        reviewDtoApiMessageDto.setData(amountReviewDto);
        reviewDtoApiMessageDto.setMessage("Get list amount review success");
        return reviewDtoApiMessageDto;
    }
    private void setAmountReviewDto(AmountReviewDto amountReviewDto,List<AmountReviewDetailDto> amountReviewDetailDtos, Integer reviewKind){
        long total = 0;
        long amount = 0;

        for (AmountReviewDetailDto amountReviewDetailDto : amountReviewDetailDtos) {
            total += amountReviewDetailDto.getStar() * amountReviewDetailDto.getAmount();
            amount += amountReviewDetailDto.getAmount();
        }
        amountReviewDto.setAverageStar(amount != 0 ?((float)total/amount) :0);
        amountReviewDto.setTotal(amount);
        amountReviewDto.setKind(reviewKind);
        amountReviewDto.setAmountReview(amountReviewDetailDtos);
    }

    private void getAmountReview(List<AmountReviewDetailDto> amountReviewDetailDtos) {
        for (Integer star: LifeUniConstant.REVIEW_STARS){
            boolean found = false;
            for (AmountReviewDetailDto amountReviewDetailDto : amountReviewDetailDtos) {
                if (amountReviewDetailDto.getStar().equals(star)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                AmountReviewDetailDto newAmountReviewDetailDto = new AmountReviewDetailDto(star, 0L);
                amountReviewDetailDtos.add(newAmountReviewDetailDto);
            }
        }
    }
    private void checkValidCourse(@PathVariable Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if (!Objects.equal(course.get().getStatus(), LifeUniConstant.STATUS_ACTIVE)) {
            throw new BadRequestException("Course not active",ErrorCode.COURSE_ERROR_NOT_ACTIVE);
        }
    }
    private void checkValidExpert(@PathVariable Long expertId) {
        Optional<Expert> expert = expertRepository.findById(expertId);
        if (expert.isEmpty()) {
            throw new NotFoundException("Expert not found", ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        if (!Objects.equal(expert.get().getStatus(), LifeUniConstant.STATUS_ACTIVE)) {
            throw new BadRequestException("Expert not active",ErrorCode.EXPERT_ERROR_NOT_ACTIVE);
        }
    }
}
