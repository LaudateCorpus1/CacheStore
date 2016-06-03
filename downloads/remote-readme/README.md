CacheStore Deploy
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
CacheStore Remote Repository: https://github.com/viant/CacheStore-deploy
CacheStore Remote .zip: https://github.com/viant/CacheStore/downloads/cachestore-deploy-1.0.2.zip
CacheStore Remote .tar.gz: https://github.com/viant/CacheStore/downloads/cachestore-deploy-1.0.2.tar.gz
CacheStore Remote .rpm: https://github.com/viant/CacheStore/downloads/cachestore-deploy-1.0.2-1.noarch.rpm

Prerequisites
==

- Operating Systems:
    Linux, Mac, Windows *Not Fully Tested*
- Java (1.7.0_09-icedtea): https://java.com/en/download/

Quickstart
==

For this guide, we will assume that CacheStore is installed into the /opt folder.

In terminal:

	- cd cachestore-deploy/
	- sudo sh bin/StartCachestore.sh

In a new terminal:

	- cd cachestore-deploy/
	- sudo sh bin/CachestoreShell.sh
	- c = open("localhost:4200", "test")
			===> url localhost:4200 store test timeout 6000 nio false
	- listKey(c)
			===> [1 INT, 2 INT, 3 INT, 4 INT]
	- getv(c, 1)
			===> this
	- getv(c, 2)
			===> is
	- getv(c, 3)
			===> a
	- getv(c, 4)
			===> test
	- :exit
	- sh bin/StopCachestore.sh

*Note* These commands are used based on the provided test store.xml and test0.* data files.

Documentation
==

Additional information and documentation can be 
found at: http://viant.github.io/CacheStore/
 

Latest Version
==

The latest version 1.1.0 can be found
at: https://github.com/viant/CacheStore


Known Bugs & Issues
==

- Scripts may not run as expected in Windows