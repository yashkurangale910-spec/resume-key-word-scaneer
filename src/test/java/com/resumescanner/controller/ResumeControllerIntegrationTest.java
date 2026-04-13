package com.resumescanner.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResumeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testScanEndpoint() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Full Stack Developer".getBytes()
        );

        mockMvc.perform(multipart("/api/scan")
                .file(file)
                .param("keywords", "Stack, Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchedCount").value(1))
                .andExpect(jsonPath("$.missingCount").value(1));
    }

    @Test
    void testScanJdEndpoint_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "", "text/plain", new byte[0]
        );

        mockMvc.perform(multipart("/api/scan-jd")
                .file(file)
                .param("jobDescription", "Senior Engineer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
