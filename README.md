# mikrotik-java

A Java client library implementation for the Mikrotik RouterOS API. 

This project provides a Java client to manipulate Mikrotik routers using the remote API. Simple things must be easy to do, and complex things must be possible.

## Versions

**The current stable version is 3.0.3**

Version 3.0.3 fixes a problem with the encoding of non-English characters when sending commands to RouterOS. See [#40](https://github.com/GideonLeGrange/mikrotik-java/issues/40) for more details. 

### Changes in version 3.0:

Version 3.0 addresses the problems the API had around TLS encryption. The way secure connections are implemented is changed so that the user has complete control over the creation of TLS sockets. To this end:
* A new method, `connect(SocketFactory fact, String host, int port, int timeout)`, was added to allow for better user control over sockets and especially encryption.
* The `connectTLS()` API methods were removed. 
* Most of the overloaded `connect()` methods were removed. 
* Added a pre-built `jar` file to the downloads.

Further changes include:
* The previously deprecated `disconnect()` method is removed. 

#### Version 2.x

Version 2.2 is the last version 2.x release and will be supported for a limited time. 

#### Version 1.x

Version 1 is considered *obsolete* and will no longer be supported or patched. 

## Getting the API

Maven users can use the artifact from Maven Central with this dependency:

```xml
<dependency>
  <groupId>me.legrange</groupId>
  <artifactId>mikrotik</artifactId>
  <version>3.0.3</version>
</dependency>
```

You can also download the pre-built jar file, or a zip or tar.gz file with the source for the latest release [here](https://github.com/GideonLeGrange/mikrotik-java/releases/latest)

## Contributing

I welcome contributions, be it bug fixes or other improvements. If you fix or change something, please submit a pull request. If you want to report a bug, please open an issue. General questions are also welcome. 

# Using the API

How to use the API is best illustrated by examples. 

These examples should illustrate how to use this library. Please note that I assume that the user is proficient in Java and understands the Mikrotik command line syntax. The command line syntax gives you an indication of what commands you can pass, but the RouterOS API used by this library does not support everyting. 

Some things to consider when debugging your API calls are:
* The RouterOS API does not support auto-completion. You need to write out command and parameter names. For example, you can't say `/ip/hotspot/user/add name=john add=10.0.0.1`, you need to write out `address`.
* You need to quote values with spaces in. You can't say `name=Joe Blogs`, you need to use `name="Joe Blogs"`
* Exceptions with a root cause of `ApiCommandException` are errors received from the remote RouterOS device and contain the error message received. 

## Opening a connection
Here is a simple example: Connect to a router and reboot it. 

```java
ApiConnection con = ApiConnection.connect("10.0.1.1"); // connect to router
con.login("admin","password"); // log in to router
con.execute("/system/reboot"); // execute a command
con.close(); // disconnect from router
```
The above example shows a easy way of creating an unencrypted connection using the default API port and timeout, which is useful for development and testing.

### TLS encryption

For production environments, encrypting API traffic is recommended. To do this you need to open a TLS connection to the router by passing an instance of the `SocketFactory` you wish to use to construct the TLS socket to the API:

```java
ApiConnection con = ApiConnection.connect(SSLSocketFactory.getDefault(), "10.0.1.1", ApiConnection.DEFAULT_TLS_PORT, ApiConnection.DEFAULT_CONNECTION_TIMEOUT);
```

Above an instance of the default SSL socket factory is passed to the API. This will work as long as the router's certificate has been added to the local key store.  Besides allowing the user to specify the socket factory, the above method also gives full control over the TCP Port and connection timeout. 

RouterOS also supports anonymous TLS. An example showing how to create a socket factory for anonymous TLS is `AnonymousSocketFactory` in the examples directory.

### Connection timeouts

By default, the API will generate an exception if it cannot connect to the specified router. This can take place immediately (typically if the OS returns a 'Connection refused' error), but can also take up to 60 seconds if the router host is firewalled or if there are other network problems. This 60 seconds is the 'default connection timeout' an can be overridded by passing the preferred timeout to the APi as last parameter in a ```connect()``` call. For example:

```java
   ApiConnection con = ApiConnection.connect(SSLSocketFactory.getDefault(), "10.0.1.1", ApiConnection.DEFAULT_TLS_PORT, 2000); // connect to router on the default API port and fail in 2 seconds
```	

### Constants
Some constants are provided in `ApiConnection` to make it easier for users to construct connections with default ports and timeouts:

Constant | Use for | Value 
---------|---------|------
DEFAULT_PORT | Default TCP `port` value for unencrypyted connections | 8728
DEFAULT_TLS_PORT | Default TCP `port` value for encrypyted connections | 8729
DEFAULT_CONNECTION_TIMEOUT | Default connection `timeout` value (ms) | 60000

### Try with resources 

The API can also be used in a "try with resources" statement which will ensure that the connection is closed:

```java
        try (ApiConnection con = ApiConnection.connect(SocketFactory.getDefault(), Config.HOST, ApiConnection.DEFAULT_PORT, 2000)) {
            con.login(Config.USERNAME, Config.PASSWORD);
            con.execute("/user/add name=eric");
        }
```

In following examples the connection, login and disconnection code will not be repeated. In all cases it is assumed that an `ApiConnection` has been established, `login()` has been called, and that the connection is called `con`.

## Reading data 

A simple example that returns a result - Print all interfaces:


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

The same query, but we only want certain result fields names: Print all interfaces of type 'vlan' and return just their name:

```java
List<Map<String, String>> rs = con.execute("/interface/print where type=vlan return name");
```

## Writing data 

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

## Asynchronous commands

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

The `ResultListener` interface has three methods the user needs to implement:
* `receive()` is called to receive results produced by the router from the API. 
* `error()` is called when an exception is raised based on a 'trap' received from the router or another (typically connection) problem.
* `completed()` is called when the router has indicated that the command has completed or has been cancelled. 

The above command will run and send results asynchronously as they become available, until it is canceled. The command (identified by the unique String returned) is canceled like this:

```java
con.cancel(tag);
```

From version 2.0.0 of the API the error() and completed() methods are part of the ResultListener interface. 

## Command timeouts

Command timeouts can be used to make sure that synchronous commands either return or fail within a specific time. Command timeouts are separate from the connection timeout used in ```connect()```, and can be set using ```setTimeout()```. Here is an example:

```java
ApiConnection con = ApiConnection.connect("10.0.1.1"); // connect to router
con.setTimeout(5000); // set command timeout to 5 seconds
con.login("admin","password"); // log in to router
con.execute("/system/reboot"); // execute a command
``` 
 	
It is important to note that command timeouts can be set before ```login()``` is called, and can therefore influence the behaviour of login. 

The default command timeout, if none is set by the user, is 60 seconds. 

# References

The RouterOS API is documented here: http://wiki.mikrotik.com/wiki/Manual:API

# Licence

This library is released under the Apache 2.0 licence. See the [Licence.md](Licence.md) file

