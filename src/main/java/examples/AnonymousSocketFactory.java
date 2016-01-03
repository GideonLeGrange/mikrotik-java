/*
 * Copyright 2016 gideon.
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @since 3.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class AnonymousSocketFactory extends SocketFactory {

    @Override
    public Socket createSocket() throws IOException {
        return fixSocket((SSLSocket) SSLSocketFactory.getDefault().createSocket());
    }
    
    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return fixSocket((SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return fixSocket((SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return fixSocket((SSLSocket) SSLSocketFactory.getDefault().createSocket(address, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return fixSocket((SSLSocket) SSLSocketFactory.getDefault().createSocket(address, port, localAddress, localPort));
    }
    
    private Socket fixSocket(SSLSocket ssl) {
        List<String> cs = new LinkedList<>();
        // not happy with this code. Without it, SSL throws a "Remote host closed connection during handshake" error
        // caused by a "SSL peer shut down incorrectly" error
        for (String s : ssl.getSupportedCipherSuites()) {
            if (s.startsWith("TLS_DH_anon")) {
                cs.add(s);
            }
        }
        ssl.setEnabledCipherSuites(cs.toArray(new String[]{}));
        return ssl; 
    }

    public static SocketFactory getDefault() {
        if (fact == null) {
            fact = new AnonymousSocketFactory();
        }
        return fact;
    }

    private AnonymousSocketFactory() {

    }

    private static AnonymousSocketFactory fact;

}
