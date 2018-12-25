package org.nam.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;

public class StorageConnector {
    private static final long MAX_SIZE = 1024 * 1024;
    private static StorageConnector instance;
    private StorageConnector(){}

    public void getImageData(String path, final IResult<Bitmap> result) {
        if(path == null || path.trim().equals("")) {
            return;
        }
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference reference = storage.getReference().child(path);
        reference.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if(bytes == null) {
                    result.onResult(null);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    result.onResult(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result.onFailure(e);
            }
        });
    }

    public static StorageConnector getInstance() {
        if(instance == null) {
            instance = new StorageConnector();
        }
        return instance;
    }
}
