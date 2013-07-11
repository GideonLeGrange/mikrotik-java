package examples;

import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 1: A very simple example: Reboot the remote router
 * @author gideon
 */
public class Example1 extends Example {
    
        public static void main(String...args) throws Exception {
            Example1 ex = new Example1();
            ex.connect();
            ex.test();
            ex.disconnect();
        }
        
        private void test() throws MikrotikApiException {
            con.execute("/system/reboot");
        }

   
}
