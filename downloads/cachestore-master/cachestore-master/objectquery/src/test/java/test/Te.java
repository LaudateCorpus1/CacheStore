package test;

import java.io.Serializable;

/**
 * Created by mhsieh on 4/26/16.
 */
public class Te implements Serializable {
    String name;
    int ek;
    boolean bl;

    public Te(String name, int in, boolean bl) {
        this.name = name;
        this.ek = in;
        this.bl = bl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEk() {
        return ek;
    }

    public void setEk(int ek) {
        this.ek = ek;
    }

    public boolean isBl() {
        return bl;
    }

    public void setBl(boolean bl) {
        this.bl = bl;
    }

    @Override
    public String toString() {
        return "T1{" +
                "name='" + name + '\'' +
                ", ek=" + ek +
                ", bl=" + bl +
                '}';
    }
}
