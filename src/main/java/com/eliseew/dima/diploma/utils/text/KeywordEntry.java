package com.eliseew.dima.diploma.utils.text;

public class KeywordEntry {
    private String key1;
    private String key2;
    private String position;
    private int wordCount = 1;

    public KeywordEntry(String key1, String position) {
        this.key1 = key1;
        this.position = position;
    }

    public KeywordEntry(String key1, String key2, String position) {
        this.key1 = key1;
        this.key2 = key2;
        this.position = position;
    }

    public KeywordEntry(String key1, String position, int wordCount) {
        this.key1 = key1;
        this.position = position;
        this.wordCount = wordCount;
    }

    public String getKey1() { return key1; }
    public String getKey2() { return key2; }
    public String getPosition() { return position; }
    public int getWordCount() { return wordCount; }

    @Override
    public String toString() {
        return "KeywordEntry{" +
                "key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                ", position='" + position + '\'' +
                ", wordCount=" + wordCount +
                '}';
    }
}
