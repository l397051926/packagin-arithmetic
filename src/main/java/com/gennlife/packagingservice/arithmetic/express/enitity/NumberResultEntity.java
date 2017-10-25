package com.gennlife.packagingservice.arithmetic.express.enitity;

import java.util.List;

/**
 * Created by Chenjinfeng on 2017/10/20.
 */
public class NumberResultEntity<T> {
    private double value;
    private List<FindIndexModel<T>> list;

    public NumberResultEntity() {
    }

    public NumberResultEntity(List<FindIndexModel<T>> list, double value) {
        this.list = list;
        this.value = formatDouble(value);
    }

    public static double formatDouble(double value) {
        return (double) Math.round(value * 1000) / 1000;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = formatDouble(value);
    }

    public List<FindIndexModel<T>> getList() {
        return list;
    }

    public void setList(List<FindIndexModel<T>> list) {
        this.list = list;
    }
}
