package org.nam.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.nam.sqlite.AddressDBConnector;
import org.nam.object.Address;
import org.nam.object.DBContract;
import org.nam.object.Location;
import org.nam.object.Store;
import org.nam.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nam.firebase.CFContract.*;

public class StoreConnector {
    private static StoreConnector instance;
    private FirebaseFunctions functions;
    private StoreConnector() {
        this.functions = FirebaseFunctions.getInstance();
    }

    public void getNearbyStores(Location location, int from, int storeType,
                               final IResult<List<Store>> IResult) {
        //Cloud functions data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(NearbyStores.CENTER_LATITUDE, location.getLatitude());
        data.put(NearbyStores.CENTER_LONGITUDE, location.getLongitude());
        data.put(NearbyStores.FROM, from);
        data.put(NearbyStores.STORE_TYPE, storeType);
        //Call cloud function
        functions.getHttpsCallable(NearbyStores.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Store> stores = new ArrayList<Store>();
                        List<Map<String, Object>> listMap = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (listMap.size() > 0) {
                            for (Map map : listMap) {
                                /*GET id, title, address, imageURL, rating, geo*/
                                //Init store
                                Store store = new Store();
                                store.setId((String) map.get(DBContract.ID));
                                store.setTitle((String) map.get(DBContract.Store.TITLE));
                                store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                                store.setAddress(getAddress(map));
                                //Error occurs when casting Integer to Float
                                store.setRating(((Number) map.get(DBContract.Store.RATING)).floatValue());
                                store.setGeo(getGeo(map));
                                stores.add(store);
                            }
                        }
                        IResult.onResult(stores);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    public void getNearbyStoresByKeywords(Location location,
                                          int from, int storeType, String keywords, double dimen,
                                          final IResult<List<Store>> IResult) {
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(NearbyStoresByKeywords.CENTER_LATITUDE, location.getLongitude());
        data.put(NearbyStoresByKeywords.CENTER_LONGITUDE, location.getLatitude());
        data.put(NearbyStoresByKeywords.FROM, from);
        data.put(NearbyStoresByKeywords.STORE_TYPE, storeType);
        data.put(NearbyStoresByKeywords.KEYWORDS, keywords);
        data.put(NearbyStoresByKeywords.RECT_DIMENSION, dimen);
        //Call cloud function
        functions.getHttpsCallable(NearbyStoresByKeywords.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        /*GET id, title, address, imageURL, rating, geo*/
                        List<Store> stores = new ArrayList<Store>();
                        List<Map<String, Object>> listMap = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (listMap.size() > 0) {
                            for (Map map : listMap) {
                                /*GET id, title, address, imageURL, rating, geo*/
                                //Init store
                                Store store = new Store();
                                store.setId((String) map.get(DBContract.ID));
                                store.setTitle((String) map.get(DBContract.Store.TITLE));
                                store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                                store.setAddress(getAddress(map));
                                //Error occurs when casting Integer to Float
                                store.setRating(((Number) map.get(DBContract.Store.RATING)).floatValue());
                                store.setGeo(getGeo(map));
                                stores.add(store);
                            }
                        }
                        IResult.onResult(stores);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    public void getStoresByKeywords(int storeType, String keywords, String lastId,
                                    final IResult<List<Store>> IResult) {
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(StoresByKeywords.STORE_TYPE, storeType);
        data.put(StoresByKeywords.KEYWORDS, keywords);
        data.put(StoresByKeywords.LAST_STORE_ID, lastId);
        //Call cloud function
        functions.getHttpsCallable(StoresByKeywords.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Store> stores = new ArrayList<Store>();
                        List<Map<String, Object>> listMap = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (listMap.size() > 0) {
                            for (Map map : listMap) {
                                /*GET id, title, address, imageURL, geo, rating*/
                                //Init store
                                Store store = new Store();
                                store.setId((String) map.get(DBContract.ID));
                                store.setTitle((String) map.get(DBContract.Store.TITLE));
                                store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                                store.setAddress(getAddress(map));
                                store.setGeo(getGeo(map));
                                //Error occurs when casting Integer to Float
                                store.setRating(((Number) map.get(DBContract.Store.RATING)).floatValue());
                                stores.add(store);
                            }
                        }
                        IResult.onResult(stores);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    public void getStoreById(String storeId, final IResult<Store> IResult) {
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(StoreById.STORE_ID, storeId);
        //Call cloud function
        functions.getHttpsCallable(StoreById.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Map<String, Object> map = (Map<String, Object>) httpsCallableResult.getData();
                        AddressDBConnector connector = AddressDBConnector.getInstance();
                        if(map != null) {
                            //Easy to get those data
                            Store store = new Store();
                            store.setId((String) map.get(DBContract.ID));
                            store.setTitle((String) map.get(DBContract.Store.TITLE));
                            store.setName((String) map.get(DBContract.Store.FULL_NAME));
                            store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                            store.setDescription((String) map.get(DBContract.Store.DESCRIPTION));
                            store.setContact((String) map.get(DBContract.Store.CONTACT));
                            store.setRating(((Number)map.get(DBContract.Store.RATING)).floatValue());
                            store.setStartEnd((String)map.get(DBContract.Store.START_END));
                            store.setAddress(getAddress(map));
                            store.setType(
                                    ObjectUtils.getStoreType(
                                            ((Number)map.get(DBContract.Store.TYPE)).intValue()
                                            ));
                            store.setUtilities(getStoreUtilities(map));
                            store.setGeo(getGeo(map));
                            IResult.onResult(store);
                        } else {
                            IResult.onResult(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    private List<Store.Utility> getStoreUtilities(Map<String, Object> map) {
        List<Integer> ids = (List<Integer>)map.get(DBContract.Store.UTILITIES);
        List<Store.Utility> utilities = new ArrayList<>();
        for(int id : ids) {
            utilities.add(ObjectUtils.getStoreUtility(id));
        }
        return utilities;
    }

    private Address getAddress(Map<String, Object> map) {
        Map<String, Object> addressMap = (Map<String, Object>)map.get(DBContract.Store.ADDRESS);
        int countryId = (int)addressMap.get(DBContract.Store.ADDRESS_COUNTRY);
        int cityId = (int)addressMap.get(DBContract.Store.ADDRESS_CITY);
        int districtId = (int)addressMap.get(DBContract.Store.ADDRESS_DISTRICT);
        int townId = (int)addressMap.get(DBContract.Store.ADDRESS_TOWN);
        String street = (String)addressMap.get(DBContract.Store.ADDRESS_STREET);
        AddressDBConnector connector = AddressDBConnector.getInstance();
        Address address = new Address();
        address.setCountry(connector.getCountry(countryId));
        address.setCity(connector.getCity(cityId));
        address.setDistrict(connector.getDistrict(districtId));
        address.setTown(connector.getTown(townId));
        address.setStreet(street);
        return address;
    }

    private Location getGeo(Map<String, Object> map) {
        Map<String, Double> geoMap = (Map<String, Double>)map.get(DBContract.Store.GEO);
        double latitude = geoMap.get(DBContract.Store.GEO_LATITUDE);
        double longitude = geoMap.get(DBContract.Store.GEO_LONGITUDE);
        return new Location(latitude, longitude);
    }

    public static StoreConnector getInstance() {
        if(instance == null) {
            instance = new StoreConnector();
        }
        return instance;
    }
}
