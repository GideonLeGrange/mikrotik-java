package me.legrange.mikrotik;

/**
 * Exception thrown if the Api experiences a connection problem
 * @author GideonLeGrange
 */
public class ApiConnectionException extends MikrotikApiException {

    public ApiConnectionException(String msg) {
        super(msg);
    }

    public ApiConnectionException(String msg, Throwable err) {
        super(msg, err);
    }
    
    
    
}
