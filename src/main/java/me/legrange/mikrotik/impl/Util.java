package me.legrange.mikrotik.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import me.legrange.mikrotik.ApiConnectionException;

/**
 * Utility library that handles the low level encoding required by the Mikrotik
 * API.
 *
 * @author GideonLeGrange. Possibly some code by janisk left.
 */
final class Util {

    /**
     * write a command to the output stream
     */
    static void write(Command cmd, OutputStream out) throws UnsupportedEncodingException, IOException {
        encode(cmd.getCommand(), out);
        for (Parameter param : cmd.getParameters()) {
            encode(String.format("=%s=%s", param.getName(), param.hasValue() ? param.getValue() : ""), out);
        }
        String tag = cmd.getTag();
        if ((tag != null) && !tag.equals("")) {
            encode(String.format(".tag=%s", tag), out);
        }
        List<String> props = cmd.getProperties();
        if (!props.isEmpty()) {
            StringBuilder buf = new StringBuilder("=.proplist=");
            for (int i = 0; i < props.size(); ++i) {
                if (i > 0) {
                    buf.append(",");
                }
                buf.append(props.get(i));
            }
            encode(buf.toString(), out);
        }
        for (String query : cmd.getQueries()) {
            encode(query, out);
        }
        out.write(0);
    }

    /**
     * decode bytes from an input stream of Mikrotik protocol sentences into
     * text
     */
    static String decode(InputStream in) throws ApiDataException, ApiConnectionException {
        StringBuilder res = new StringBuilder();
        decode(in, res);
        return res.toString();
    }

    /**
     * decode bytes from an input stream into Mikrotik protocol sentences
     */
    private static void decode(InputStream in, StringBuilder result) throws ApiDataException, ApiConnectionException {
        try {
            int len = readLen(in);
            if (len > 0) {
                byte buf[] = new byte[len];
                for (int i = 0; i < len; ++i) {
                    int c = in.read();
                    if (c < 0) {
                        throw new ApiDataException("Truncated data. Expected to read more bytes");
                    }
                    buf[i] = (byte) (c & 0xFF);
                }
                String res = new String(buf);
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(res);
                decode(in, result);
            }
        } catch (IOException ex) {
            throw new ApiConnectionException(ex.getMessage(), ex);
        }
    }

    /**
     * makes MD5 hash of string for use with RouterOS API
     *
     * @param s - variable to make hash from
     * @return - the md5 hash
     */
    static String hashMD5(String s) throws ApiDataException {
        MessageDigest algorithm = null;
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new ApiDataException("Cannot find MD5 digest algorithm");
        }
        byte[] defaultBytes = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            defaultBytes[i] = (byte) (0xFF & s.charAt(i));
        }
        algorithm.reset();
        algorithm.update(defaultBytes);
        byte messageDigest[] = algorithm.digest();
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * converts hex value string to normal strint for use with RouterOS API
     *
     * @param s - hex string to convert to
     * @return - converted string.
     */
    static String hexStrToStr(String s) {
        String ret = "";
        for (int i = 0; i < s.length(); i += 2) {
            ret += (char) Integer.parseInt(s.substring(i, i + 2), 16);
        }
        return ret;
    }

    /**
     * encode text using Mikrotik's encoding scheme and write it to an output
     * stream.
     */
    private static void encode(String word, OutputStream out) throws UnsupportedEncodingException, IOException {
//        byte bytes[] = word.getBytes("US-ASCII");
        byte bytes[] = word.getBytes("UTF-8");
        int len = bytes.length;
        if (len < 0x80) {
            out.write(len);
        } else if (len < 0x4000) {
            len = len | 0x8000;
            out.write(len >> 8);
            out.write(len);
        } else if (len < 0x20000) {
            len = len | 0xC00000;
            out.write(len >> 16);
            out.write(len >> 8);
            out.write(len);
        } else if (len < 0x10000000) {
            len = len | 0xE0000000;
            out.write(len >> 24);
            out.write(len >> 16);
            out.write(len >> 8);
            out.write(len);
        } else {
            out.write(0xF0);
            out.write(len >> 24);
            out.write(len >> 16);
            out.write(len >> 8);
            out.write(len);
        }
        out.write(bytes);
    }

    /**
     * read length bytes from stream and return length of coming word
     */
    private static int readLen(InputStream in) throws IOException {
        int c = in.read();
        if (c > 0) {
            if ((c & 0x80) == 0) {
            } else if ((c & 0xC0) == 0x80) {
                c = c & ~0xC0;
                c = (c << 8) | in.read();
            } else if ((c & 0xE0) == 0xC0) {
                c = c & ~0xE0;
                c = (c << 8) | in.read();
                c = (c << 8) | in.read();
            } else if ((c & 0xF0) == 0xE0) {
                c = c & ~0xF0;
                c = (c << 8) | in.read();
                c = (c << 8) | in.read();
                c = (c << 8) | in.read();
            } else if ((c & 0xF8) == 0xF0) {
                c = in.read();
                c = (c << 8) | in.read();
                c = (c << 8) | in.read();
                c = (c << 8) | in.read();
                c = (c << 8) | in.read();
            }
        }
        return c;
    }
}