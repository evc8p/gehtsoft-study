package com.evch.rrm.lombok;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class Configuration {
    @Getter @Setter
    private long id;
    @Getter @Setter
    private String address;
    @Getter
    private int counter = 1000;

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.setId(100L).setAddress("dfs");
        System.out.println("id = " + configuration.getId());
        System.out.println("address = " + configuration.getAddress());
        System.out.println("counter = " + configuration.getCounter());
    }
}
