package com.resumescanner.model;

import java.util.List;
import java.util.Map;

public class ScanResult {
    private String fileName;
    private int totalKeywords;
    private int matchedCount;
    private int missingCount;
    private double matchPercentage;
    private List<KeywordMatch> matchedKeywords;
    private List<String> missingKeywords;
    private Map<String, Integer> keywordFrequency;
    private String resumeWordCount;

    // Constructors
    public ScanResult() {}

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getTotalKeywords() { return totalKeywords; }
    public void setTotalKeywords(int totalKeywords) { this.totalKeywords = totalKeywords; }

    public int getMatchedCount() { return matchedCount; }
    public void setMatchedCount(int matchedCount) { this.matchedCount = matchedCount; }

    public int getMissingCount() { return missingCount; }
    public void setMissingCount(int missingCount) { this.missingCount = missingCount; }

    public double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(double matchPercentage) { this.matchPercentage = matchPercentage; }

    public List<KeywordMatch> getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(List<KeywordMatch> matchedKeywords) { this.matchedKeywords = matchedKeywords; }

    public List<String> getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }

    public Map<String, Integer> getKeywordFrequency() { return keywordFrequency; }
    public void setKeywordFrequency(Map<String, Integer> keywordFrequency) { this.keywordFrequency = keywordFrequency; }

    public String getResumeWordCount() { return resumeWordCount; }
    public void setResumeWordCount(String resumeWordCount) { this.resumeWordCount = resumeWordCount; }
}
