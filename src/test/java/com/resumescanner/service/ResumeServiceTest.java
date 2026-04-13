package com.resumescanner.service;

import com.resumescanner.model.ScanResult;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResumeServiceTest {

    private final ResumeService resumeService = new ResumeService();

    @Test
    void testScanResume_TextFile() throws IOException {
        String content = "Experienced Java developer with Spring Boot and Microservices experience.";
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.txt", "text/plain", content.getBytes()
        );
        String keywords = "Java, Spring Boot, Python";

        ScanResult result = resumeService.scanResume(file, keywords);

        assertEquals("resume.txt", result.getFileName());
        assertEquals(3, result.getTotalKeywords());
        assertEquals(2, result.getMatchedCount());
        assertEquals(1, result.getMissingCount());
        assertEquals(66.67, result.getMatchPercentage());
        assertTrue(result.getMissingKeywords().contains("Python"));
    }

    @Test
    void testExtractKeywordsFromJobDescription() throws IOException {
        String jd = "We are looking for a Senior Java Developer with expertise in Spring Boot, " +
                     "REST APIs, and Cloud computing (AWS). The ideal candidate has experience in Microservices.";
        
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.txt", "text/plain", "Java and Spring Boot developer.".getBytes()
        );

        ScanResult result = resumeService.scanResumeWithJobDescription(file, jd);

        assertTrue(result.getMatchedCount() > 0);
        // "Java" should be extracted and matched
        assertTrue(result.getMatchedKeywords().stream().anyMatch(m -> m.getKeyword().equalsIgnoreCase("java")));
    }

    @Test
    void testWordCount() throws IOException {
        String content = "One two three four five.";
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.txt", "text/plain", content.getBytes()
        );
        
        ScanResult result = resumeService.scanResume(file, "One");
        assertEquals("5 words", result.getResumeWordCount());
    }
}
