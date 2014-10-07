    package me.legrange.mikrotik.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A result from an API command. 
 * @author GideonLeGrange
 */
class Result extends Response implements Map<String, String> {

    public String get(String key) {
        return map.get(key);
    }
    
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("tag=%s, data=%s", getTag(), map);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return map.get(o);
    }

    @Override
    public String put(String k, String v) {
        return map.put(k, v);
    }

    @Override
    public String remove(Object o) {
        return map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<String> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    Result() {
        super(null);
        this.map = new HashMap<>();
    }
   
    private final Map<String, String> map;

}
