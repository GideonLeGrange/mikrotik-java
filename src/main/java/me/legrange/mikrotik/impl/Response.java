package me.legrange.mikrotik.impl;

/**
 * Super type of possible API responses
 *
 * @author GideonLeGrange
 */
abstract class Response {

    public String getTag() {
        return tag;
    }
    
    @Override
    public String toString() {
        return String.format("%s: tag=%s", getClass().getSimpleName(), tag);
    }
    
    void setTag(String tag) { 
        this.tag = tag;
    }
    
    protected Response(String tag) {
        this.tag = tag;
    }
    
    private  String tag;
}
