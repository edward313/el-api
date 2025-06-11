package com.easylearning.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.booking.BookingAdminDto;
import com.easylearning.api.dto.booking.BookingDto;
import com.easylearning.api.dto.booking.CreateBookingDto;
import com.easylearning.api.dto.booking.PaymentInfoDto;
import com.easylearning.api.dto.notification.RevenueShareInfoForNotificationMap;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.booking.ApprovalBookingForm;
import com.easylearning.api.form.booking.CreateBookingForm;
import com.easylearning.api.form.booking.CreateFreeCourseBookingForm;
import com.easylearning.api.form.booking.UpdateBookingForm;
import com.easylearning.api.mapper.BookingMapper;
import com.easylearning.api.mapper.CourseTransactionMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.BookingCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.payos.*;
import com.easylearning.api.service.rabbitMQ.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/v1/booking")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class BookingController extends ABasicController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private StudentRepository studentRepository;


    @Autowired
    PayosService payosService;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;

    @Autowired
    private CourseRetailRepository courseRetailRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PromotionCodeRepository promotionCodeRepository;

    @Autowired
    private RevenueShareRepository revenueShareRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ReferralSellerLogRepository referralSellerLogRepository;

    @Autowired
    private ReferralExpertLogRepository referralExpertLogRepository;
    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private CourseTransactionMapper courseTransactionMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;



    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_V')")
    public ApiMessageDto<BookingAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<BookingAdminDto> apiMessageDto = new ApiMessageDto<>();
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            throw new NotFoundException("Booking not found",ErrorCode.BOOKING_ERROR_NOT_FOUND);
        }
        List<CourseTransaction> courseTransactions = courseTransactionRepository.findAllByBookingId(id);
        BookingAdminDto bookingAdminDto = bookingMapper.fromEntityToBookingAdminDto(booking);
        bookingAdminDto.setTransactions(courseTransactionMapper.fromEntityToCourseTransactionAdminDtoList(courseTransactions));
        apiMessageDto.setData(bookingAdminDto);
        apiMessageDto.setMessage("Get booking detail success");
        return apiMessageDto;
    }
    @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<BookingDto> clientGet(@PathVariable("id") Long id) {
        ApiMessageDto<BookingDto> apiMessageDto = new ApiMessageDto<>();
        Booking booking = bookingRepository.findFirstByStudentIdAndId(getCurrentUser(),id).orElse(null);
        if (booking == null) {
            throw new NotFoundException("Booking not found",ErrorCode.BOOKING_ERROR_NOT_FOUND);
        }
        List<CourseTransaction> transactions = courseTransactionRepository.findAllByBookingId(id);
        BookingDto bookingDto = bookingMapper.fromEntityToBookingDtoForClient(booking);
        bookingDto.setTransactions(courseTransactionMapper.fromEntityToCourseTransactionDtoForMyClientBookingList(transactions));
        apiMessageDto.setData(bookingDto);
        apiMessageDto.setMessage("Get booking detail success");
        return apiMessageDto;
    }

    @GetMapping(value = "/payment-info/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_PI')")
    public ApiMessageDto<String> getPaymentInfo(@PathVariable("bookingId") Long bookingId) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isStudent()){
            throw new UnauthorizationException("Not allow get");
        }
        Booking booking = bookingRepository.findFirstByStudentIdAndId(getCurrentUser(),bookingId).orElse(null);
        if (booking == null) {
            throw new NotFoundException("Booking not found",ErrorCode.BOOKING_ERROR_NOT_FOUND);
        }
        if(Objects.equals(booking.getState(), LifeUniConstant.BOOKING_STATE_PAID)){
            throw new NotFoundException("Booking already paid",ErrorCode.BOOKING_ERROR_ALREADY_PAID);
        }
        String bankInfo = settingsRepository.findValueByGroupName(LifeUniConstant.SETTING_GROUP_NAME_BANK_INFO);

        apiMessageDto.setData(getJsonPaymentInfo(booking, bankInfo));
        apiMessageDto.setMessage("Get booking detail success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_L')")
    public ApiMessageDto<ResponseListDto<List<BookingAdminDto>>> list(BookingCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<BookingAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        pageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.asc("state"), Sort.Order.desc("createdDate")));
        Page<Booking> bookingList = bookingRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<BookingAdminDto>> responseListObj = new ResponseListDto<>();
        List<BookingAdminDto> bookingDtoList = bookingMapper.fromEntityToBookingAdminDtoList(bookingList.getContent());

        responseListObj.setContent(bookingDtoList);
        responseListObj.setTotalPages(bookingList.getTotalPages());
        responseListObj.setTotalElements(bookingList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list of bookings success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<BookingDto>>> autoComplete(BookingCriteria criteria) {
        ApiMessageDto<ResponseListDto<List<BookingDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Pageable pageable = PageRequest.of(0,10);
        criteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<Booking> bookingList = bookingRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<BookingDto>> responseListObj = new ResponseListDto<>();
        List<BookingDto> bookingDtoList = bookingMapper.fromEntityToBookingDtoAutoCompleteList(bookingList.getContent());

        responseListObj.setContent(bookingDtoList);
        responseListObj.setTotalPages(bookingList.getTotalPages());
        responseListObj.setTotalElements(bookingList.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list of bookings success");
        return responseListObjApiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            throw new NotFoundException("Booking not found",ErrorCode.BOOKING_ERROR_NOT_FOUND);
        }
        revenueShareRepository.deleteAllByBookingId(id);
        courseTransactionRepository.deleteAllByBookingId(id);
        bookingRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete booking success");
        return apiMessageDto;
    }
    public void checkExistCourses(List<CartItem> cartItems, Long studentId){
        List<Long> courseIds = new ArrayList<>();
        for(CartItem cartItem: cartItems){
            courseIds.add(cartItem.getCourse().getId());
        }
        Registration registration = registrationRepository.findFirstByCourseIdInAndStudentId(courseIds,studentId);
        if (registration != null) {
            throw new BadRequestException("Registration already exist",ErrorCode.REGISTRATION_ERROR_EXIST);
        }
        CourseTransaction existingCourseTransaction = courseTransactionRepository.findFirstByCourseIdInAndStudentIdAndBookingStateOrBookingState(courseIds,studentId,LifeUniConstant.BOOKING_STATE_PAID, LifeUniConstant.BOOKING_STATE_UNPAID);
        if (existingCourseTransaction != null) {
            throw new BadRequestException("CourseTransaction already exist",ErrorCode.COURSE_TRANSACTION_ERROR_ALREADY_EXIST);
        }
    }

    @Transactional
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_C')")
    public ApiMessageDto<PayosPaymentDto> create(@Valid @RequestBody CreateBookingForm createBookingForm, BindingResult bindingResult) {
        ApiMessageDto<PayosPaymentDto> apiMessageDto = new ApiMessageDto<>();
        Student student = getCurrentStudent();
        if(Boolean.TRUE.equals(student.getIsSystemSeller())){
            throw new BadRequestException("System seller can not buy course",ErrorCode.BOOKING_ERROR_NOT_ALLOW_SYSTEM_SELLER_BUY_COURSE);
        }
        List<CartItem> cartItems = cartItemRepository.findAllByStudentId(getCurrentUser());
        for (CartItem cartItem : cartItems) {
            if (cartItem.getTempSellCode() != null) {
                cartItem.setSellCode(cartItem.getTempSellCode());
            }

        }

        Booking booking = createBookingAndCourseTransactionByCartItem(cartItems,student,createBookingForm.getPaymentMethod(),createBookingForm.getPaymentData(),createBookingForm.getPromotionCode(), createBookingForm.getSellCode());
        cartItemRepository.deleteAllByStudentId(student.getId());
        // for wallet method
        if(Objects.equals(createBookingForm.getPaymentMethod(), LifeUniConstant.BOOKING_PAYMENT_METHOD_WALLET)) {
            processRevenueShareAndRegistrationForWalletAndVoucherMethod(booking, student);
        }
        PayosPaymentDto paymentInfo;
        paymentInfo = payosService.getPayosPaymentDto(booking.getTotalMoney().longValue(),booking.getId(),
                "Don hang " + booking.getId());
//        paymentInfo = payosService.getPayosPaymentDto(10000L,booking.getId(),
//                "Don hang " + booking.getId());
        apiMessageDto.setData(paymentInfo);
        apiMessageDto.setMessage("Tạo mã thành công");

        return apiMessageDto;
    }

    @PostMapping(value = "/change-state", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_CS')")
    public ApiMessageDto<String> approve(@Valid @RequestBody ApprovalBookingForm approvalBookingForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Booking booking = bookingRepository.findById(approvalBookingForm.getId()).orElse(null);
        if(booking == null){
            throw new BadRequestException("Booking is not found", ErrorCode.BOOKING_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(booking.getState(), LifeUniConstant.BOOKING_STATE_UNPAID)){
            throw new BadRequestException("Only can change state unpaid booking", ErrorCode.BOOKING_ERROR_NOT_ALLOW_CHANGE_STATE);
        }
        if(!Objects.equals(booking.getPaymentMethod(), LifeUniConstant.BOOKING_PAYMENT_METHOD_BANKING)){
            throw new BadRequestException("Only can change state payment method banking", ErrorCode.BOOKING_ERROR_NOT_ALLOW_CHANGE_STATE);
        }
        Integer notificationKind;
        //approve
        if(Objects.equals(approvalBookingForm.getState(), LifeUniConstant.BOOKING_STATE_PAID)){
            List<CourseTransaction> courseTransactions = courseTransactionRepository.findAllByBookingId(booking.getId());
            // set totalMoney and saleOffMoney
            List<RevenueShare> revenueShares = new ArrayList<>();
            createReferralSellerLogFromCouponSellerCode(booking);
            checkExistCourseRetail(booking);
            for(CourseTransaction courseTransaction: courseTransactions){
                // create RevenueShare and Registration
                revenueShares.addAll(createRevenueShareAndRegistrationWhenPayBooking(courseTransaction, booking.getStudent()));
            }
//            checkFirstBooking(booking);

            notificationKind = LifeUniConstant.NOTIFICATION_KIND_APPROVE_BOOKING;
            // send notifications to those who receive revenue money
            sendNotificationForRevenue(revenueShares);
        }
        // reject
        else {
            notificationKind = LifeUniConstant.NOTIFICATION_KIND_REJECT_BOOKING;
        }
        notificationService.createNotificationAndSendMessageForBuyer(notificationKind, booking.getStudent().getId(), booking.getId(),approvalBookingForm.getState(),booking.getCode());
        booking.setState(approvalBookingForm.getState());
        bookingRepository.save(booking);

        apiMessageDto.setMessage("Change state booking success");
        return apiMessageDto;
    }

    Booking createBookingAndCourseTransactionByCartItem(List<CartItem> cartItems, Student student, Integer paymentMethod, String paymentData, String promotionCode, String sellCode) {
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty", ErrorCode.BOOKING_ERROR_CART_ITEM_EMPTY);
        }
        if (paymentMethod.equals(LifeUniConstant.BOOKING_PAYMENT_METHOD_BANKING) && paymentData == null) {
            throw new BadRequestException("paymentData is required");
        }

        checkExistCourses(cartItems, getCurrentUser());
        Booking booking = createBooking(student, paymentMethod, paymentData, LifeUniConstant.BOOKING_STATE_REJECT, LifeUniConstant.BOOKING_PAYOUT_STATUS_UNPAID);
        List<CourseTransaction> courseTransactions = createListCourseTransaction(cartItems, booking);

        double totalMoney = 0;
        for (CourseTransaction courseTransaction : courseTransactions) {
            totalMoney += courseTransaction.getPrice();
        }
        booking.setTotalMoney(totalMoney);

        if (StringUtils.isNoneBlank(promotionCode)) {
            booking = setSaleOffMoneyForBooking(totalMoney, promotionCode, booking);
        }
        if (booking.getSaleOffMoney() == null) {
            booking.setSaleOffMoney(0.0);
        }

        booking.setState(LifeUniConstant.BOOKING_STATE_REJECT);
        double price = booking.getTotalMoney() - booking.getSaleOffMoney();

        boolean isUseSellCode = false;
        ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(student.getId());
        if (referralSellerLog != null) {
            booking.setCouponSellCode(referralSellerLog.getRefStudent().getReferralCode());
            isUseSellCode = true;
        } else if (StringUtils.isNotBlank(sellCode)) {
            validationCoupon(sellCode, student);
            booking.setCouponSellCode(sellCode);
            isUseSellCode = true;
        } else if (student.getIsSeller()) {
            booking.setCouponSellCode(student.getReferralCode());
            isUseSellCode = true;
        }

        if (isUseSellCode) {
            double systemCouponMoney = getSystemCouponMoney();
            double couponMoney = calculateCouponMoneyFromSystem(courseTransactions, systemCouponMoney);

            booking.setCouponMoney(couponMoney);
            if (price <= couponMoney) {
                booking.setCouponMoney(price);
                booking.setState(LifeUniConstant.BOOKING_STATE_PAID);
            }
            price = price - couponMoney;
        }

        if (paymentMethod.equals(LifeUniConstant.BOOKING_PAYMENT_METHOD_WALLET)) {
            Wallet wallet = walletRepository.findFirstByAccountId(student.getAccount().getId());
            if (wallet == null || (booking.getState().equals(LifeUniConstant.BOOKING_STATE_UNPAID) && wallet.getBalance() < price)) {
                throw new BadRequestException("Do not enough balance in wallet", ErrorCode.BOOKING_ERROR_NOT_ENOUGH_BALANCE_IN_WALLET);
            }
//            checkFirstBooking(booking);
            booking.setState(LifeUniConstant.BOOKING_STATE_PAID);
            String note = LifeUniConstant.WALLET_TRANSACTION_NOTE_BUY_COURSE + booking.getCode();
            double lastBalance = wallet.getBalance() - price;

            createWalletTransaction(wallet, LifeUniConstant.WALLET_TRANSACTION_KIND_BUY_COURSE, price, LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, note, lastBalance);
            wallet.setBalance(lastBalance >= 0 ? lastBalance : 0);
            walletRepository.save(wallet);
        }
        bookingRepository.save(booking);
        courseTransactionRepository.saveAll(courseTransactions);

        return booking;
    }

    private double calculateCouponMoneyFromSystem(List<CourseTransaction> courseTransactions, double systemCouponMoney) {
        double couponMoney = 0D;
        for (CourseTransaction courseTransaction : courseTransactions) {
            if (courseTransaction.getPrice() <= systemCouponMoney) {
                couponMoney = couponMoney + courseTransaction.getPrice();
                courseTransaction.setPrice(0D);
            }else {
                couponMoney = couponMoney + systemCouponMoney;
                courseTransaction.setPrice(courseTransaction.getPrice() - systemCouponMoney);
            }
        }
        return couponMoney;
    }
    private void validationCoupon(String coupon, Student student){
        Student seller = studentRepository.findByReferralCodeAndIsSellerAndStatus(coupon,
                LifeUniConstant.STUDENT_IS_SELLER,LifeUniConstant.STATUS_ACTIVE).orElse(null);
        if(seller == null || (student.getReferralCode() != null && StringUtils.equals(student.getReferralCode(), coupon))){
            throw new BadRequestException("Invalid coupon",ErrorCode.STUDENT_REFERRAL_CODE_INVALID);
        }
    }

    private void processRevenueShareAndRegistrationForWalletAndVoucherMethod(Booking booking, Student student) {
        List<CourseTransaction> courseTransactions = courseTransactionRepository.findAllByBookingId(booking.getId());
        List<RevenueShare> revenueShares = new ArrayList<>();
        createReferralSellerLogFromCouponSellerCode(booking);
        checkExistCourseRetail(booking);
        for (CourseTransaction courseTransaction : courseTransactions) {
            revenueShares.addAll(createRevenueShareAndRegistrationWhenPayBooking(courseTransaction, student));
        }
        sendNotificationForRevenue(revenueShares);
    }

    public String getJsonPaymentInfo(Booking booking, String bankInfo){
        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        paymentInfoDto.setBookingId(booking.getId());
        paymentInfoDto.setBankInfo(bankInfo);
        if(booking.getSaleOffMoney() == null){
            booking.setSaleOffMoney(0.0);
        }
        paymentInfoDto.setTotalMoney(booking.getTotalMoney() - booking.getSaleOffMoney());
        try {
            return objectMapper.writeValueAsString(paymentInfoDto);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<RevenueShare> createRevenueShareAndRegistrationWhenPayBooking(CourseTransaction courseTransaction, Student student){
        // upgrade student to seller when buy seller course
        if(courseTransaction.getCourse().getIsSellerCourse()){
            updateSeller(courseTransaction.getBooking().getStudent());
        }
        // Create Registration combo course
        if(Objects.equals(courseTransaction.getCourse().getKind(), LifeUniConstant.COURSE_KIND_COMBO)){
            List<Course> coursesInCombo =  courseRepository.findAllCourseByComboId(courseTransaction.getCourse().getId());
            for (Course c: coursesInCombo){
                createRegistrationAndCreateTopCategoryHome(c,courseTransaction.getBooking().getStudent());
            }
        } else { // Create Registration for single course
            createRegistrationAndCreateTopCategoryHome(courseTransaction.getCourse(),courseTransaction.getBooking().getStudent());
        }
        // create RevenueShare
        List<RevenueShare> revenueShareList = createValidRevenueShare(courseTransaction, student);
        revenueShareRepository.saveAll(revenueShareList);
        return revenueShareList;
    }
    public Student getCurrentStudent(){
        if (!isStudent()){
            throw new UnauthorizationException("Not allow create");
        }
        Student student = studentRepository.findById(getCurrentUser()).orElse(null);
        checkNotNullAndActiveStudent(student);
        return student;
    }

    public Booking createBooking(Student student, Integer paymentMethod, String paymentData, Integer bookingState, Integer payoutStatus){
        Booking booking = new Booking();
        booking.setPaymentMethod(paymentMethod);
        booking.setStudent(student);
        booking.setCode(getUniqueCode());
        booking.setState(bookingState);
        booking.setPayoutStatus(payoutStatus);
        booking.setPaymentData(paymentData);
        return booking;
    }
    @Transactional
    @PostMapping(value = "/buy-free-course", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_C')")
    public ApiMessageDto<String> buyFreeCourse(@Valid @RequestBody CreateFreeCourseBookingForm createForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = getCurrentStudent();
        if(Boolean.TRUE.equals(student.getIsSystemSeller())){
            throw new BadRequestException("System seller can not buy course",ErrorCode.BOOKING_ERROR_NOT_ALLOW_SYSTEM_SELLER_BUY_COURSE);
        }
        CourseTransaction existingCourseTransaction = courseTransactionRepository.findFirstByCourseIdAndStudentIdAndBookingStateOrBookingState(createForm.getCourseId(),getCurrentUser(),LifeUniConstant.BOOKING_STATE_PAID, LifeUniConstant.BOOKING_STATE_UNPAID);
        Registration registration = registrationRepository.findFirstByCourseIdAndStudentId(createForm.getCourseId(),getCurrentUser());
        if (registration != null) {
            throw new BadRequestException("Registration already exist",ErrorCode.REGISTRATION_ERROR_EXIST);
        }
        if (existingCourseTransaction != null) {
            throw new BadRequestException("Course transaction already exist",ErrorCode.COURSE_TRANSACTION_ERROR_ALREADY_EXIST);
        }
        Course course = courseRepository.findById(createForm.getCourseId()).orElse(null);
        checkNotNullAndActiveCourse(course);
        if(course==null || !Objects.equals(course.getPrice(),0D)){
            throw new BadRequestException("Course not free",ErrorCode.COURSE_ERROR_NOT_FREE);
        }
        Booking booking = createBooking(student,null,null,LifeUniConstant.BOOKING_STATE_PAID,LifeUniConstant.BOOKING_PAYOUT_STATUS_UNPAID);
        booking.setTotalMoney(course.getPrice());
//        checkFirstBooking(booking);
        bookingRepository.save(booking);

        CourseTransaction courseTransaction = createValidCourseTransaction(course,createForm.getSellCode(),0.0,booking, false);
        courseTransaction.setBooking(booking);
        courseTransactionRepository.save(courseTransaction);
        List<RevenueShare> revenueShares = createRevenueShareAndRegistrationWhenPayBooking(courseTransaction, student);
        // send notifications to those who receive revenue money
        sendNotificationForRevenue(revenueShares);
        cartItemRepository.deleteAllByStudentIdAndCourseId(student.getId(),createForm.getCourseId());
        apiMessageDto.setMessage("Create booking success");
        return apiMessageDto;
    }

    private void checkFirstBooking(Booking booking) {
        // Set init money if student does not have a referral
        Integer countBookingStatePaid = bookingRepository.countByStudentIdAndState(booking.getStudent().getId(), LifeUniConstant.BOOKING_STATE_PAID);
        if (!booking.getStudent().getIsReferralBonusPaid() && countBookingStatePaid == 0) { // Check booking first to set default balance
            double systemBonusMoney = getSystemBonusMoney();
            Wallet studentWallet = walletRepository.findByAccountIdAndKind(booking.getStudent().getAccount().getId(), LifeUniConstant.WALLET_KIND_STUDENT);
            if (studentWallet != null) {
                Double balance = studentWallet.getBalance() != null ?
                        studentWallet.getBalance() + systemBonusMoney : systemBonusMoney;
                createWalletTransaction(studentWallet, LifeUniConstant.WALLET_TRANSACTION_KIND_INIT_MONEY, balance, LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, LifeUniConstant.WALLET_TRANSACTION_NOTE_BUY_FIRST_COURSE, balance);
                studentWallet.setBalance(balance);
                walletRepository.save(studentWallet);

                Student student = booking.getStudent();
                student.setIsReferralBonusPaid(true);
                studentRepository.save(student);
            }
        }
    }


    private void sendNotificationForRevenue(List<RevenueShare> revenueShares) {
        Map<String, RevenueShareInfoForNotificationMap> processedPairs = new HashMap<>();
        // Xử lý và cộng dồn giá trị revenueMoney cho từng cặp key
        for (RevenueShare revenueShare : revenueShares) {
            if (!Objects.equals(revenueShare.getKind(), LifeUniConstant.REVENUE_SHARE_KIND_SYSTEM)) {
                Long userId = Optional.ofNullable(revenueShare.getSeller())
                        .map(Student::getId)
                        .orElseGet(() -> revenueShare.getExpert().getId());
                Long courseTransactionId = revenueShare.getCourseTransaction().getId();
                String courseName = revenueShare.getCourseTransaction().getCourse().getName();
                double revenueMoney = revenueShare.getRevenueMoney();
                // Tạo một key duy nhất cho cặp userId và courseTransactionId
                String pairKey = userId + "_" + courseTransactionId;

                if (!processedPairs.containsKey(pairKey)) {
                    // Nếu chưa tồn tại, tạo mới đối tượng RevenueShareInfo và thêm vào Map
                    RevenueShareInfoForNotificationMap info = new RevenueShareInfoForNotificationMap();
                    info.setUserId(userId);
                    info.setCourseName(courseName);
                    info.setTotalRevenueMoney(revenueMoney);
                    processedPairs.put(pairKey, info);
                } else {
                    // Nếu đã tồn tại, cộng dồn revenueMoney vào giá trị hiện tại
                    RevenueShareInfoForNotificationMap info = processedPairs.get(pairKey);
                    info.setTotalRevenueMoney(info.getTotalRevenueMoney() + revenueMoney);
                }
            }
        }

        // Sau khi xử lý, gửi thông báo cho từng cặp key trong Map
        for (Map.Entry<String, RevenueShareInfoForNotificationMap> entry : processedPairs.entrySet()) {
            RevenueShareInfoForNotificationMap info = entry.getValue();
            Long userId = info.getUserId();
            String courseName = info.getCourseName();
            double totalRevenueMoney = info.getTotalRevenueMoney();

            // Gửi thông báo cho cặp key với thông tin
            notificationService.createNotificationAndSendMessageForRevenueShare(LifeUniConstant.NOTIFICATION_KIND_RECEIVE_REVENUE, userId, courseName, totalRevenueMoney);
        }
    }
    private void updateSeller(Student student){
        if(!student.getIsSeller()){
            student.setIsSeller(LifeUniConstant.STUDENT_IS_SELLER);
            student.setReferralCode(getUniqueSellerCode());
            studentRepository.save(student);

            Wallet wallet = walletRepository.findByAccountId(student.getAccount().getId());
            if(wallet == null) {
                wallet = new Wallet();
                wallet.setAccount(student.getAccount());
                wallet.setKind(LifeUniConstant.WALLET_KIND_SELLER);
                wallet.setWalletNumber(getUniqueWalletNumber());
                walletRepository.save(wallet);
            }else {
                wallet.setKind(LifeUniConstant.WALLET_KIND_SELLER);
            }
            walletRepository.save(wallet);

            notificationService.createNotificationAndSendMessage(LifeUniConstant.NOTIFICATION_KIND_UPGRADE_SELLER,student.getId(),student.getAccount().getFullName(),student.getAccount().getAvatarPath());
        }
    }
    private void createRegistrationAndCreateTopCategoryHome(Course course, Student student){
        Registration registration = registrationRepository.findFirstByCourseIdAndStudentId(course.getId(),getCurrentUser());
        if(registration == null){
            // create registration
            registration = new Registration();
            registration.setStudent(student);
            registration.setCourse(course);
            registration.setIsFinished(false);
            // increase Course Sold Quantity
            if(course.getSoldQuantity() == null){
                course.setSoldQuantity(0);
            }
            course.setSoldQuantity(course.getSoldQuantity()+1);
            //increase Expert Total Student
            updateTotalStudentExpert(course.getExpert(), student.getId(),true);
            registrationRepository.save(registration);
            courseRepository.save(course);
            createCategoryHomeTopSold(course);
        }
    }


    public Booking setSaleOffMoneyForBooking(Double totalMoney, String code, Booking booking){
        PromotionCode promotionCode = promotionCodeRepository.findValidPromotionCodeByCode(code,LifeUniConstant.STATUS_ACTIVE,
                LifeUniConstant.PROMOTION_STATE_RUNNING, LifeUniConstant.STATUS_ACTIVE).orElse(null);
        if(promotionCode==null) {
            throw new NotFoundException("Promotion Code not found", ErrorCode.PROMOTION_CODE_ERROR_NOT_FOUND);
        }
        Promotion promotion = promotionCode.getPromotion();
        // check student used code or promotion code already reach maximu quality
        if( (promotion.getType().equals(LifeUniConstant.PROMOTION_TYPE_USE_ONE) && promotionCode.getQuantityUsed() > 0)
                || (promotion.getType().equals(LifeUniConstant.PROMOTION_TYPE_USE_MULTIPLE) && promotionCode.getQuantityUsed() >= promotion.getQuantity())
                || (bookingRepository.findFirstByStudentIdAndPromotionId(booking.getStudent().getId(),promotion.getId()).orElse(null) != null)){
            throw new BadRequestException("Promotion code has reached its maximum used quantity",ErrorCode.PROMOTION_CODE_ERROR_REACHED_MAXIMUM_QUANTITY);
        }
        double saleOffMoney;
        if(promotion.getKind().equals(LifeUniConstant.PROMOTION_KIND_MONEY)){
            saleOffMoney = promotion.getDiscountValue();
        }else {
            saleOffMoney = (promotion.getDiscountValue()*totalMoney)/100;
            saleOffMoney = (saleOffMoney > promotion.getLimitValue()) ? promotion.getLimitValue() : saleOffMoney;
        }
        saleOffMoney = (saleOffMoney > totalMoney) ? totalMoney : saleOffMoney;
        promotionCode.setQuantityUsed(promotionCode.getQuantityUsed() + 1);
        promotionCodeRepository.save(promotionCode);
        booking.setPromotion(promotion);
        booking.setSaleOffMoney(saleOffMoney);
        return booking;
    }
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('B_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateBookingForm updateBookingForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Booking booking = bookingRepository.findById(updateBookingForm.getId()).orElse(null);
        if(booking == null){
            throw new NotFoundException("Booking not found",ErrorCode.BOOKING_ERROR_NOT_FOUND);
        }
        if(!booking.getState().equals(updateBookingForm.getState())) {
            if(updateBookingForm.getState().equals(LifeUniConstant.BOOKING_STATE_UNPAID)){
                throw new NotFoundException("Booking not allow update state paid to unpaid",ErrorCode.BOOKING_ERROR_NOT_ALLOW_UPDATE_PAID_TO_UNPAID);
            }else {
                List<CourseTransaction> courseTransactions = courseTransactionRepository.findAllByBookingId(booking.getId());
                for (CourseTransaction courseTransaction: courseTransactions){
                    createRevenueShareAndRegistrationWhenPayBooking(courseTransaction, booking.getStudent());
                }
            }
        }
        bookingMapper.updateBookingFromUpdateForm(updateBookingForm,booking);
        bookingRepository.save(booking);
        apiMessageDto.setMessage("Update booking success");
        return apiMessageDto;
    }

    private List<CourseTransaction> createListCourseTransaction(List<CartItem> cartItems, Booking booking) {
        List<CourseTransaction> courseTransactionList = new ArrayList<>();
        // Create CourseTransaction only if not already created for this courseId
        boolean isCreatedCourseTransaction;
        for(CartItem cartItem: cartItems){
            isCreatedCourseTransaction = false;
            for(CourseTransaction courseTransaction: courseTransactionList){
                if(courseTransaction.getCourse().getId().equals(cartItem.getCourse().getId())){
                    isCreatedCourseTransaction = true;
                    break;
                }
            }
            if(!isCreatedCourseTransaction){
                boolean isUseTempSellCode = false;
                if (cartItem.getTempSellCode() != null) {
                    isUseTempSellCode = true;
                }
                courseTransactionList.add(createValidCourseTransaction(cartItem.getCourse(),cartItem.getSellCode(),cartItem.getExtraMoney(),booking, isUseTempSellCode));
            }
        }
        return courseTransactionList;
    }
    private CourseTransaction createValidCourseTransaction(Course course, String sellCode, Double extraMoney, Booking booking, Boolean isUseTempSellCode){
        checkNotNullAndActiveCourse(course);
        CourseTransaction courseTransaction = new CourseTransaction();
        if(StringUtils.isNoneBlank(sellCode)){
            Student seller;
            if (!isUseTempSellCode) {
                CourseRetail courseRetail = courseRetailRepository.findFirstByCourseIdAndSeller_ReferralCodeAndSeller_IsSellerAndSeller_Status(course.getId(),
                        sellCode, LifeUniConstant.STUDENT_IS_SELLER, LifeUniConstant.STATUS_ACTIVE).orElse(null);
                isValidCourseRetail(courseRetail);
                seller = courseRetail.getSeller();
            }else {
                Student tempSeller = studentRepository.findByReferralCodeAndIsSellerAndStatus(sellCode, LifeUniConstant.STUDENT_IS_SELLER, LifeUniConstant.STATUS_ACTIVE).orElse(null);
                if (tempSeller == null){
                    throw new BadRequestException("Invalid seller");
                }
                seller = tempSeller;
            }
            courseTransaction.setSeller(seller);
            courseTransaction.setRefSellCode(sellCode);
        }
        courseTransaction.setCourse(course);
        courseTransaction.setBooking(booking);
        Integer saleOff = course.getSaleOff();
        int discount = (saleOff != null) ? saleOff : 0;
        if(extraMoney != null){
            courseTransaction.setPrice(course.getPrice() + extraMoney - course.getPrice() * discount / 100);
            courseTransaction.setOriginalPrice(course.getPrice() + extraMoney);
        }else {
            courseTransaction.setPrice(course.getPrice() - course.getPrice() * discount / 100);
            courseTransaction.setOriginalPrice(course.getPrice());
        }
        return courseTransaction;
    }
    private List<RevenueShare> createValidRevenueShare(CourseTransaction courseTransaction, Student student){
        List<RevenueShare> revenueShareList = new ArrayList<>();
        // nếu giá course là 0d, thì tạo record cho root seller, lấy 100%
        if(courseTransaction.getCourse().getPrice() == 0.0){
            Integer ratioShare = 100;
            RevenueShare revenueShare;
            Student seller;
            if(courseTransaction.getSeller()!=null){
                seller = courseTransaction.getSeller();
            }
            else {
                seller = getSystemSeller();
            }
            revenueShare = createRevenue(courseTransaction,null,null,null,seller,null,LifeUniConstant.REVENUE_SHARE_KIND_SYSTEM_FREE_COURSE,null, ratioShare);
            revenueShareList.add(revenueShare);

            // create free course revenue share for course expert
            ratioShare = 0;
            revenueShare = createRevenue(courseTransaction,null,courseTransaction.getCourse().getExpert(),null,null, null,LifeUniConstant.REVENUE_SHARE_KIND_SYSTEM_FREE_COURSE,null, ratioShare);
            revenueShareList.add(revenueShare);
        } else if(courseTransaction.getCourse().getExpert().getIsSystemExpert()){
            // nếu là course của system expert, tạo record cho system expert lấy 100%
            Integer ratioShare = 100;
            RevenueShare revenueShare = createRevenue(courseTransaction,null,getSystemExpert(),null,null,null,LifeUniConstant.REVENUE_SHARE_KIND_DIRECT,null, ratioShare);
            revenueShareList.add(revenueShare);
        } else {
            // share cho hệ thống
            Integer ratioShare = getRevenueShareValue(LifeUniConstant.SETTING_KEY_SYSTEM_REVENUE_SHARE);
            RevenueShare revenueShare = createRevenue(courseTransaction,null,null,null,null,null,LifeUniConstant.REVENUE_SHARE_KIND_SYSTEM,null, ratioShare);
            revenueShareList.add(revenueShare);
            // share cho seller và expert
            revenueShareList.addAll(createRevenueShareSeller(courseTransaction, student));
            revenueShareList.addAll(createRevenueShareExpert(courseTransaction));
        }
        return revenueShareList;
    }
    private List<RevenueShare> createRevenueShareExpert(CourseTransaction courseTransaction){
        List<RevenueShare> revenueShareList = new ArrayList<>();
        int ratioShareExpert = getRevenueShareValue(LifeUniConstant.SETTING_KEY_EXPERT_REVENUE_SHARE);
        Expert sourceExpert = null;
        //Create revenue share for Expert create Course
        RevenueShare revenueShareExpert = createRevenue(courseTransaction,null,courseTransaction.getCourse().getExpert(),null,null,null,LifeUniConstant.REVENUE_SHARE_KIND_DIRECT,null, ratioShareExpert);
        revenueShareList.add(revenueShareExpert);

        ReferralExpertLog referralExpertLog = referralExpertLogRepository.findFirstByExpertId(courseTransaction.getCourse().getExpert().getId());
        Expert refExpert;
        if(referralExpertLog == null || referralExpertLog.getExpert() == null){
            refExpert = getSystemExpert();
        }
        else {
            refExpert = referralExpertLog.getRefExpert();
            sourceExpert = referralExpertLog.getExpert();
        }
        ratioShareExpert = getRevenueShareValue(LifeUniConstant.SETTING_KEY_EXPERT_REF_REVENUE_SHARE);
        RevenueShare revenueSystemExpert = createRevenue(courseTransaction,null,refExpert,sourceExpert,null,null,LifeUniConstant.REVENUE_SHARE_KIND_REF,LifeUniConstant.REVENUE_SHARE_REF_KIND_EXPERT, ratioShareExpert);

        revenueShareList.add(revenueSystemExpert);
        return revenueShareList;
    }

    @PostMapping(value = "/payos-webhook", produces = MediaType.APPLICATION_JSON_VALUE)
    public PayosConfirmForm getData(@Valid @RequestBody WebhookPayosPaymentForm webhookPayosPaymentForm) {
        PayosConfirmForm payosConfirmForm = new PayosConfirmForm();
        log.error(webhookPayosPaymentForm.getData().getOrderCode().toString()  + 1111);
        if(!Objects.equals(payosService.createSignature(webhookPayosPaymentForm.getData()), webhookPayosPaymentForm.getSignature())){

            ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
            Booking booking = bookingRepository.findById(webhookPayosPaymentForm.getData().getOrderCode()).orElse(null);
//            if(booking == null){
//                throw new BadRequestException("Booking is not found", ErrorCode.BOOKING_ERROR_NOT_FOUND);
//            }
//            if(!Objects.equals(booking.getState(), LifeUniConstant.BOOKING_STATE_UNPAID)){
//                throw new BadRequestException("Only can change state unpaid booking", ErrorCode.BOOKING_ERROR_NOT_ALLOW_CHANGE_STATE);
//            }
//            if(!Objects.equals(booking.getPaymentMethod(), LifeUniConstant.BOOKING_PAYMENT_METHOD_BANKING)){
//                throw new BadRequestException("Only can change state payment method banking", ErrorCode.BOOKING_ERROR_NOT_ALLOW_CHANGE_STATE);
//            }
            Integer notificationKind;
            //approve
            if(booking != null){
                List<CourseTransaction> courseTransactions = courseTransactionRepository.findAllByBookingId(booking.getId());
                // set totalMoney and saleOffMoney
                List<RevenueShare> revenueShares = new ArrayList<>();
                createReferralSellerLogFromCouponSellerCode(booking);
                checkExistCourseRetail(booking);
                for(CourseTransaction courseTransaction: courseTransactions){
                    // create RevenueShare and Registration
                    revenueShares.addAll(createRevenueShareAndRegistrationWhenPayBooking(courseTransaction, booking.getStudent()));
                }
//            checkFirstBooking(booking);

                notificationKind = LifeUniConstant.NOTIFICATION_KIND_APPROVE_BOOKING;
                // send notifications to those who receive revenue money
                sendNotificationForRevenue(revenueShares);
                payosConfirmForm.setSuccess(true);
                booking.setState(1);
                bookingRepository.save(booking);
            }

        }
        else {
            log.error("Order payment data is inconsistent");
            payosConfirmForm.setSuccess(false);
        }
        return payosConfirmForm;
    }

    private List<RevenueShare> createRevenueShareSeller(CourseTransaction courseTransaction, Student student){
        List<RevenueShare> revenueShareList = new ArrayList<>();
        int ratioShareSeller = getRevenueShareValue(LifeUniConstant.SETTING_KEY_ROOT_SELLER_REVENUE_SHARE);
        Student refStudent;
        Student sourceStudent = null;
        boolean hasSellerRefer = false;

        ReferralSellerLog referralLog = referralSellerLogRepository.findFirstByStudentId(student.getId());
        // if don't have a referralSeller, create revenueShare for systemSeller.
        if(referralLog != null && referralLog.getRefStudent() != null) {
             refStudent = referralLog.getRefStudent();
             sourceStudent = referralLog.getStudent();
             hasSellerRefer = true;
        }
        else {
            refStudent = getSystemSeller();
        }
        RevenueShare revenueShareStudent = createRevenue(courseTransaction, sourceStudent,null,null, refStudent,null, LifeUniConstant.REVENUE_SHARE_KIND_REF,LifeUniConstant.REVENUE_SHARE_REF_KIND_STUDENT, ratioShareSeller);
        revenueShareList.add(revenueShareStudent);
        if(courseTransaction.getSeller() != null){
            ratioShareSeller = getRevenueShareValue(LifeUniConstant.SETTING_KEY_SELLER_REVENUE_SHARE);
            // create Revenue share for Direct seller
            RevenueShare revenueShareSeller = createRevenue(courseTransaction,null,null,null,courseTransaction.getSeller(),null,LifeUniConstant.REVENUE_SHARE_KIND_DIRECT,null, ratioShareSeller);
            revenueShareList.add(revenueShareSeller);

            ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(courseTransaction.getSeller().getId());
            Student refSeller;
            Student sourceSeller = null;
            // if don't have a referralSeller, create revenueShare for systemSeller
            ratioShareSeller = getRevenueShareValue(LifeUniConstant.SETTING_KEY_SELLER_REF_REVENUE_SHARE);
            if(referralSellerLog == null || referralSellerLog.getRefStudent() == null){
                refSeller = getSystemSeller();
            }
            else {
                refSeller = referralSellerLog.getRefStudent();
                sourceSeller = referralSellerLog.getStudent();
            }
            // create Revenue share for Referral seller
            RevenueShare revenueRefSeller = createRevenue(courseTransaction,null,null,null,refSeller,sourceSeller,LifeUniConstant.REVENUE_SHARE_KIND_REF,LifeUniConstant.REVENUE_SHARE_REF_KIND_SELLER, ratioShareSeller);
            revenueShareList.add(revenueRefSeller);
        } else { // seller is null.
            ratioShareSeller = getRevenueShareValue(LifeUniConstant.SETTING_KEY_SELLER_REVENUE_SHARE);
            RevenueShare revenueShareSeller;
            RevenueShare revenueShareRefSeller;

            Student refSeller;
            Student sourceSeller = null;
            if (student.getIsSeller()) {
                // Create revenue share for student. If student is seller
                revenueShareSeller = createRevenue(courseTransaction, null, null, null, student, null, LifeUniConstant.REVENUE_SHARE_KIND_DIRECT, null, ratioShareSeller);
            }else {
                // Create revenue share for ref student
                // If student has root seller
                //      -> ref student = root seller
                // else
                //      -> ref student = system seller
                revenueShareSeller = createRevenue(courseTransaction, null, null, null, refStudent, null, LifeUniConstant.REVENUE_SHARE_KIND_DIRECT, null, ratioShareSeller);
            }

            if (hasSellerRefer) {
                ReferralSellerLog refSellerLog = referralSellerLogRepository.findFirstByStudentId(refStudent.getId());
                if (refSellerLog == null || refSellerLog.getRefStudent() == null) {
                    refSeller = getSystemSeller();
                }else {
                    refSeller = refSellerLog.getRefStudent();
                    sourceSeller = refSellerLog.getStudent();
                }
            }else {
                refSeller = getSystemSeller();
            }
            // Create revenue share for ref seller
            // If root seller has ref root seller
            //      -> ref seller = ref root seller
            // else
            //      -> ref seller = system seller
            ratioShareSeller = getRevenueShareValue(LifeUniConstant.SETTING_KEY_SELLER_REF_REVENUE_SHARE);
            revenueShareRefSeller = createRevenue(courseTransaction, sourceSeller, null, null, refSeller, null, LifeUniConstant.REVENUE_SHARE_KIND_REF, LifeUniConstant.REVENUE_SHARE_REF_KIND_SELLER, ratioShareSeller);

            revenueShareList.add(revenueShareRefSeller);
            revenueShareList.add(revenueShareSeller);
        }
        return revenueShareList;
    }
    private void isValidCourseRetail(CourseRetail courseRetail){
        if(courseRetail == null){
            throw new NotFoundException("Course Retail is not found",ErrorCode.COURSE_RETAIL_ERROR_NOT_FOUND);
        }
        if(courseRetail.getStatus() != LifeUniConstant.STATUS_ACTIVE){
            throw new BadRequestException("Course Retail is not active",ErrorCode.COURSE_RETAIL_ERROR_NOT_ACTIVE);
        }
        if(courseRetail.getSeller() == null || !courseRetail.getSeller().getIsSeller()
                || courseRetail.getSeller().getStatus() != LifeUniConstant.STATUS_ACTIVE){
           throw new BadRequestException("Seller or Course Retail not valid", ErrorCode.SELLER_ERROR_NOT_ACTIVE);
        }
    }

    private String getUniqueCode(){
        String code;
        Booking existBookingCode;
        do {
            code = com.easylearning.api.utils.StringUtils.generateRandomString(6);
            existBookingCode = bookingRepository.findFirstByCode(code).orElse(null);
        } while (existBookingCode != null);
        return code;
    }

    private String getUniqueSellerCode(){
        String code;
        Student existSellerCode;
        do {
            code = com.easylearning.api.utils.StringUtils.generateRandomString(6);
            existSellerCode = studentRepository.findByReferralCode(code).orElse(null);
        } while (existSellerCode != null);
        return code;
    }

}
