package com.sm.query.utils;

import com.sm.query.Result.Type;

/**
 * Created by mhsieh on 12/30/14.
 */
public class ClassInfo {
    private String className;
    // store only field after process type
    // preprocess for performance tuning
    private FieldInfo[] fieldInfos;
    private Type type;
    public ClassInfo(String className, Type type, FieldInfo[] fieldInfos) {
        super();
        this.className = className;
        this.fieldInfos = fieldInfos;
        this.type = type;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public FieldInfo[] getFieldInfos() {
        return fieldInfos;
    }
    public void setFieldInfos(FieldInfo[] fieldInfos) {
        this.fieldInfos = fieldInfos;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

}
