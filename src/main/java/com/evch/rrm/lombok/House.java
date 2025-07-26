package com.evch.rrm.lombok;

import lombok.Builder;

@Builder // = @AllArgsConstructor(access = AccessLevel.PACKAGE)
public class House {
    private String address;
    private int rooms;
    private final int area;
    private int price;

    public static void main(String[] args) {
        HouseBuilder houseBuilder = new HouseBuilder();
        System.out.println(houseBuilder);
        House newHouse = houseBuilder.address("sdfsdf").rooms(5).area(250).price(1000).build();
        System.out.println(newHouse);
    }

    @Override
    public String toString() {
        return "House{" +
                "address='" + address + '\'' +
                ", rooms=" + rooms +
                ", area=" + area +
                ", price=" + price +
                '}';
    }
}
