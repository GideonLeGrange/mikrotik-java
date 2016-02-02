/*
 * Copyright 2015 gideon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example: Open an Anonymous TLS connection
 *
 * @author gideon
 */
public class ConnectTLSAnonymous {

    public static void main(String... args) throws Exception {
        ConnectTLSAnonymous ex = new ConnectTLSAnonymous();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException {
        List<Map<String, String>> results = con.execute("/interface/print");
        for (Map<String, String> result : results) {
            System.out.println(result);
        }
    }

    protected void connect() throws Exception {
        con = ApiConnection.connect(AnonymousSocketFactory.getDefault(), Config.HOST, ApiConnection.DEFAULT_TLS_PORT, ApiConnection.DEFAULT_CONNECTION_TIMEOUT);
        con.login(Config.USERNAME, Config.PASSWORD);
    }

    protected void disconnect() throws Exception {
        con.close();
    }

    private ApiConnection con;
}
