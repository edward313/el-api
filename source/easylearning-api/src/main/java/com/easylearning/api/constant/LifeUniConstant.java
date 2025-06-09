package com.easylearning.api.constant;


import java.util.Arrays;
import java.util.List;

public class LifeUniConstant {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String SCHEDULE_DATE_TIME_FORMAT = "ddMMyyyyhhmmss";


    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_MANAGER = 2;
    public static final Integer USER_KIND_USER = 3;
    public static final Integer USER_KIND_STUDENT = 4;
    public static final Integer USER_KIND_EXPERT = 5;

    public static final Integer STATUS_UPGRADING = 2;
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_LOCK = -1;
    public static final Integer STATUS_DELETE = -2;
    public static final Integer STATUS_REJECT = -3;

    public static final Integer NATION_KIND_PROVINCE = 1;
    public static final Integer NATION_KIND_DISTRICT = 2;
    public static final Integer NATION_KIND_COMMUNE = 3;

    public static final Integer GROUP_KIND_ADMIN = 1;
    public static final Integer GROUP_KIND_MANAGER = 2;
    public static final Integer GROUP_KIND_USER = 3;
    public static final Integer GROUP_KIND_STUDENT = 4;
    public static final Integer GROUP_KIND_EXPERT = 5;



    public static final Integer MAX_ATTEMPT_FORGET_PWD = 5;
    public static final int MAX_TIME_FORGET_PWD = 5 * 60 * 1000; //5 minutes
    public static final Integer MAX_ATTEMPT_LOGIN = 5;

    public static final Integer CATEGORY_ORDERING_TOP = -1;

    public static final Integer CATEGORY_KIND_SPECIALIZED = 1;
    public static final Integer CATEGORY_KIND_TOP_FREE = 2;
    public static final Integer CATEGORY_KIND_TOP_CHARGE = 3;
    public static final Integer CATEGORY_KIND_TOP_NEW = 4;
    public static final Integer CATEGORY_KIND_NEWS = 5;
    public static final String CATEGORY_NAME_TOP_FREE = "TOP FREE COURSE";
    public static final String CATEGORY_NAME_TOP_CHARGE = "TOP CHARGE COURSE";
    public static final String CATEGORY_NAME_TOP_NEW = "TOP NEW COURSE";

    public static final Boolean STUDENT_IS_SELLER = true;
    public static final Boolean STUDENT_IS_NOT_SELLER = false;

    public static final Integer COURSE_KIND_SINGLE = 1;
    public static final Integer COURSE_KIND_COMBO = 2;
    public static final Integer COURSE_KIND_NO_LESSON = 3;
    public static final Integer LESSON_KIND_TEXT = 1;
    public static final Integer LESSON_KIND_VIDEO = 2;
    public static final Integer LESSON_KIND_SECTION = 3;

    public static final Integer LESSON_STATE_PROCESS = 1;
    public static final Integer LESSON_STATE_DONE = 2;
    public static final Integer LESSON_STATE_FAIL = 3;

    public static final String LESSON_URL_KIND_INTERNAL = "internal";
    public static final String BACKEND_PROCESS_VIDEO_CMD = "BACKEND_PROCESS_VIDEO";
    public static final String BACKEND_POST_NOTIFICATION_CMD = "BACKEND_POST_NOTIFICATION";
    public static final String MEDIA_COMPLETED_PROCESS_VIDEO_CMD = "MEDIA_COMPLETED_PROCESS_VIDEO";
    public static final String BACKEND_APPROVE_VERSIONS_CMD = "BACKEND_APPROVE_VERSION";
    public static final String BACKEND_SYNC_ELASTIC_CMD = "BACKEND_SYNC_ELASTIC";

    public static final Integer NOTIFICATION_STATE_SENT = 0;
    public static final Integer NOTIFICATION_STATE_READ = 1;

    public static final Integer NOTIFICATION_KIND_APPROVE_SELLER = 1; // remove
    public static final Integer NOTIFICATION_KIND_REJECT_SELLER = 2;  // remove
    public static final Integer NOTIFICATION_KIND_EXPERT_REGISTRATION = 3; // remove
    public static final Integer NOTIFICATION_KIND_APPROVE_EXPERT = 4; //portal
    public static final Integer NOTIFICATION_KIND_UPGRADE_SELLER = 5; //fe
    public static final Integer NOTIFICATION_KIND_SIGNUP_STUDENT = 6; //portal
    public static final Integer NOTIFICATION_KIND_RECEIVE_REVENUE = 7; //portal
    public static final Integer NOTIFICATION_KIND_APPROVE_COURSE = 8; //portal
    public static final Integer NOTIFICATION_KIND_REJECT_COURSE = 9; //portal
    public static final Integer NOTIFICATION_KIND_APPROVE_BOOKING = 10; //fe
    public static final Integer NOTIFICATION_KIND_REJECT_BOOKING = 11; //fe

    // Danh sách các giá trị liên quan đến 'portal'
    public static final List<Integer> PORTAL_NOTIFICATION_KINDS = Arrays.asList(
            NOTIFICATION_KIND_APPROVE_EXPERT,
            NOTIFICATION_KIND_SIGNUP_STUDENT,
            NOTIFICATION_KIND_RECEIVE_REVENUE,
            NOTIFICATION_KIND_APPROVE_COURSE,
            NOTIFICATION_KIND_REJECT_COURSE
    );

    // Danh sách các giá trị liên quan đến 'fe'
    public static final List<Integer> FE_NOTIFICATION_KINDS = Arrays.asList(
            NOTIFICATION_KIND_UPGRADE_SELLER,
            NOTIFICATION_KIND_APPROVE_BOOKING,
            NOTIFICATION_KIND_REJECT_BOOKING
    );

    public static final Integer BOOKING_STATE_UNPAID = 0;
    public static final Integer BOOKING_STATE_PAID = 1;
    public static final Integer BOOKING_STATE_REJECT = 2;

    public static final Integer BOOKING_PAYOUT_STATUS_UNPAID = 0;
    public static final Integer BOOKING_PAYOUT_STATUS_PAID = 1;

    public static final Integer BOOKING_PAYMENT_METHOD_BANKING = 0;
    public static final Integer BOOKING_PAYMENT_METHOD_WALLET = 2;


    public static final Integer ACCOUNT_PLATFORM_GOOGLE = 1;
    public static final Integer ACCOUNT_PLATFORM_FACEBOOK = 2;

    public static final Integer PROMOTION_KIND_MONEY = 1;
    public static final Integer PROMOTION_KIND_PERCENT = 2;

    public static final Integer PROMOTION_TYPE_USE_ONE = 1;
    public static final Integer PROMOTION_TYPE_USE_MULTIPLE = 2;

    public static final Integer PROMOTION_STATE_CREATED = 0;
    public static final Integer PROMOTION_STATE_RUNNING = 1;
    public static final Integer PROMOTION_STATE_END = 2;
    public static final Integer PROMOTION_STATE_CANCEL = 3;

    public static final Integer SLIDESHOW_NO_ACTION = 0;
    public static final Integer SLIDESHOW_OPEN_URL = 1;

    public static final Integer[] PROMOTION_STATE = {
            PROMOTION_STATE_CREATED,
            PROMOTION_STATE_RUNNING,
            PROMOTION_STATE_END,
            PROMOTION_STATE_CANCEL
    };
    public static final String DATA_TYPE_INT = "int";
    public static final String DATA_TYPE_STRING = "string";
    public static final String DATA_TYPE_DOUBLE = "double";
    public static final String DATA_TYPE_OBJECT = "object";
    public static final String DATA_TYPE_BOOLEAN = "boolean";
    public static final String DATA_TYPE_PERCENT = "percent";

    public static final String SETTING_KEY_SYSTEM_REVENUE_SHARE = "system_revenue";
    public static final String SETTING_KEY_EXPERT_REVENUE_SHARE = "expert_revenue";
    public static final String SETTING_KEY_EXPERT_REF_REVENUE_SHARE = "expert_ref_revenue";
    public static final String SETTING_KEY_SELLER_REVENUE_SHARE = "seller_revenue";
    public static final String SETTING_KEY_SELLER_REF_REVENUE_SHARE = "seller_ref_revenue";
    public static final String SETTING_KEY_DEFAULT_COURSE_PRICE  = "default_course_price";
    public static final String SETTING_KEY_ROOT_SELLER_REVENUE_SHARE = "root_seller_revenue";
    public static final String SETTING_KEY_DEFAULT_BALANCE = "default_balance";
    public static final String SETTING_KEY_SYSTEM_COUPON = "system_coupon";
    public static final String SETTING_GROUP_NAME_REVENUE_SHARE = "revenue_share";

    public static final String SETTING_GROUP_NAME_BANK_INFO = "bank_info";

    public static final String SETTING_GROUP_NAME_PAYOUT = "payout";
    public static final String SETTING_KEY_TAX_PERCENT = "tax_percent";
    public static final String SETTING_KEY_MIN_BALANCE = "min_balance";
    public static final String SETTING_KEY_MIN_MONEY_OUT = "min_money_out";

    public static final Integer PERIOD_DETAIL_STATE_UNPAID = 0;
    public static final Integer PERIOD_DETAIL_STATE_PAID = 1;
    public static final Integer PERIOD_DETAIL_STATE_CANCEL = 2;

    public static final Integer PERIOD_DETAIL_KIND_SELLER = 0;
    public static final Integer PERIOD_DETAIL_KIND_EXPERT = 1;
    public static final Integer PERIOD_DETAIL_KIND_SYSTEM = 2;

    public static final Integer MONTHLY_PERIOD_STATE_PENDING = 0;
    public static final Integer MONTHLY_PERIOD_STATE_CALCULATED = 1;
    public static final Integer MONTHLY_PERIOD_STATE_DONE = 2;
    public static final Integer MONTHLY_PERIOD_STATE_CANCEL = 3;
    public static final Integer WALLET_KIND_SELLER = 0;
    public static final Integer WALLET_KIND_EXPERT = 1;
    public static final Integer WALLET_KIND_STUDENT = 2;
    public static final Integer WALLET_KIND_SYSTEM = 3;

    public static final Integer WALLET_TRANSACTION_KIND_IN = 1;
    public static final Integer WALLET_TRANSACTION_KIND_WITH_DRAW = 2;
    public static final Integer WALLET_TRANSACTION_KIND_INIT_MONEY = 3;
    public static final Integer WALLET_TRANSACTION_KIND_BUY_COURSE = 4;

    public static final String WALLET_TRANSACTION_NOTE_IN = "Nhận tiền bán hàng";
    public static final String WALLET_TRANSACTION_NOTE_WITH_DRAW = "Rút tiền";
    public static final String WALLET_TRANSACTION_NOTE_INIT_MONEY = "Nhận tiền giới thiệu";
    public static final String WALLET_TRANSACTION_NOTE_BUY_COURSE = "Mua khóa học có mã là: ";
    public static final String WALLET_TRANSACTION_NOTE_SYSTEM_REVENUE = "Doanh thu hệ thống kỳ: ";
    public static final String WALLET_TRANSACTION_NOTE_BUY_FIRST_COURSE = "Nhận tiền thưởng khóa học đầu tiên";

    public static final Integer WALLET_TRANSACTION_STATE_PENDING = 0;
    public static final Integer WALLET_TRANSACTION_STATE_FAIL = 1;
    public static final Integer WALLET_TRANSACTION_STATE_SUCCESS = 2;

    public static final Integer REVENUE_SHARE_KIND_REF = 0;
    public static final Integer REVENUE_SHARE_KIND_SYSTEM = 1;
    public static final Integer REVENUE_SHARE_KIND_SYSTEM_FREE_COURSE = 2;
    public static final Integer REVENUE_SHARE_KIND_DIRECT = 3;

    public static final Integer REVENUE_SHARE_REF_KIND_STUDENT = 1;
    public static final Integer REVENUE_SHARE_REF_KIND_SELLER = 2;
    public static final Integer REVENUE_SHARE_REF_KIND_EXPERT = 3;

    public static final Integer REVENUE_SHARE_PAYOUT_STATUS_UNPAID = 0;
    public static final Integer REVENUE_SHARE_PAYOUT_STATUS_PAID = 1;
    public static final Integer REVENUE_SHARE_ROUND_NUMBER = 3;

    public static final Integer CATEGORY_HOME_LIMIT_COURSE_NUMBER = 10;

    public static final String FILE_TYPE_AVATAR = "AVATAR";

    public static final Integer REVIEW_ONE_STAR = 1;
    public static final Integer REVIEW_TWO_STAR = 2;
    public static final Integer REVIEW_THREE_STAR = 3;
    public static final Integer REVIEW_FOUR_STAR = 4;
    public static final Integer REVIEW_FIVE_STAR = 5;

    public static final Integer REVIEW_KIND_EXPERT = 1;
    public static final Integer REVIEW_KIND_COURSE = 2;
    public static final Integer REVIEW_KIND_SYSTEM = 3;
    public static final Integer[] REVIEW_STARS = {
            REVIEW_ONE_STAR,
            REVIEW_TWO_STAR,
            REVIEW_THREE_STAR,
            REVIEW_FOUR_STAR,
            REVIEW_FIVE_STAR
    };

    public static final Integer NEWS_KIND_INTRODUCTION = 1;

    public static final String STATISTICAL_FE_STATISTIC_KEY = "fe_statistic";
    public static final String PHONE_NUMBER_REGEX = "^0[1-9][0-9]{8}$";

    public static final Integer VERSION_STATE_PROCESS_ERROR = -1;
    public static final Integer VERSION_STATE_INIT = 0;
    public static final Integer VERSION_STATE_SUBMIT = 1;
    public static final Integer VERSION_STATE_APPROVE = 2;
    public static final Integer VERSION_STATE_REJECT = 3;
    public static final Integer VERSION_STATE_PROCESSING = 4;
    public static final Integer VERSION_STATE_WAITING = 5;

    public static final Integer VERSION_CODE_INIT = 1;

    public static final Integer COURSE_REVIEW_HISTORY_STATE_INIT = 0;
    public static final Integer COURSE_REVIEW_HISTORY_STATE_SUBMIT = 1;
    public static final Integer COURSE_REVIEW_HISTORY_STATE_APPROVE = 2;
    public static final Integer COURSE_REVIEW_HISTORY_STATE_REJECT = 3;

    public static final Integer REGISTER_PAYOUT_STATE_PENDING = 0;
    public static final Integer REGISTER_PAYOUT_STATE_CALCULATED = 1;
    public static final Integer REGISTER_PAYOUT_STATE_APPROVED = 2;
    public static final Integer REGISTER_PAYOUT_STATE_CANCELLED = 3;
    public static final Integer REGISTER_PAYOUT_STATE_ADMIN_CANCELLED = 4;

    public static final Integer REGISTER_PAYOUT_DATE = 16;

    public static final Integer REGISTER_PAYOUT_KIND_INDIVIDUAL = 1;
    public static final Integer REGISTER_PAYOUT_KIND_SUM = 2;

    public static final Integer REGISTER_PERIOD_STATE_PENDING = 0;
    public static final Integer REGISTER_PERIOD_STATE_APPROVE = 1;
    public static final Integer REGISTER_PERIOD_STATE_REJECT = 2;

    public static final Integer CLIENT_APP = 1;
    public static final Integer PORTAL_APP = 2;

    private LifeUniConstant(){
        throw new IllegalStateException("Utility class");
    }
}
