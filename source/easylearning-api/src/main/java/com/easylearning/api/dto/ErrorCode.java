package com.easylearning.api.dto;

public class ErrorCode {
    /**
     * General error code
     */
    public static final String GENERAL_ERROR_REQUIRE_PARAMS = "ERROR-GENERAL-0000";
    public static final String GENERAL_ERROR_STORE_LOCKED = "ERROR-GENERAL-0001";
    public static final String GENERAL_ERROR_ACCOUNT_LOCKED = "ERROR-GENERAL-0002";
    public static final String GENERAL_ERROR_SHOP_LOCKED = "ERROR-GENERAL-0003";
    public static final String GENERAL_ERROR_STORE_NOT_FOUND = "ERROR-GENERAL-0004";
    public static final String GENERAL_ERROR_ACCOUNT_NOT_FOUND = "ERROR-GENERAL-0005";

    /**
     * Starting error code Account
     */
    public static final String ACCOUNT_ERROR_UNKNOWN = "ERROR-ACCOUNT-0000";
    public static final String ACCOUNT_ERROR_USERNAME_EXIST = "ERROR-ACCOUNT-0001";
    public static final String ACCOUNT_ERROR_NOT_FOUND = "ERROR-ACCOUNT-0002";
    public static final String ACCOUNT_ERROR_WRONG_PASSWORD = "ERROR-ACCOUNT-0003";
    public static final String ACCOUNT_ERROR_WRONG_HASH_RESET_PASS = "ERROR-ACCOUNT-0004";
    public static final String ACCOUNT_ERROR_LOCKED = "ERROR-ACCOUNT-0005";
    public static final String ACCOUNT_ERROR_OPT_INVALID = "ERROR-ACCOUNT-0006";
    public static final String ACCOUNT_ERROR_LOGIN = "ERROR-ACCOUNT-0007";
    public static final String ACCOUNT_ERROR_MERCHANT_LOGIN_ERROR_DEVICE = "ERROR-ACCOUNT-0008";
    public static final String ACCOUNT_ERROR_MERCHANT_LOGIN_ERROR_STORE = "ERROR-ACCOUNT-0009";
    public static final String ACCOUNT_ERROR_MERCHANT_LOGIN_WRONG_STORE = "ERROR-ACCOUNT-0010";
    public static final String ACCOUNT_ERROR_MERCHANT_SERVICE_NOT_REGISTER = "ERROR-ACCOUNT-0011";
    public static final String ACCOUNT_ERROR_NOT_PENDING = "ERROR-ACCOUNT-0012";
    public static final String ACCOUNT_ERROR_PHONE_EXIST = "ERROR-ACCOUNT-0013";
    public static final String ACCOUNT_ERROR_EMAIL_EXIST = "ERROR-ACCOUNT-0014";
    public static final String ACCOUNT_ERROR_EXIST = "ERROR-ACCOUNT-0015";
    public static final String ACCOUNT_ERROR_NEW_PASSWORD_MATCH_CURRENT_PASSWORD = "ERROR-ACCOUNT-0016";


    /**
     * Starting error code Customer
     */
    public static final String CUSTOMER_ERROR_UNKNOWN = "ERROR-CUSTOMER-0000";
    public static final String CUSTOMER_ERROR_EXIST = "ERROR-CUSTOMER-0002";
    public static final String CUSTOMER_ERROR_UPDATE = "ERROR-CUSTOMER-0003";
    public static final String CUSTOMER_ERROR_NOT_FOUND = "ERROR-CUSTOMER-0004";


    /**
     * Starting error code Store
     */
    public static final String SERVICE_ERROR_UNKNOWN = "ERROR-SERVICE-0000";
    public static final String SERVICE_ERROR_NOT_FOUND = "ERROR-SERVICE-0001";
    public static final String SERVICE_ERROR_DUPLICATE_PATH = "ERROR-SERVICE-0002";
    public static final String SERVICE_ERROR_USERNAME_EXIST = "ERROR-SERVICE-0003";
    public static final String SERVICE_ERROR_WRONG_OLD_PWD = "ERROR-SERVICE-0004";
    public static final String SERVICE_ERROR_TENANT_ID_EXIST = "ERROR-SERVICE-0005";

    /**
     * Starting error code SHOP ACCOUNT
     */
    public static final String SHOP_ACCOUNT_ERROR_UNKNOWN = "ERROR-SHOP_ACCOUNT-0000";
    /**
     * Starting error code NATION
     */
    public static final String NATION_ERROR_NOT_FOUND = "ERROR-NATION-0001";
    public static final String NATION_ERROR_NOT_ALLOW_HAVE_PARENT = "ERROR-NATION-0002";
    public static final String NATION_ERROR_PARENT_INVALID = "ERROR-NATION-0003";
    public static final String NATION_ERROR_NOT_ALLOW_UPDATE_KIND = "ERROR-NATION-0004";
    /**
     * Starting error code ADDRESS
     */
    public static final String ADDRESS_ERROR_NOT_FOUND = "ERROR-ADDRESS-0001";

    /**
     * Starting error code CATEGORY
     */
    public static final String CATEGORY_ERROR_NOT_FOUND = "ERROR-CATEGORY-0000";
    public static final String CATEGORY_ERROR_EXIST = "ERROR-CATEGORY-0001";
    public static final String CATEGORY_ERROR_INVALID_KIND = "ERROR-CATEGORY-0002";

    /**
     * Starting error code News
     */
    public static final String NEWS_ERROR_NOT_FOUND = "ERROR-NEWS-0000";
    public static final String NEWS_ERROR_EXISTED = "ERROR-NEWS-0001";
    /**
     * Starting error code Settings
     */
    public static final String SETTINGS_ERROR_NOT_FOUND = "ERROR-SETTING-0000";
    public static final String SETTINGS_ERROR_SETTING_KEY_EXISTED = "ERROR-SETTING-0001";

    /**
     * Starting error code USER
     */
    public static final String USER_ERROR_EXIST = "ERROR-USER-0000";
    public static final String USER_ERROR_NOT_FOUND = "ERROR-USER-0001";
    public static final String USER_ERROR_LOGIN_FAILED = "ERROR-USER-0002";
    public static final String USER_ERROR_GOOGLE_CODE_INVALID = "ERROR-USER-0003";
    public static final String USER_ERROR_INVALID_CLIENT_ID = "ERROR-USER-0004";
    public static final String USER_ERROR_FACEBOOK_CODE_INVALID = "ERROR-USER-0005";
    public static final String USER_ERROR_INVALID_PLATFORM = "ERROR-USER-0006";

    /**
     * Starting error code EXPERT
     */
    public static final String EXPERT_ERROR_NOT_FOUND = "ERROR-EXPERT-0000";

    public static final String EXPERT_ERROR_EXIST = "ERROR-EXPERT-0001";

    public static final String EXPERT_ERROR_LOGIN_FAILED = "ERROR-EXPERT-0002";
    public static final String EXPERT_ERROR_WRONG_PASSWORD = "ERROR-EXPERT-0003";
    public static final String EXPERT_ERROR_NOT_ACTIVE = "ERROR-EXPERT-0004";
    public static final String EXPERT_ERROR_REFERRAL_CODE_INVALID = "ERROR-EXPERT-0005";
    public static final String EXPERT_ERROR_CANNOT_DELETE_SYSTEM_EXPERT = "ERROR-EXPERT-0006";
    public static final String EXPERT_ERROR_NOT_ENTER_ORDERING = "ERROR-EXPERT-0007";


    /**
     * Starting error code ReferralExpertLog
     */
    public static final String REFERRAL_EXPERT_LOG_ERROR_NOT_FOUND = "ERROR-REFERRAL-EXPERT-LOG-0000";
    public static final String REFERRAL_CODE_ERROR_NOT_FOUND = "ERROR-REFERRAL-CODE-0000";

    /**
     * Starting error code STUDENT
     */
    public static final String STUDENT_ERROR_EXIST = "ERROR-STUDENT-0000";
    public static final String STUDENT_ERROR_NOT_FOUND = "ERROR-STUDENT-0001";
    public static final String STUDENT_ERROR_LOGIN_FAILED = "ERROR-STUDENT-0002";
    public static final String STUDENT_ERROR_STUDENT_IS_ALREADY_SELLER = "ERROR-STUDENT-0003";
    public static final String STUDENT_REFERRAL_CODE_INVALID = "ERROR-STUDENT-0004";
    public static final String STUDENT_ERROR_NOT_UPGRADING = "ERROR-STUDENT-0005";
    public static final String STUDENT_ERROR_CANNOT_GET_INFO_FROM_TOKEN = "ERROR-STUDENT-0006";
    public static final String STUDENT_ERROR_NOT_ACTIVE = "ERROR-STUDENT-0007";
    public static final String STUDENT_ERROR_NOT_ALLOW_USE_SYSTEM_CODE = "ERROR-STUDENT-0008";

    /**
     * Starting error code SELLER
     */
    public static final String SELLER_ERROR_NOT_FOUND = "ERROR-SELLER-0000";
    public static final String SELLER_ERROR_EXIST = "ERROR-SELLER-0001";
    public static final String SELLER_ERROR_NOT_ACTIVE = "ERROR-SELLER-0002";
    public static final String SELLER_ERROR_MAIL_EXIST = "ERROR-SELLER-0003";
    public static final String SELLER_ERROR_CANNOT_DELETE_SYSTEM_SELLER = "ERROR-SELLER-0004";


    /**
     * Starting error code REFERRAL SELLER LOG
     */
    public static final String REFERRAL_SELLER_LOG_ERROR_NOT_FOUND = "ERROR-REFERRAL-SELLER-LOG-0000";

    /**
     * Starting error code COURSE
     */
    public static final String COURSE_ERROR_NOT_FOUND = "ERROR-COURSE-0000";
    public static final String COURSE_ERROR_EXIST = "ERROR-COURSE-0001";
    public static final String COURSE_ERROR_NOT_TRUE_KIND = "ERROR-COURSE-0002";
    public static final String COURSE_ERROR_NOT_ACTIVE = "ERROR-COURSE-0003";
    public static final String COURSE_ERROR_NOT_ALLOW_CREATE_IS_SYSTEM_COURSE = "ERROR-COURSE-0004";
    public static final String COURSE_ERROR_EXPERT_ONLY_CREATE_SELLER_COURSE = "ERROR-COURSE-0005";
    public static final String COURSE_ERROR_COURSE_CAN_NOT_HAVE_LESSON = "ERROR-COURSE-0006";
    public static final String COURSE_ERROR_EXPERT_ONLY_MODIFY_THEIR_COURSE = "ERROR-COURSE-0007";
    public static final String COURSE_ERROR_CAN_NOT_UPDATE_KIND_FROM_NO_LESSON_AND_BACK = "ERROR-COURSE-0008";
    public static final String COURSE_ERROR_NOT_FREE = "ERROR-COURSE-0009";
    public static final String COURSE_ERROR_NOT_ALLOW_UPDATE_COURSE = "ERROR-COURSE-0010";
    public static final String COURSE_ERROR_NOT_ALLOW_SUBMIT_COURSE = "ERROR-COURSE-0011";
    public static final String COURSE_ERROR_NOT_CONTAIN_CHILDREN_COURSE = "ERROR-COURSE-0012";
    public static final String COURSE_ERROR_COURSE_IS_NOT_YOURS = "ERROR-COURSE-0013";
    public static final String COURSE_ERROR_COURSE_PENDING_PUBLICATION = "ERROR-COURSE-0014";


    /**
     * Starting error code COURSE_COMBO_DETAIL
     */
    public static final String COURSE_COMBO_DETAIL_ERROR_EXIST = "ERROR-COURSE-COMBO-DETAIL-0001";

    /**
     * Starting error code LESSON
     */
    public static final String LESSON_ERROR_NOT_FOUND = "ERROR-LESSON-0000";
    public static final String LESSON_ERROR_EXISTED = "ERROR-LESSON-0001";

    public static final String LESSON_ERROR_CONTENT_IS_BLANK = "ERROR-LESSON-0003";
    public static final String LESSON_ERROR_NOT_FAIL = "ERROR-LESSON-0004";
    public static final String LESSON_ERROR_NOT_DONE = "ERROR-LESSON-0005";
    public static final String LESSON_ERROR_CAN_NOT_UPDATE_KIND_FROM_SECTION_AND_BACK = "ERROR-LESSON-0006";
    public static final String LESSON_ERROR_NOT_KIND_VIDEO = "ERROR-LESSON-0007";
    public static final String LESSON_ERROR_VIDEO_STATE_NOT_DONE = "ERROR-LESSON-0008";
    public static final String LESSON_ERROR_LESSON_IS_NOT_YOURS = "ERROR-LESSON-009";
    public static final String LESSON_ERROR_IS_PROCESSING = "ERROR-LESSON-0010";
    public static final String LESSON_ERROR_MOVE_VIDEO_FILE_FALSE = "ERROR-LESSON-0011";

    /**
     * Starting error code COURSE RETAIL
     */
    public static final String COURSE_RETAIL_ERROR_NOT_FOUND = "ERROR-COURSE-RETAIL-0000";
    public static final String COURSE_RETAIL_ERROR_ALREADY_EXIST = "ERROR-COURSE-RETAIL-0001";
    public static final String COURSE_RETAIL_ERROR_NOT_PENDING = "ERROR-COURSE-RETAIL-0002";
    public static final String COURSE_RETAIL_ERROR_NOT_ACTIVE = "ERROR-COURSE-RETAIL-0003";
    public static final String COURSE_RETAIL_ERROR_NOT_LOCK = "ERROR-COURSE-RETAIL-0004";
    public static final String COURSE_RETAIL_ERROR_NOT_ALLOW_CREATE = "ERROR-COURSE-RETAIL-0005";


    /**
     * Starting error code COURSE TRANSACTION
     */
    public static final String COURSE_TRANSACTION_ERROR_NOT_FOUND = "ERROR-COURSE-TRANSACTION-0000";
    public static final String COURSE_TRANSACTION_ERROR_ALREADY_EXIST = "ERROR-COURSE-TRANSACTION-0001";
    public static final String COURSE_TRANSACTION_ERROR_SELL_CODE_INVALID = "ERROR-COURSE-TRANSACTION-0002";


    /**
     * Starting error code COMPLETION
     */
    public static final String COMPLETION_ERROR_NOT_FOUND = "ERROR-COMPLETION-0000";
    public static final String COMPLETION_ERROR_EXIST = "ERROR-COMPLETION-0001";

    public static final String COMPLETION_ERROR_CAN_NOT_CREATE = "ERROR-COMPLETION-0002";
    public static final String COMPLETION_ERROR_ALREADY_FINISHED = "ERROR-COMPLETION-0003";

    /**
     * Starting error code REGISTRATION
     */
    public static final String REGISTRATION_ERROR_NOT_FOUND = "ERROR-REGISTRATION-0000";
    public static final String REGISTRATION_ERROR_EXIST = "ERROR-REGISTRATION-0001";

    /**
     * Starting error code SELLER CODE TRACKING
     */
    public static final String SELLER_CODE_TRACKING_ERROR_NOT_FOUND = "ERROR-SELLER_CODE_TRACKING-0000";

    /**
     * Starting error code Notification
     */
    public static final String NOTIFICATION_ERROR_NOT_FOUND = "ERROR-NOTIFICATION-ERROR-0000";

    public static final String NOTIFICATION_USER_ERROR_NOT_FOUND = "ERROR-NOTIFICATION-ERROR-0001";

    /**
     * Starting error code Cart Item
     */
    public static final String CART_ITEM_ERROR_NOT_FOUND = "ERROR-CART-ITEM-0000";
    public static final String CART_ITEM_ERROR_ALREADY_EXIST = "ERROR-CART-ITEM-0001";
    public static final String CART_ITEM_ERROR_CANNOT_ADD_YOUR_SELLCODE= "ERROR-CART-ITEM-0002";

    /**
     * Starting error code Cart Item
     */
    public static final String BOOKING_ERROR_NOT_FOUND = "ERROR-BOOKING-0000";
    public static final String BOOKING_ERROR_CART_ITEM_EMPTY = "ERROR-BOOKING-0001";
    public static final String BOOKING_ERROR_NOT_ALLOW_UPDATE_PAID_TO_UNPAID = "ERROR-BOOKING-0002";
    public static final String BOOKING_ERROR_ALREADY_PAID = "ERROR-BOOKING-0003";
    public static final String BOOKING_ERROR_NOT_ALLOW_SYSTEM_SELLER_BUY_COURSE = "ERROR-BOOKING-0004";
    public static final String BOOKING_ERROR_NOT_ALLOW_CHANGE_STATE = "ERROR-BOOKING-0005";
    public static final String BOOKING_ERROR_NOT_ENOUGH_BALANCE_IN_WALLET= "ERROR-BOOKING-0006";
    public static final String BOOKING_ERROR_WALLET_HAS_ENOUGH_BALANCE= "ERROR-BOOKING-0007";

    /**
     * Starting error code Promotion
     */
    public static final String PROMOTION_ERROR_NOT_FOUND = "ERROR-PROMOTION-0000";
    public static final String PROMOTION_ERROR_EXIST = "ERROR-PROMOTION-0001";
    public static final String PROMOTION_ERROR_NOT_TRUE_FLOW = "ERROR-PROMOTION-0002";

    /**
     * Starting error code Promotion Code
     */
    public static final String PROMOTION_CODE_ERROR_NOT_FOUND = "ERROR-PROMOTION-CODE-0000";
    public static final String PROMOTION_CODE_ERROR_EXIST = "ERROR-PROMOTION-CODE-0001";
    public static final String PROMOTION_CODE_ERROR_REACHED_MAXIMUM_QUANTITY = "ERROR-PROMOTION-CODE-0002";
    /**
     * Starting error code Revenue Share
     */
    public static final String REVENUE_SHARE_NOT_FOUND = "ERROR-REVENUE-SHARE-0000";
    public static final String REVENUE_SHARE_IS_EXIST = "ERROR-REVENUE-SHARE-0001";
    /**
     * Starting error code SlideShow Scheduler
     */
    public static final String SLIDESHOW_SCHEDULER_NOT_FOUND = "ERROR-SLIDESHOW-SCHEDULER-SHARE-0000";
    public static final String SLIDESHOW_SCHEDULER_IS_EXIST = "ERROR-SLIDESHOW-SCHEDULER-SHARE-0001";

    /**
     * Starting error code Expert Registration
     */
    public static final String EXPERT_REGISTRATION_ERROR_EXIST = "ERROR-EXPERT-REGISTRATION-0000";
    public static final String EXPERT_REGISTRATION_ERROR_NOT_FOUND = "ERROR-EXPERT-REGISTRATION-0001";
    public static final String EXPERT_REGISTRATION_ERROR_PHONE_OR_EMAIL_ALREADY_USED = "ERROR-EXPERT-REGISTRATION-0002";
    public static final String EXPERT_REGISTRATION_ERROR_REFERRAL_CODE_INVALID = "ERROR-EXPERT-REGISTRATION-0003";
    public static final String EXPERT_REGISTRATION_ERROR_NOT_ALLOW_USE_SYSTEM_CODE = "ERROR-EXPERT-REGISTRATION-0004";


    /**
     * Starting error code Payout Period
     */
    public static final String MONTHLY_PERIOD_ERROR_EXIST = "ERROR-MONTHLY-PERIOD-0000";
    public static final String MONTHLY_PERIOD_ERROR_NOT_FOUND = "ERROR-MONTHLY-PERIOD-0001";

    /**
     * Starting error code Period Detail
     */
    public static final String PERIOD_DETAIL_ERROR_EXIST = "ERROR-PERIOD-DETAIL-0000";
    public static final String PERIOD_DETAIL_ERROR_NOT_FOUND = "ERROR-PERIOD-DETAIL-0001";

    /**
     * Starting error code Wallet
     */
    public static final String WALLET_ERROR_NOT_FOUND = "ERROR-WALLET-0000";

    /**
     * Starting error code Wallet Transaction
     */
    public static final String WALLET_TRANSACTION_ERROR_NOT_FOUND = "ERROR-WALLET-TRANSACTION-0000";

    /**
     * General error code
     */
    public static final String PERMISSION_ERROR_ALREADY_EXIST = "ERROR-PERMISSION-0000";
    public static final String PERMISSION_ERROR_NOT_FOUND = "ERROR-PERMISSION-0001";

    /**
     * Starting error code CATEGORY HOME
     */
    public static final String CATEGORY_HOME_ERROR_NOT_FOUND = "ERROR-CATEGORY-HOME-0000";
    public static final String CATEGORY_HOME_ERROR_EXIST = "ERROR-CATEGORY-HOME-0001";

    /**
     * Starting error code Review
     */
    public static final String REVIEW_ERROR_NOT_FOUND = "ERROR-REVIEW-ERROR-0000";
    public static final String REVIEW_ERROR_STUDENT_NOT_BUY_COURSE = "ERROR-REVIEW-ERROR-0001";
    public static final String REVIEW_ERROR_STUDENT_REVIEWED = "ERROR-REVIEW-ERROR-0002";
    public static final String REVIEW_ERROR_NOT_ENTER_COURSE_ID = "ERROR-REVIEW-ERROR-0003";
    public static final String REVIEW_ERROR_NOT_ENTER_EXPERT_ID = "ERROR-REVIEW-ERROR-0004";
    public static final String REVIEW_ERROR_USER_NOT_ADMIN = "ERROR-REVIEW-ERROR-0005";
    public static final String REVIEW_ERROR_NULL_STUDENT_ID = "ERROR-REVIEW-ERROR-0006";
    public static final String REVIEW_ERROR_KIND_NOT_ALLOW_FOR_CURRENT_USER = "ERROR-REVIEW-ERROR-0007";
    public static final String REVIEW_ERROR_KIND_JUST_ALLOW_ADMIN_UPDATE_COURSE_IN_SYSTEM_REVIEW = "ERROR-REVIEW-ERROR-0008";

    /**
     * Starting error code Statistical
     */
    public static final String STATISTICAL_ERROR_NOT_FOUND = "ERROR-STATISTICAL-0000";

    /**
     * Starting error code version
     */
    public static final String VERSION_ERROR_NOT_FOUND = "ERROR-VERSION-0000";
    public static final String VERSION_ERROR_COURSE_ID_CAN_NOT_NULL = "ERROR-VERSION-0001";
    public static final String VERSION_ERROR_NOT_INIT_OR_APPROVE = "ERROR-VERSION-0002";
    public static final String VERSION_ERROR_NOT_LATEST = "ERROR-VERSION-0003";
    /**
     * Starting error code course versioning
     */
    public static final String COURSE_VERSIONING_VERSION_ERROR_NOT_FOUND = "ERROR-COURSE-VERSIONING-0000";
    public static final String COURSE_VERSIONING_VERSION_ERROR_SINGLE_COURSE_NOT_CONTAIN_LESSON = "ERROR-COURSE-VERSIONING-0001";
    /**
     * Starting error code review history
     */
    public static final String COURSE_REVIEW_HISTORY_ERROR_NOT_FOUND = "ERROR-COURSE-REVIEW-HISTORY-0000";
    public static final String COURSE_REVIEW_HISTORY_ERROR_EXIST = "ERROR-COURSE-REVIEW-HISTORY-0001";

    /**
     * Starting error code LESSON VERSIONING
     */
    public static final String LESSON_VERSIONING_ERROR_NOT_FOUND = "ERROR-LESSON-VERSIONING-0000";
    /**
     * Starting error code REGISTER PAYOUT
     */
    public static final String REGISTER_PAYOUT_ERROR_NOT_FOUND = "ERROR-REGISTER-PAYOUT-0000";
    public static final String REGISTER_PAYOUT_ERROR_IS_EXISTED = "ERROR-REGISTER-PAYOUT-0001";
    public static final String REGISTER_PAYOUT_ERROR_NOT_REACH_MINIMUM = "ERROR-REGISTER-PAYOUT-0002";
    public static final String REGISTER_PAYOUT_NOT_ALLOW_CREATE = "ERROR-REGISTER-PAYOUT-0003";
    public static final String REGISTER_PAYOUT_BANK_INFO_NOT_ENOUGH = "ERROR-REGISTER-PAYOUT-0004";
    public static final String REGISTER_PAYOUT_ERROR_ALLOW_CHANGE_STATE = "ERROR-REGISTER-PAYOUT-0005";
    public static final String REGISTER_PAYOUT_ERROR_NOT_ENOUGH_BALANCE = "ERROR-REGISTER-PAYOUT-0006";
    public static final String REGISTER_PAYOUT_ERROR_STATE_APPROVE = "ERROR-REGISTER-PAYOUT-0007";
    public static final String REGISTER_PAYOUT_ERROR_STATE_CALCULATED = "ERROR-REGISTER-PAYOUT-0008";
    /**
     * Starting error code REGISTER PERIOD
     */
    public static final String REGISTER_PERIOD_ERROR_NOT_FOUND = "ERROR-REGISTER-PERIOD-0001";

}
