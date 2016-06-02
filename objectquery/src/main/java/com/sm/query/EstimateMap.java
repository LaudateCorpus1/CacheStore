package com.sm.query;

/**
 * interface, mostly will be backup by sorted RemoteStore or LocalStoreImpl
 * Created by mhsieh on 2/1/16.
 */
public interface EstimateMap {
    /**
     * return value of key in double, either perecentage or total population
     * @param key
     * @return
     */
    public Double get(String key);
    public Object getObject(String key);

}
