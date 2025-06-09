package com.easylearning.api.service.rabbitMQ;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.controller.ABasicController;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.form.BaseMsgForm;
import com.easylearning.api.form.courseReviewHistory.ApproveVersionData;
import com.easylearning.api.mapper.CourseMapper;
import com.easylearning.api.mapper.LessonMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.repository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Log4j2
public class ApproveVersionService extends ABasicController {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    private CourseVersioningRepository courseVersioningRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CategoryHomeRepository categoryHomeRepository;
    @Autowired
    private CourseComboDetailVersionRepository courseComboDetailVersionRepository;
    @Autowired
    private LessonMapper lessonMapper;
    @Autowired
    private CompletionRepository completionRepository;

    @Value("${rabbitmq.backend.app}")
    private String backendApp;

    @Value("${rabbitmq.approve.version.queue}")
    private String approveVersionQueue;
    @Autowired
    private CourseComboDetailRepository courseComboDetailRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseReviewHistoryRepository courseReviewHistoryRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SyncDataElasticService syncDataElasticService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    public void sendApproveVersionMessage(Long versionId) {
        ApproveVersionData data = new ApproveVersionData();
        data.setVersionId(versionId);
        rabbitService.handleSendMsg(data, LifeUniConstant.BACKEND_APPROVE_VERSIONS_CMD, approveVersionQueue);
    }

    @RabbitListener(queues = "${rabbitmq.approve.version.queue}")
    public void receiveChatMsg(String message) {
        // Define transaction attributes
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // Start a new transaction
        TransactionStatus transactionStatus = transactionManager.getTransaction(def);
        Course course = null;
        try {
            log.error("Received message: " + message);
            BaseMsgForm form = objectMapper.readValue(message, BaseMsgForm.class);
            if(form != null) {
                if(!form.getApp().equals(backendApp)) {
                    log.error("===========> Invalid app: " + form.getApp());
                    return;
                }
                if(form.getCmd().equals(LifeUniConstant.BACKEND_APPROVE_VERSIONS_CMD)) {
                    ApproveVersionData data = objectMapper.convertValue(form.getData(), ApproveVersionData.class);
                    if(data!= null && data.getVersionId() != null) {
                        course = approveProcess(data.getVersionId());
                    }
                }else {
                    log.error("===========> Invalid cmd: " + form.getCmd());
                }
            }

            // Commit the transaction
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // Rollback the transaction if an exception occurs
            transactionManager.rollback(transactionStatus);
            log.error("Error processing message: " + e.getMessage());
        }
        if(course != null) {
            syncDataElasticService.sendElasticDataMessage(course.getId());
        }
    }

    public Course approveProcess(Long versionId){
        Course courseCheck = null;
        Version version = versionRepository.findFirstByIdAndState(versionId, LifeUniConstant.VERSION_STATE_WAITING).orElse(null);
        if(version != null){
            version.setState(LifeUniConstant.VERSION_STATE_PROCESSING);
            versionRepository.save(version);
            CourseVersioning courseVersioning = courseVersioningRepository.findFirstByVersionId(version.getId());
            List<LessonVersioning> lessonVersioningList = lessonVersioningRepository.findAllByVersionId(version.getId());
            // approve for course
            if(courseVersioning != null){
                Course course = courseRepository.findById(courseVersioning.getVisualId()).orElse(null);
                if(course == null){
                    log.error("Can not found course");
                    version.setState(LifeUniConstant.VERSION_STATE_PROCESS_ERROR);
                    versionRepository.save(version);
                    return null;
                }
                // trường hợp approve lúc create course
                if(course.getStatus() == LifeUniConstant.STATUS_PENDING){
                    updateTotalExpertCourseAndTotalLessonTime(course,courseVersioning,1);
                    courseCheck = mapCourseFormCourseVersioning(courseVersioning,course,version,LifeUniConstant.STATUS_ACTIVE);
                    createCategoryHomeTopNewAndTopSpecialize(course.getField(),course);
                }
                // case approve cho update status
                else if (course.getStatus() != courseVersioning.getStatus()){
                    if(courseVersioning.getStatus() == LifeUniConstant.STATUS_LOCK){
                        updateTotalExpertCourseAndTotalLessonTime(course,courseVersioning,-1);
                        courseCheck = mapCourseFormCourseVersioning(courseVersioning,course,version,LifeUniConstant.STATUS_LOCK);
                        categoryHomeRepository.deleteByCourse(course.getId());
                        updateAllCategoryHome();
                    }
                    else{
                        updateTotalExpertCourseAndTotalLessonTime(course,courseVersioning,1);
                        courseCheck = mapCourseFormCourseVersioning(courseVersioning,course,version,LifeUniConstant.STATUS_ACTIVE);
                        createCategoryHomeTopNewAndTopSpecialize(course.getField(),course);
                        createCategoryHomeTopSold(course);
                    }
                }
                else {
                    // active -> active
                    boolean isChangeTopSold = false;
                    if(courseVersioning.getStatus() == LifeUniConstant.STATUS_ACTIVE){
                        updateTotalExpertCourseAndTotalLessonTime(course,courseVersioning,0);
                        // check if update from free course -> charged course and back
                        if ((course.getPrice() == 0) != (courseVersioning.getPrice() == 0)) {
                            isChangeTopSold = true;
                            categoryHomeRepository.deleteAllByCourseIdAndCategoryKindIn(course.getId(), List.of(LifeUniConstant.CATEGORY_KIND_TOP_FREE, LifeUniConstant.CATEGORY_KIND_TOP_CHARGE));
                        }
                    }
                    // lock -> lock
                    courseCheck = mapCourseFormCourseVersioning(courseVersioning,course,version,courseVersioning.getStatus());
                }

                if(Objects.equals(course.getKind(), LifeUniConstant.COURSE_KIND_COMBO)){
                    List<CourseComboDetailVersion> courseComboDetailVersions = courseComboDetailVersionRepository.findAllByComboId(courseVersioning.getId());
                    List<Long> ids = courseComboDetailVersions.stream()
                            .map(CourseComboDetailVersion::getId)
                            .collect(Collectors.toList());

                    courseComboDetailRepository.deleteByComboIdsNotIn(ids,course.getId());
                    List<CourseComboDetail> courseComboDetails = new ArrayList<>();
                    for (CourseComboDetailVersion ccd : courseComboDetailVersions){
                        if(!courseComboDetailRepository.existsByComboIdAndCourseId(courseVersioning.getId(),ccd.getCourse().getId())){
                            CourseComboDetail newCombo = new CourseComboDetail();
                            newCombo.setCombo(course);
                            newCombo.setCourse(ccd.getCourse());
                            courseComboDetails.add(newCombo);
                        }
                    }
                    courseComboDetailRepository.saveAll(courseComboDetails);
                }
            }
            // approve for lesson
            if(!lessonVersioningList.isEmpty()){
                List<Lesson> lessons = new ArrayList<>();
                List<Long> deleteLessonIds = new ArrayList<>();
                for(LessonVersioning lessonVersioning: lessonVersioningList){
                    Lesson lesson = lessonRepository.findById(lessonVersioning.getVisualId()).orElse(null);
                    if(lesson!=null){
                        if(Objects.equals(lessonVersioning.getStatus(),LifeUniConstant.STATUS_DELETE)){
                            deleteLessonIds.add(lesson.getId());
                        }
                        lesson.setVersion(version);
                        // pending to active
                        if(Objects.equals(lessonVersioning.getStatus(),LifeUniConstant.STATUS_PENDING)){
                            lessonVersioning.setStatus(LifeUniConstant.STATUS_ACTIVE);
                        }
                        lessonMapper.fromLessonVersioningEntityToLesson(lessonVersioning,lesson);
                        lessons.add(lesson);
                    }
                }
                lessonRepository.saveAll(lessons);
                lessonVersioningRepository.saveAll(lessonVersioningList);

                //delete lesson
                completionRepository.deleteAllByLessonIdIn(deleteLessonIds);
                lessonRepository.deleteAllByIdIn(deleteLessonIds);
            }

            version.setState(LifeUniConstant.VERSION_STATE_APPROVE);
            versionRepository.save(version);

            CourseReviewHistory courseReviewHistory = courseReviewHistoryRepository.findFirstByVersionIdAndState(version.getId(), LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_SUBMIT);
            courseReviewHistory.setState(LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_APPROVE);
            courseReviewHistory.setDate(new Date());
            courseReviewHistoryRepository.save(courseReviewHistory);
            //send notification
            CourseVersioning course = courseVersioningRepository.findLatestCourseVersioningByCourseId(version.getCourseId());
            notificationService.createNotificationAndSendForApproveOrRejectCourse(LifeUniConstant.NOTIFICATION_KIND_APPROVE_COURSE,
                    course.getExpert().getId(),course.getName(),version.getCourseId());
        }
        return courseCheck;
    }

    Course mapCourseFormCourseVersioning(CourseVersioning courseVersioning, Course course,Version version, Integer status){
        courseMapper.fromCourseVersioningEntityToCourse(courseVersioning,course);
        course.setStatus(status);
        course.setField(courseVersioning.getField());
        course.setExpert(courseVersioning.getExpert());
        course.setVersion(version);
        courseVersioning.setStatus(status);
        courseVersioningRepository.save(courseVersioning);
        return courseRepository.save(course);
    }

    void createCategoryHomeTopNewAndTopSpecialize(Category category, Course course){
        createCategoryHomeTopNew(category, course);

        // set top new in category home
        Category categoryNew = categoryRepository.findFirstByKind(LifeUniConstant.CATEGORY_KIND_TOP_NEW);
        if (categoryNew == null) {
            categoryNew = new Category();
        }
        categoryNew.setOrdering(LifeUniConstant.CATEGORY_ORDERING_TOP);
        categoryNew.setKind(LifeUniConstant.CATEGORY_KIND_TOP_NEW);
        categoryNew.setName(LifeUniConstant.CATEGORY_NAME_TOP_NEW);

        categoryRepository.save(categoryNew);
        createCategoryHomeTopNew(categoryNew, course);
    }
}
