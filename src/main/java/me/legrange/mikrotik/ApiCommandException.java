package me.legrange.mikrotik;

/**
 * Thrown when the Mikrotik returns an error when receiving our command.
 * @author GideonLeGrange
 */
public class ApiCommandException extends MikrotikApiException {


    /** return the tag associated with this exception, if there is one */
    public String getTag() {
        return tag;
    }

     ApiCommandException(String msg) {
        super(msg);
    }

     ApiCommandException(String msg, Throwable err) {
        super(msg, err);
    }


    ApiCommandException(Error err) {
        super(err.getMessage());
        tag = err.getTag();
    }
    
    private String tag = null;
    
}
