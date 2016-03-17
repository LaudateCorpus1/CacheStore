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
import java.util.Arrays;
import java.util.List;

public class ClusterNodes implements Serializable {

    public final static int Http_Port = 8100;
    public final static String COMMA =",";
    private short id;
//    private int port;
//    private int adminPort;
    private int httpPort;
    private String[] serverArray;
    private int[] partitionArray;

   public ClusterNodes(short id, String[] serverArray, int[] partitionArray) {
        this.id = id;
//        this.port = port;
//        this.adminPort = port+1 ;
        this.httpPort = Http_Port;
        this.serverArray = serverArray;
        this.partitionArray = partitionArray;
        validate();
    }

    private int[] convertInt(List<Integer> list) {
        int[] toReturn = new int[list.size()];
        for ( int i=0 ; i < list.size() ; i++) {
            toReturn[i] = list.get(i);
        }
        return toReturn;
    }


    public void validate() {
        int error = 0;
        StringBuffer sb = new StringBuffer();
        if ( serverArray == null || serverArray.length == 0) {
            error++;
            sb.append("serverArray is "+ (serverArray == null ? "null ":" "));
            if ( serverArray != null ) sb.append(" length == 0 ");
        }
        if ( partitionArray == null || partitionArray.length == 0 ) {
            error++;
            sb.append(" partitionArray is"+( partitionArray == null ? " null ":" "));
            if ( partitionArray == null ) sb.append(" length == 0");
        }
        if ( error > 0 ) throw new RuntimeException(sb.toString());
    }






    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public int getAdminPort() {
//        return adminPort;
//    }
//
//    public void setAdminPort(int adminPort) {
//        this.adminPort = adminPort;
//    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String[] getServerArray() {
        return serverArray;
    }

    public void setServerArray(String[] serverArray) {
        this.serverArray = serverArray;
    }

    public int[] getPartitionArray() {
        return partitionArray;
    }

    public void setPartitionArray(int[] partitionArray) {
        this.partitionArray = partitionArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusterNodes that = (ClusterNodes) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }


    @Override
    public String toString() {
        return "ClusterNodes{" +
                "id=" + id +
                ", httpPort=" + httpPort +
                ", serverArray=" + (serverArray == null ? null : Arrays.asList(serverArray)) +
                ", partitionArray=" + partitionArray +
                '}';
    }
}
