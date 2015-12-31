package examples;

import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 1: A very simple command: Reboot the remote router
 * @author gideon
 */
public class SimpleCommand extends Example {
    
        public static void main(String...args) throws Exception {
            SimpleCommand ex = new SimpleCommand();
            ex.connect();
            ex.test();
            ex.disconnect();
        }
        
        private void test() throws MikrotikApiException {
            con.execute("/system/reboot");
        }

   
}
