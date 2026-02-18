# 🤖 AI Interview Preparation Platform

![Project Icon](project_icon.png)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot-green)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/Frontend-React-blue)](https://reactjs.org/)

## 🚀 Overview

The **AI Interview Preparation Platform** is a comprehensive solution designed to simulate real-world technical interviews. By leveraging advanced AI models, we provide an immersive environment where users can practice, receive instant feedback, and improve their interview skills.

Our platform integrates:

- **OpenAI GPT-4** for dynamic, context-aware question generation.
- **D-ID** for realistic AI avatars that conduct the interview.
- **ElevenLabs** for natural-sounding text-to-speech.
- **AssemblyAI** for accurate speech-to-text transcription and analysis.

## ✨ Key Features

- **🧠 Intelligent Questioning**: Tailored questions based on job roles and resume analysis using GPT-4.
- **🗣️ Realistic Avatars**: Interactive interviews with lifelike AI avatars via D-ID.
- **🎥 Video Response Analysis**: Secure video recording and storage with AWS S3.
- **📊 Comprehensive Feedback**: Detailed performance analytics, highlighting strengths and areas for improvement.
- **📄 Resume Parsing**: Automatic extraction of skills and experience from resumes (PDF/DOCX).
- **📈 Progress Tracking**: User dashboard to monitor interview history and growth over time.

## 🛠️ Technology Stack

### Backend

- **Framework**: Spring Boot 3.x (Java 17+)
- **Database**: MySQL 8.0 with Hibernate/JPA
- **Security**: Spring Security & JWT
- **Cloud**: AWS S3 for object storage
- **Testing**: JUnit 5, Mockito

### Frontend

- **Library**: React 18 with TypeScript
- **Styling**: Material-UI (MUI) v5
- **State Management**: Context API
- **Routing**: React Router v6

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Node.js 18+ & npm
- MySQL 8.0
- Maven 3.8+

### 🔧 Backend Setup

1.  Clone the repository:
    ```bash
    git clone https://github.com/RavenRepo/ai-interveiwprep.git
    cd ai-interveiwprep/backend
    ```
2.  Configure `src/main/resources/application.properties` with your API keys and database credentials.
3.  Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```

### 💻 Frontend Setup

1.  Navigate to the frontend directory:
    ```bash
    cd ../frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm start
    ```

## 🤝 Contributing

We welcome contributions! Please feel free to fork the repository and submit a Pull Request.

## 📄 License

This project is licensed under the MIT License.
