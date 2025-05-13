package com.eliseew.dima.diploma.utils;

public class KeywordEntry {private String key1;
    private String key2; // Для случая "между"
    private String position;

    public KeywordEntry(String key1, String key2, String position) {
        this.key1 = key1;
        this.key2 = key2;
        this.position = position;
    }

    public KeywordEntry(String key1, String position) {
        this(key1, null, position);
    }

    public String getKey1() {
        return key1;
    }

    public String getKey2() {
        return key2;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        if ("между".equals(position)) {
            return "Между \"" + key1 + "\" и \"" + key2 + "\"";
        } else {
            return position + " \"" + key1 + "\"";
        }
    }
}