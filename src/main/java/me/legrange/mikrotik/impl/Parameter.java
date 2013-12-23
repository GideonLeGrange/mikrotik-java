package me.legrange.mikrotik.impl;

/**
 * A command parameter
 *
 * @author GideonLeGrange
 */
class Parameter {

    @Override
    public String toString() {
        if (hasValue()) {
            return String.format("%s=%s", name, value);
        } else {
            return name;
        }
    }

    Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    Parameter(String name) {
        this(name, null);
    }

    boolean hasValue() {
        return value != null;
    }

    String getName() {
        return name;
    }

    String getValue() {
        return value;
    }
    private String name;
    private String value;
}
