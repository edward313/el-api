package com.easylearning.api.controller;

import com.easylearning.api.service.UserBaseApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.MoveVideoFileDto;
import com.easylearning.api.dto.cartItem.CartItemDto;
import com.easylearning.api.dto.courseTransaction.MyTransactionDto;
import com.easylearning.api.dto.revenueShare.RevenueShareDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.form.course.DeleteCourseFolderForm;
import com.easylearning.api.form.expert.CreateExpertForm;
import com.easylearning.api.form.expert.DeleteExpertFolderForm;
import com.easylearning.api.form.lesson.DeleteFolderLessonForm;
import com.easylearning.api.form.lesson.DeleteFolderLessonVersioningForm;
import com.easylearning.api.form.lesson.MoveVideoFileForm;
import com.easylearning.api.form.lesson.UrlForm;
import com.easylearning.api.jwt.UserBaseJwt;
import com.easylearning.api.mapper.*;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.RevenueShareCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.Oauth2JWTTokenService;
import com.easylearning.api.service.feign.FeignLifeUniMediaService;
import com.easylearning.api.service.impl.UserServiceImpl;
import com.easylearning.api.service.rabbitMQ.NotificationService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class ABasicController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private Oauth2JWTTokenService oauth2JWTTokenService;
    @Autowired
    private FeignLifeUniMediaService feignLifeUniMediaService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NationRepository nationRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExpertMapper expertMapper;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private ReferralExpertLogRepository referralExpertLogRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private CategoryHomeRepository categoryHomeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private RevenueShareRepository revenueShareRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CourseTransactionMapper courseTransactionMapper;
    @Autowired
    private CourseRetailRepository courseRetailRepository;
    @Autowired
    private CourseVersioningRepository courseVersioningRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private CourseVersioningMapper courseVersioningMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    private CourseComboDetailVersionRepository courseComboDetailVersionRepository;
    @Autowired
    private CompletionRepository completionRepository;
    @Autowired
    private CourseComboDetailRepository courseComboDetailRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseReviewHistoryRepository courseReviewHistoryRepository;
    @Autowired
    private ElasticCourseRepository elasticCourseRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Value("${admin.username}")
    private String admin;
    @Autowired
    private RevenueShareMapper revenueShareMapper;
    @Autowired
    private ReferralSellerLogRepository referralSellerLogRepository;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    UserBaseApiService userBaseApiService;
    public Long getCurrentUser(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        if (userBaseJwt == null) {
            return null;
        }
        return userBaseJwt.getAccountId();
    }
    public Boolean isLogin(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        return userBaseJwt != null;
    }

    public long getTokenId(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        return userBaseJwt.getTokenId();
    }

    public UserBaseJwt getSessionFromToken(){
        return userService.getAddInfoFromToken();
    }

    public boolean isSuperAdmin(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        if(userBaseJwt !=null){
            return userBaseJwt.getIsSuperAdmin();
        }
        return false;
    }
    public boolean isExpert(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        if(userBaseJwt !=null){
            return Objects.equals(userBaseJwt.getUserKind(), LifeUniConstant.USER_KIND_EXPERT);
        }
        return false;
    }
    public boolean isAdmin(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        if(userBaseJwt !=null){
            return Objects.equals(userBaseJwt.getUserKind(), LifeUniConstant.USER_KIND_ADMIN);
        }
        return false;
    }

    public boolean isStudent(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        if(userBaseJwt !=null){
            return Objects.equals(userBaseJwt.getUserKind(), LifeUniConstant.USER_KIND_STUDENT);
        }
        return false;
    }
    public boolean isSeller(){
        UserBaseJwt userBaseJwt = userService.getAddInfoFromToken();
        if(userBaseJwt !=null){
            return Objects.equals(userBaseJwt.getUserKind(), LifeUniConstant.USER_KIND_STUDENT) && userService.isSeller();
        }
        return false;
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                return oauthDetails.getTokenValue();
            }
        }
        return null;
    }

    public void deleteFilesByListUrls(List<String> urls){
        String deleteInternalUrls = String.join(";",urls);
        if(StringUtils.isNoneBlank(deleteInternalUrls)){
            feignLifeUniMediaService.deleteFiles(deleteInternalUrls, "Bearer "+getCurrentToken());
        }
    }

    public void deleteLessonFolder(Long lessonId, Long expertId, Long courseId) {
        DeleteFolderLessonForm form = new DeleteFolderLessonForm();
        form.setLessonId(lessonId);
        form.setCourseId(courseId);
        form.setExpertId(expertId);
        feignLifeUniMediaService.deleteLessonFolder(form,"Bearer " + getCurrentToken());
    }
    public void deleteLessonVersioningFolder(Long lessonId,Integer versionCode, Long expertId, Long courseId) {
        DeleteFolderLessonVersioningForm form = new DeleteFolderLessonVersioningForm();

        form.setLessonId(lessonId);
        form.setCourseId(courseId);
        form.setExpertId(expertId);
        form.setVersionCode(versionCode);
        feignLifeUniMediaService.deleteLessonVersioningFolder(form,"Bearer " + getCurrentToken());
    }
    public ApiMessageDto<MoveVideoFileDto> moveVideoFile(String url, Long courseId, Long expertId) {
        MoveVideoFileForm form = new MoveVideoFileForm();

        form.setUrl(url);
        form.setCourseId(courseId);
        form.setExpertId(expertId);
        return feignLifeUniMediaService.moveVideoFile(form,"Bearer " + getCurrentToken());
    }
    public void deleteExpertFolder(Long expertId) {
        DeleteExpertFolderForm form = new DeleteExpertFolderForm();
        form.setExpertId(expertId);
        feignLifeUniMediaService.deleteExpertFolder(form,"Bearer " + getCurrentToken());
    }
    public void deleteCourseFolder(Long expertId,Long courseId, String token) {
        DeleteCourseFolderForm form = new DeleteCourseFolderForm();
        form.setExpertId(expertId);
        form.setCourseId(courseId);
        feignLifeUniMediaService.deleteCourseFolder(form,"Bearer " + token);
    }

    public List<String> getListUrlsByKindFromJsonUrlDocument(String jsonUrlDocument, String kind) {
        try {
            UrlForm[] urlForms = objectMapper.readValue(jsonUrlDocument, UrlForm[].class);

            return Arrays.stream(urlForms)
                    .filter(item -> kind.equals(item.getKind()))
                    .map(UrlForm::getUrl)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error reading JSON document: {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    public void deleteFilesFromListUrlDocument(List<String> urlDocuments){
        List<String> urls = new ArrayList<>();
        for (String urlDocument: urlDocuments){
            urls.addAll(getListUrlsByKindFromJsonUrlDocument(urlDocument,LifeUniConstant.LESSON_URL_KIND_INTERNAL));
        }
        deleteFilesByListUrls(urls);
    }

    public Nation getNationByIdAndKind(Long id, Integer kind){
        Nation nation = nationRepository.findByIdAndKind(id,kind).orElse(null);
        if(nation == null){
            throw new BadRequestException("nation not found", ErrorCode.NATION_ERROR_NOT_FOUND);
        }
        return nation;
    }
    public String getUniqueWalletNumber() {
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String walletNumber;
        Random random = new Random();
        StringBuilder randomNumbers = new StringBuilder();
        Wallet existWallet;
        do {
            for (int i = 0; i < 6; i++) {
                int randomNumber = random.nextInt(10); // Số ngẫu nhiên từ 0 đến 9
                randomNumbers.append(randomNumber);
            }
            walletNumber = currentDate + randomNumbers;
            existWallet = walletRepository.findByWalletNumber(walletNumber);
        } while (existWallet != null);
        return walletNumber;
    }
    public void createWalletTransaction(Wallet wallet, Integer kind, Double money, Integer state, String note, Double lastBalance) {
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setMoney(lastBalance >= 0 ? money : 0);
        walletTransaction.setKind(kind);
        walletTransaction.setState(state);
        walletTransaction.setNote(note);
        walletTransaction.setLastBalance(lastBalance >= 0 ? lastBalance : 0);
        walletTransactionRepository.save(walletTransaction);
    }

    public void createReferralSellerLogFromCouponSellerCode(Booking booking) {
        if (booking.getCouponSellCode() != null) {
            Student seller = studentRepository.findByReferralCodeAndIsSeller(booking.getCouponSellCode(), LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
            ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(booking.getStudent().getId());
            if (booking.getStudent().getIsSeller() && booking.getStudent().getReferralCode().equals(booking.getCouponSellCode())) {
                return;
            }
            if (referralSellerLog == null && seller != null) {
                referralSellerLog = new ReferralSellerLog();
                referralSellerLog.setRefStudent(seller);
                referralSellerLog.setStudent(booking.getStudent());
                referralSellerLogRepository.save(referralSellerLog);
            }
        }
    }

    public void checkExistCourseRetail(Booking booking) {
        List<Long> courseIds = courseRepository.findAllCourseIdByBookingId(booking.getId());
        if (booking.getCouponSellCode() != null) {
            Student seller = studentRepository.findByReferralCode(booking.getCouponSellCode()).orElse(null);
            if (seller != null) {
                List<Course> courses = courseRepository.findAllBySellerIdAndCourseIdNotIn(seller.getId(), courseIds);
                // Create course retail for seller if not exists
                if (!courses.isEmpty()) {
                    List<CourseRetail> courseRetails = new ArrayList<>();
                    for (Course course : courses) {
                        CourseRetail courseRetail = new CourseRetail();
                        courseRetail.setCourse(course);
                        courseRetail.setSeller(seller);
                        courseRetails.add(courseRetail);
                    }
                    courseRetailRepository.saveAll(courseRetails);
                }
            }
        }
    }

    public double getSystemBonusMoney() { // Get init money from setting
        String systemBonusMoney = settingsRepository.findValueByKey(LifeUniConstant.SETTING_KEY_DEFAULT_BALANCE);
        if (systemBonusMoney == null || systemBonusMoney.isEmpty()){
            throw new BadRequestException("System bonus money is not available yet");
        }
        return Double.parseDouble(systemBonusMoney);
    }

    public double getSystemCouponMoney() {
        String systemCouponMoney = settingsRepository.findValueByKey(LifeUniConstant.SETTING_KEY_SYSTEM_COUPON);
        if (systemCouponMoney == null || systemCouponMoney.isEmpty()) {
            throw new BadRequestException("System coupon money is not available yet");
        }
        return Double.parseDouble(systemCouponMoney);
    }
    List<RevenueShareDto> getRevenueShareDtoList(List<RevenueShare> revenueShares) {
        List<RevenueShareDto> revenueShareDtoList = new ArrayList<>();
        for(RevenueShare revenueShare: revenueShares){
            RevenueShareDto revenueShareDto = revenueShareMapper.fromEntityToRevenueShareDtoForGetTransaction(revenueShare);
            Account sourceAccount = null;
            if(revenueShare.getSourceExpert()!=null){
                sourceAccount = revenueShare.getSourceExpert().getAccount();
            }
            if(revenueShare.getSourceSeller()!=null){
                sourceAccount = revenueShare.getSourceSeller().getAccount();
            }
            if(revenueShare.getSourceStudent()!=null){
                sourceAccount = revenueShare.getSourceStudent().getAccount();
            }
            revenueShareDto.setSourceAccount(accountMapper.fromAccountToDtoForPeriodDetail(sourceAccount));
            revenueShareDtoList.add(revenueShareDto);
        }
        return revenueShareDtoList;
    }
    void createExpertAccount(CreateExpertForm createExpertForm){
        Account existAccount;
        if(StringUtils.isNoneBlank(createExpertForm.getEmail())){
            existAccount = accountRepository.findByPhoneOrEmailAndKind(createExpertForm.getPhone(),createExpertForm.getEmail(),LifeUniConstant.USER_KIND_EXPERT);
        }else {
            existAccount = accountRepository.findAccountByPhoneAndKind(createExpertForm.getPhone(),LifeUniConstant.USER_KIND_EXPERT);
        }
        if(existAccount != null){
            throw new BadRequestException("Phone or Email already used",ErrorCode.EXPERT_REGISTRATION_ERROR_PHONE_OR_EMAIL_ALREADY_USED);
        }
        Account account = accountMapper.fromCreateExpertFormToAccount(createExpertForm);
        account.setKind(LifeUniConstant.USER_KIND_EXPERT);

        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <title>Thông Báo Xét Duyệt Tài Khoản Chuyên Gia</title>" +
                "</head>" +
                "<body>" +
                "    <p><b>Subject:</b> Thông Báo Xét Duyệt Tài Khoản Chuyên Gia</p>" +
                "    <p>Kính gửi <b>%s</b>,</p>" + // expertName
                "    <p>Chúng tôi xin vui mừng thông báo rằng tài khoản chuyên gia của bạn đã được <b>xét duyệt thành công</b>. Dưới đây là thông tin tài khoản của bạn:</p>" +
                "    <p><b>Tên tài khoản:</b> %s</p>" + // username
                "    <p><b>Mật khẩu tạm thời:</b> %s</p>" + // temporaryPassword
                "    <p><b>Liên kết đăng nhập:</b> <a href='%s'>Đăng nhập tại đây</a></p>" + // loginLink
                "    <p>Vì lý do bảo mật, chúng tôi khuyến nghị bạn <b>đổi mật khẩu ngay sau khi đăng nhập lần đầu</b>. Vui lòng không chia sẻ thông tin tài khoản của bạn với bất kỳ ai.</p>" +
                "    <p>Nếu bạn có bất kỳ câu hỏi nào hoặc cần hỗ trợ, xin vui lòng liên hệ với chúng tôi qua email <a href='mailto:easylearning445@gmail.com'>easylearning445@gmail.com</a></p>" +
                "    <p>Trân trọng,</p>" +
                "    <p>Đội ngũ hỗ trợ</p>" +
                "    <p>Tên Công ty/Tổ chức</p>" +
                "    <p>Thông tin liên hệ</p>" +
                "</body>" +
                "</html>",
                account.getEmail(), account.getEmail(), createExpertForm.getPassword(), "https://el-fe.edward.io.vn/"
        );
        userBaseApiService.sendEmail(account.getEmail(), htmlContent, "Thông Báo Xét Duyệt Tài Khoản Chuyên Gia", true);
        if(StringUtils.isNoneBlank(createExpertForm.getPassword())){
            account.setPassword(passwordEncoder.encode(createExpertForm.getPassword()));
        }
        Group group = groupRepository.findFirstByKind(LifeUniConstant.GROUP_KIND_EXPERT);
        account.setGroup(group);
        Expert expert = expertMapper.fromCreateFormToExpert(createExpertForm);
        //set nation
        setNation(createExpertForm.getWardId()
                ,createExpertForm.getDistrictId(),
                createExpertForm.getProvinceId(),expert);
        expert.setReferralCode(getUniqueCodeForExpert());
        accountRepository.save(account);
        expert.setAccount(account);
        expertRepository.save(expert);
        createExpertWallet(account);
        if(StringUtils.isNoneBlank(createExpertForm.getReferralCode())){
            createReferralExpertLog(createExpertForm.getReferralCode(),expert);
            Expert referralExpert = expertRepository.findByReferralCodeAndStatusAndAccountStatus(createExpertForm.getReferralCode(),LifeUniConstant.STATUS_ACTIVE,LifeUniConstant.STATUS_ACTIVE);
            if(referralExpert == null){
                throw new BadRequestException("Invalid referral code",ErrorCode.EXPERT_REGISTRATION_ERROR_REFERRAL_CODE_INVALID);
            }
            if(Boolean.TRUE.equals(referralExpert.getIsSystemExpert())){
                throw new BadRequestException("Not allow use system expert code",ErrorCode.EXPERT_REGISTRATION_ERROR_NOT_ALLOW_USE_SYSTEM_CODE);
            }
            notificationService.createNotificationAndSendMessage(LifeUniConstant.NOTIFICATION_KIND_APPROVE_EXPERT,referralExpert.getId(),expert.getAccount().getFullName(),expert.getAccount().getAvatarPath());
        }
    }
    private void createReferralExpertLog(String code, Expert expert){
        Expert ex = expertRepository.findByReferralCodeAndStatus(code, LifeUniConstant.STATUS_ACTIVE);
        if(ex == null){
            throw new BadRequestException("referralCode invalid",ErrorCode.EXPERT_ERROR_REFERRAL_CODE_INVALID);
        }
        ReferralExpertLog referralExpertLog = new ReferralExpertLog();
        referralExpertLog.setRefExpert(ex);
        referralExpertLog.setExpert(expert);
        referralExpertLog.setUsedTime(new Date());
        referralExpertLogRepository.save(referralExpertLog);
    }
    private void createExpertWallet(Account account){
        Wallet oldWallet = walletRepository.findByAccountId(account.getId());
        if(oldWallet == null){
            Wallet wallet = new Wallet();
            wallet.setAccount(account);
            wallet.setKind(LifeUniConstant.WALLET_KIND_EXPERT);
            wallet.setWalletNumber(getUniqueWalletNumber());
            walletRepository.save(wallet);
        }
    }
    private String getUniqueCodeForExpert(){
        String referralCode;
        Expert existExpert;
        do {
            referralCode = com.easylearning.api.utils.StringUtils.generateRandomString(6);
            existExpert = expertRepository.findByReferralCode(referralCode);
        } while (existExpert != null);
        return referralCode;
    }
    public Integer getRevenueShareValue(String key){
        Settings setting = settingsRepository.findBySettingKeyAndGroupName(key,LifeUniConstant.SETTING_GROUP_NAME_REVENUE_SHARE);
        if(setting == null){
            throw new NotFoundException("Can not find settings of revenue share",ErrorCode.SETTINGS_ERROR_NOT_FOUND);
        }
        return Integer.parseInt(setting.getSettingValue());
    }
    public Double getMinBalance(){
        Settings setting = settingsRepository.findBySettingKeyAndGroupName(LifeUniConstant.SETTING_KEY_MIN_BALANCE,LifeUniConstant.SETTING_GROUP_NAME_PAYOUT);
        if(setting == null){
            throw new NotFoundException("Can not find payout settings",ErrorCode.SETTINGS_ERROR_NOT_FOUND);
        }
        return Double.parseDouble(setting.getSettingValue());
    }
    public Double getMinMoneyOut(){
        Settings setting = settingsRepository.findBySettingKeyAndGroupName(LifeUniConstant.SETTING_KEY_MIN_MONEY_OUT,LifeUniConstant.SETTING_GROUP_NAME_PAYOUT);
        if(setting == null){
            throw new NotFoundException("Can not find payout settings",ErrorCode.SETTINGS_ERROR_NOT_FOUND);
        }
        return Double.parseDouble(setting.getSettingValue());
    }
    public Integer getTaxPercent(){
        Settings setting = settingsRepository.findBySettingKeyAndGroupName(LifeUniConstant.SETTING_KEY_TAX_PERCENT,LifeUniConstant.SETTING_GROUP_NAME_PAYOUT);
        if(setting == null){
            throw new NotFoundException("Can not find payout settings",ErrorCode.SETTINGS_ERROR_NOT_FOUND);
        }
        return Integer.parseInt(setting.getSettingValue());
    }
    public void createCategoryHomeTopNew(Category category, Course course){
        Integer amount = categoryHomeRepository.countByCategoryId(category.getId());
        if( amount >= LifeUniConstant.CATEGORY_HOME_LIMIT_COURSE_NUMBER){
            categoryHomeRepository.deleteOldestCategoryHome(category.getId());
        }
        CategoryHome categoryHome = new CategoryHome();
        categoryHome.setCategory(category);
        categoryHome.setCourse(course);
        categoryHomeRepository.save(categoryHome);
    }

    public void changeCategoryHome(Course oldCourse){
        CategoryHome categoryHome;
        Boolean isFree;
        if(oldCourse.getPrice() == 0){
            categoryHome = categoryHomeRepository.findFirstByCategoryKindAndCourseId(LifeUniConstant.CATEGORY_KIND_TOP_FREE,oldCourse.getId());
            isFree = true;
        }else {
            categoryHome = categoryHomeRepository.findFirstByCategoryKindAndCourseId(LifeUniConstant.CATEGORY_KIND_TOP_CHARGE,oldCourse.getId());
            isFree = false;
        }
        if(categoryHome != null){
            Course course  = courseRepository.findCourseByTopSoldQuantityAndCategoryId(categoryHome.getCategory().getId(), isFree);
            if(course != null){
                CategoryHome newCategoryHome = new CategoryHome();
                newCategoryHome.setCategory(categoryHome.getCategory());
                newCategoryHome.setCourse(course);
                categoryHomeRepository.save(newCategoryHome);
            }
        }
    }
    public void checkNotNullAndActiveCourse(Course course){
        if (course == null) {
            throw new BadRequestException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(course.getStatus(),LifeUniConstant.STATUS_ACTIVE)){
            throw new BadRequestException("Course not active",ErrorCode.COURSE_ERROR_NOT_ACTIVE);
        }
    }
    public void checkNotNullAndActiveStudent(Student student){
        if (student == null) {
            throw new BadRequestException("Student not found",ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(student.getStatus(),LifeUniConstant.STATUS_ACTIVE)){
            throw new BadRequestException("Student not active",ErrorCode.STUDENT_ERROR_NOT_ACTIVE);
        }
    }

    public Page<MyTransactionDto> setMyTransactionDto(RevenueShareCriteria revenueShareCriteria, Pageable pageable) {
        List<RevenueShare> revenueShares = revenueShareRepository.findAll(revenueShareCriteria.getSpecification());
        Map<Long, MyTransactionDto> uniqueTransactionsMap = new HashMap<>();
        for (RevenueShare revenueShare : revenueShares) {
            CourseTransaction courseTransaction = revenueShare.getCourseTransaction();
            MyTransactionDto myTransactionDto;
            if (uniqueTransactionsMap.containsKey(courseTransaction.getId())) {
                myTransactionDto = uniqueTransactionsMap.get(courseTransaction.getId());
            } else {
                myTransactionDto = new MyTransactionDto();
                myTransactionDto.setCourseTransaction(courseTransactionMapper.fromEntityToCourseTransactionDtoForMyRevenue(courseTransaction));
                uniqueTransactionsMap.put(courseTransaction.getId(), myTransactionDto);
            }
            // Cộng thêm tiền từ RevenueShare vào MyTransactionDto
            Double revenueMoney = revenueShare.getRevenueMoney();
            if(revenueMoney == null){
                revenueMoney = 0.0;
            }
            revenueShareRepository.save(revenueShare);
            myTransactionDto.setRevenueMoney(myTransactionDto.getRevenueMoney() + revenueMoney);
        }
        List<MyTransactionDto> uniqueTransactions = new ArrayList<>(uniqueTransactionsMap.values());
        // mới nhất lên trước
        uniqueTransactions.sort(Comparator.comparing(dto -> dto.getCourseTransaction().getCreatedDate(), Comparator.reverseOrder()));

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int fromIndex = Math.min(pageNumber * pageSize, uniqueTransactions.size());
        int toIndex = Math.min(fromIndex + pageSize, uniqueTransactions.size());
        List<MyTransactionDto> pagedTransactions = uniqueTransactions.subList(fromIndex, toIndex);

        return new PageImpl<>(
                pagedTransactions,
                pageable,
                uniqueTransactions.size()
        );
    }
    CartItem createCartItem(Long courseId, Student student, String sellCode){
        CartItem cartItem = new CartItem();
        cartItem.setCourse(getValidCourse(courseId));
        cartItem.setStudent(student);
        cartItem.setTimeCreated(new Date());
        setValidSellCode(cartItem, sellCode);
        return cartItem;
    }

    List<CartItemDto> mappingTempSellCodeIntoSellCodeInCartItem(List<CartItem> cartItems) {
        List<CartItemDto> cartItemDtos = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            CartItemDto cartItemDto = cartItemMapper.fromEntityToCartItemDto(cartItem);
            if (cartItem.getTempSellCode() != null) {
                cartItemDto.setSellCode(cartItem.getTempSellCode());
            }
            cartItemDtos.add(cartItemDto);
        }
        return cartItemDtos;
    }
    void setValidSellCode(CartItem cartItem, String sellCode){
        if(StringUtils.isNoneBlank(sellCode)){
            // tìm xem nó có đăng ký bán chưa

            CourseRetail courseRetail = courseRetailRepository.findFirstByCourseIdAndSeller_ReferralCodeAndSeller_IsSellerAndSeller_Status(cartItem.getCourse().getId(),
                    sellCode,LifeUniConstant.STUDENT_IS_SELLER,LifeUniConstant.STATUS_ACTIVE).orElse(null);
            if(courseRetail == null){
                throw new NotFoundException("Invalid sellCode",ErrorCode.REFERRAL_SELLER_LOG_ERROR_NOT_FOUND);
            }
            else {
                cartItem.setSellCode(sellCode);
            }
        }
    }
    Course getValidCourse(Long courseId){
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            throw new NotFoundException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(course.getStatus(),LifeUniConstant.STATUS_ACTIVE)){
            throw new BadRequestException("Course not active",ErrorCode.COURSE_ERROR_NOT_ACTIVE);
        }
        return course;
    }
    public String getReasonFromCourseReviewHistory(Long versionId){
        CourseReviewHistory courseReviewHistory = courseReviewHistoryRepository.findFirstByVersionIdAndStateOrderByDateDesc(versionId,LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_REJECT);
        if(courseReviewHistory!=null){
            return courseReviewHistory.getReason();
        }
        return null;
    }
    void checkCategoryKind(Category category, Integer kind){
        if(!Objects.equals(category.getKind(), kind)){
            throw new BadRequestException("Invalid category kind",ErrorCode.CATEGORY_ERROR_INVALID_KIND);
        }
    }
    void updateTotalStudentExpert(Expert expert, Long studentId, boolean isAddition) {
        if (!registrationRepository.existsByCourse_ExpertIdAndStudentId(expert.getId(), studentId)) {
            expert.setTotalStudent(Optional.ofNullable(expert.getTotalStudent()).orElse(0) + (isAddition ? 1 : -1));
            expertRepository.save(expert);
        }
    }
    CourseVersioning createNewCourseVersioningFromOldCourseVersioning(Long courseId, Version version) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if(course == null){
            throw new BadRequestException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        CourseVersioning newCourseVersioning = courseVersioningMapper.fromCourseEntityToCourseVersioning(course);
        newCourseVersioning.setVersion(version);
        newCourseVersioning.setField(course.getField());
        newCourseVersioning.setVisualId(course.getId());
        newCourseVersioning.setExpert(course.getExpert());
        courseVersioningRepository.save(newCourseVersioning);
        return newCourseVersioning;
    }
    public Version getInitVersion(Long courseId){
        Version highestVersion = versionRepository.findHighestVersionByCourseId(courseId);
        if(highestVersion == null){
            throw new NotFoundException("Version not found",ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        if(highestVersion.getState().equals(LifeUniConstant.VERSION_STATE_WAITING)){
            throw new BadRequestException("Course is pending publication",ErrorCode.COURSE_ERROR_COURSE_PENDING_PUBLICATION);
        }
        if(!highestVersion.getState().equals(LifeUniConstant.VERSION_STATE_INIT) && !highestVersion.getState().equals(LifeUniConstant.VERSION_STATE_APPROVE)){
            throw new BadRequestException("Highest version state is not init or approve",ErrorCode.COURSE_ERROR_NOT_ALLOW_UPDATE_COURSE);
        }
        if(highestVersion.getState().equals(LifeUniConstant.VERSION_STATE_APPROVE)){
            // create new init version
            Version version = new Version();
            version.setCourseId(highestVersion.getCourseId());
            version.setVersionCode(highestVersion.getVersionCode()+1);
            version.setDate(new Date());
            version.setState(LifeUniConstant.VERSION_STATE_INIT);
            versionRepository.save(version);
            return version;
        }
        return highestVersion;
    }
    public void updateTotalExpertCourseAndTotalLessonTime(Course course,CourseVersioning courseVersioning,Integer totalCourseExpertIncreaseVale){
        Expert expert = course.getExpert();
        if(expert.getTotalCourse() == null){
            expert.setTotalCourse(0);
        }
        expert.setTotalCourse(expert.getTotalCourse() + totalCourseExpertIncreaseVale);
        if(expert.getTotalLessonTime() == null){
            expert.setTotalLessonTime(0L);
        }
        expert.setTotalLessonTime(expert.getTotalLessonTime() - course.getTotalStudyTime() + courseVersioning.getTotalStudyTime());
        expertRepository.save(expert);
    }
    public void updateAllCategoryHome(){
        List<Category> categories = categoryRepository.findAllExceptKind(LifeUniConstant.CATEGORY_KIND_NEWS);
        for(Category category: categories){
            Integer amount = categoryHomeRepository.countByCategoryId(category.getId());
            if(amount < LifeUniConstant.CATEGORY_HOME_LIMIT_COURSE_NUMBER){
                Integer courseAmountNeeded = LifeUniConstant.CATEGORY_HOME_LIMIT_COURSE_NUMBER - amount;
                List<Course> courses = new ArrayList<>();
                if(Objects.equals(category.getKind(), LifeUniConstant.CATEGORY_KIND_SPECIALIZED)){
                    courses = courseRepository.findAllByCourseNotInCategoryHomeTopNewWithKindOfCourse(category.getId(),LifeUniConstant.STATUS_ACTIVE,courseAmountNeeded);
                }
                if(Objects.equals(category.getKind(), LifeUniConstant.CATEGORY_KIND_TOP_CHARGE)){
                    courses = courseRepository.findAllByCourseNotInCategoryHomeTopBuyWithKind(category.getId(),false,LifeUniConstant.STATUS_ACTIVE,courseAmountNeeded);
                }
                if(Objects.equals(category.getKind(), LifeUniConstant.CATEGORY_KIND_TOP_FREE)){
                    courses = courseRepository.findAllByCourseNotInCategoryHomeTopBuyWithKind(category.getId(),true,LifeUniConstant.STATUS_ACTIVE,courseAmountNeeded);
                }
                if(Objects.equals(category.getKind(), LifeUniConstant.CATEGORY_KIND_TOP_NEW)){
                    courses= courseRepository.findAllByCourseNotInCategoryHomeTopNewWithKind(category.getId(),LifeUniConstant.STATUS_ACTIVE,courseAmountNeeded);
                }
                List<CategoryHome> categoryHomeList = new ArrayList<>();
                for(Course c: courses){
                    CategoryHome categoryHome = new CategoryHome();
                    categoryHome.setCategory(category);
                    categoryHome.setCourse(c);
                    categoryHomeList.add(categoryHome);
                }
                categoryHomeRepository.saveAll(categoryHomeList);
            }
        }
    }
    public void createCategoryHomeTopSold(Course course){
        Category category;
        Integer kind;
        if(course.getPrice() == 0){
            kind = LifeUniConstant.CATEGORY_KIND_TOP_FREE;
        }else {
            kind = LifeUniConstant.CATEGORY_KIND_TOP_CHARGE;
        }
        category = categoryRepository.findFirstByKind(kind);
        // Create new Top category if not exist
        if(category == null){
            category = new Category();
            category.setKind(kind);
            if(kind.equals(LifeUniConstant.CATEGORY_KIND_TOP_FREE)){
                category.setName(LifeUniConstant.CATEGORY_NAME_TOP_FREE);
            }else {
                category.setName(LifeUniConstant.CATEGORY_NAME_TOP_CHARGE);
            }
            category.setOrdering(LifeUniConstant.CATEGORY_ORDERING_TOP);
            categoryRepository.save(category);
        }
        CategoryHome existCategoryHome = categoryHomeRepository.findFirstByCategoryKindAndCourseId(category.getKind(),course.getId());
        if(existCategoryHome == null){
            CategoryHome categoryHomeLeastPurchased = categoryHomeRepository.findTheLeastPurchasedAndOldestCategoryHome(category.getId()).orElse(null);
            Integer amount = categoryHomeRepository.countByCategoryId(category.getId());
            if(categoryHomeLeastPurchased == null || categoryHomeLeastPurchased.getCourse().getSoldQuantity() <= course.getSoldQuantity() || amount <= LifeUniConstant.CATEGORY_HOME_LIMIT_COURSE_NUMBER){
                if( amount >= LifeUniConstant.CATEGORY_HOME_LIMIT_COURSE_NUMBER && categoryHomeLeastPurchased != null){
                    //delete out top categoryHome
                    categoryHomeRepository.deleteById(categoryHomeLeastPurchased.getId());
                }
                CategoryHome categoryHome = new CategoryHome();
                categoryHome.setCategory(category);
                categoryHome.setCourse(course);
                categoryHomeRepository.save(categoryHome);
            }
        }
    }
    public void deleteCourse(Course course){
        deleteCourseFolder(course.getExpert().getId(), course.getId(),oauth2JWTTokenService.getInternalAccessToken(admin).getValue());
        reviewRepository.deleteAllByCourseId(course.getId());
        elasticCourseRepository.deleteByCourseId(course.getId());
        categoryHomeRepository.deleteByCourse(course.getId());
        revenueShareRepository.deleteAllByCourseId(course.getId());
        courseTransactionRepository.deleteAllByCourseId(course.getId());
        courseRetailRepository.deleteAllByCourseId(course.getId());
        cartItemRepository.deleteAllByCourseId(course.getId());
        registrationRepository.deleteAllByCourseId(course.getId());
        completionRepository.deleteAllByCourseId(course.getId());
        lessonVersioningRepository.deleteAllByCourseId(course.getId());
        lessonRepository.deleteAllLessonByCourseId(course.getId());
        courseComboDetailRepository.deleteAllByCourseIdOrComboId(course.getId());
        courseComboDetailVersionRepository.deleteAllByCourseId(course.getId());
        courseRepository.deleteById(course.getId());
    }
    public Expert getSystemExpert(){
        Expert systemExpert = expertRepository.findFirstByIsSystemExpert(true);
        if(systemExpert == null){
            throw new NotFoundException("Can not find system expert account",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        return systemExpert;
    }
    public Student getSystemSeller(){
        Student systemSeller = studentRepository.findFirstByIsSystemSeller(true).orElse(null);
        if(systemSeller == null){
            throw new BadRequestException("Can not find system seller account", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        return systemSeller;
    }
    public List<Date> getDateRange(Date currentDate) {
        List<Date> dateRange =  new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        Date startDate;
        Date endDate;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonth < LifeUniConstant.REGISTER_PAYOUT_DATE) {
            // currentDate nằm trong khoảng từ ngày 1 đến ngày 15 của tháng hiện tại
            // Set start date là 00:00 ngày 1
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();

            // Set end date là 00:00 ngày 16
            calendar.set(Calendar.DAY_OF_MONTH, LifeUniConstant.REGISTER_PAYOUT_DATE);
            endDate = calendar.getTime();
        } else {
            // Set start date là 00:00 ngày 16 của tháng hiện tại
            calendar.set(Calendar.DAY_OF_MONTH, LifeUniConstant.REGISTER_PAYOUT_DATE);
            startDate = calendar.getTime();

            // Set end date là 00:00 ngày 1 của tháng sau
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            endDate = calendar.getTime();
        }
        dateRange.add(startDate);
        dateRange.add(endDate);
        return dateRange;
    }
    public void handleMoneyWhenCancelRegisterPayout(RegisterPayout registerPayout, WalletRepository walletRepository) {
        Wallet wallet = walletRepository.findByAccountId(registerPayout.getAccount().getId());
        if(wallet == null){
            throw new NotFoundException("Wallet is not found", ErrorCode.WALLET_ERROR_NOT_FOUND);
        }
        Double currentBalance = (wallet.getBalance() != null) ? wallet.getBalance() : 0.0;
        Double currentHoldingBalance = (wallet.getHoldingBalance() != null) ? wallet.getHoldingBalance() : 0.0;
        Double moneyOut = registerPayout.getMoney();
        wallet.setBalance(currentBalance + moneyOut);
        wallet.setHoldingBalance(currentHoldingBalance - moneyOut);
        walletRepository.save(wallet);
    }
    public RevenueShare createRevenue(CourseTransaction courseTransaction,Student sourceStudent, Expert expert,Expert sourceExpert,Student seller, Student sourceSeller, Integer kind,Integer refKind, Integer ratioShare){
        RevenueShare revenueShare = new RevenueShare();
        double money;
        revenueShare.setCourseTransaction(courseTransaction);
        revenueShare.setSourceStudent(sourceStudent);
        revenueShare.setSeller(seller);
        revenueShare.setSourceSeller(sourceSeller);
        revenueShare.setExpert(expert);
        revenueShare.setSourceExpert(sourceExpert);
        revenueShare.setKind(kind);
        revenueShare.setRefKind(refKind);
        revenueShare.setRatioShare(ratioShare);
        if (Objects.equals(kind, LifeUniConstant.REVENUE_SHARE_KIND_SYSTEM_FREE_COURSE)
                || Objects.equals(kind, LifeUniConstant.REVENUE_SHARE_KIND_SYSTEM )
                || ((Objects.equals(kind, LifeUniConstant.REVENUE_SHARE_KIND_DIRECT) && (revenueShare.getExpert() !=null) && (Objects.equals(revenueShare.getExpert().getId(), getSystemExpert().getId()))))) {
            //ratio là % tính theo 100% của price ở transaction
            money = com.easylearning.api.utils.StringUtils.roundNumber((courseTransaction.getPrice() * ratioShare / 100.0), LifeUniConstant.REVENUE_SHARE_ROUND_NUMBER);
        } else {
            //ratio là % tính theo 100% của phần còn lại(sau khi chia cho hệ thống) ở transaction
            money = com.easylearning.api.utils.StringUtils.roundNumber(
                    (courseTransaction.getPrice() * ratioShare / 100.0) *
                            ((double) (100 - getRevenueShareValue(LifeUniConstant.SETTING_KEY_SYSTEM_REVENUE_SHARE)) / 100),
                    LifeUniConstant.REVENUE_SHARE_ROUND_NUMBER
            );
        }
        revenueShare.setRevenueMoney(money);
        revenueShare.setPayoutStatus(LifeUniConstant.REVENUE_SHARE_PAYOUT_STATUS_UNPAID);
        return revenueShare;
    }
    void setNation(Long wardId, Long districtId, Long provinceId, Expert expert){
        if(wardId !=null){
            expert.setWard(getNationByIdAndKind(wardId,LifeUniConstant.NATION_KIND_COMMUNE));
        }
        if(districtId != null){
            expert.setDistrict(getNationByIdAndKind(districtId,LifeUniConstant.NATION_KIND_DISTRICT));
        }
        if(provinceId != null){
            expert.setProvince(getNationByIdAndKind(provinceId,LifeUniConstant.NATION_KIND_PROVINCE));
        }
    }

    public Account getSystemAccount(){
        Wallet wallet = walletRepository.findFirstByKind(LifeUniConstant.WALLET_KIND_SYSTEM);
        if(wallet == null){
            throw new BadRequestException("System wallet not found", ErrorCode.WALLET_ERROR_NOT_FOUND);
        }
        return wallet.getAccount();
    }

    public void createReferralSellerLog(String code, Student student){
        Student seller = studentRepository.findByReferralCodeAndIsSellerAndStatus(code,
                LifeUniConstant.STUDENT_IS_SELLER,LifeUniConstant.STATUS_ACTIVE).orElse(null);
        if(seller == null){
            throw new BadRequestException("referralCode invalid",ErrorCode.STUDENT_REFERRAL_CODE_INVALID);
        }
        if(Boolean.TRUE.equals(seller.getIsSystemSeller())){
            throw new BadRequestException("Not allow use system seller code",ErrorCode.STUDENT_ERROR_NOT_ALLOW_USE_SYSTEM_CODE);
        }
        ReferralSellerLog referralSellerLog = new ReferralSellerLog();
        referralSellerLog.setRefStudent(seller);
        referralSellerLog.setStudent(student);
        referralSellerLogRepository.save(referralSellerLog);

        notificationService.createNotificationAndSendMessage(LifeUniConstant.NOTIFICATION_KIND_SIGNUP_STUDENT,seller.getId(),student.getAccount().getFullName(),student.getAccount().getAvatarPath());
    }

    public void validationSellCode(Student student, String sellCode) {
        if (student.getIsSeller()
                && student.getReferralCode() != null
                && student.getReferralCode().equals(sellCode)) {
            throw new BadRequestException("Invalid sellCode");
        }
        ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(student.getId());
        if (referralSellerLog != null) {
            throw new BadRequestException("Referral is existed");
        }
    }
}
