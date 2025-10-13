package com.evch.rrm.lombok;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "offf", access = AccessLevel.PRIVATE)
public class Book {
    private String title;
    private String author;
    private int pages;
    private int price;
    private final int price1;

    public static void main(String[] args) {
        Book book = new Book("dsf", "sdf", 1, 1, 1);
        Book book1 = Book.offf("dsf", "sdf", 1, 1, 1);
        String author1 = book1.author;
    }

    public static void main() {
        main();
    }
}

class Book1 {
    public static void main(String[] args) {
//        Book book = new Book("dsf", "sdf", 1, 1, 1); // Class Book has only private constructors
        Book.main(); // Class Book has only private constructors
    }
}
