package com.evch.rrm.lombok;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

//can be used here
//@Setter
//@Getter
public class Product {
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter(AccessLevel.NONE)
    private String category;
    @Getter
    @Setter(AccessLevel.NONE)
    private int price;
    @Getter
    @Setter(AccessLevel.NONE)
    boolean test = false;

    public static void main(String[] args) {
        Product product = new Product();
        //setters
        product.setName("sadfsf");
        product.setCategory("sadfsf");
        //getters
        product.getName();
        product.getPrice();
        product.isTest();
    }
}
