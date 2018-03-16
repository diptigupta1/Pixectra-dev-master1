package com.pixectra.app.Utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pixectra.app.Models.CheckoutData;
import com.pixectra.app.Models.Product;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

/**
 * Created by XCODER on 3/5/2018.
 */

public class ImageController {

    public static void placeOrder(CheckoutData data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd~HH-mm-ss~", Locale.getDefault());
        String folder = "Present/" + format.format(new Date());
        Random random = new Random();
        folder = folder + random.nextInt(1000) + 1;
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(folder);
        mStorageRef.child("info.txt").putBytes(data.toString().getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
        int i = 0, j;
        for (Pair<Product, Vector<Bitmap>> pair : CartHolder.getInstance().getCart()) {
            j = 0;
            for (Bitmap image : pair.second) {
                StorageReference riversRef = mStorageRef.child(pair.first.getId() + "~~" + i).child("" + j + ".png");
                riversRef.putBytes(getBytes(image))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });
                j++;
            }
            i++;
        }
    }

    static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
