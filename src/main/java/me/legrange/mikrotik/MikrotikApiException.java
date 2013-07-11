package me.legrange.mikrotik;

/**
 * Thrown by the Mikrotik API to indicate errors
 *
 * @author GideonLeGrange
 */
public class MikrotikApiException extends Exception {

    MikrotikApiException(String msg) {
        super(msg);
    }

    MikrotikApiException(String msg, Throwable err) {
        super(msg, err);
    }
}
