CacheStore Deploy Cluster
=


What is CacheStore?
==

CacheStore is a largely scalable and high performance storage 
plugin for Project Voldemort distributed system that includes 
user plugins to determine block size, concurrent compaction 
and backup without downtime, and user plugins to purge data 
without downtime. Key features include stored procedures, 
triggers, queries, scans, and multiple gets and puts.

Download
==
CacheStore Cluster Repository: https://github.com/viant/CacheStore-deploy-cluster
CacheStore Cluster .zip: https://github.com/viant/CacheStore/downloads/cachestore-deploy-cluster-1.0.2.zip
CacheStore Cluster .tar.gz: https://github.com/viant/CacheStore/downloads/cachestore-deploy-cluster-1.0.2.tar.gz
CacheStore Cluster .rpm: https://github.com/viant/CacheStore/downloads/cachestore-deploy-cluster-1.0.2-1.noarch.rpm

Prerequisites
==

- Operating Systems:
    Linux, Mac, Windows *Not Fully Tested*
- Java (1.7.0_09-icedtea): https://java.com/en/download/

Quickstart Guides
==

For this guide, we will assume that CacheStore is installed into the /opt folder.

Local 2 Node Cluster
-------------------------------

In terminal:

    - cd /opt/cachestore-deploy-cluster/
    - sudo sh bin/StartCacheCluster.sh -configPath "config/node_examples/localNode1"
    
In a second terminal:

    - cd /opt/cachestore-deploy-cluster/
    - sudo sh bin/StartCacheCluster.sh -configPath "config/node_examples/localNode2"
    
In a third terminal:

    - cd /opt/cachestore-deploy-cluster/
    - sudo sh bin/CachestoreShell.sh
    - c1 = openCluster("localhost:6172", "test")
        ===> com.sm.store.client.ClusterClient@xxxxxxxx
    - c2 = openCluster("localhost:6182", "test")
        ===> com.sm.store.client.ClusterClient@xxxxxxxx
    - getv(c1, 1)
        ===> this
    - getv(c2, 2)
        ===> is
    - getv(c1, 3)
        ===> a
    - getv(c2, 4)
        ===> test
    - put(c1, 5, "working")
        ===> null
    - getv(c2, 5)
        ===> working
    - :exit

*Note* These commands are used based on the provided test store.xml and test data files.


2 Node Cluster on Separate Servers
-------------------------------

- Deploy CacheStore-deploy-cluster on two different devices
- Configure clusters.xml and node.properties in config/node_examples/node1
    and config/node_examples/node2
    
In terminal 1:

    - cd /opt/cachestore-deploy-cluster/
    - sudo sh bin/StartCacheCluster.sh -configPath "config/node_examples/node1"
    
In terminal 2:

    - cd /opt/cachestore-deploy-cluster/
    - sudo sh bin/StartCacheCluster.sh -configPath "config/node_examples/node2"

In a third terminal:

    - cd /opt/cachestore-deploy-cluster/
    - sudo sh bin/CachestoreShell.sh
    - c1 = openCluster("server1:port1", "test")
        ===> com.sm.store.client.ClusterClient@xxxxxxxx
    - c2 = openCluster("server2:port2", "test")
        ===> com.sm.store.client.ClusterClient@xxxxxxxx
        
The two nodes and CacheStore Shell should now be set up.

*Note* These commands are used based on the provided test store.xml and test0.* data files.

Documentation
==

Additional information and documentation can be 
found at: http://viant.github.io/CacheStore/
 

Latest Version
==

The latest version can be found
at: https://github.com/viant/CacheStore


Known Bugs & Issues
==

- Scripts may not run as expected in Windows
- Stopping a multinode cluster that is running on the same device with 
	StopCachestore.sh may not work as expected