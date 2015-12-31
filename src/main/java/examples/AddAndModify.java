package examples;

import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 6: Create and modify object
 *
 * @author gideon
 */
public class AddAndModify extends Example {

    public static void main(String... args) throws Exception {
        AddAndModify ex = new AddAndModify();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException, InterruptedException {
        System.out.println("Creating interface gre1");
        con.execute("/interface/gre/add remote-address=1.2.3.4 name=gre1 keepalive=10 comment='test comment'");
        System.out.println("Adding firewall rule for interface gre1");
        con.execute("/ip/firewall/filter/add action=drop chain=forward in-interface=gre1 protocol=udp dst-port=78,80");//,80,32");
        System.out.println("Waiting 10 seconds");
        Thread.sleep(10000); // 10 seconds for the user to look on the router to see the interface with /interface gre print
        System.out.println("Changing IP for interface gre1");
        con.execute("/interface/gre/set remote-address=172.16.1.1 .id=gre1");
        // now look again and the IP has changed
    }
}
