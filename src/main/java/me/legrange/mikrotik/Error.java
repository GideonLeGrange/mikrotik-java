package me.legrange.mikrotik;

/**
 * Used to encapsulate API error information. We need to pass both the message and the tag (if one was used). 
 * @author GideonLeGrange
 */
public class Error extends Response {

     Error(String tag, String message) {
        super(tag);
        this.message = message;
    }

    Error() {
        super(null);
    }

    String getMessage() {
        return message;
    }
    void setMessage(String message) {
        this.message = message;
    }
    
    private String message;

    
}
