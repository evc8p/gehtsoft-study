package com.evch.rrm.lombok;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Arrays;

@Data(staticConstructor = "of")
public class Person {
    @Setter(AccessLevel.PRIVATE)
    protected String name; // this field is private now
    protected final String name1; // this field is private now
    private int age;
    private String email;

    public static void main(String[] args) {
        Person person = new Person("hi");
        Person person1 = Person.of("hi");
        //setters
        person.setName("Martin");
        person.setAge(35);
        person.setEmail("martin@lombok.email");
        //getters
        person.getName();
        person.getName1();
        person.getAge();
        person.getEmail();
        //toString()
        System.out.println(person);
        //hashCode()
        person.hashCode();
        //equals()
        person.equals(person);


        Arrays.stream(person.getClass().getMethods()).toList().forEach(System.out::println);
    }
}

class PersonView {
    public static void main(String[] args) {
        Person person = Person.of("hi");
        person.setEmail("dsfsdf");
        person.setAge(20);
        person.getEmail();
        person.getName();
        person.getName1();
        person.getAge();
        person.hashCode();
        person.equals(person);

    }
}
