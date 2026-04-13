package com.resumescanner.service;

import com.resumescanner.model.KeywordMatch;
import com.resumescanner.model.ScanResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    /**
     * Scan a resume file against provided keywords
     */
    public ScanResult scanResume(MultipartFile file, String keywords) throws IOException {
        // Extract text from resume
        String resumeText = extractText(file);
        String resumeTextLower = resumeText.toLowerCase();

        // Parse keywords (comma or newline separated)
        List<String> keywordList = parseKeywords(keywords);

        // Perform scanning
        List<KeywordMatch> matchedKeywords = new ArrayList<>();
        List<String> missingKeywords = new ArrayList<>();
        Map<String, Integer> keywordFrequency = new LinkedHashMap<>();

        for (String keyword : keywordList) {
            String keywordLower = keyword.toLowerCase().trim();
            if (keywordLower.isEmpty()) continue;

            int frequency = countOccurrences(resumeTextLower, keywordLower);

            if (frequency > 0) {
                String context = findContext(resumeText, keyword);
                matchedKeywords.add(new KeywordMatch(keyword, frequency, context));
                keywordFrequency.put(keyword, frequency);
            } else {
                missingKeywords.add(keyword);
            }
        }

        // Build result
        ScanResult result = new ScanResult();
        result.setFileName(file.getOriginalFilename());
        result.setTotalKeywords(keywordList.size());
        result.setMatchedCount(matchedKeywords.size());
        result.setMissingCount(missingKeywords.size());
        result.setMatchPercentage(
                keywordList.isEmpty() ? 0 :
                Math.round((double) matchedKeywords.size() / keywordList.size() * 10000.0) / 100.0
        );
        result.setMatchedKeywords(matchedKeywords);
        result.setMissingKeywords(missingKeywords);
        result.setKeywordFrequency(keywordFrequency);

        // Word count
        int wordCount = resumeText.split("\\s+").length;
        result.setResumeWordCount(wordCount + " words");

        return result;
    }

    /**
     * Scan resume against a full job description — extracts important keywords automatically
     */
    public ScanResult scanResumeWithJobDescription(MultipartFile file, String jobDescription) throws IOException {
        String keywords = extractKeywordsFromJobDescription(jobDescription);
        return scanResume(file, keywords);
    }

    /**
     * Extract text from PDF or DOCX files
     */
    private String extractText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new IOException("File name is null");

        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".pdf")) {
            return extractFromPdf(file.getInputStream(), file.getBytes());
        } else if (lowerName.endsWith(".docx")) {
            return extractFromDocx(file.getInputStream());
        } else if (lowerName.endsWith(".txt")) {
            return new String(file.getBytes());
        } else {
            throw new IOException("Unsupported file format. Please upload PDF, DOCX, or TXT files.");
        }
    }

    private String extractFromPdf(InputStream inputStream, byte[] bytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString();
        }
    }

    /**
     * Parse comma/newline separated keywords
     */
    private List<String> parseKeywords(String keywords) {
        return Arrays.stream(keywords.split("[,\n]+"))
                .map(String::trim)
                .filter(k -> !k.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Count word/phrase occurrences (case-insensitive)
     */
    private int countOccurrences(String text, String keyword) {
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }

    /**
     * Find context snippet around the first occurrence of a keyword
     */
    private String findContext(String text, String keyword) {
        int index = text.toLowerCase().indexOf(keyword.toLowerCase());
        if (index == -1) return "";

        int start = Math.max(0, index - 40);
        int end = Math.min(text.length(), index + keyword.length() + 40);

        String snippet = text.substring(start, end).replaceAll("\\s+", " ").trim();
        return (start > 0 ? "..." : "") + snippet + (end < text.length() ? "..." : "");
    }

    /**
     * Extract important keywords from a job description
     */
    private String extractKeywordsFromJobDescription(String jobDescription) {
        // Common stop words to filter out
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "a", "an", "the", "and", "or", "but", "in", "on", "at", "to", "for",
                "of", "with", "by", "from", "is", "are", "was", "were", "be", "been",
                "being", "have", "has", "had", "do", "does", "did", "will", "would",
                "could", "should", "may", "might", "must", "shall", "can", "need",
                "dare", "ought", "used", "not", "no", "nor", "as", "if", "than",
                "that", "this", "these", "those", "it", "its", "we", "our", "you",
                "your", "they", "their", "he", "she", "him", "her", "his", "hers",
                "about", "above", "after", "again", "all", "also", "am", "any",
                "because", "before", "between", "both", "each", "few", "get",
                "here", "how", "into", "just", "like", "more", "most", "my",
                "new", "now", "only", "other", "over", "own", "same", "so",
                "some", "such", "then", "there", "through", "too", "under",
                "until", "up", "very", "what", "when", "where", "which", "while",
                "who", "whom", "why", "work", "working", "role", "position",
                "company", "team", "join", "looking", "ideal", "candidate",
                "requirements", "responsibilities", "qualifications", "experience",
                "ability", "strong", "preferred", "required", "including", "etc",
                "well", "within", "across", "using"
        ));

        // Common tech keywords / skills to prioritize
        Set<String> techKeywords = new HashSet<>(Arrays.asList(
                "java", "python", "javascript", "typescript", "react", "angular", "vue",
                "node.js", "spring", "spring boot", "hibernate", "jpa", "sql", "nosql",
                "mongodb", "postgresql", "mysql", "redis", "kafka", "rabbitmq",
                "docker", "kubernetes", "aws", "azure", "gcp", "ci/cd", "jenkins",
                "git", "github", "gitlab", "rest", "restful", "api", "microservices",
                "agile", "scrum", "jira", "confluence", "html", "css", "sass",
                "webpack", "maven", "gradle", "junit", "selenium", "testng",
                "machine learning", "deep learning", "tensorflow", "pytorch",
                "data structures", "algorithms", "oop", "design patterns",
                "linux", "unix", "bash", "powershell", "terraform", "ansible",
                "elasticsearch", "graphql", "oauth", "jwt", "security",
                "devops", "full stack", "frontend", "backend", "cloud",
                "distributed systems", "scalability", "performance", "optimization",
                "c++", "c#", ".net", "ruby", "go", "rust", "swift", "kotlin",
                "flutter", "react native", "ios", "android", "mobile",
                "tableau", "power bi", "excel", "communication", "leadership",
                "problem solving", "analytical", "teamwork", "collaboration"
        ));

        String jdLower = jobDescription.toLowerCase();
        Set<String> extracted = new LinkedHashSet<>();

        // First, find known tech keywords present in JD
        for (String tech : techKeywords) {
            if (jdLower.contains(tech)) {
                extracted.add(tech);
            }
        }

        // Then extract multi-word phrases and significant single words
        String[] words = jobDescription.split("\\s+");
        for (String word : words) {
            String clean = word.replaceAll("[^a-zA-Z0-9+#.]", "").toLowerCase();
            if (clean.length() > 3 && !stopWords.contains(clean) && !extracted.contains(clean)) {
                extracted.add(clean);
            }
        }

        // Limit to top 30 keywords
        return extracted.stream().limit(30).collect(Collectors.joining(", "));
    }
}
