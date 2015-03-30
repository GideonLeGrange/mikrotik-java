package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 9: Test special characters in usernames
 *
 * @author gideon
 */
public class Example9 extends Example {

    public static void main(String... args) throws Exception {
        Example9 ex = new Example9();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException, InterruptedException {
       List<Map<String, String>> res = con.execute("/user/add name=çãáõ");
        for (Map<String, String> r : res) {
            System.out.println(r);
        } 
//        con.execute("/ip/firewall/filter/add chain=forward hotspot=!auth protocol=tcp src-port=8000-8084");
    }
}
