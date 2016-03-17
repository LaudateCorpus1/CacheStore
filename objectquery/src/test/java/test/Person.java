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

package test;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Person implements Serializable {
    String name;
    long age;
    double income;
    boolean male;
    Address address;
    List<String> list;
    List<Address> addressList;
    List<List<String>> nestLists;
    Set<Set<String>> setAdd;
    Set<Short> setShort;

    //public Person(){}

    public Person(String name, long age, double income, boolean male, Address address) {
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
        List<String> lst;
        Set<String> set;

        public Address(Street street, int zip, String city) {
            this.street = street;
            this.zip = zip;
            this.city = city;
        }

        public Set<String> getSet() {
            return set;
        }

        public void setSet(Set<String> set) {
            this.set = set;
        }

        public Address(){}

        public List<String> getLst() {
            return lst;
        }

        public void setLst(List<String> lst) {
            this.lst = lst;
        }

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

    public long getAge() {
        return age;
    }

    public double getIncome() {
        return income;
    }

    public boolean isMale() {
        return male;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<List<String>> getNestLists() {
        return nestLists;
    }

    public void setNestLists(List<List<String>> nestLists) {
        this.nestLists = nestLists;
    }

    public Set<Set<String>> getSetAdd() {
        return setAdd;
    }

    public void setSetAdd(Set<Set<String>> setAdd) {
        this.setAdd = setAdd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(long age) {
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

    public Set<Short> getSetShort() {
        return setShort;
    }

    public void setSetShort(Set<Short> setShort) {
        this.setShort = setShort;
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
