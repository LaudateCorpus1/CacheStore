package com.sm.test;

import java.io.Serializable;

/**
 * Created by mhsieh on 12/31/14.
 */
public class Person implements Serializable {
    String name;
    int age;
    double income;
    boolean male;
    Address address;

    //public Person(){}

    public Person(String name, int age, double income, boolean male, Address address) {
        this.name = name;
        this.age = age;
        this.income = income;
        this.male = male;
        this.address = address;
    }

    public static class Address implements Serializable {
        Street street;
        int zip;
        String city;

        public Address(Street street, int zip, String city) {
            this.street = street;
            this.zip = zip;
            this.city = city;
        }

        public Address(){}

        @Override
        public String toString() {
            return "Address{" +
                    "street='" + street + '\'' +
                    ", zip=" + zip +
                    ", city='" + city + '\'' +
                    '}';
        }
    }

    public static class Street implements Serializable{
        int no;
        String road;

        public Street(int no, String road) {
            this.no = no;
            this.road = road;
        }
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getIncome() {
        return income;
    }

    public boolean isMale() {
        return male;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", income=" + income +
                ", male=" + male +
                ", address=" + address +
                '}';
    }
}
