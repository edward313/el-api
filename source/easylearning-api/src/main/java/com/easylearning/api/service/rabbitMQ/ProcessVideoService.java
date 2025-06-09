package com.easylearning.api.service.rabbitMQ;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.controller.ABasicController;
import com.easylearning.api.form.BaseMsgForm;
import com.easylearning.api.form.lesson.ProcessVideoData;
import com.easylearning.api.form.lesson.ProcessVideoSuccessData;
import com.easylearning.api.mapper.CourseVersioningMapper;
import com.easylearning.api.model.CourseVersioning;
import com.easylearning.api.model.LessonVersioning;
import com.easylearning.api.repository.CourseVersioningRepository;
import com.easylearning.api.repository.LessonVersioningRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@Log4j2
public class ProcessVideoService extends ABasicController {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    private CourseVersioningRepository courseVersioningRepository;
    @Autowired
    private CourseVersioningMapper courseVersioningMapper;

    @Value("${rabbitmq.video.app}")
    private String videoApp;

    @Value("${rabbitmq.process.video.queue}")
    private String processVideoQueue;

    public void sendProcessVideoMessage(Long lessonId,Integer versionCode,Long expertId,Long courseId, String url) {
        deleteLessonVersioningFolder(lessonId,versionCode,expertId,courseId);
        ProcessVideoData data = new ProcessVideoData();
        data.setUrl(url);
        data.setLessonId(lessonId);
        data.setVersionCode(versionCode);
        rabbitService.handleSendMsg(data, LifeUniConstant.BACKEND_PROCESS_VIDEO_CMD, processVideoQueue);
    }

    @RabbitListener(queues = "${rabbitmq.media.completed.process.video.queue}")
    public void receiveChatMsg(String message) {
        log.error("Received message: " + message);
        BaseMsgForm form;
        try {
            form = objectMapper.readValue(message,BaseMsgForm.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if(form != null) {
            if(!form.getApp().equals(videoApp)) {
                log.error("===========> Invalid app: " + form.getApp());
                return;
            }
            if(form.getCmd().equals(LifeUniConstant.MEDIA_COMPLETED_PROCESS_VIDEO_CMD)){
                ProcessVideoSuccessData data = objectMapper.convertValue(form.getData(), ProcessVideoSuccessData.class);
                // update lesson when received data success
                updateLessonProcessed(data);
            }
            else {
                log.error("===========> Invalid cmd: " + form.getCmd());
            }
        }
    }

    void updateLessonProcessed(ProcessVideoSuccessData processVideoSuccessData){
        log.error("Update state lesson processed.............");
        LessonVersioning lessonVersioning = lessonVersioningRepository.findLatestLessonVersioningByLessonId(processVideoSuccessData.getLessonId());
        if(lessonVersioning != null && lessonVersioning.getVersion().getState().equals(LifeUniConstant.VERSION_STATE_INIT)){
            if(processVideoSuccessData.getIsFail()){
                lessonVersioning.setState(LifeUniConstant.LESSON_STATE_FAIL);
            }
            else {
                // not update total lesson & time of course if lesson statusS delete
                if(!Objects.equals(lessonVersioning.getStatus(),LifeUniConstant.STATUS_DELETE)){
                    updateCourseVersioningTotalStudyTimeAndTotalLesson(lessonVersioning,processVideoSuccessData);
                }
                lessonVersioning.setState(LifeUniConstant.LESSON_STATE_DONE);
                lessonVersioning.setThumbnail(processVideoSuccessData.getThumbnail());
                lessonVersioning.setContent(processVideoSuccessData.getContentPath());
                lessonVersioning.setVideoDuration(processVideoSuccessData.getVideoDuration());
            }
            lessonVersioningRepository.save(lessonVersioning);
        }
    }
    void updateCourseVersioningTotalStudyTimeAndTotalLesson(LessonVersioning lessonVersioning,ProcessVideoSuccessData processVideoSuccessData){
        CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(lessonVersioning.getCourse().getId());
        if(!courseVersioning.getVersion().getId().equals(lessonVersioning.getVersion().getId())){
            courseVersioning = courseVersioningMapper.fromCourseEntityToCourseVersioning(lessonVersioning.getCourse());
            courseVersioning.setVersion(lessonVersioning.getVersion());
            courseVersioning.setExpert(lessonVersioning.getCourse().getExpert());
            courseVersioning.setVisualId(lessonVersioning.getCourse().getId());
            courseVersioning.setField(lessonVersioning.getCourse().getField());
        }
        if(courseVersioning.getTotalLesson() == null){
            courseVersioning.setTotalLesson(0);
        }
        courseVersioning.setTotalLesson(courseVersioning.getTotalLesson()+1);

        if(courseVersioning.getTotalStudyTime() == null){
            courseVersioning.setTotalStudyTime(0L);
        }
        courseVersioning.setTotalStudyTime(courseVersioning.getTotalStudyTime() + processVideoSuccessData.getVideoDuration());
        courseVersioningRepository.save(courseVersioning);
    }
}
