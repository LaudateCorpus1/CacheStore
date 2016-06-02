package com.sm.connector;

import voldemort.store.cachestore.Key;
import voldemort.utils.Pair;

import java.io.IOException;

/**
 * Created by mhsieh on 2/13/16.
 */
public interface MRIterator {
    public Pair<Key,byte[]> next() throws IOException;
    public boolean hasNext() ;
    public void setCurrent(int current);
    public boolean isEnd(int last);
    public String getFilename();
    public void close() throws IOException;
}
