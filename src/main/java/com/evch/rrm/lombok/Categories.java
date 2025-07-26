package com.evch.rrm.lombok;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Categories {
    @SneakyThrows({IOException.class})
    public static void main(String[] args) {
        Categories categories = new Categories();
        categories.method();
    }

    @SneakyThrows
    private void method() throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(
                "src/main/java/com/evch/rrm/benchmarks/results/console1000000_10000.txt"));
        bf.readLine();
    }
}
