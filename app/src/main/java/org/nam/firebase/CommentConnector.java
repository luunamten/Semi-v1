package org.nam.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.nam.object.Comment;
import org.nam.object.DBContract;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentConnector {
    private static CommentConnector instance;

    public void getComments(String storeId, long fromTime, final IResult<List<Comment>> result) {
        Map<String, Object> data = new HashMap<>();
        data.put(CFContract.GetComments.FROM_TIME, fromTime);
        data.put(CFContract.GetComments.STORE_ID, storeId);
        FirebaseFunctions.getInstance().getHttpsCallable(CFContract.GetComments.NAME)
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        List<Comment> comments = new ArrayList<>();
                        if(mapList.size() > 0) {
                            for(Map<String, Object> map : mapList) {
                                Comment comment = new Comment();
                                comment.setId((String) map.get(DBContract.ID));
                                comment.setComment((String) map.get(DBContract.Comment.COMMENT));
                                comment.setTime(((Number)map.get(DBContract.Comment.TIME)).longValue());
                                comment.setStoreId((String) map.get(DBContract.Comment.STORE_ID));
                                comments.add(comment);
                            }
                        }
                        result.onResult(comments);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    public void postComment(String storeId, String comment, String rating, final IResult<Object> result) {
        Map<String, Object> data = new HashMap<>();
        data.put(CFContract.PostComment.STORE_ID, storeId);
        data.put(CFContract.PostComment.COMMENT, comment);
        data.put(CFContract.PostComment.USER_RATING, rating);
        FirebaseFunctions.getInstance().getHttpsCallable(CFContract.PostComment.NAME)
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        result.onResult(httpsCallableResult.getData());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    public static CommentConnector getInstance() {
        if(instance == null) {
            instance = new CommentConnector();
        }
        return instance;
    }
}
