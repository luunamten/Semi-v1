package org.nam.contract;

import com.google.android.gms.location.LocationRequest;

import org.nam.R;

public final class Contract {
    public static final float[] RATING_LEVELS = {0, 2.5f, 5};
    public static final int[] RATING_COLORS =  {0xffdd0000, 0xff00aa00};
    public static final int[] UTILITY_RESOURCES = {
            //R.drawable.minh_ic_store_detail_direct,   //0 -
            //R.drawable.minh_ic_store_detail_descrip,  //1 -
            //R.drawable.minh_ic_home_mall,             //2 -
            //R.drawable.minh_ic_home_mini_store        //3 -
            R.drawable.utility_wifi_black,            //0 - wifi
            R.drawable.utility_deliver_black,         //1 - deliver
            R.drawable.utlity_bike_black              //2 - bike guard
    };
    public static final float RATING_LOW = 0;
    public static final float RATING_NORMAL = 2.5f;
    public static final float RATING_HIGH = 5;
    //spinner position of Store and Product
    public static final int STORE_MODE = 0;
    public static final int PRODUCT_MODE = 1;
    public static final String BUNDLE_MODE_KEY = "mode";
    public static final String BUNDLE_SEARCH_HINT_KEY = "string";
    public static final String BUNDLE_ACTION_LOGO_KEY = "logo";
    public static final String BUNDLE_MODE_TYPE_KEY = "type";
    public static final String BUNDLE_STORE_ID_KEY = "storeId";
    public static final String BUNDLE_PRODUCT_ID_KEY = "productId";
    //currency
    public static final String VN_CURRENCY = "đ";
    //location
    public static final int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static final int LOCATION_INTERVAL = 3000; //ms
    public static final int LOCATION_FASTEST_INTERVAL = 3000; //ms
    //box
    public static final double VISIBLE_BOX_MIN_DIMEN = 0.125; //km
    public static final int DIMEN_SCALE_FACTOR_MAX = 5;
    public static final double VISIBLE_BOX_MAX_DIMEN = VISIBLE_BOX_MIN_DIMEN * (1 << DIMEN_SCALE_FACTOR_MAX); //km
    public static final double HIDING_BOX_DIMEN = 16;
    public static final int HIDING_BOX_COLOR = 0x33000000;
    public static final int VISIBLE_BOX_PADDING = 100; //px
    //shared preferences
    public static final String SHARED_MY_STATE = "myState";
    public static final String SHARED_COUNTRY_KEY = "country";
    public static final String SHARED_CITY_KEY = "city";
    public static final String SHARED_DISTRICT_KEY = "district";
    public static final String SHARED_TOWN_KEY = "town";
    private Contract() {}
}
