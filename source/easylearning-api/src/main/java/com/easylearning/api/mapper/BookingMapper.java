package com.easylearning.api.mapper;

import com.easylearning.api.dto.booking.BookingAdminDto;
import com.easylearning.api.dto.booking.BookingDto;
import com.easylearning.api.form.booking.CreateBookingForm;
import com.easylearning.api.form.booking.UpdateBookingForm;
import com.easylearning.api.model.Booking;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {StudentMapper.class, PromotionMapper.class})
public interface BookingMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "saleOffMoney", target = "saleOffMoney")
    @Mapping(source = "couponMoney", target = "couponMoney")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "paymentData", target = "paymentData")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "payoutStatus", target = "payoutStatus")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "promotion", target = "promotion", qualifiedByName = "fromEntityToPromotionDtoForBooking")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToBookingAdminDto")
    BookingAdminDto fromEntityToBookingAdminDto(Booking booking);

    @IterableMapping(elementTargetType = BookingAdminDto.class, qualifiedByName = "fromEntityToBookingAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<BookingAdminDto> fromEntityToBookingAdminDtoList(List<Booking> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "saleOffMoney", target = "saleOffMoney")
    @Mapping(source = "couponMoney", target = "couponMoney")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "paymentData", target = "paymentData")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "payoutStatus", target = "payoutStatus")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToBookingDtoAutoComplete")
    BookingDto fromEntityToBookingDtoAutoComplete(Booking booking);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "saleOffMoney", target = "saleOffMoney")
    @Mapping(source = "couponMoney", target = "couponMoney")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "paymentData", target = "paymentData")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "payoutStatus", target = "payoutStatus")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToBookingDtoForClient")
    BookingDto fromEntityToBookingDtoForClient(Booking booking);
    @IterableMapping(elementTargetType = BookingDto.class, qualifiedByName = "fromEntityToBookingDtoForClient")
    @BeanMapping(ignoreByDefault = true)
    List<BookingDto> fromEntityToBookingDtoForClientList(List<Booking> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "saleOffMoney", target = "saleOffMoney")
    @Mapping(source = "promotion", target = "promotion", qualifiedByName = "fromEntityToPromotionDtoForBooking")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "paymentData", target = "paymentData")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "payoutStatus", target = "payoutStatus")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToBookingDtoForTransaction")
    BookingDto fromEntityToBookingDtoForTransaction(Booking booking);

    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @BeanMapping(ignoreByDefault = true)
    @Named("createBookingFromCreateForm")
    Booking createBookingFromCreateForm(CreateBookingForm createBookingForm);

    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateBookingFromUpdateForm")
    void updateBookingFromUpdateForm(UpdateBookingForm updateBookingForm,@MappingTarget Booking booking);

    @IterableMapping(elementTargetType = BookingDto.class, qualifiedByName = "fromEntityToBookingDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<BookingDto> fromEntityToBookingDtoAutoCompleteList(List<Booking> list);
}
