package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.cartItem.CartItemDto;
import com.easylearning.api.dto.cartItem.PaymentCartDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.cartItem.CreateCartItemForm;
import com.easylearning.api.form.cartItem.CreateListCartItemForm;
import com.easylearning.api.form.cartItem.UpdateCartItemForm;
import com.easylearning.api.mapper.CartItemMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.CartItemCriteria;
import com.easylearning.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/v1/cart-item")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CartItemController extends ABasicController {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private CourseRetailRepository courseRetailRepository;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private ReferralSellerLogRepository referralSellerLogRepository;


    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CI_V')")
    public ApiMessageDto<CartItemDto> get(@PathVariable("id") Long id) {
        if(!isStudent() && !isSuperAdmin()){
            throw new UnauthorizationException("Not allow get");
        }
        CartItem cartItem = getCartItemByCurrentUser(id);
        ApiMessageDto<CartItemDto> apiMessageDto = new ApiMessageDto<>();
        CartItemDto cartItemDto = cartItemMapper.fromEntityToCartItemDto(cartItem);
        if (cartItem.getTempSellCode() != null) {
            cartItemDto.setSellCode(cartItem.getTempSellCode());
        }

        apiMessageDto.setData(cartItemDto);
        apiMessageDto.setMessage("Get cart item success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CI_L')")
    public ApiMessageDto<ResponseListDto<PaymentCartDto>> list(CartItemCriteria criteria, Pageable pageable) {
        if(!isStudent() && !isSuperAdmin()){
            throw new UnauthorizationException("Not allow list");
        }
        if(isStudent()){
            criteria.setStudentId(getCurrentUser());
        }
        ApiMessageDto<ResponseListDto<PaymentCartDto>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<CartItem> cartItems = cartItemRepository.findAll(criteria.getSpecification() , pageable);
        ResponseListDto<PaymentCartDto> responseListObj = new ResponseListDto<>();
        List<CartItemDto> cartItemDtos = mappingTempSellCodeIntoSellCodeInCartItem(cartItems.getContent());

        String bankInfo = settingsRepository.findValueByGroupName(LifeUniConstant.SETTING_GROUP_NAME_BANK_INFO);
        String couponMoney = settingsRepository.findValueByKey(LifeUniConstant.SETTING_KEY_SYSTEM_COUPON);
        ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(getCurrentUser());

        PaymentCartDto paymentCartDto = new PaymentCartDto();
        paymentCartDto.setCartItems(cartItemDtos);
        paymentCartDto.setBankInfo(bankInfo);
        paymentCartDto.setCouponMoney(Double.parseDouble(couponMoney));
        paymentCartDto.setIsUseSellCode(referralSellerLog == null ? Boolean.FALSE : Boolean.TRUE);

        responseListObj.setContent(paymentCartDto);
        responseListObj.setTotalPages(cartItems.getTotalPages());
        responseListObj.setTotalElements(cartItems.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list cart item success");
        return responseListObjApiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("hasRole('CI_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        if(!isStudent() && !isSuperAdmin()){
            throw new UnauthorizationException("Not allow delete");
        }
        CartItem cartItem = getCartItemByCurrentUser(id);
        if (cartItem.getSellCode() != null){
            // Find item has sell code to replace for current item
            CartItem replaceItem = cartItemRepository.findFirstByStudentIdAndSellCodeIsNotNullAndSellCodeNot(getCurrentUser(), cartItem.getSellCode());
            if (replaceItem != null && cartItem.getSellCode() != null) {
                cartItemRepository.updateAllTempSellCodeByNewTempSellCode(replaceItem.getSellCode(),getCurrentUser(), cartItem.getSellCode());
            }else {
                // Remove tempSellCode of item has tempSellCode equal with sell code of current item
                cartItemRepository.removeTempSellCode(getCurrentUser(), cartItem.getSellCode());
            }
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        cartItemRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete cart item success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CI_DA')")
    public ApiMessageDto<String> deleteAll() {
        if(!isStudent()){
            throw new UnauthorizationException("Not allow delete");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        cartItemRepository.deleteAllByStudentId(getCurrentUser());
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete cart item success");
        return apiMessageDto;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CI_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateCartItemForm createCartItemForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed create.");
        }
        CourseTransaction existingCourseTransaction = courseTransactionRepository.findFirstByCourseIdAndStudentIdAndBookingStateOrBookingState(createCartItemForm.getCourseId(),getCurrentUser(),LifeUniConstant.BOOKING_STATE_PAID, LifeUniConstant.BOOKING_STATE_UNPAID);
        if (existingCourseTransaction != null) {
            throw new BadRequestException("Course transaction already exist",ErrorCode.COURSE_TRANSACTION_ERROR_ALREADY_EXIST);
        }
        Registration registration = registrationRepository.findFirstByCourseIdAndStudentId(createCartItemForm.getCourseId(),getCurrentUser());
        if (registration != null) {
            throw new BadRequestException("Registration already exist",ErrorCode.REGISTRATION_ERROR_EXIST);
        }
        Student student = getValidStudent(getCurrentUser());
        checkExistCartItemByStudentIdAndCourseId(getCurrentUser(),createCartItemForm.getCourseId());
        //create cart Items
        CartItem newItem = createCartItem(createCartItemForm.getCourseId(), student,createCartItemForm.getSellCode());

        if (student.getIsSeller()) {
            if (!StringUtils.isNotBlank(newItem.getSellCode())) {
                newItem.setTempSellCode(student.getReferralCode());
            }
        }else {
            ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(student.getId());
            if (referralSellerLog == null) {
                CartItem itemHasSellCode = cartItemRepository.findFirstByStudentIdAndSellCodeIsNotNullOrderByTimeCreatedAsc(student.getId());
                if (itemHasSellCode == null && StringUtils.isNotBlank(newItem.getSellCode())) {
                    cartItemRepository.updateAllTempSellCode(student.getId(), newItem.getSellCode());
                } else if (itemHasSellCode != null && !StringUtils.isNotBlank(newItem.getSellCode())) {
                    newItem.setTempSellCode(itemHasSellCode.getSellCode());
                }
            } else if (!StringUtils.isNotBlank(newItem.getSellCode())) {
                newItem.setTempSellCode(referralSellerLog.getRefStudent().getReferralCode());
            }
        }
        cartItemRepository.save(newItem);

        //remove exist cartItem if add combo contain cartItem
        Course course = courseRepository.findById(createCartItemForm.getCourseId()).orElse(null);
        if(course != null && Objects.equals(course.getKind(), LifeUniConstant.COURSE_KIND_COMBO)){
            List<Long> courseInCombo = courseRepository.findAllIdsCourseByComboId(createCartItemForm.getCourseId());
            cartItemRepository.deleteAllByCourseIdInAndStudentId(courseInCombo,getCurrentUser());
        }
        apiMessageDto.setMessage("Create cart item success");
        return apiMessageDto;
    }
    @PostMapping(value = "/create-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CI_CL')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateListCartItemForm createListCartItemForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed create.");
        }
        Student student = getValidStudent(getCurrentUser());
        List<CartItem> cartItemList = new ArrayList<>();
        List<Long> processedCourseIds = new ArrayList<>();
        List<Long> courseInCombo = new ArrayList<>();

        for (CreateCartItemForm createCartItemForm: createListCartItemForm.getCartItems()){
            // if courseId already processed => next course
            if(processedCourseIds.contains(createCartItemForm.getCourseId())){
                continue;
            }
            CourseTransaction existingCourseTransaction = courseTransactionRepository.findFirstByCourseIdAndStudentIdAndBookingStateOrBookingState(createCartItemForm.getCourseId(),getCurrentUser(),LifeUniConstant.BOOKING_STATE_PAID, LifeUniConstant.BOOKING_STATE_UNPAID);
            Registration registration = registrationRepository.findFirstByCourseIdAndStudentId(createCartItemForm.getCourseId(),getCurrentUser());
            if(existingCourseTransaction == null && registration == null){
                CartItem existCartItem = cartItemRepository.findFirstByCourseIdAndStudentId(createCartItemForm.getCourseId(), getCurrentUser()).orElse(null);
                if(existCartItem !=null ){
                    existCartItem.setSellCode(createCartItemForm.getSellCode());
                    cartItemList.add(existCartItem);
                }else {
                    Course course = courseRepository.findById(createCartItemForm.getCourseId()).orElse(null);
                    if(course !=null && Objects.equals(course.getKind(), LifeUniConstant.COURSE_KIND_COMBO)){
                        courseInCombo.addAll(courseRepository.findAllIdsCourseByComboId(createCartItemForm.getCourseId()));
                    }
                    CartItem cartItem = createCartItem(createCartItemForm.getCourseId(), student,createCartItemForm.getSellCode());
                    cartItemList.add(cartItem);
                }
            }
            processedCourseIds.add(createCartItemForm.getCourseId());
        }

        CartItem newItem = cartItemList.stream().filter(subItem -> subItem.getSellCode() != null).findFirst().orElse(null);
        if (student.getIsSeller()) {
            if (newItem != null && !StringUtils.isNotBlank(newItem.getSellCode())) {
                setValidTempSellCodeIntoCartItem(cartItemList, student.getReferralCode());
            }
        }else {
            ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(student.getId());
            if (referralSellerLog == null) {
                CartItem itemHasSellCode = cartItemRepository.findFirstByStudentIdAndSellCodeIsNotNullOrderByTimeCreatedAsc(student.getId());
                if (itemHasSellCode == null && newItem != null) {
                    cartItemRepository.updateAllTempSellCode(student.getId(), newItem.getSellCode());
                    setValidTempSellCodeIntoCartItem(cartItemList, newItem.getSellCode());
                } else if (itemHasSellCode != null && newItem == null) {
                    setValidTempSellCodeIntoCartItem(cartItemList, itemHasSellCode.getSellCode());
                }
            } else {
                setValidTempSellCodeIntoCartItem(cartItemList, referralSellerLog.getRefStudent().getReferralCode());
            }
        }

        cartItemRepository.saveAll(cartItemList);
        cartItemRepository.deleteAllByCourseIdInAndStudentId(courseInCombo,getCurrentUser());
        apiMessageDto.setMessage("Create list cart item success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CI_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateCartItemForm updateCartItemForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed update");
        }
        getValidStudent(getCurrentUser());
        checkExistCartItemByStudentIdAndCourseId(getCurrentUser(),updateCartItemForm.getCourseId());
        CartItem cartItem = cartItemRepository.findFirstByIdAndStudentId(updateCartItemForm.getId(),getCurrentUser()).orElse(null);
        if(cartItem == null){
            throw new BadRequestException("Cart item not found",ErrorCode.CART_ITEM_ERROR_NOT_FOUND);
        }

        String currentSellCode = null;
        if (StringUtils.isNotBlank(cartItem.getSellCode())) {
            currentSellCode = cartItem.getSellCode();
        }
        cartItem.setCourse(getValidCourse(updateCartItemForm.getCourseId()));
        setValidSellCode(cartItem,updateCartItemForm.getSellCode());

        if (currentSellCode != null && !StringUtils.equals(currentSellCode, cartItem.getSellCode())) {
            cartItemRepository.updateAllTempSellCodeByNewTempSellCode(cartItem.getSellCode(), getCurrentUser(), currentSellCode);
        }

        cartItemRepository.save(cartItem);
        apiMessageDto.setMessage("Update cart item success");
        return apiMessageDto;
    }

    private void setValidTempSellCodeIntoCartItem(List<CartItem> cartItems, String tempSellCode) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getSellCode() == null) {
                cartItem.setTempSellCode(tempSellCode);
            }
        }
    }

    private void checkExistCartItemByStudentIdAndCourseId(Long studentId, Long courseId){
        CartItem existCartItem = cartItemRepository.findFirstByCourseIdAndStudentId(courseId,studentId).orElse(null);
        if(existCartItem != null){
           throw new BadRequestException("Cart Item already exist", ErrorCode.CART_ITEM_ERROR_ALREADY_EXIST);
        }
    }
    private Student getValidStudent(Long studentId){
        Student student = studentRepository.findById(studentId).orElse(null);
        if(student == null){
            throw new NotFoundException("Student not found",ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(student.getStatus(),LifeUniConstant.STATUS_ACTIVE)){
            throw new NotFoundException("Student not active",ErrorCode.STUDENT_ERROR_NOT_ACTIVE);
        }
        return student;
    }

    private CartItem getCartItemByCurrentUser(Long cartItemId){
        CartItem cartItem;
        if(isStudent()){
            cartItem = cartItemRepository.findFirstByIdAndStudentId(cartItemId,getCurrentUser()).orElse(null);
        }else {
            cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        }
        if(cartItem == null){
            throw new NotFoundException("Cart Item not found",ErrorCode.CART_ITEM_ERROR_NOT_FOUND);
        }
        return cartItem;
    }
}
