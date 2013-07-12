package examples;

import java.util.List;
import me.legrange.mikrotik.MikrotikApiException;
import me.legrange.mikrotik.Result;

/**
 * Example 3: Queries. Print all interfaces of a certain type. 
 *
 * @author gideon
 */
public class Example3 extends Example {

    public static void main(String... args) throws Exception {
        Example3 ex = new Example3();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException {
        List<Result> results =  con.execute("/interface/print where type=ether");
        for (Result result : results) {
            System.out.println(result);
        }
    }
}
