package me.legrange.mikrotik;

/**
 * Thrown if the API cannot log in 
 * @author GideonLeGrange
 */
public class ApiLoginException extends MikrotikApiException {

    ApiLoginException(String msg) {
        super(msg);
    }

    ApiLoginException(String msg, Throwable err) {
        super(msg, err);
    }
   
    
}
