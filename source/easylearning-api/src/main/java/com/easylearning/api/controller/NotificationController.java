package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.notification.MyNotificationDto;
import com.easylearning.api.dto.notification.NotificationDto;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.form.notification.ChangeStateNotification;
import com.easylearning.api.form.notification.UpdateNotificationForm;
import com.easylearning.api.mapper.NotificationMapper;
import com.easylearning.api.model.Account;
import com.easylearning.api.model.Notification;
import com.easylearning.api.model.criteria.NotificationCriteria;
import com.easylearning.api.repository.AccountRepository;
import com.easylearning.api.repository.NotificationRepository;
import com.easylearning.api.service.rabbitMQ.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/notification")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@Validated
public class NotificationController extends ABasicController {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private AccountRepository accountRepository;

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NT_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            throw new NotFoundException("Notification not found",ErrorCode.NOTIFICATION_ERROR_NOT_FOUND);
        }
        notificationRepository.delete(notification);
        apiMessageDto.setMessage("delete notification success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NT_L')")
    public ApiMessageDto<ResponseListDto<List<NotificationDto>>> list(NotificationCriteria notificationCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<NotificationDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<NotificationDto>> responseListDto = new ResponseListDto<>();
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());
        Page<Notification> listNotification = notificationRepository.findAll(notificationCriteria.getCriteria(), page);
        responseListDto.setContent(notificationMapper.fromEntityToNotiListDto(listNotification.getContent()));
        responseListDto.setTotalPages(listNotification.getTotalPages());
        responseListDto.setTotalElements(listNotification.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("get list sucess");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<NotificationDto>>> listAutoComplete(NotificationCriteria notificationCriteria) {
        ApiMessageDto<ResponseListDto<List<NotificationDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<NotificationDto>> responseListDto = new ResponseListDto<>();
        notificationCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> listNotification = notificationRepository.findAll(notificationCriteria.getCriteria(), pageable);
        responseListDto.setContent(notificationMapper.fromEntityToNotiListDtoAuto(listNotification.getContent()));
        responseListDto.setTotalPages(listNotification.getTotalPages());
        responseListDto.setTotalElements(listNotification.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("get list sucess");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NT_V')")
    public ApiMessageDto<NotificationDto> getNotification(@PathVariable("id") Long id) {
        ApiMessageDto<NotificationDto> apiMessageDto = new ApiMessageDto<>();
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            throw new NotFoundException("Notification not found",ErrorCode.NOTIFICATION_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(notificationMapper.fromEntityToNotificatonDto(notification));
        apiMessageDto.setMessage("get notification success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NT_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateNotificationForm updateNotificationForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Notification notification = notificationRepository.findById(updateNotificationForm.getId()).orElse(null);
        if (notification == null) {
            throw new NotFoundException("Notification not found",ErrorCode.NOTIFICATION_ERROR_NOT_FOUND);
        }
        Account account = accountRepository.findById(updateNotificationForm.getIdUser()).orElse(null);
        if (account == null) {
            throw new NotFoundException("User not found",ErrorCode.NOTIFICATION_USER_ERROR_NOT_FOUND);
        }
        notificationMapper.fromUpdateNotiToEntity(updateNotificationForm, notification);
        notificationRepository.save(notification);
        apiMessageDto.setMessage("update notification success");
        return apiMessageDto;
    }

    @PutMapping(value = "/change-state", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NT_CS')")
    public ApiMessageDto<String> changeState(@Valid @RequestBody ChangeStateNotification changeStateNotification, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Notification notification = notificationRepository.findById(changeStateNotification.getId()).orElse(null);
        if (notification == null)
        {
            throw new NotFoundException("Notification not found",ErrorCode.NOTIFICATION_ERROR_NOT_FOUND);
        }
        if (notification.getState().equals(LifeUniConstant.NOTIFICATION_STATE_SENT))
        {
            notification.setState(LifeUniConstant.NOTIFICATION_STATE_READ);
        }

        notificationRepository.save(notification);
        apiMessageDto.setMessage("change to state read success");
        return apiMessageDto;
    }

    @GetMapping(value = "/my-notification", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<MyNotificationDto> getMyNotification(NotificationCriteria criteria, @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiMessageDto<MyNotificationDto> apiMessageDto = new ApiMessageDto<>();
        criteria.setIdUser(getCurrentUser());
        MyNotificationDto mynotificationDto = new MyNotificationDto();
        Page<Notification> notificationPage = notificationRepository.findAll(criteria.getCriteria(), pageable);
        mynotificationDto.setContent(notificationMapper.fromEntityToNotiListDto(notificationPage.getContent()));
        mynotificationDto.setTotalPages(notificationPage.getTotalPages());
        mynotificationDto.setTotalElements(notificationPage.getTotalElements());

        List<Integer> kinds = getNotificationKindByAppKind(criteria.getAppKind());
        Long totalUnread = kinds.isEmpty()
                ? notificationRepository.countByIdUserAndState(getCurrentUser(), LifeUniConstant.NOTIFICATION_STATE_SENT)
                : notificationRepository.countByIdUserAndStateAndKindIn(getCurrentUser(), LifeUniConstant.NOTIFICATION_STATE_SENT, kinds);
        mynotificationDto.setTotalUnread(totalUnread);

        apiMessageDto.setData(mynotificationDto);
        apiMessageDto.setMessage("get my notification success");
        return apiMessageDto;
    }

    @PutMapping(value = "/read-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> readAll(@RequestParam(required = false) Integer appKind) {
        NotificationCriteria criteria = new NotificationCriteria();
        criteria.setIdUser(getCurrentUser());
        criteria.setAppKind(appKind);
        criteria.setState(LifeUniConstant.NOTIFICATION_STATE_SENT);

        List<Notification> notifications = notificationRepository.findAll(criteria.getCriteria());
        notifications.forEach(noti -> noti.setState(LifeUniConstant.NOTIFICATION_STATE_READ));
        notificationRepository.saveAll(notifications);
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setMessage("Has moved to the fully read state");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> deleteAll(@RequestParam(required = false) Integer appKind) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        List<Integer> kinds = getNotificationKindByAppKind(appKind);
        if(kinds.isEmpty()){
            notificationRepository.deleteAllByIdUser(getCurrentUser());
        }else {
            notificationRepository.deleteAllByIdUserAndKindIn(getCurrentUser(), kinds);
        }

        apiMessageDto.setMessage("Delete all success");
        return apiMessageDto;
    }

    public List<Integer> getNotificationKindByAppKind(Integer appKind){
        List<Integer> kinds = new ArrayList<>();
        if(appKind != null){
            if(Objects.equals(appKind, LifeUniConstant.PORTAL_APP)){
                kinds = LifeUniConstant.PORTAL_NOTIFICATION_KINDS;
            }else if (Objects.equals(appKind, LifeUniConstant.CLIENT_APP)){
                kinds = LifeUniConstant.FE_NOTIFICATION_KINDS;
            }
        }
        return kinds;
    }
}