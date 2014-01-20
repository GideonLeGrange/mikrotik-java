package me.legrange.mikrotik.impl;

import me.legrange.mikrotik.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * The Mikrotik API connection implementation. This is the class used to connect
 * to a remote Mikrotik and send commands to it.
 *
 * @author GideonLeGrange
 */
public final class ApiConnectionImpl extends ApiConnection {

    /**
     * Create a new API connection to the give device on the supplied port
     *
     * @param host The host to which to connect.
     * @param port The TCP port to use.
     * @return The ApiConnection
     */
    public static ApiConnection connect(String host, int port, boolean secure) throws ApiConnectionException {
        ApiConnectionImpl con = new ApiConnectionImpl();
        con.open(host, port, secure);
        return con;
    }

    /**
     * Check the state of connection.
     *
     * @return if connection is established to router it returns true.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Disconnect from the remote API
     */
    public void disconnect() throws ApiConnectionException {
        if (!connected) {
            throw new ApiConnectionException(("Not/no longer connected to remote Mikrotik"));
        }
        connected = false;
        reader.interrupt();
        try {
            sock.close();
        } catch (IOException ex) {
            throw new ApiConnectionException(String.format("Error closing socket: %s", ex.getMessage()), ex);
        }
    }

    /**
     * Log in to the remote router.
     *
     * @param username - username of the user on the router
     * @param password - password for the user
     */
    public void login(String username, String password) throws MikrotikApiException, InterruptedException {
        List<Map<String, String>> list = execute("/login");
        Map<String, String> res = list.get(0);
        String hash = res.get("ret");
        String chal = Util.hexStrToStr("00") + new String(makePass(password)) + Util.hexStrToStr(hash);
        chal = Util.hashMD5(chal);
        execute("/login name=" + username + " response=00" + chal);
    }

    /**
     * execute a command and return a list of results.
     *
     * @param cmd Command to execute
     * @return The list of results
     */
    public List<Map<String, String>> execute(String cmd) throws MikrotikApiException {
        return execute(Parser.parse(cmd));
    }

    /**
     * execute a command and attach a result listener to receive it's results.
     *
     * @param cmd Command to execute
     * @param lis ResultListener that will receive the results
     * @return A command object that can be used to cancel the command.
     * @throws MikrotikApiException
     */
    public String execute(String cmd, ResultListener lis) throws MikrotikApiException {
        return execute(Parser.parse(cmd), lis);
    }

    /**
     * cancel a command
     */
    public void cancel(String tag) throws MikrotikApiException {
        execute(String.format("/cancel tag=%s", tag));
    }

    private List<Map<String, String>> execute(Command cmd) throws MikrotikApiException {
        SyncListener l = new SyncListener();
        execute(cmd, l);
        return l.getResults();
    }

    private String execute(Command cmd, ResultListener lis) throws MikrotikApiException {
        String tag = nextTag();
        cmd.setTag(tag);
        listeners.put(tag, lis);
        try {
            Util.write(cmd, out);
        } catch (UnsupportedEncodingException ex) {
            throw new ApiDataException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new ApiConnectionException(ex.getMessage(), ex);
        }
        return tag;
    }

    private ApiConnectionImpl() {
    }

    /**
     * Start the API. Connects to the Mikrotik without using encryption
     */
    private void open(String host, int port) throws ApiConnectionException {
        open(host, port, false);
    }

    /**
     * Start the API. Connects to the Mikrotik
     */
    private void open(String host, int port, boolean secure) throws ApiConnectionException {
        try {
            InetAddress ia = InetAddress.getByName(host);
            if (secure) {
                sock = openSSLSocket(ia, port);
            } else {
                sock = new Socket(ia, port);
            }
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
            connected = true;
            reader = new Reader();
            reader.setDaemon(true);
            reader.start();
            processor = new Processor();
            processor.setDaemon(true);
            processor.start();
        } catch (UnknownHostException ex) {
            connected = false;
            throw new ApiConnectionException(String.format("Unknown host '%s'", host), ex);
        } catch (IOException ex) {
            connected = false;
            throw new ApiConnectionException(String.format("Error connecting to %s:%d : %s", host, port, ex.getMessage()), ex);
        }
    }

    /**
     * open and configure a SSL socket.
     */
    private Socket openSSLSocket(InetAddress ia, int port) throws IOException {
        SSLSocket ssl = (SSLSocket) SSLSocketFactory.getDefault().createSocket(ia, port);
        List<String> cs = new LinkedList<String>();
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

    private char[] makePass(String pass) {
        if (true) {
            return pass.toCharArray();
        }
        char[] res = new char[pass.length() + 1];
        System.arraycopy(pass.toCharArray(), 0, res, 0, pass.length());
        res[pass.length()] = 0x0;
        return res;
    }

    private synchronized String nextTag() {
        _tag++;
        return Integer.toHexString(_tag);
    }
    private static final int DEFAULT_PORT = 8728;
    private Socket sock = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private boolean connected = false;
    private Reader reader;
    private Processor processor;
    private final Map<String, ResultListener> listeners = new HashMap<String, ResultListener>();
    private Integer _tag = 0;

    /**
     * thread to read data from the socket and process it into Strings
     */
    private class Reader extends Thread {

        private String take() throws ApiConnectionException, ApiDataException {
            Object val = null;
            try {
                val = queue.take();
            } catch (InterruptedException ex) {
                throw new ApiConnectionException("Interrupted while reading data from queue.", ex);
            }
            if (val instanceof ApiConnectionException) {
                throw (ApiConnectionException) val;
            } else if (val instanceof ApiDataException) {
                throw (ApiDataException) val;
            }
            return (String) val;
        }

        private boolean isEmpty() {
            return queue.isEmpty();
        }

        @Override
        public void run() {
            while (connected) {
                try {
                    String s = Util.decode(in);
                    if (s != null) {
                        queue.put(s);
                    }
                } catch (ApiDataException ex) {
                    try {
                        queue.put(ex);
                    } catch (InterruptedException ex2) {
                    }
                } catch (ApiConnectionException ex) {
                } catch (InterruptedException ex1) {
                }
            }
        }
        private LinkedBlockingQueue queue = new LinkedBlockingQueue(40);
    }

    /**
     * Thread to take the received strings and process it into Result objects
     */
    private class Processor extends Thread {

        @Override
        public void run() {
            while (connected) {
                Response res;
                try {
                    res = unpack();
                } catch (ApiCommandException ex) {
                    String tag = ex.getTag();
                    if (tag != null) {
                        res = new Error(tag, ex.getMessage());
                    } else {
                        continue;
                    }
                } catch (MikrotikApiException ex) {
                    ex.printStackTrace();
                    continue;
                }
                ResultListener l = listeners.get(res.getTag());
                if (l != null) {
                    if (res instanceof Result) {
                        l.receive((Result) res);
                    } else {
                        if (res instanceof Done) {
                            listeners.remove(res.getTag());
                        }
                        if (l instanceof ResponseListener) {
                            ResponseListener rl = (ResponseListener) l;
                            if (res instanceof Done) {
                                if (rl instanceof SyncListener) {
                                    ((SyncListener) rl).completed((Done) res);
                                } else {
                                    rl.completed();
                                }
                            } else if (res instanceof Error) {
                                rl.error(new ApiCommandException((Error) res));
                            }
                        }
                    }
                }
            }
        }

        private void nextLine() throws ApiConnectionException, ApiDataException {
            if (lines.isEmpty()) {
                String block = reader.take();
                String parts[] = block.split("\n");
                lines.addAll(Arrays.asList(parts));
            }
            line = lines.remove(0);
        }

        private boolean hasNextLine() {
            return !lines.isEmpty() || !reader.isEmpty();
        }

        private Response unpack() throws MikrotikApiException {
            if (line == null) {
                nextLine();
            }
            if (line.equals("!re")) {
                return unpackRe();
            } else if (line.equals("!done")) {
                return unpackDone();
            } else if (line.equals("!trap")) {
                return unpackError();
            } else if (line.equals("!halt")) {
                return unpackError();
            } else {
                throw new ApiDataException(String.format("Unexpected line '%s'", line));
            }
        }

        private Result unpackRe() throws ApiDataException, ApiConnectionException {
            nextLine();
            int l = 0;
            Result res = new Result();
            while (!line.startsWith(("!"))) {
                l++;
                if (line.startsWith(("="))) {
                    String parts[] = line.split("=", 3);
                    if (parts.length == 3) {
                        res.put(parts[1], parts[2]);
                    } else {
                        throw new ApiDataException(String.format("Malformed line '%s'", line));
                    }
                } else if (line.startsWith(".tag=")) {
                    String parts[] = line.split("=", 2);
                    if (parts.length == 2) {
                        res.setTag(parts[1]);
                    }
                } else {
                    throw new ApiDataException(String.format("Unexpected line '%s'", line));
                }
                if (hasNextLine()) {
                    nextLine();
                } else {
                    line = null;
                    break;
                }
            }
            return res;
        }

        private Done unpackDone() throws MikrotikApiException {
            Done done = new Done(null);
            if (hasNextLine()) {
                nextLine();

                while (!line.startsWith("!")) {
                    if (line.startsWith(".tag=")) {
                        String parts[] = line.split("=", 2);
                        if (parts.length == 2) {
                            done.setTag(parts[1]);
                        }
                    } else if (line.startsWith(("=ret"))) {
                        String parts[] = line.split("=", 3);
                        if (parts.length == 3) {
                            done.setHash(parts[2]);
                        } else {
                            throw new ApiDataException(String.format("Malformed line '%s'", line));
                        }
                    }
                    if (hasNextLine()) {
                        nextLine();
                    } else {
                        line = null;
                        break;
                    }
                }
            }
            return done;
        }

        private Error unpackError() throws MikrotikApiException {
            nextLine();
            Error err = new Error();
            if (hasNextLine()) {
                while (!line.startsWith("!")) {
                    if (line.startsWith(".tag=")) {
                        String parts[] = line.split("=", 2);
                        if (parts.length == 2) {
                            err.setTag(parts[1]);
                        }
                    } else if (line.startsWith("=message=")) {
                        err.setMessage(line.split("=", 3)[2]);
                    }
                    if (hasNextLine()) {
                        nextLine();
                    } else {
                        line = null;
                        break;
                    }
                }
            }
            return err;
        }

        private void queue(Response res) {
            String tag = res.getTag();
            if (tag != null) {
                ResultListener rl = listeners.get(tag);
                if (rl != null) {
                    if (res instanceof Result) {
                        rl.receive((Result) res);
                    } else {
                        //          rl.error((Error)res);
                    }
                }
            }
        }
        private List<String> lines = new LinkedList<String>();
        private String line;
    }

    private class SyncListener implements ResponseListener {

        public synchronized void error(MikrotikApiException ex) {
            this.err = ex;
            notify();
        }

        public synchronized void completed() {
            notify();
        }

        synchronized void completed(Done done) {
            if (done.getHash() != null) {
                Result res = new Result();
                res.put("ret", done.getHash());
                results.add(res);
            }
            notify();
        }

        public void receive(Map<String, String> result) {
            results.add(result);
        }

        private List<Map<String, String>> getResults() throws MikrotikApiException {
            try {
                synchronized (this) { // don't wait if we already have a result. 
                    if ((err == null) && results.isEmpty()) {
                        wait();
                    }
                }
            } catch (InterruptedException ex) {
                throw new ApiConnectionException(ex.getMessage(), ex);
            }
            if (err != null) {
                throw err;
            }
            return results;
        }
        private List<Map<String, String>> results = new LinkedList<Map<String, String>>();
        private MikrotikApiException err;
    }
}
