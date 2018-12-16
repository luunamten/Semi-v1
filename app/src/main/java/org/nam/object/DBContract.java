package org.nam.object;

//Contract for database containing constants representing collection name, field name,...
public class DBContract {
    public static final String ID = "id";
    private DBContract() {}
    public static class Store {
        private Store() {}
        public static final String COLLECTION = "store";
        public static final String TITLE = "title";
        public static final String FULL_NAME = "fullName";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE_URL = "imageURL";
        public static final String ADDRESS = "address";
        public static final String ADDRESS_COUNTRY = "country";
        public static final String ADDRESS_CITY = "city";
        public static final String ADDRESS_DISTRICT = "district";
        public static final String ADDRESS_TOWN = "town";
        public static final String ADDRESS_STREET = "street";
        public static final String TYPE = "type";
        public static final String UTILITIES = "utilities";
        public static final String START_END = "startEnd";
        public static final String GEO = "geo";
        public static final String GEO_LATITUDE = "_latitude";
        public static final String GEO_LONGITUDE = "_longitude";
        public static final String GRID_NUMBER = "gridNumber";
        public static final String RATING = "rating";
        public static final String CONTACT = "contact";
    }

    public static class Product {
        private Product() {}
        public static final String COLLECTION = "product";
        public static final String TITLE = "title";
        public static final String FULL_NAME = "fullName";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE_URL = "imageURL";
        public static final String COST = "cost";
        public static final String TYPE = "type";
    }
}
