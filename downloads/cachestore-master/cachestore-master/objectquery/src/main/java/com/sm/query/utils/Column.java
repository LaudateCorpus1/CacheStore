package com.sm.query.utils;

import java.io.Serializable;

import static com.sm.query.Result.Type;

/**
 * Created by mhsieh on 3/8/16.
 */
public class Column implements Serializable {
    String name;
    Type type;
    int len;
    int offset;

    public Column(String name, Type type, int offset) {
        this.name = name;
        this.type = type;
        this.offset = offset;
        init();
    }

    private void init() {
        switch (type) {
            case INT:
                len = 4;
                break;
            case BYTE:
                len = 1;
                break;
            case SHORT:
                len = 2;
                break;
            case LONG:
                len = 8;
                break;
            case DOUBLE:
                len = 8;
                break;
            case FLOAT:
                len = 4;
                break;
            case STRING:
                len = 0;
                break;
            default:
                len = 0;
        }
    }

    public int getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getLen() {
        return len;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Column)) return false;

        Column column = (Column) o;

        if (len != column.len) return false;
        if (!name.equals(column.name)) return false;
        if (type != column.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        return result;
    }
}
