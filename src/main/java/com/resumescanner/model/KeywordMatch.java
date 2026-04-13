package com.resumescanner.model;

public class KeywordMatch {
    private String keyword;
    private int frequency;
    private String context;

    public KeywordMatch() {}

    public KeywordMatch(String keyword, int frequency, String context) {
        this.keyword = keyword;
        this.frequency = frequency;
        this.context = context;
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}
