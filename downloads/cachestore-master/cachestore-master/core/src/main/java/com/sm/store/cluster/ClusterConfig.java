/*
 *
 *  * Copyright 2012-2015 Viant.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package com.sm.store.cluster;

import com.sm.store.RemoteConfig;

import java.io.Serializable;
import java.util.List;

public class ClusterConfig  implements Serializable {
    private List<ClusterNodes> clusterNodesList;
    private RemoteConfig remoteConfig;
    private int clusterNo;

    public ClusterConfig(List<ClusterNodes> clusterNodesList, RemoteConfig remoteConfig, int clusterNo) {
        this.clusterNodesList = clusterNodesList;
        this.remoteConfig = remoteConfig;
        this.clusterNo = clusterNo;
    }

    public List<ClusterNodes> getClusterNodesList() {
        return clusterNodesList;
    }

    public RemoteConfig getRemoteConfig() {
        return remoteConfig;
    }


    public int getClusterNo() {
        return clusterNo;
    }

    public void setClusterNo(int clusterNo) {
        this.clusterNo = clusterNo;
    }
}
