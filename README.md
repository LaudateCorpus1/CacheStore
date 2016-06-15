## What is CacheStore?
CacheStore is a key-value hybrid storage system between memory cache and disk. It is high performance, horizontally scalable, and high availability. It was initially developed to support real time auction and high performance ad serving systems.

So why choose CacheStore? Take a look at our website: [CacheStore](http://viant.github.io/CacheStore/)


## Features

* High performance
* Three types of deployment
* Rich API
* Supports sorted and hash map stores
* Groovy command line shell
* Supports plug-ins for heterogeneous storage
* Server side serialization
* Map Reduce
* Currently supports Java and Groovy with other languages coming soon

## Prerequisites

* Java 1.6 or 1.7: https://java.com/en/download/
* Apache Maven 3 or higher: https://maven.apache.org/download.cgi

 
## Documentation

* User Guides:
  * [CacheStore Usage Guide](http://viant.github.io/CacheStore/CacheStore-For-Dummies.html)
  * [Object Query Guide] (http://viant.github.io/CacheStore/Object-Query.html)
  * [CacheStore Groovy Shell Guide] (http://viant.github.io/CacheStore/CacheStore-Shell.html)
* Changelog:
	* [Changelog] (http://viant.github.io/CacheStore/Changelog.html)

## Download

* CacheStore Code Base: https://github.com/viant/CacheStore
* Remote CacheStore Package: https://github.com/viant/CacheStore-deploy
* Cluster CacheStore Package: https://github.com/viant/CacheStore-deploy-cluster

To get CacheStore and its modules into your own project add these dependencies to your project's pom.xml file:

    <dependency>
        <groupId>com.viantinc.cachestore</groupId>
        <artifactId>cachestore-client</artifactId>
        <version>1.6.0</version>
    </dependency>
    <dependency>
        <groupId>com.viantinc.cachestore</groupId>
        <artifactId>cachestore-core</artifactId>
        <version>1.2.5</version>
    </dependency>
    <dependency>
        <groupId>com.viantinc.cachestore</groupId>
        <artifactId>cachestore-server</artifactId>
        <version>1.6.5</version>
    </dependency>
    <dependency>
        <groupId>com.viantinc.cachestore</groupId>
        <artifactId>objectquery</artifactId>
        <version>1.5.8</version>
    </dependency>
    <dependency>
        <groupId>com.viantinc.cachestore</groupId>
        <artifactId>replica</artifactId>
        <version>2.2.3</version>
    </dependency>
    <dependency>
        <groupId>com.viantinc.cachestore</groupId>
        <artifactId>transport</artifactId>
        <version>2.2.3</version>
    </dependency>
    <dependency>
        <groupId>com.viantinc.voldemort</groupId>
        <artifactId>cachestore-storage</artifactId>
        <version>4.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.viantinc</groupId>
        <artifactId>hessian-sm</artifactId>
        <version>4.1.0</version>
    </dependency>

## Latest Version

The latest stable version is CacheStore 1.1.0
CacheStore Code Base: https://github.com/viant/CacheStore

## Copyright and Licensing

The source code is available under the Apache 2.0 license. We are actively looking for contributors. If you have ideas, code, bug reports, or fixes you would like to contribute, please do so. Pull requests will be handled by Mickey Hsieh.

## Credits and Acknowledgements
[CacheStore Creator: Mickey Hsieh]

[Technical Writer: Lester Pi supervised by Mike Yang and Mickey Hsieh] 

[Open-source Admin: Lester Pi supervised by Mike Yang and Mickey Hsieh]

[CacheStore Website: Lester Pi supervised by the Viant Engineering Team ]

[QA/Test: Viant Engineering Team]
