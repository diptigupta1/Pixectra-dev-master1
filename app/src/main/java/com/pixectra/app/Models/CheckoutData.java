package com.pixectra.app.Models;

/**
 * Created by XCODER on 3/6/2018.
 */

public class CheckoutData {
    User user;
    Address address;
    Price price;

    public CheckoutData() {
        user = new User();
        address = new Address();
        price = new Price();
    }

    @Override
    public String toString() {
        return "CheckoutData{" +
                "user=" + user +
                ", address=" + address +
                ", price=" + price +
                '}';
    }
}
