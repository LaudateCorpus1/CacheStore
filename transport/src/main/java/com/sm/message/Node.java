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

package com.sm.message;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;

public final class Node {
    // nodeId need to be unique
    private long nodeId;
    // url use for connection
    private String url;
    // type node
    private NodeType type;
    private long since = System.currentTimeMillis();
    // for future use
    private short cluster;
    // type (1) + cluster (2) + nodeId id (8) + timeStamp
    public static final int FIRST_PART = 1 + 2 + 8 + 8;

    public Node(long nodeId, String url, NodeType type, short cluster) {
        this.nodeId = nodeId;
        this.url = url;
        if ( url == null ) this.url ="localhost:7100";
        this.type = type;
        this.cluster = cluster;
    }

    public Node(int node, String url, NodeType type) {
        this(node, url, type, (short) 0);
    }


    public static String getHost(String url) {
        String[] names = url.split(":");
        if ( names.length !=2 ) throw new RuntimeException("Wrong format of url "+url+ " missing : ");
        return names[0];
    }

    @Override
    public String toString() {
        return "nodeId "+ nodeId +" type "+type+" url "+url+" cluster "+ cluster +" since "+ new Date( since).toString();
    }

    public String getInfo() {
        return "nodeId "+ nodeId +" cluster "+ cluster ;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public short getCluster() {
        return cluster;
    }

    public void setCluster(short cluster) {
        this.cluster = cluster;
    }

    public void setSince(long since) {
        this.since = since;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        else {
            if ( ! (obj instanceof Node) ) return false;
            else {
                Node n = (Node) obj;
                if ( n.getNodeId() == nodeId && n.getCluster() == cluster &&
                        n.getType().equals( type) && n.getUrl().equals( url))
                    return true;
                else
                    return false;
            }
        }
    }

    @Override
    public int hashCode() {
        int result = (int) nodeId;
        result = 31 * result + (int) cluster;
        return result;
    }

    public byte[] toBytes() throws UnsupportedEncodingException {

        byte[] u = url.getBytes("UTF-8");
        byte[] bytes = new byte[ FIRST_PART + u.length];
        int i = 0 ;
        bytes[i] = type.value ;
        // cluster
        System.arraycopy(ByteBuffer.allocate(2).putShort(cluster).array(),0 ,bytes ,i+1 ,2);
        // nodeId
        System.arraycopy( ByteBuffer.allocate(8).putLong(nodeId).array(),0 , bytes , i+3, 8);
        // since
        System.arraycopy(ByteBuffer.allocate(8).putLong( since).array(),0 ,bytes, i+11, 8);
        System.arraycopy(u, 0, bytes, i+FIRST_PART , u.length);
        return bytes;
    }

    public static Node toNode(byte[] bytes) throws UnsupportedEncodingException {
        ByteBuffer buf = ByteBuffer.wrap( bytes);
        NodeType type = NodeType.getNodeType(buf.get());
        short p = buf.getShort();
        long n = buf.getLong();
        long s = buf.getLong();
        byte[] b = new byte[ bytes.length - FIRST_PART ];
        buf.get(b);
        String u = new String( b, "UTF-8") ;
        Node node = new Node(n, u, type,  p);
        node.setSince( s);
        return node ;
    }
}

