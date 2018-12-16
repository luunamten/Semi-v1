package org.nam.contract;

public final class Contract {
    public static final float RATING_LOW_MIN = 0;
    public static final float RATING_LOW_MAX = 4.9f;
    public static final float RATING_MEDIUM_MIN = 5;
    public static final float RATING_MEDIUM_MAX = 6.4f;
    public static final float RATING_HIGH_MIN = 6.5f;
    public static final float RATING_HIGH_MAX = 10;
    //spinner position of Store and Product
    public static final int STORE_MODE = 0;
    public static final int PRODUCT_MODE = 1;
    public static final String BUNDLE_MODE_KEY = "mode";
    public static final String BUNDLE_SEARCH_HINT_KEY = "string";
    public static final String BUNDLE_ACTION_LOGO_KEY = "logo";
    //currency
    public static final String VN_CURRENCY = "đ";
    //location
    public static final int LOCATION_INTERVAL = 2000; //ms
    public static final int LOCATION_FASTEST_INTERVAL = 2000; //ms

    private Contract() {}
}
