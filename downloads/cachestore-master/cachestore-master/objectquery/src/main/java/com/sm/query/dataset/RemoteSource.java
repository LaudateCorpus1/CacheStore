package com.sm.query.dataset;

/**
 * Created on 4/26/16.
 */
public class RemoteSource {
    public final String name;
    public final Long size;

    public RemoteSource(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemoteSource that = (RemoteSource) o;

        if (!name.equals(that.name)) return false;
        return size.equals(that.size);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
