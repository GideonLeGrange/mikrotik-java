package examples;

import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 6: Create and modify object
 *
 * @author gideon
 */
public class Example6 extends Example {

    public static void main(String... args) throws Exception {
        Example6 ex = new Example6();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException, InterruptedException {
        con.execute("/interface/gre/add remote-address=10.0.1.1 name=gre1 keepalive=10");
        Thread.sleep(10000); // 10 seconds for the user to look on the router to see the interface with /interface gre print
        con.execute("/interface/gre/set remote-address=172.16.1.1 .id=gre1");
        // now look again and the IP has changed
    }
}
