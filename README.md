mikrotik-java
=============

A Java client library implementation for the Mikrotik RouterOS API. 

This project provides a Java client to manipulate Mikrotik routers using the remote API. Simple things must be easy to do, and complex things must be possible.

Versions
--------

This library uses [semantic versioning](http://semver.org/)

The current stable version is 2.0.2. 

* Fixed bug #13 - processor thread wasn't being stopped on disconnect(), causing non-exit of application in some cases. 

Previous 2.0 versions:

* 2.0.1 fixed parsing of !=, < and > operators in a where clause. 
* 2.0.0 changed ResultListener to receive errors and completion notifications. This version is not backwards compatible with version 1.x. 

Previous 1.1 versions:

* 1.1.5 fixed even more comamnd line parsing bugs.
* 1.1.4 fixed command line parsing bugs #7 and #8.
* 1.1.3 fixed severe command line parsing bugs, #3, #4 and #5.
* 1.1.2 added support for handling multi-line results, like for example /file print. 
* 1.1 added TLS (SSL) support to encrypt API traffic. 

The latest development version is 2.0.0 which is available using the 2.0.0 branch. 


Getting the API
---------------

I recommend using the Maven artifact from Maven Central using this dependency:

```xml
<dependency>
  <groupId>me.legrange</groupId>
  <artifactId>mikrotik</artifactId>
  <version>2.0.2</version>
</dependency>
```
You can also clone or fork the repository, or download the source as a zip or tar.gz from [Releases](https://github.com/GideonLeGrange/mikrotik-java/releases)

Contributing
------------

I welcome contributions, be it bug fixes or other improvements. If you fix or change something, please submit a pull request. If you want to report a bug, please open an issue. 


Examples
========
These examples should illustrate how to use this library. Please note that I assume that the user is proficient in Java and understands the Mikrotik command line syntax.

Here is a simple example: Connect to a router and reboot it. 

```java
ApiConnection con = ApiConnection.connect("10.0.1.1"); // connect to router
con.login("admin","password"); // log in to router
con.execute("/system/reboot"); // execute a command
con.disconnect(); // disconnect from router
```
If your router does not use the default API port, the port can be specified when connecting: 

```java 
ApiConnection con = ApiConnection.connect("10.0.1.1", 1337); // connect to router on port 1337
```

To open an encrypted (TLS) connection is as simple, assuming the default API-SSL port is used:

```java
ApiConnection con = ApiConnection.connectTLS("10.0.1.1"); // connect to router using TLS
```

#### Notes about TLS: 
* Currently only anonymous TLS is supported, not certificates. 
* There is a compatibility problem between the current versions of RouterOS supporting API over TLS and the Java Cryptography Extension (JCE) in Java 7 and earlier. TLS encryption works in Java 8 and later. For more information, feel free to contact me. 



In following examples the connection, login and disconnection code will not be repeated. 

Reading data 
------------

A simple example that returns a result: Print all interfaces.


```java
List<Map<String, String>> rs = con.execute("/interface/print");
for (Map<String,String> r : rs) {
  System.out.println(r);
}
```

Results are returned as a list of maps of String key/value pairs. The reason for this is that a command can return multiple results, which have multpile variables. For example, to print the names of all the interfaces returned in the command above, do:

```java
for (Map<String, String> map : rs) { 
  System.out.println(map.get("name"));
}
```

### Filtering results

The same query, but with the results filtered: Print all interfaces of type 'vlan'.

```java
List<Map<String, String>> rs = con.execute("/interface/print where type=vlan");
```

### Selecting returned fields

The same query, but we only want certain result fields names: Print all interfaces of type 'vlan' and return just their name.

```java
List<Map<String, String>> rs = con.execute("/interface/print where type=vlan return name");
```

Writing data 
------------

Creating, modifying and deleting configuration objects is of course possible.

### Creating an object 

This example shows how to create a new GRE interface: 

```java
con.execute("/interface/gre/add remote-address=192.168.1.1 name=gre1 keepalive=10");
```

### Modify an existing object

Change the IP address in the object created by the above example:

```java
con.execute("/interface/gre/set .id=gre1 remote-address=10.0.1.1"); 
```

### Remove an existing object

And now remove the object:

```java
con.execute("/interface/gre/remove .id=gre1"); 
```

Asynchronous commands
---------------------

We can run some commands asynchronously in order to continue receiving updates:

This example shows how to run '/interface wireless monitor' and have the result sent to a listener object, which prints it:

```java
String tag = con.execute("/interface/wireless/monitor .id=wlan1 return signal-to-noise", 
      new ResultListener() {

            public void receive(Map<String, String> result) {
                System.out.println(result);
            }

           public void error(MikrotikApiException e) {
               System.out.println("An error occurred: " + e.getMessage());
           }

           public void completed() {
                System.out.println("Asynchronous command has finished"); 
           }
            
        }
  );
```

The above command will run and send results asynchronously as they become available, until it is canceled. The command (identified by the unique String returned) is canceled like this:

```java
con.cancel(tag);
```

From version 2.0.0 of the API the error() and completed() methods are part of the ResultListener interface. 

References
==========

The RouterOS API is documented here: http://wiki.mikrotik.com/wiki/Manual:API

Licence
=======

This library is released under the Apache 2.0 licence. See the [Licence.md](Licence.md) file

