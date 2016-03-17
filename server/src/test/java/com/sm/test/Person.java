/*
 *
 *
 * Copyright 2012-2015 Viant.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 *
 */

package com.sm.test;

import java.io.Serializable;

public class Person implements Serializable {
    String name;
    int age;
    double income;
    boolean male;
    Address address;

    //public Person(){}

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

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

        public Street getStreet() {
            return street;
        }

        public void setStreet(Street street) {
            this.street = street;
        }

        public int getZip() {
            return zip;
        }

        public void setZip(int zip) {
            this.zip = zip;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
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

        public Street(){}

        public String getRoad() {
            return road;
        }

        public void setRoad(String road) {
            this.road = road;
        }

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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
