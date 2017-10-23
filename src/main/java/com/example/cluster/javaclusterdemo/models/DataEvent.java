package com.example.cluster.javaclusterdemo.models;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class DataEvent {
    private final String key;
    private final String value;
    private final long time_created;

    public DataEvent(String key, String value) {
        this.key = key;
        this.value = value;
        this.time_created = System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public long getTime_created() {
        return time_created;
    }
}
