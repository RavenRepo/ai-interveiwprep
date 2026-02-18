# AI Interview Preparation Platform

A comprehensive, AI-driven platform designed to simulate real-world technical interviews. The application leverages advanced AI models for question generation (OpenAI GPT-4), realistic avatar video interactions (D-ID), text-to-speech (ElevenLabs), and speech-to-text transcription (AssemblyAI), providing users with detailed feedback on their performance.

## 🚀 Key Features

*   **AI-Generated Interviews**: Dynamic interview questions tailored to specific job roles and resumes using OpenAI GPT-4.
*   **Realistic AI Avatars**: Interactive video questions delivered by AI avatars via D-ID API.
*   **Video Responses**: Users record video answers which are securely stored on AWS S3.
*   **Speech-to-Text Analysis**: User responses are transcribed using AssemblyAI for detailed content analysis.
*   **Comprehensive Feedback**: Automated evaluation of strengths, weaknesses, and actionable recommendations.
*   **Resume Analysis**: Extracts text from PDFs/DOCX resumes to personalize interview context.
*   **Dashboard**: view interview history, performance metrics, and managing user profile.

> **Note**: For detailed product requirements, see [PRD.md](file:///Users/shresthchauhan/.gemini/antigravity/brain/831b6dad-a1fb-4977-b61f-89c0e816f982/PRD.md).
> For a comprehensive technical deep-dive (Senior Engineer level), see [TECHNICAL_REFERENCE.md](file:///Users/shresthchauhan/.gemini/antigravity/brain/831b6dad-a1fb-4977-b61f-89c0e816f982/TECHNICAL_REFERENCE.md).
> For general project documentation and architecture, see [project_documentation.md](file:///Users/shresthchauhan/.gemini/antigravity/brain/831b6dad-a1fb-4977-b61f-89c0e816f982/project_documentation.md).

## 🛠️ Technology Stack

### Backend
*   **Core**: Java 17+, Spring Boot 3.x
*   **Database**: MySQL 8.0 (Hibernate/JPA)
*   **Security**: Spring Security, JWT Authentication
*   **Cloud Storage**: AWS S3 (Video storage)
*   **Testing**: JUnit 5, Mockito, AssertJ
*   **Build Tool**: Maven

### Frontend
*   **Core**: React 18, TypeScript
*   **State Management**: React Context API
*   **Styling**: Material-UI (MUI) v5
*   **Routing**: React Router v6
*   **HTTP Client**: Axios
*   **Testing**: Jest, React Testing Library

### AI & 3rd Party APIs
*   **OpenAI API**: GPT-4 for logic and question generation.
*   **D-ID API**: Generating talking head avatar videos.
*   **ElevenLabs API**: High-quality text-to-speech generation.
*   **AssemblyAI API**: Speech-to-text transcription.

---

## 📋 Prerequisites

Ensure you have the following installed:
*   **Java 21** or higher (Project uses modern Java features)
*   **Node.js 18+** & npm
*   **MySQL 8.0** (running locally or via Docker)
*   **Maven** 3.8+ (optional, wrapper included)

---

## ⚙️ Backend Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd ai-interview-project/backend
    ```

2.  **Configure Environment Variables:**
    Open `src/main/resources/application.properties` and verify the settings. It is recommended to use environment variables for sensitive keys rather than hardcoding them.

    | Application Property | Environment Variable | Description |
    | :--- | :--- | :--- |
    | `spring.datasource.username` | `DB_USER` | MySQL Username |
    | `spring.datasource.password` | `DB_PASSWORD` | MySQL Password |
    | `aws.access.key.id` | `AWS_ACCESS_KEY_ID` | AWS Credentials |
    | `aws.secret.access.key` | `AWS_SECRET_ACCESS_KEY` | AWS Secret Key |
    | `aws.s3.bucket.name` | `AWS_S3_BUCKET_NAME` | S3 Bucket Name |
    | `openai.api.key` | `OPENAI_API_KEY` | OpenAI API Key |
    | `elevenlabs.api.key` | `ELEVENLABS_API_KEY` | ElevenLabs API Key |
    | `did.api.key` | `DID_API_KEY` | D-ID API Key |
    | `assemblyai.api.key` | `ASSEMBLYAI_API_KEY` | AssemblyAI Key |
    | `app.jwtSecret` | `JWT_SECRET` | Secret for signing JWTs |

3.  **Run the Database:**
    Ensure your MySQL server is running on port `3306` with a database named `interview_platform`.
    ```sql
    CREATE DATABASE interview_platform;
    ```
    *(Alternatively, use the provided `docker-compose.yml` if available)*.

4.  **Build and Run:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The backend server will start at `http://localhost:8080`.

5.  **Run Tests:**
    ```bash
    ./mvnw test
    ```

---

## 💻 Frontend Setup

1.  **Navigate to frontend directory:**
    ```bash
    cd ../frontend
    ```

2.  **Install Dependencies:**
    ```bash
    npm install
    ```

3.  **Environment Configuration:**
    Create a `.env` file in the root of the frontend folder if needed (React scripts use `process.env.REACT_APP_*`).
    *   `REACT_APP_API_URL`: Defaults to `http://localhost:8080` if not set.

4.  **Start Development Server:**
    ```bash
    npm start
    ```
    The application will be available at `http://localhost:3000`.

5.  **Run Tests:**
    ```bash
    npm test
    ```
    *(Note: Interactive watch mode is enabled by default. Use `npm test -- --watchAll=false` for a single run).*

---

## 📂 Project Structure

```
ai-interview-project/
├── backend/
│   ├── src/main/java/com/interview/platform/
│   │   ├── config/          # Security & App Config
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA Entities
│   │   ├── repository/      # Spring Data Repositories
│   │   └── service/         # Business Logic & AI Integrations
│   └── src/test/            # JUnit 5 Tests
│
└── frontend/
    ├── src/
    │   ├── components/      # React Components (Auth, Interview, Video)
    │   ├── context/         # Context API (AuthContext)
    │   ├── hooks/           # Custom Hooks (useAuth, useVideoRecording)
    │   ├── services/        # API Service calls (Axios)
    │   └── types/           # TypeScript Interfaces
    └── public/
```

## 🤝 Contributing

1.  Fork the repository.
2.  Create your feature branch: `git checkout -b feature/AmazingFeature`
3.  Commit your changes: `git commit -m 'Add some AmazingFeature'`
4.  Push to the branch: `git push origin feature/AmazingFeature`
5.  Open a Pull Request.

## 📄 License

This project is licensed under the MIT License.
