package com.evch.rrm.lombok;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Car {
    private final String brand;
    private final String model;
    @NonNull
    private String color;
    private String body;

    public static void main(String[] args) {
        // One constructor is created for all final and @NonNull fields
        Car car = new Car("asdf", "sadf", "green");
        Car car1 = new Car("asdf", "sadf", null); //compiler: color is marked non-null but is null
    }
}
