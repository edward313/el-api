package com.easylearning.api.service.rabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.notification.*;
import com.easylearning.api.form.notification.PostNotificationData;
import com.easylearning.api.model.Notification;
import com.easylearning.api.repository.NotificationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class NotificationService {
    @Value("${rabbitmq.app}")
    private String lifeUniAppName;

    @Value("${rabbitmq.notification.queue}")
    private String queueName;
    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(String message, Integer kind, Long userId) {
        PostNotificationData data = new PostNotificationData();
        data.setMessage(message);
        data.setCmd(LifeUniConstant.BACKEND_POST_NOTIFICATION_CMD);
        data.setKind(kind);
        data.setMessage(message);
        data.setUserId(userId);
        data.setApp(lifeUniAppName);
        if(LifeUniConstant.PORTAL_NOTIFICATION_KINDS.contains(kind)){
            data.setAppKind(LifeUniConstant.PORTAL_APP);
        }
        rabbitService.handleSendMsg(data, LifeUniConstant.BACKEND_POST_NOTIFICATION_CMD, queueName);
    }

    public Notification createNotification(Integer kind, Integer state, Long userId){
        Notification notification = new Notification();
        notification.setState(state);
        notification.setKind(kind);
        notification.setIdUser(userId);
        notificationRepository.save(notification);
        return notification;
    }

    public void createNotificationAndSendMessage(Integer notificationKind, Long userId, String fullName, String avatar) {
        Notification notification = createNotification(notificationKind, LifeUniConstant.NOTIFICATION_STATE_SENT, userId);
        String msg = null;
        if(notificationKind.equals(LifeUniConstant.NOTIFICATION_KIND_APPROVE_EXPERT)) {
            ApproveExpertNotificationMessage approveExpertNotificationMessage = new ApproveExpertNotificationMessage();
            approveExpertNotificationMessage.setNotificationId(notification.getId());
            approveExpertNotificationMessage.setFullName(fullName);
            approveExpertNotificationMessage.setAvatar(avatar);
            msg = convertObjectToJson(approveExpertNotificationMessage);
        }else if(notificationKind.equals(LifeUniConstant.NOTIFICATION_KIND_UPGRADE_SELLER)) {
            UpgradeSellerNotificationMessage upgradeSellerNotificationMessage = new UpgradeSellerNotificationMessage();
            upgradeSellerNotificationMessage.setNotificationId(notification.getId());
            upgradeSellerNotificationMessage.setFullName(fullName);
            upgradeSellerNotificationMessage.setAvatar(avatar);
            msg = convertObjectToJson(upgradeSellerNotificationMessage);
        }else if(notificationKind.equals(LifeUniConstant.NOTIFICATION_KIND_SIGNUP_STUDENT)) {
            SignUpStudentNotificationMessage signUpStudentNotificationMessage = new SignUpStudentNotificationMessage();
            signUpStudentNotificationMessage.setFullName(fullName);
            signUpStudentNotificationMessage.setAvatar(avatar);
            signUpStudentNotificationMessage.setNotificationId(notification.getId());
            msg = convertObjectToJson(signUpStudentNotificationMessage);
        }
        notification.setMsg(msg);
        notificationRepository.save(notification);
        sendMessage(notification.getMsg(),notification.getKind(),notification.getIdUser());
    }

    public void createNotificationAndSendMessageForBuyer(Integer notificationKind, Long userId, Long bookingId, Integer bookingState, String code) {
        Notification notification = createNotification(notificationKind, LifeUniConstant.NOTIFICATION_STATE_SENT, userId);

        ApproveAndRejectBookingNotificationMessage approveAndRejectBookingNotificationMessage = new ApproveAndRejectBookingNotificationMessage();
        approveAndRejectBookingNotificationMessage.setNotificationId(notification.getId());
        approveAndRejectBookingNotificationMessage.setBookingId(bookingId);
        approveAndRejectBookingNotificationMessage.setBookingState(bookingState);
        approveAndRejectBookingNotificationMessage.setCode(code);
        String msg = convertObjectToJson(approveAndRejectBookingNotificationMessage);

        notification.setMsg(msg);
        notificationRepository.save(notification);
        sendMessage(notification.getMsg(), notification.getKind(), notification.getIdUser());
    }
    public void createNotificationAndSendMessageForRevenueShare(Integer notificationKind, Long userId, String courseName, Double revenueShareMoney){
        Notification notification = createNotification(notificationKind, LifeUniConstant.NOTIFICATION_STATE_SENT, userId);

        ReceiveRevenueNotificationMessage receiveRevenueNotificationMessage = new ReceiveRevenueNotificationMessage();
        receiveRevenueNotificationMessage.setNotificationId(notification.getId());
        receiveRevenueNotificationMessage.setRevenueMoney(revenueShareMoney);
        receiveRevenueNotificationMessage.setCourseName(courseName);
        String msg = convertObjectToJson(receiveRevenueNotificationMessage);

        notification.setMsg(msg);
        notificationRepository.save(notification);
        sendMessage(notification.getMsg(),notification.getKind(),notification.getIdUser());
    }

    public void createNotificationAndSendForApproveOrRejectCourse(Integer notificationKind, Long expertId, String courseName, Long courseId){
        Notification notification = createNotification(notificationKind, LifeUniConstant.NOTIFICATION_STATE_SENT, expertId);

        ApproveAndRejectCourseNotificationMessage message = new ApproveAndRejectCourseNotificationMessage();
        message.setNotificationId(notification.getId());
        message.setCourseId(courseId);
        message.setCourseName(courseName);
        String msg = convertObjectToJson(message);

        notification.setMsg(msg);
        notificationRepository.save(notification);
        sendMessage(notification.getMsg(),notification.getKind(),notification.getIdUser());
    }
    public String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
