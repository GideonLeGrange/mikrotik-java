package examples;

import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;
import me.legrange.mikrotik.ResultListener;

/**
 * Example 4: Asynchronous results. Run a command and receive results for it asynchronously with a ResultListener
 *
 * @author gideon
 */
public class Example4 extends Example {

    public static void main(String... args) throws Exception {
        Example4 ex = new Example4();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException, InterruptedException {
       String id = con.execute("/interface/wireless/monitor .id=wlan1", new ResultListener() {
           private int prev = 0;

           public void receive(Map<String, String> result) {
               int val = Integer.parseInt(result.get("signal-strength"));
               String sym = (val == prev) ? " " : ((val < prev) ? "-" : "+");
               System.out.printf("%d %s\n", val, sym);
               prev = val;
           }


        });
       // let it run for 60 seconds 
       Thread.sleep(60000);
       con.cancel(id);
    }
}
