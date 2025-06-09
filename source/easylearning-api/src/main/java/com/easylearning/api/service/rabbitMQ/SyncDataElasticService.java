package com.easylearning.api.service.rabbitMQ;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.form.BaseMsgForm;
import com.easylearning.api.form.course.SyncElasticDataForm;
import com.easylearning.api.mapper.CourseMapper;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.elastic.ElasticCourse;
import com.easylearning.api.repository.CourseRepository;
import com.easylearning.api.repository.ElasticCourseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@Log4j2
public class SyncDataElasticService {

    @Value("${rabbitmq.sync.elastic.queue}")
    private String syncElasticQueue;

    @Value("${rabbitmq.backend.app}")
    private String backendApp;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ElasticCourseRepository elasticCourseRepository;

    @Autowired
    private CourseMapper courseMapper;
    public void sendElasticDataMessage(Long courseId) {
        SyncElasticDataForm data = new SyncElasticDataForm();
        data.setCourseId(courseId);
        rabbitService.handleSendMsg(data, LifeUniConstant.BACKEND_SYNC_ELASTIC_CMD, syncElasticQueue);
    }

    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queues = "${rabbitmq.sync.elastic.queue}")
    public void receiveChatMsg(String message) {
        log.error("Received message: " + message);
        BaseMsgForm form;
        try {
            form = objectMapper.readValue(message, BaseMsgForm.class);
            if(form != null) {
                if(!form.getApp().equals(backendApp)) {
                    log.error("===========> Invalid app: " + form.getApp());
                    return;
                }
                if(form.getCmd().equals(LifeUniConstant.BACKEND_SYNC_ELASTIC_CMD)) {
                    SyncElasticDataForm data = objectMapper.convertValue(form.getData(), SyncElasticDataForm.class);
                    if(data!= null && data.getCourseId() != null) {
                        syncElasticData(data.getCourseId());
                    }
                }else {
                    log.error("===========> Invalid cmd: " + form.getCmd());
                }
            }
        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
            // Rollback transaction
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    public void syncElasticData(Long courseId){
        Course course = courseRepository.findById(courseId).orElse(null);
        ElasticCourse elasticCourse = elasticCourseRepository.findByCourseId(courseId);
        if(elasticCourse == null){
            elasticCourse = courseMapper.fromEntityToElasticCourse(course);
        }
        else {
            courseMapper.updateCourseEntityToElasticEntity(course,elasticCourse);
        }
        elasticCourseRepository.save(elasticCourse);
        log.error("Elastic search data is successfully synchronized");
    }
}
