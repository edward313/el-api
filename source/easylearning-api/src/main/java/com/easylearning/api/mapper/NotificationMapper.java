package com.easylearning.api.mapper;

import com.easylearning.api.dto.notification.NotificationDto;
import com.easylearning.api.form.notification.UpdateNotificationForm;
import com.easylearning.api.model.Notification;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "refId", target = "refId")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "msg", target = "msg")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNotiDto")
    NotificationDto fromEntityToNotificatonDto(Notification notification);

    @IterableMapping(elementTargetType = NotificationDto.class, qualifiedByName = "fromEntityToNotiDto")
    List<NotificationDto> fromEntityToNotiListDto(List<Notification> notifications);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "refId", target = "refId")
    @Mapping(source = "msg", target = "msg")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNotiDtoAuto")
    NotificationDto fromEntityToNotificatonDtoAuto(Notification notification);
    @IterableMapping(elementTargetType = NotificationDto.class, qualifiedByName = "fromEntityToNotiDtoAuto")
    List<NotificationDto> fromEntityToNotiListDtoAuto(List<Notification> notifications);

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "msg", target = "msg")
    @Mapping(source = "refId", target = "refId")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateNotiToEntity(UpdateNotificationForm updateNotificationForm, @MappingTarget Notification notification);
}
