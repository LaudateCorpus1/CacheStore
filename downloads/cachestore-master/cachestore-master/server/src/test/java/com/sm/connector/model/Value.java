package com.sm.connector.model;

import java.io.Serializable;

/**
 * Created by mhsieh on 12/20/15.
 */
public class Value implements Serializable {
    int count ;
    int total ;
    double totalDbl;

    public Value() { }

    public Value(int count, int total, double totalDbl) {
        this.count = count;
        this.total = total;
        this.totalDbl = totalDbl;
    }

    public void count() {
        count++;
    }
    public  void addCount(int count) {
        this.count += count;
    }

    public void addTotal(int value) {
        total += value;
    }

    public void addDbl(double dvalue) {
        totalDbl += dvalue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getTotalDbl() {
        return totalDbl;
    }

    public void setTotalDbl(double totalDbl) {
        this.totalDbl = totalDbl;
    }

    @Override
    public String toString() {
        return "Value{" +
                "count=" + count +
                ", total=" + total +
                ", totalDbl=" + totalDbl +
                '}';
    }
}
