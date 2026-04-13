# Executive Analyst | Resume Keyword Scanner & ATS Optimizer

![Executive Analyst Banner](https://img.shields.io/badge/Executive-Analyst-1a2b4a?style=for-the-badge&logoScale=1.2)
![Status](https://img.shields.io/badge/Status-Live-success?style=for-the-badge)

**Executive Analyst** is a premium, high-fidelity resume optimization tool designed for elite job seekers. It parses resumes against job descriptions to identify critical keyword gaps, providing actionable insights to help candidates beat Applicant Tracking Systems (ATS) and resonate with executive recruiters.

---

## 🚀 Key Features

- **Precision Analysis**: Deep-keyword matching using natural language processing patterns.
- **Expert Curation View**: A simulated "paper" canvas that visually highlights matches and critical gaps.
- **Actionable Insights**: categorized suggestions for technical stack optimization and keyword density.
- **Persistent History**: Track your professional trajectory with a built-in scan archive and match-score trends.
- **Multi-Format Support**: High-performance parsing for PDF, DOCX, and TXT files.

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.4
- **Language**: Java 17
- **Document Parsing**: 
  - Apache PDFBox (PDF)
  - Apache POI (DOCX)
- **Logging**: SLF4J with Logback
- **Build Tool**: Maven

### Frontend
- **Design System**: Custom Premium CSS (Glassmorphism, Executive Aesthetic)
- **Engine**: Vanilla JavaScript (ES6+)
- **Icons**: Lucide/Feather SVG Set
- **Typography**: Google Fonts (Inter)
- **Data Persistence**: LocalStorage API

### Testing
- **Unit Testing**: JUnit 5, AssertJ
- **Integration Testing**: Spring Boot Test, MockMvc

---

## 📸 UI Screenshots

> **Note**: The application features a high-fidelity "Executive Analyst" theme with a split-layout dashboard and a professional results canvas.

---

## 🏗️ Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.9+

### Installation & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/yashkurangale910-spec/resume-key-word-scaneer.git
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the portal at: `http://localhost:8080`

---

## 🧪 Running Tests
To ensure the scanning engine accuracy:
```bash
mvn test
```

---

## 📄 License
Created by **Mayur Yewale** — Built with Spring Boot & ❤️
