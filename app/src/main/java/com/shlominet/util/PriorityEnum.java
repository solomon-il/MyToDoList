package com.shlominet.util;

public enum PriorityEnum {
    URGENT,
    MEDIUM,
    LOW;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
