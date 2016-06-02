package com.sm.query.utils;

import com.sm.query.Result.Type;

import java.lang.reflect.Field;

/**
 * Created by mhsieh on 12/30/14.
 */
public class FieldInfo {
    private Field field;
    private Type type;
    private String className;

    public FieldInfo(Field field, Type type, String className) {
        this.field = field;
        this.type = type;
        this.className = className;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
