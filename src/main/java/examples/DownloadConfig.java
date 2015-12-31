package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 7: Dump your complete config
 *
 * @author gideon
 */
public class DownloadConfig extends Example {

    public static void main(String... args) throws Exception {
        DownloadConfig ex = new DownloadConfig();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException, InterruptedException {
        con.execute("/export file=conf");
        List<Map<String, String>> res = con.execute("/file/print detail where name=conf.rsc");
        con.execute("/file/remove conf.rsc");
        String text = res.get(0).get("contents");
        for (String line : text.split("\r")) {
            System.out.println(line);
        }
    }
}
