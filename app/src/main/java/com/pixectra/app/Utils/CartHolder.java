package com.pixectra.app.Utils;

import android.graphics.Bitmap;
import android.util.Pair;

import com.pixectra.app.Models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class CartHolder {

    private static CartHolder sSoleInstance;
    private static HashMap<String, Vector<Bitmap>> data;
    private static HashMap<String, Product> details;
    private static ArrayList<Pair<Product, Vector<Bitmap>>> cart;
    private static ImageChangedListner listner;
    private CartHolder(){
        data=new HashMap<>();
        cart = new ArrayList<>();
        details = new HashMap<>();
    }  //private constructor.

    public static CartHolder getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new CartHolder();
            listner = null;
        }

        return sSoleInstance;
    }

    public void setOnImageChangedListner(ImageChangedListner mlistner) {
        listner = mlistner;
    }

    public ArrayList<Pair<Product, Vector<Bitmap>>> getCart() {
        return cart;
    }

    public void addToCart(String key) {
        Pair<Product, Vector<Bitmap>> temp = new Pair<>(details.get(key), new Vector<>(data.get(key)));
        cart.add(temp);
        data.remove(key);
    }

    public Product getDetails(String key) {
        return details.get(key);
    }

    public Product addDetails(String key, Product product) {
        return details.put(key, product);
    }

    public void addImage(String key, Bitmap image) {
        if (data.containsKey(key)){
            boolean added = !data.get(key).contains(image);
            if (added) {
                data.get(key).add(image);
                int size = data.get(key).size();
                listner.onImageAdded(image, size);
            } else {
                listner.alreadyPresent(image);
            }
        } else {
            Vector<Bitmap> set = new Vector<>();
                set.add(image);
                data.put(key,set);
            int size = data.get(key).size();
            listner.onImageAdded(image, size);
        }
    }

    public boolean isImagePresent(String key, Bitmap image) {
        return data.containsKey(key) && data.get(key).contains(image);
    }

    public void removeImage(String key, Bitmap image) {
        int size = 0;
        if (isImagePresent(key,image)){
            data.get(key).remove(image);
            if (data.containsKey(key))
                size = data.get(key).size();
            listner.onImageDeleted(image, size);
        }

    }

    public Bitmap getImage(String key, int index) {
        return data.get(key).get(index);
    }

    public void setImage(String key, int index, Bitmap img) {
        data.get(key).set(index, img);
        listner.alreadyPresent(img);
    }

    public int getSize(String key) {
        if (data.containsKey(key))
            return data.get(key).size();
        else
            return 0;
    }

    public Vector<Bitmap> getAllImages(String key) {
        if (data.containsKey(key)) {
            return new Vector<>(data.get(key));
        } else {
            return new Vector<>();
        }
    }


    public interface ImageChangedListner {
        void onImageAdded(Bitmap img, int size);

        void onImageDeleted(Bitmap img, int size);

        void alreadyPresent(Bitmap img);
    }
}