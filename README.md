mikrotik-java
=============

A Java client library implementation for the Mikrotik RouterOS API. 

This project provides a Java client to manipulate Mikrotik routers using the remote API. Simple things must be easy to do, and complex things must be possible.

Versions
--------

The current stable version is 1.0. The master branch is on version 1.1, which is the currently being tested and will be released soon. 

Version 1.1 will add: 
* TLS (SSL) support to encrypt API traffic. 


Examples
========

Here is a simple example: Connect to a router and reboot it. 

```java
ApiConnection con = ApiConnection.connect("10.0.1.1"); // connect to router
con.login("admin","password"); // log in to router
con.execute("/system/reboot"); // execute a command
con.disconnect(); // disconnect from router
```

A simple example that returns a result: Print all interfaces.


```java
List<Map<String, String>> rs = con.execute("/interface/print");
for (Map<String,String> r : rs) {
  System.out.println(r);
}
```

The same query, but with the results filtered: Print all interfaces of type 'vlan'.

```java
List<Map<String, String>> rs = con.execute("/interface/print where type=vlan");
```

The same query, but we only want certain result fields names Print all interfaces of type 'vlan' and return their name.

```java
List<Map<String, String>> rs = con.execute("/interface/print where type=vlan return name");
```

We can run asynchrynous commands:

This example shows how to run '/interface wireless monitor' and have the result sent to a listener object, which prints it

```java
String tag = con.execute("/interface/wireless/monitor .id=wlan1 return signal-to-noise", 
      new ResultListener() {

            public void receive(Map<String, String> result) {
                System.out.println(result);
            }
            
        }
  );
```

The above command will run and send results asynchrynously as they become available, until it is canceled. The command (identified by the unique String retruned) 
 is cancelled like this:

```java
con.cancel(tag);
```

References
==========

The RouterOS API is documented here: http://wiki.mikrotik.com/wiki/Manual:API

Licence
=======

This library is released under the Apache 2.0 licence. See the Licence.md file
