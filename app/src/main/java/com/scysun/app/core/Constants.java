

package com.scysun.app.core;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.scysun.app";

        /**
         * Account name
         */
        public static final String BOOTSTRAP_ACCOUNT_NAME = "FaceBank";

        /**
         * Provider id
         */
        public static final String BOOTSTRAP_PROVIDER_AUTHORITY = "com.scysun.app.sync";

        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;
    }

    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static final class Http {
        private Http() {}


        /**
         * Base URL for all requests
         */
//        public static final String URL_BASE = "https://api.parse.com";
//        public static final String URL_AUTH_FRAG = "/1/login";
//        public static final String URL_USERS_FRAG =  "/1/users";

        //public static final String URL_BASE = "http://s.facebank.com.cn/fbserver";
        public static final String URL_BASE = "http://119.146.191.102/fbserver";
        public static final String URL_AUTH_FRAG = "/user/auth";
        public static final String URL_USERS_FRAG =  "/user/list";

        /**
         * Authentication URL
         */
        public static final String URL_AUTH = URL_BASE + URL_AUTH_FRAG;

        /**
         * List Users URL
         */
        public static final String URL_USERS = URL_BASE + URL_USERS_FRAG;


        /**
         * List News URL
         */
        public static final String URL_NEWS_FRAG = "/1/classes/News";
        public static final String URL_NEWS = URL_BASE + URL_NEWS_FRAG;


        /**
         * List Checkin's URL
         */
        public static final String URL_CHECKINS_FRAG = "/1/classes/Locations";
        public static final String URL_CHECKINS = URL_BASE + URL_CHECKINS_FRAG;

        /**
         * PARAMS for auth
         */
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_PASSWORD = "password";


        public static final String PARSE_APP_ID = "zHb2bVia6kgilYRWWdmTiEJooYA17NnkBSUVsr4H";
        public static final String PARSE_REST_API_KEY = "N2kCY1T3t3Jfhf9zpJ5MCURn3b25UpACILhnf5u9";
        public static final String HEADER_PARSE_REST_API_KEY = "X-Parse-REST-API-Key";
        public static final String HEADER_PARSE_APP_ID = "X-Parse-Application-Id";
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String SESSION_TOKEN = "sessionToken";


    }


    public static final class Extra {
        private Extra() {}

        public static final String NEWS_ITEM = "news_item";

        public static final String USER = "user";

    }

    public static final class Intent {
        private Intent() {}

        /**
         * Action prefix for all intents created
         */
        public static final String INTENT_PREFIX = "com.scysun.app.";

    }

    public static class Notification {
        private Notification() {
        }

        public static final int TIMER_NOTIFICATION_ID = 1000; // Why 1000? Why not? :)
        public static final int CONTACT_SCANNING_NOTIFICATION_ID = 1001;
        public static final int CONTACT_SCANNING_RESULT_NOTIFICATION_ID = 1002;
    }

    public static class DateFormat{
        private DateFormat(){

        }

        public static final String DATE_FORMAT = "yyyy-MM-dd";

        public static final long TIME_MILLIS_PER_DAY = 3600 * 24 * 1000;
    }

    public static class SharedPreferences_LiveChronometer {
        public static final String NAME = "LiveChronometer";
        public static final String BIRTHDAY = "BIRTHDAY";
        public static final String AGE_OF_RETIREMENT = "AGE_OF_RETIREMENT";
        public static final String AGE_OF_DEATH = "AGE_OF_DEATH";
    }

    public static class SharedPreferences_Contact {
        public static final String NAME = "Contact";
        public static final String HAS_SCANNED = "HAS_SCANNED";
    }

    public static class Separator {
        public static final String COMMA = ",";
    }
}


