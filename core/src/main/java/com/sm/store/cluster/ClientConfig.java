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

package com.sm.store.cluster;

import java.io.Serializable;

public class ClientConfig implements Serializable {
    private int maxTry ;
    private long timeOut;
    private long iterationInterval;
    private boolean lazyLoad ;
    private int freq = 1;


    public ClientConfig(int maxTry, long timeOut, long iterationInterval, boolean lazyLoad, int freq) {
        this.maxTry = maxTry;
        this.timeOut = timeOut;
        this.iterationInterval = iterationInterval;
        this.lazyLoad = lazyLoad;
        this.freq = freq;
    }

    public ClientConfig(int maxTry, long timeOut, long iterationInterval, boolean lazyLoad) {
        this( maxTry, timeOut, iterationInterval, lazyLoad, 1);
    }

    public boolean isLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public int getMaxTry() {
        return maxTry;
    }

    public void setMaxTry(int maxTry) {
        this.maxTry = maxTry;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public long getIterationInterval() {
        return iterationInterval;
    }

    public void setIterationInterval(long iterationInterval) {
        this.iterationInterval = iterationInterval;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }
}
