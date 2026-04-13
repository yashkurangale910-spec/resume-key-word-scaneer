package com.resumescanner.controller;

import com.resumescanner.model.ScanResult;
import com.resumescanner.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * Scan resume with manually entered keywords
     */
    @PostMapping("/scan")
    public ResponseEntity<?> scanResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("keywords") String keywords) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please upload a resume file."));
            }
            if (keywords == null || keywords.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please provide keywords to scan for."));
            }

            log.info("Scanning resume '{}' ({} bytes) with {} keywords",
                    file.getOriginalFilename(), file.getSize(), keywords.split(",").length);

            ScanResult result = resumeService.scanResume(file, keywords);

            log.info("Scan complete: {}% match ({}/{})",
                    result.getMatchPercentage(), result.getMatchedCount(), result.getTotalKeywords());

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            log.error("IO error scanning resume: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error scanning resume", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred. Please try again."));
        }
    }

    /**
     * Scan resume against a job description (auto-extracts keywords)
     */
    @PostMapping("/scan-jd")
    public ResponseEntity<?> scanWithJobDescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please upload a resume file."));
            }
            if (jobDescription == null || jobDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please provide a job description."));
            }

            log.info("Scanning resume '{}' against job description ({} chars)",
                    file.getOriginalFilename(), jobDescription.length());

            ScanResult result = resumeService.scanResumeWithJobDescription(file, jobDescription);

            log.info("JD scan complete: {}% match ({}/{})",
                    result.getMatchPercentage(), result.getMatchedCount(), result.getTotalKeywords());

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            log.error("IO error during JD scan: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during JD scan", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred. Please try again."));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "message", "Resume Keyword Scanner is running!"));
    }
}
