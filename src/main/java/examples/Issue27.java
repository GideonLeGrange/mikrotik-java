package examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;
import me.legrange.mikrotik.ResultListener;

/**
 * Example to show that different character sets may work some times.
 *
 * @author gideon
 */
public class Issue27 extends Example {

    public static void main(String... args) throws Exception {
        Issue27 ex = new Issue27();
        ex.connect();
        ex.con.setTimeout(2000);
        ex.test2();
        ex.test1();
        Thread.sleep(60000);
        for (String tag : ex.tags) {
            ex.con.cancel(tag);
        }
        ex.disconnect();
    }

    private void test1() throws MikrotikApiException {
        String tag = con.execute("/interface/pppoe-server/server/listen return .id,.dead,disabled,service-name,interface,max-mtu,max-mru,mrru,default-profile,authentication", new ResultListener() {
            @Override
            public void receive(Map<String, String> result) {
                System.out.println(result);
            }

            @Override
            public void error(MikrotikApiException ex) {
                System.out.println(ex);
            }

            @Override
            public void completed() {
            }
        });
       tags.add(tag);
    }

    private void test2() throws MikrotikApiException {
       String tag = con.execute("/interface/listen where type=l2tp-in or type=l2tp-out or type=ovpn-in or type=ovpn-out or type=ppp-in or type=ppp-out or type=pppoe-in or type=pppoe-out or type=pptp-in or type=pptp-out or type=sstp-in or type=sstp-out return .id,.dead,.nextid,disabled,dynamic,running,slave,name,type,comment", new ResultListener() {
            @Override
            public void receive(Map<String, String> result) {
                System.out.println(result);
            }

            @Override
            public void error(MikrotikApiException ex) {
             System.out.println(ex);
            }

            @Override
            public void completed() {
            }
        });
       tags.add(tag);
    }
    
     List<String> tags = new ArrayList<>();

}
