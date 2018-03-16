package com.pixectra.app.Models;

/**
 * Created by XCODER on 3/6/2018.
 */

public class Price {
    int base, cgst, sgst, discount, total;

    public Price(int base, int cgst, int sgst, int discount, int total) {
        this.base = base;
        this.cgst = cgst;
        this.sgst = sgst;
        this.discount = discount;
        this.total = total;
    }

    public Price() {

    }

    public int getBase() {
        return base;
    }

    public int getCgst() {
        return cgst;
    }

    public int getSgst() {
        return sgst;
    }

    public int getDiscount() {
        return discount;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Price{" +
                "base=" + base +
                ", cgst=" + cgst +
                ", sgst=" + sgst +
                ", discount=" + discount +
                ", total=" + total +
                '}';
    }
}
