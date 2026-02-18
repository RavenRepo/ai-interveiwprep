## User

# ROLE DEFINITION
Act as a Senior Principal Full Stack Engineer and Solutions Architect with 15+ years of experience in Java Enterprise systems, Modern Frontend frameworks, and Cloud-Native architectures. Your expertise lies in building high-concurrency applications that orchestrate multiple third-party AI services.

# CONTEXT: THE PROJECT
I am building "AI Interview Preparation Platform," a system that simulates real technical interviews using a chain of AI models. 

**Core Stack:**
- **Backend:** Java 21, Spring Boot 3.x, MySQL, Hibernate, Maven.
- **Frontend:** React 18, TypeScript, MUI, Axios, Context API.
- **Infrastructure:** AWS S3 (Video Storage).
- **AI Integrations:** 
  1. OpenAI GPT-4 (Logic/Question Gen)
  2. ElevenLabs (Text-to-Speech)
  3. D-ID (Avatar Video Gen)
  4. AssemblyAI (Speech-to-Text)

**Current Architecture Flow:**
User uploads resume -> Backend extracts text -> GPT generates questions -> User records video -> Video uploaded to S3 -> AssemblyAI transcribes -> GPT evaluates -> D-ID/ElevenLabs generates avatar feedback.

# THE README & DOCUMENTATION
[PASTE THE FULL README CONTENT HERE]

# YOUR OBJECTIVE
Analyze the provided README and project context. You must provide a "Foolproof Technical Execution Plan" to move this project from a prototype to a production-ready enterprise application. You must identify architectural bottlenecks, potential race conditions, cost traps, and security vulnerabilities.

# REQUIRED OUTPUT STRUCTURE

Please organize your response into the following four phases:

## PHASE 1: Critical Architecture & Security (The Foundation)
*   **S3 Integration:** Analyze the video upload flow. If I am currently uploading to the backend server first, provide the code strategy for implementing **AWS S3 Presigned URLs** to offload traffic.
*   **Security:** Review the `application.properties` approach. Recommend a strategy for managing secrets (API keys) in production (e.g., AWS Secrets Manager or Vault).
*   **Java Versioning:** Resolve the discrepancy between Java 17 and Java 21 in the documentation. Explain why Java 21's **Virtual Threads** are critical for this specific application (I/O heavy AI calls).

## PHASE 2: AI Orchestration & Latency Management
*   **The Latency Problem:** The chain of `GPT -> ElevenLabs -> D-ID` is blocking and slow. Propose an **Asynchronous Event-Driven Architecture**.
*   **Implementation:** Provide a high-level design using Spring Boot's `@Async`, `CompletableFuture`, or a message queue (RabbitMQ) to handle these long-running tasks without timing out the HTTP request.
*   **Frontend Polling:** Explain how the React frontend should handle the wait time (e.g., WebSockets vs. Polling) while the video is rendering.

## PHASE 3: Cost Optimization & Caching
*   **Caching Strategy:** Since D-ID and OpenAI are expensive, propose a caching layer (Redis or Spring Cache).
*   **Logic:** Define the logic for caching: "If Question ID X is asked, check if Video URL Y already exists before calling D-ID."

## PHASE 4: Frontend Robustness & State
*   **State Management:** Analyze the use of Context API. Predict where performance issues will occur during video recording/uploading and suggest if/when to move to Zustand/Redux.
*   **Error Handling:** Provide a TypeScript strategy for handling failures in the 3rd party APIs (e.g., if D-ID is down, fall back to text-only mode).

# CONSTRAINTS & CODE STYLE
*   All backend code examples must use **Java 21** syntax.
*   All frontend examples must use **TypeScript** with strict typing.
*   Do not offer generic advice. Be specific to the Spring Boot + React + AI stack.
*   Focus on **Resilience** (Circuit Breakers/Retries) for the external API calls.

## Assistant



# AI Interview Preparation Platform — Foolproof Technical Execution Plan

Let me start by deeply analyzing the existing codebase to ground every recommendation in reality, not assumptions.

**Tool Call: List the `Ai-interview-preparation--main` directory's contents**
Status: Completed

# Folders:
Ai-interview-preparation--main/.idea
Ai-interview-preparation--main/.vscode
Ai-interview-preparation--main/backend
Ai-interview-preparation--main/frontend

# Files:
Ai-interview-preparation--main/PRD.md.resolved
Ai-interview-preparation--main/README.md
Ai-interview-preparation--main/TECHNICAL_REFERENCE.md.resolved
Ai-interview-preparation--main/docker-compose.yml
Ai-interview-preparation--main/project_documentation.md.resolved


**Tool Call: Read file `README.md`**
Status: Completed

````Ai-interview-preparation--main/README.md
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

````


**Tool Call: List the `Ai-interview-preparation--main/frontend` directory's contents**
Status: Completed

Ai-interview-preparation--main/frontend is empty.


**Tool Call: Read file `docker-compose.yml`**
Status: Completed

```Ai-interview-preparation--main/docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: interview-platform-mysql
    restart: always
    environment:
      MYSQL_DATABASE: interview_platform
      MYSQL_USER: ${DB_USER:-user}
      MYSQL_PASSWORD: ${DB_PASSWORD:-password}
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD:-root}
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:

```


**Tool Call: List the `Ai-interview-preparation--main/backend` directory's contents**
Status: Completed

# Folders:
Ai-interview-preparation--main/backend/src

# Files:
Ai-interview-preparation--main/backend/.gitignore
Ai-interview-preparation--main/backend/pom.xml


**Tool Call: Read file `backend/pom.xml`**
Status: Completed

```Ai-interview-preparation--main/backend/pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.2</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.interview</groupId>
	<artifactId>platform</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>interview-platform</name>
	<description>AI Interview Preparation Platform</description>
	<properties>
		<java.version>17</java.version>
		<spring-cloud.version>2023.0.0</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>

		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.11.5</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>com.amazonaws</groupId>
			<artifactId>aws-java-sdk-s3</artifactId>
			<version>1.12.529</version>
		</dependency>
		<!-- PDF parsing -->
		<dependency>
			<groupId>org.apache.pdfbox</groupId>
			<artifactId>pdfbox</artifactId>
			<version>2.0.29</version>
		</dependency>
		<!-- DOCX parsing -->
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi-ooxml</artifactId>
			<version>5.2.3</version>
		</dependency>
		<!-- Mockito subclass mock-maker for Java 17+ / 25 compatibility -->
		<dependency>
			<groupId>org.mockito</groupId>
			<artifactId>mockito-subclass</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<configuration>
					<argLine>
						--add-opens java.base/java.lang=ALL-UNNAMED
						--add-opens java.base/java.lang.reflect=ALL-UNNAMED
					</argLine>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>

```


**Tool Call: Read file `TECHNICAL_REFERENCE.md.resolved`**
Status: Completed

````Ai-interview-preparation--main/TECHNICAL_REFERENCE.md.resolved
# Technical Reference & Architecture Guide
# AI Interview Preparation Platform

> **Target Audience**: Senior Engineers & AI Agents
> **Purpose**: Provide a complete, end-to-end technical dissection of the platform, enabling immediate context loading for development, debugging, and architectural review.

---

## 1. System Context & Architecture

The platform allows users to practice technical interviews by simulating a real-time interaction with an AI avatar. It orchestrates multiple generative AI services to create a coherent feedback loop.

### 1.1 High-Level Architecture Diagram

```mermaid
graph TD
    User[Clients (Web/Mobile)] -->|HTTPS/REST| LB[Load Balancer / Nginx]
    LB -->|API Requests| Backend[Spring Boot Backend]
    
    subgraph "Backend Infrastructure"
        Backend -->|Persist Data| DB[(MySQL 8.0)]
        Backend -->|Store Media| S3[AWS S3 Bucket]
    end
    
    subgraph "AI Services Layer"
        Backend -->|Generate Logic| OpenAI[OpenAI GPT-4]
        Backend -->|Generate Avatar Video| DID[D-ID API]
        Backend -->|Synthesize Speech| Eleven[ElevenLabs TTS]
        Backend -->|Transcribe Answer| Assembly[AssemblyAI STT]
    end
```

### 1.2 Tech Stack Decisions

| Layer | Technology | Decision Rationale |
| :--- | :--- | :--- |
| **Frontend** | React 19, TypeScript | Strict typing for reliability; React 19 for modern concurrency features; MUI for rapid, accessible UI development. |
| **Backend** | Java 17, Spring Boot 3 | Robust ecosystem, strict compile-time safety, easy integration with enterprise-grade libraries (Spring Security, Data JPA). |
| **Database** | MySQL 8.0 | Relational integrity essential for structured interview data (Users <-> Interviews <-> Questions <-> Responses). |
| **Async** | Java `CompletableFuture` | Non-blocking orchestration of long-running AI tasks (video generation, transcription) without freezing HTTP threads. |
| **Auth** | JWT (Stateless) | Scalable authentication; decouples frontend state from backend sessions. |

---

## 2. Key Workflows & Data Flow

### 2.1 Authentication Flow
The system uses **stateless JWT authentication**.

1.  **Frontend**: User submits credentials to `/api/auth/login`.
2.  **Backend**: [AuthController](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/controller/AuthController.java#11-59) delegates to `AuthenticationManager`.
3.  **Security**: `DaoAuthenticationProvider` checks BCrypt-hashed password in MySQL.
4.  **Token**: If valid, [JwtTokenProvider](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/config/JwtTokenProvider.java#13-89) signs a JWT (HS256) containing [sub](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/service/InterviewService.java#153-210) (email) and `iat`/`exp` claims.
5.  **Per-Request**:
    *   Frontend [api.service.ts](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/services/api.service.ts) Axios interceptor injects `Authorization: Bearer <token>`.
    *   Backend `JwtAuthenticationFilter` intercepts request -> validates signature -> sets `SecurityContextHolder`.

### 2.2 Interview Orchestration (The "Core Loop")
This is the most complex flow, involving multiple async AI calls.

**Phase 1: Initialization**
1.  User uploads Resume (PDF/DOCX) -> Backend extracts text (Apache PDFBox/POI).
2.  User selects Job Role -> `InterviewService.startInterview()`.
3.  **Synchronous**: Backend calls **OpenAI GPT-4** to generate 5 JSON-structured questions based on Resume + Role.
4.  **Asynchronous (Fire-and-Forget)**:
    *   For each generated question, [AvatarVideoService](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/service/AvatarVideoService.java#19-321) requests a video from **D-ID**.
    *   Question is saved immediately with `avatarVideoUrl = null`.
    *   When D-ID completes (webhook or polling), the URL is updated.

**Phase 2: User Response**
1.  User watches Avatar video -> Records Answer (WebAPI MediaRecorder).
2.  Frontend uploads `Blob` to `/api/interviews/{id}/submit`.
3.  **Backend**:
    *   Saves video to **AWS S3**.
    *   Triggers **AssemblyAI** for async transcription.
    *   Returns 200 OK immediately ("Response Received").
4.  **Background**: AssemblyAI webhook/polling updates [Response](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/model/Response.java#7-137) entity with `transcription` text.

**Phase 3: Feedback Generation**
1.  User clicks "Complete Interview".
2.  Backend aggregates all Q&A pairs (Question Text + Transcribed Answer).
3.  Backend sends prompt to **OpenAI** for comprehensive analysis.
4.  Result stored in [Feedback](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/types/index.ts#87-96) entity (Score, Strengths, Weaknesses).

---

## 3. Backend Deep Dive

### 3.1 Package Structure (`com.interview.platform`)
*   `config/`: **Critical**. Contains [SecurityConfig](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/config/SecurityConfig.java#21-69) (filter chains), [AsyncConfig](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/config/AsyncConfig.java#10-25) (thread pools for AI calls), and `WebConfig` (CORS).
*   `controller/`: Thin layer. DTO <-> Entity mapping happens here or in Service. Global Exception Handler (`GlobalExceptionHandler`) catches all runtime exceptions and returns standard [ApiError](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/types/index.ts#136-141) JSON.
*   `service/`: **Fat services**. Contains all business logic.
    *   [InterviewService](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/service/InterviewService.java#17-356): Orchestrator. Relies on specialized services ([OpenAIService](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/service/OpenAIService.java#21-273), [VideoStorageService](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/service/VideoStorageService.java#19-184)).
    *   [VideoStorageService](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/service/VideoStorageService.java#19-184): Abstraction over S3. Currently creates unique file keys based on `userId/interviewId/questionId`.

### 3.2 Database Schema (ERD Description)
*   **User** [(1) ---- (N)](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/App.tsx#198-228) **Resume**
*   **User** [(1) ---- (N)](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/App.tsx#198-228) **Interview**
*   **Interview** [(1) ---- (N)](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/App.tsx#198-228) **Question**
*   **Question** [(1) ---- (0..1)](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/App.tsx#198-228) **Response**
*   **Interview** [(1) ---- (0..1)](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/App.tsx#198-228) **Feedback**

> **Note**: [JobRole](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/model/JobRole.java#5-77) is a lookup table seeded with 15 common roles (e.g., "Frontend Developer", "DevOps Engineer").

---

## 4. Frontend Deep Dive

### 4.1 State Management
*   **Global Auth**: [AuthContext.tsx](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/context/AuthContext.tsx) holds the [User](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/model/User.java#12-128) object and [isAuthenticated](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/services/api.service.ts#93-96) flag. It initializes by checking `localStorage` for a token and verifying it with `/api/auth/profile`.
*   **Network Resilience**: [api.service.ts](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/services/api.service.ts) has a response interceptor that listens for `401 Unauthorized`. It dispatches a custom `auth:logout` event to force the UI to redirect to Login, ensuring the user isn't stuck in a bad state.

### 4.2 Component Hierarchy
*   [App.tsx](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/App.tsx): Routing root. Uses [ProtectedRoute](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/App.tsx#20-34) wrapper to block unauthenticated access.
*   [Dashboard](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/components/Dashboard/Dashboard.tsx#67-419): Main hub. Fetches `interviewRepository.findByUserId` via service.
*   [Interview/](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/types/index.ts#39-49):
    *   [InterviewStart](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/components/Interview/InterviewStart.tsx#42-334): Resume/Role selection.
    *   [InterviewRoom](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/components/Interview/InterviewRoom.tsx#32-233): The "Active" state. Manages `mediaStream` state, recording logic, and question navigation.
    *   [InterviewReview](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/src/components/Interview/InterviewReview.tsx#80-487): Read-only view of completed interview with video playback.

---

## 5. Development & Deployment

### 5.1 Environment Variables
Strictly separated configuration.
*   **Backend**: [application.properties](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/resources/application.properties) reads from env vars (`OPENAI_API_KEY`, `DB_PASSWORD`, etc.) with safe defaults for dev.
*   **Frontend**: [.env](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/frontend/.env) handles API base URL (`REACT_APP_API_URL`).

### 5.2 CI/CD Pipelines (Recommended)
*   **Build**:
    *   Backend: `./mvnw clean package` (Produces JAR)
    *   Frontend: `npm run build` (Produces static [build/](file:///Users/shresthchauhan/Documents/Projects/AI-Interview-Preparation-Platform/ai%20interview%20project/backend/src/main/java/com/interview/platform/dto/AuthResponse.java#59-62) folder)
*   **Docker**:
    *   `Dockerfile` in root can leverage multi-stage builds to serve frontend static assets via Spring Boot or Nginx.

````


**Tool Call: List the `Ai-interview-preparation--main/backend/src` directory's contents**
Status: Completed

# Folders:
Ai-interview-preparation--main/backend/src/main
Ai-interview-preparation--main/backend/src/test


**Tool Call: Read file `PRD.md.resolved`**
Status: Completed

```Ai-interview-preparation--main/PRD.md.resolved
# Product Requirements Document (PRD)
# AI Interview Preparation Platform

## 1. Problem Statement
Job seekers often struggle with technical interviews due to a lack of realistic practice, immediate feedback, and personalized guidance. Traditional mock interviews are expensive or hard to schedule, while static coding platforms lack the pressure and interaction of a real interview.

## 2. Product Goals
-   **Realistic Simulation**: Mimic the environment of a real video interview using AI avatars and voice interaction.
-   **Instant Feedback**: Provide immediate, actionable feedback on answers, covering both technical accuracy and soft skills.
-   **Personalization**: Tailor questions to the user's specific resume and target job role.
-   **Accessibility**: Make high-quality interview coaching accessible 24/7 without human interviewers.

---

## 3. User Personas

### **Alex (The Job Seeker)**
-   **Background**: Junior Software Developer looking for their first big role.
-   **Pain Point**: Nervous about speaking technically and explaining thought processes.
-   **Goal**: Wants to practice answering behavioral and technical questions out loud.

### **Priya (The Career Switcher)**
-   **Background**: Transitioning from Marketing to Data Science.
-   **Pain Point**: unsure if her resume highlights the right skills and what questions to expect.
-   **Goal**: Wants interview questions strictly relevant to her new field and resume.

---

## 4. Functional Requirements

### 4.1. Authentication & Profile
-   **FR-01**: Users can register and login using email/password.
-   **FR-02**: Users can manage their profile (name, career goals).
-   **FR-03**: Users can view their interview history and progress stats.

### 4.2. Resume Parsing
-   **FR-04**: Users can upload a resume (PDF/DOCX).
-   **FR-05**: System extracts text from the resume to influence question generation.

### 4.3. Interview Session
-   **FR-06**: Users can start a new interview by selecting a Job Role (e.g., "Java Developer", "Product Manager").
-   **FR-07**: System generates 5 unique questions based on the role and resume (OpenAI GPT-4).
-   **FR-08**: An AI Avatar reads the question aloud to the user (D-ID + ElevenLabs).
-   **FR-09**: Users answer via microphone/camera (Video recording).
-   **FR-10**: System transcribes the user's spoken answer (AssemblyAI).

### 4.4. Feedback & Analytics
-   **FR-11**: System analyzes the transcribed answer against the question.
-   **FR-12**: System provides a score (1-100) and structured feedback (Strengths, Weaknesses, Tips).
-   **FR-13**: Users can review past sessions, including video playback and written feedback.

---

## 5. Non-Functional Requirements
-   **Performance**: Audio transcription should complete within 30 seconds of submission.
-   **Reliability**: Video uploads to S3 must be retried on failure.
-   **Scalability**: The system should support concurrent interview sessions.
-   **Security**: Personal data (resumes, videos) must be stored securely.

---

## 6. User Flows

### **Start Interview Flow**
1.  User logs in -> Dashboard.
2.  User uploads Resume (optional).
3.  User clicks "Start Interview" -> Selects "Senior Backend Engineer".
4.  System generates interview context.
5.  User enters "Interview Room".

### **Question-Answer Flow**
1.  Avatar reads Question 1.
2.  User clicks "Start Recording".
3.  User answers the question.
4.  User clicks "Stop Recording" -> "Submit".
5.  Video uploads -> Transcribes -> AI Generated Feedback.
6.  System moves to Question 2.

### **Review Flow**
1.  Interview finishes.
2.  User sees "Interview Summary" (Overall Score).
3.  User clicks specific question to see "Detailed Feedback".

```


**Tool Call: List the `Ai-interview-preparation--main/backend/src/main` directory's contents**
Status: Completed

# Folders:
Ai-interview-preparation--main/backend/src/main/java
Ai-interview-preparation--main/backend/src/main/resources


**Tool Call: List the `Ai-interview-preparation--main/backend/src/test` directory's contents**
Status: Completed

# Folders:
Ai-interview-preparation--main/backend/src/test/java
Ai-interview-preparation--main/backend/src/test/resources


**Tool Call: List the `Ai-interview-preparation--main/backend/src/main/java` directory's contents**
Status: Completed

# Folders:
Ai-interview-preparation--main/backend/src/main/java/com


**Tool Call: List the `Ai-interview-preparation--main/backend/src/main/resources` directory's contents**
Status: Completed


# Files:
Ai-interview-preparation--main/backend/src/main/resources/application.properties
Ai-interview-preparation--main/backend/src/main/resources/data.sql


**Tool Call: Read file `backend/src/main/resources/application.properties`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/resources/application.properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/interview_platform
spring.datasource.username=user
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# spring.cloud.aws.region.static=us-east-1

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.defer-datasource-initialization=true


# File Upload Configuration
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# AWS S3 Configuration
aws.access.key.id=${AWS_ACCESS_KEY_ID:test}
aws.secret.access.key=${AWS_SECRET_ACCESS_KEY:test}
aws.s3.bucket.name=${AWS_S3_BUCKET_NAME:interview-platform-bucket}
aws.region=${AWS_REGION:us-east-1}
spring.cloud.aws.region.static=us-east-1
spring.cloud.aws.s3.enabled=false

# JWT Configuration
app.jwtSecret=ap7W6mpqkLxJgRRvZIAiABFLkQGS5LK+vrG3pI/ImO4=
app.jwtExpirationInMs=3600000

# OpenAI Configuration
openai.api.key=sk-placeholder
openai.model=gpt-4
openai.max-tokens=2000
openai.temperature=${OPENAI_TEMPERATURE:0.7}

# ElevenLabs Text-to-Speech Configuration
elevenlabs.api.key=${ELEVENLABS_API_KEY:placeholder}
elevenlabs.api.url=https://api.elevenlabs.io/v1
elevenlabs.voice.id=21m00Tcm4TlvDq8ikWAM
elevenlabs.model.id=eleven_monolingual_v1
elevenlabs.voice.stability=0.5
elevenlabs.voice.similarity-boost=0.75

# D-ID Avatar Video Configuration
did.api.key=placeholder
did.api.url=https://api.d-id.com
did.avatar.image.url=https://d-id-public-bucket.s3.us-west-2.amazonaws.com/alice.jpg

# AssemblyAI Speech-to-Text Configuration
assemblyai.api.key=placeholder
assemblyai.api.url=https://api.assemblyai.com/v2

# Spring Boot Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
management.health.db.enabled=true

```


**Tool Call: 50 matches**
Status: Completed

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/InterviewPlatformApplication.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/AWSS3Config.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/AsyncConfig.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/JwtAuthenticationFilter.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/JwtTokenProvider.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/OpenAIConfig.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/SecurityConfig.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/controller/AuthController.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/controller/InterviewController.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/controller/ResumeController.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/AuthResponse.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/FeedbackDTO.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/InterviewDTO.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/InterviewQuestionDTO.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/LoginRequest.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/QuestionDTO.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/RegisterRequest.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/ResumeResponse.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/StartInterviewRequest.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/TranscriptionResult.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/UpdateProfileRequest.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/UserProfile.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/dto/VideoResponseDTO.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/exception/EmailAlreadyExistsException.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/exception/GlobalExceptionHandler.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/exception/InvalidCredentialsException.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/exception/UserNotFoundException.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Feedback.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Interview.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/InterviewStatus.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/InterviewType.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/JobRole.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Question.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Response.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Resume.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/User.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/repository/FeedbackRepository.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/repository/InterviewRepository.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/repository/JobRoleRepository.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/repository/QuestionRepository.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/repository/ResponseRepository.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/repository/ResumeRepository.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/repository/UserRepository.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/AIFeedbackService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/AvatarVideoService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/InterviewService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/OpenAIService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/ResumeService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/SpeechToTextService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/TextToSpeechService.java

**Tool Call: Read file `backend/src/main/resources/data.sql`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/resources/data.sql
-- Insert predefined job roles
INSERT INTO job_roles (title, description, category, active) VALUES
('Software Engineer', 'Develop, test, and maintain software applications', 'Technology', true),
('Data Analyst', 'Analyze and interpret complex data to help organizations make decisions', 'Technology', true),
('Product Manager', 'Oversee product development from conception to launch', 'Business', true),
('Business Analyst', 'Identify business needs and find technical solutions', 'Business', true),
('Marketing Manager', 'Plan and execute marketing strategies and campaigns', 'Marketing & Sales', true),
('Sales Executive', 'Drive sales growth and manage client relationships', 'Marketing & Sales', true),
('HR Manager', 'Manage recruitment, employee relations, and organizational culture', 'Operations', true),
('Financial Analyst', 'Analyze financial data and provide insights for business decisions', 'Business', true),
('UI/UX Designer', 'Design user interfaces and enhance user experience', 'Technology', true),
('Project Manager', 'Plan, execute, and close projects while managing resources', 'Business', true),
('Data Scientist', 'Build predictive models and extract insights from data', 'Technology', true),
('DevOps Engineer', 'Manage infrastructure, automation, and deployment pipelines', 'Technology', true),
('Customer Success Manager', 'Ensure customer satisfaction and drive product adoption', 'Marketing & Sales', true),
('Content Writer', 'Create compelling content for various marketing channels', 'Marketing & Sales', true),
('Operations Manager', 'Oversee daily business operations and process optimization', 'Operations', true);

```


**Tool Call: 5 matches**
Status: Completed

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/UserService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/VideoStorageService.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/test/java/com/interview/platform/service/InterviewServiceTest.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/test/java/com/interview/platform/service/UserServiceTest.java

file:///home/luciousfox/Projects/Ai-interview-preparation--main/backend/src/test/java/com/interview/platform/service/VideoStorageServiceTest.java

## Assistant

Now let me read the critical service and config files:

**Tool Call: Read file `backend/src/main/java/com/interview/platform/service/AvatarVideoService.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/AvatarVideoService.java
package com.interview.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AvatarVideoService {

    private static final Logger logger = Logger.getLogger(AvatarVideoService.class.getName());

    private static final int MAX_POLL_RETRIES = 60;
    private static final long POLL_INTERVAL_MS = 3000;
    private static final int MAX_API_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    private final RestTemplate restTemplate;
    private final TextToSpeechService textToSpeechService;
    private final VideoStorageService videoStorageService;
    private final ObjectMapper objectMapper;

    @Value("${did.api.key}")
    private String apiKey;

    @Value("${did.api.url:https://api.d-id.com}")
    private String apiBaseUrl;

    @Value("${did.avatar.image.url}")
    private String avatarImageUrl;

    public AvatarVideoService(RestTemplate restTemplate, TextToSpeechService textToSpeechService,
            VideoStorageService videoStorageService, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.textToSpeechService = textToSpeechService;
        this.videoStorageService = videoStorageService;
        this.objectMapper = objectMapper;
    }

    /**
     * Generate an AI avatar video for a question.
     * 1. Converts question text to speech via ElevenLabs
     * 2. Creates a D-ID talk with the audio
     * 3. Polls until video is ready
     * 4. Downloads and uploads to S3
     *
     * @param questionText the interview question text
     * @param questionId   unique identifier for S3 key naming
     * @return S3 URL of the generated avatar video
     */
    public String generateAvatarVideo(String questionText, Long questionId) {
        logger.info("Starting avatar video generation for question ID: " + questionId);

        // Step 1: Generate speech audio
        String audioUrl = textToSpeechService.generateSpeech(questionText, questionId);
        logger.info("Speech audio generated for question ID: " + questionId);

        // Step 2: Create D-ID talk
        String talkId = createTalk(audioUrl);
        logger.info("D-ID talk created with ID: " + talkId);

        // Step 3: Poll for video completion
        String resultUrl = pollForVideoCompletion(talkId);
        logger.info("D-ID video ready at: " + resultUrl);

        // Step 4: Download video and upload to S3
        String s3Url = downloadAndUploadToS3(resultUrl, questionId);
        logger.info("Avatar video uploaded to S3 for question ID: " + questionId);

        return s3Url;
    }

    /**
     * Async version of generateAvatarVideo for non-blocking operation.
     */
    @Async("avatarTaskExecutor")
    public CompletableFuture<String> generateAvatarVideoAsync(String questionText, Long questionId) {
        try {
            String url = generateAvatarVideo(questionText, questionId);
            return CompletableFuture.completedFuture(url);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Async avatar video generation failed for question ID: " + questionId, e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // ========================
    // D-ID API: Create Talk
    // ========================

    /**
     * Call D-ID /talks endpoint to create a video talk with retry logic.
     *
     * @param audioUrl the URL of the speech audio file
     * @return the talk ID
     */
    private String createTalk(String audioUrl) {
        int attempt = 0;
        long delay = INITIAL_RETRY_DELAY_MS;

        while (attempt < MAX_API_RETRIES) {
            try {
                return callCreateTalk(audioUrl);
            } catch (HttpServerErrorException e) {
                attempt++;
                logger.warning("D-ID API server error (attempt " + attempt + "/" + MAX_API_RETRIES
                        + "): " + e.getStatusCode());
                if (attempt >= MAX_API_RETRIES) {
                    throw new RuntimeException(
                            "D-ID API failed after " + MAX_API_RETRIES + " retries due to server errors", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempt++;
                logger.warning("D-ID API rate limited (attempt " + attempt + "/" + MAX_API_RETRIES + ")");
                if (attempt >= MAX_API_RETRIES) {
                    throw new RuntimeException(
                            "D-ID API rate limit exceeded after " + MAX_API_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (ResourceAccessException e) {
                attempt++;
                logger.warning("D-ID API connection error (attempt " + attempt + "/" + MAX_API_RETRIES
                        + "): " + e.getMessage());
                if (attempt >= MAX_API_RETRIES) {
                    throw new RuntimeException(
                            "D-ID API unreachable after " + MAX_API_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException e) {
                logger.log(Level.SEVERE, "D-ID API client error: " + e.getStatusCode()
                        + " - " + e.getResponseBodyAsString());
                throw new RuntimeException("D-ID API request failed: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("D-ID API failed after " + MAX_API_RETRIES + " retries");
    }

    private String callCreateTalk(String audioUrl) {
        String url = apiBaseUrl + "/talks";

        HttpHeaders headers = buildDIDHeaders();

        // Build request body per D-ID API spec
        Map<String, Object> script = new LinkedHashMap<>();
        script.put("type", "audio");
        script.put("audio_url", audioUrl);

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("fluent", true);
        config.put("pad_audio", 0.5);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("script", script);
        requestBody.put("source_url", avatarImageUrl);
        requestBody.put("config", config);

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize D-ID request body", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String talkId = root.path("id").asText();
            if (talkId == null || talkId.isBlank()) {
                throw new RuntimeException("No talk ID returned from D-ID API");
            }
            return talkId;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed to parse D-ID create talk response", e);
        }
    }

    // ========================
    // D-ID API: Poll Status
    // ========================

    /**
     * Poll D-ID /talks/{talkId} endpoint until the video is ready.
     *
     * @param talkId the D-ID talk ID to check
     * @return the result_url of the completed video
     */
    private String pollForVideoCompletion(String talkId) {
        String url = apiBaseUrl + "/talks/" + talkId;
        HttpHeaders headers = buildDIDHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        for (int attempt = 0; attempt < MAX_POLL_RETRIES; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, String.class);

                JsonNode root = objectMapper.readTree(response.getBody());
                String status = root.path("status").asText();

                switch (status) {
                    case "done":
                        String resultUrl = root.path("result_url").asText();
                        if (resultUrl == null || resultUrl.isBlank()) {
                            throw new RuntimeException("D-ID video completed but result_url is empty");
                        }
                        logger.info("D-ID talk " + talkId + " completed after " + (attempt + 1) + " polls");
                        return resultUrl;

                    case "error":
                        String errorMessage = root.path("error").path("description").asText("Unknown error");
                        throw new RuntimeException("D-ID video generation failed: " + errorMessage);

                    case "created":
                    case "started":
                        logger.fine("D-ID talk " + talkId + " status: " + status
                                + " (poll " + (attempt + 1) + "/" + MAX_POLL_RETRIES + ")");
                        break;

                    default:
                        logger.warning("D-ID talk " + talkId + " unexpected status: " + status);
                        break;
                }

            } catch (HttpClientErrorException e) {
                logger.log(Level.SEVERE, "D-ID poll error for talk " + talkId + ": " + e.getMessage());
                throw new RuntimeException("Failed to poll D-ID talk status: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                // Re-throw RuntimeExceptions (including our own error cases)
                if (e.getMessage() != null && e.getMessage().startsWith("D-ID video")) {
                    throw e;
                }
                logger.warning("Error polling D-ID talk " + talkId + ": " + e.getMessage());
            } catch (Exception e) {
                logger.warning("Error parsing D-ID poll response for talk " + talkId + ": " + e.getMessage());
            }

            sleep(POLL_INTERVAL_MS);
        }

        throw new RuntimeException("D-ID video generation timed out after "
                + (MAX_POLL_RETRIES * POLL_INTERVAL_MS / 1000) + " seconds for talk: " + talkId);
    }

    // ========================
    // Download + Upload to S3
    // ========================

    /**
     * Download the video from D-ID result URL and upload to S3.
     */
    private String downloadAndUploadToS3(String videoUrl, Long questionId) {
        try {
            // Download video bytes
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    videoUrl, HttpMethod.GET, null, byte[].class);

            byte[] videoData = response.getBody();
            if (videoData == null || videoData.length == 0) {
                throw new RuntimeException("Empty video downloaded from D-ID");
            }

            logger.info("Downloaded avatar video: " + videoData.length + " bytes");

            // Upload to S3
            String timestamp = String.valueOf(System.currentTimeMillis());
            String s3Key = String.format("avatar-videos/question_%d_%s.mp4", questionId, timestamp);

            return videoStorageService.uploadAudioBytes(videoData, s3Key, "video/mp4");

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.log(Level.SEVERE, "Failed to download D-ID video: " + e.getMessage(), e);
            throw new RuntimeException("Failed to download avatar video: " + e.getMessage(), e);
        }
    }

    // ========================
    // Helpers
    // ========================

    private HttpHeaders buildDIDHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(apiKey, "");
        return headers;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/service/InterviewService.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/InterviewService.java
package com.interview.platform.service;

import com.interview.platform.dto.*;
import com.interview.platform.model.*;
import com.interview.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class InterviewService {

    private static final Logger logger = Logger.getLogger(InterviewService.class.getName());

    private final InterviewRepository interviewRepository;
    private final ResumeRepository resumeRepository;
    private final JobRoleRepository jobRoleRepository;
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final AvatarVideoService avatarVideoService;
    private final VideoStorageService videoStorageService;
    private final SpeechToTextService speechToTextService;
    private final AIFeedbackService aiFeedbackService;

    public InterviewService(InterviewRepository interviewRepository,
            ResumeRepository resumeRepository,
            JobRoleRepository jobRoleRepository,
            QuestionRepository questionRepository,
            ResponseRepository responseRepository,
            UserRepository userRepository,
            OpenAIService openAIService,
            AvatarVideoService avatarVideoService,
            VideoStorageService videoStorageService,
            SpeechToTextService speechToTextService,
            AIFeedbackService aiFeedbackService) {
        this.interviewRepository = interviewRepository;
        this.resumeRepository = resumeRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
        this.userRepository = userRepository;
        this.openAIService = openAIService;
        this.avatarVideoService = avatarVideoService;
        this.videoStorageService = videoStorageService;
        this.speechToTextService = speechToTextService;
        this.aiFeedbackService = aiFeedbackService;
    }

    // ============================================================
    // 1. Start Interview
    // ============================================================

    /**
     * Start a new interview session.
     * Creates the Interview entity, generates AI questions, and kicks off
     * async avatar video generation for each question.
     */
    @Transactional
    public InterviewDTO startInterview(Long userId, Long resumeId, Long jobRoleId) {
        logger.info("Starting interview for user: " + userId + ", resume: " + resumeId
                + ", jobRole: " + jobRoleId);

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Validate and fetch resume
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found: " + resumeId));

        if (!resume.getUser().getId().equals(userId)) {
            throw new RuntimeException("Resume does not belong to user: " + userId);
        }

        String resumeText = resume.getExtractedText();
        if (resumeText == null || resumeText.isBlank()) {
            throw new RuntimeException("Resume text has not been extracted. Please re-upload the resume.");
        }

        // Validate and fetch job role
        JobRole jobRole = jobRoleRepository.findById(jobRoleId)
                .orElseThrow(() -> new RuntimeException("Job role not found: " + jobRoleId));

        // Create Interview entity
        Interview interview = new Interview();
        interview.setUser(user);
        interview.setResume(resume);
        interview.setJobRole(jobRole);
        interview.setStatus(InterviewStatus.IN_PROGRESS);
        interview.setType(InterviewType.VIDEO);
        interview = interviewRepository.save(interview);

        logger.info("Interview created with ID: " + interview.getId());

        // Generate questions using OpenAI
        List<QuestionDTO> generatedQuestions = openAIService.generateInterviewQuestions(
                resumeText, jobRole.getTitle());

        // Save Question entities and trigger async avatar video generation
        List<Question> savedQuestions = new ArrayList<>();
        int questionNumber = 1;

        for (QuestionDTO dto : generatedQuestions) {
            Question question = new Question();
            question.setInterview(interview);
            question.setQuestionText(dto.getQuestionText());
            question.setQuestionNumber(questionNumber);
            question.setCategory(dto.getCategory());
            question.setDifficulty(dto.getDifficulty());
            question = questionRepository.save(question);

            savedQuestions.add(question);

            // Trigger async avatar video generation
            final Long questionId = question.getId();
            final String questionText = question.getQuestionText();
            try {
                avatarVideoService.generateAvatarVideoAsync(questionText, questionId)
                        .thenAccept(videoUrl -> {
                            // Update question with avatar video URL when ready
                            questionRepository.findById(questionId).ifPresent(q -> {
                                q.setAvatarVideoUrl(videoUrl);
                                questionRepository.save(q);
                                logger.info("Avatar video URL updated for question ID: " + questionId);
                            });
                        });
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to initiate avatar video for question ID: "
                        + questionId + " — will be generated later", e);
            }

            questionNumber++;
        }

        logger.info("Saved " + savedQuestions.size() + " questions for interview ID: " + interview.getId());

        return mapToInterviewDTO(interview, savedQuestions);
    }

    // ============================================================
    // 2. Submit Video Response
    // ============================================================

    /**
     * Submit a video response to a question.
     * Uploads the video to S3, saves the Response entity,
     * and triggers async transcription.
     */
    @Transactional
    public String submitResponse(Long userId, Long interviewId, Long questionId, MultipartFile videoFile) {
        logger.info("Submitting response — user: " + userId + ", interview: " + interviewId
                + ", question: " + questionId);

        // Validate interview belongs to user
        Interview interview = interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Interview not found or does not belong to user: " + interviewId));

        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new RuntimeException("Interview is not in progress. Current status: " + interview.getStatus());
        }

        // Validate question belongs to interview
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found: " + questionId));

        if (!question.getInterview().getId().equals(interviewId)) {
            throw new RuntimeException("Question does not belong to this interview");
        }

        // Check for duplicate response
        Optional<Response> existingResponse = responseRepository.findByQuestionId(questionId);
        if (existingResponse.isPresent()) {
            throw new RuntimeException("Response already submitted for question: " + questionId);
        }

        // Upload video to S3
        String videoUrl = videoStorageService.uploadVideo(videoFile, userId, interviewId, questionId);

        // Save Response entity
        Response response = new Response();
        response.setQuestion(question);
        response.setInterview(interview);
        response.setUser(interview.getUser());
        response.setVideoUrl(videoUrl);
        responseRepository.save(response);

        logger.info("Response saved for question ID: " + questionId);

        // Trigger async transcription
        try {
            speechToTextService.transcribeVideoAsync(videoUrl, questionId);
            logger.info("Async transcription triggered for question ID: " + questionId);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to initiate transcription for question ID: "
                    + questionId + " — can be retried later", e);
        }

        return "Response submitted successfully";
    }

    // ============================================================
    // 3. Get Interview Details
    // ============================================================

    /**
     * Fetch interview with all questions and response status.
     */
    @Transactional(readOnly = true)
    public InterviewDTO getInterview(Long interviewId, Long userId) {
        Interview interview = interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Interview not found or does not belong to user: " + interviewId));

        List<Question> questions = questionRepository.findByInterviewIdOrderByQuestionNumber(interviewId);

        return mapToInterviewDTO(interview, questions);
    }

    // ============================================================
    // 4. Complete Interview
    // ============================================================

    /**
     * Mark an interview as complete and trigger async feedback generation.
     */
    @Transactional
    public String completeInterview(Long interviewId, Long userId) {
        logger.info("Completing interview ID: " + interviewId + " for user: " + userId);

        Interview interview = interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Interview not found or does not belong to user: " + interviewId));

        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new RuntimeException("Interview is not in progress. Current status: " + interview.getStatus());
        }

        // Validate that at least one response has been submitted
        List<Response> responses = responseRepository.findByInterviewId(interviewId);
        if (responses.isEmpty()) {
            throw new RuntimeException("Cannot complete interview without any responses");
        }

        // Update interview status to PROCESSING
        interview.setStatus(InterviewStatus.PROCESSING);
        interview.setCompletedAt(LocalDateTime.now());
        interviewRepository.save(interview);

        logger.info("Interview ID: " + interviewId + " status set to PROCESSING");

        // Trigger async feedback generation
        try {
            aiFeedbackService.generateFeedbackAsync(interviewId)
                    .thenAccept(feedback -> {
                        // Update interview status to COMPLETED when feedback is ready
                        interviewRepository.findById(interviewId).ifPresent(i -> {
                            i.setStatus(InterviewStatus.COMPLETED);
                            interviewRepository.save(i);
                            logger.info("Interview ID: " + interviewId
                                    + " completed with score: " + feedback.getOverallScore());
                        });
                    });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initiate feedback generation for interview ID: "
                    + interviewId, e);
        }

        return "Interview submitted for processing. Feedback will be generated shortly.";
    }

    // ============================================================
    // 5. Interview History
    // ============================================================

    /**
     * Fetch all interviews for a user.
     */
    @Transactional(readOnly = true)
    public List<InterviewDTO> getInterviewHistory(Long userId) {
        List<Interview> interviews = interviewRepository.findByUserId(userId);

        List<InterviewDTO> history = new ArrayList<>();
        for (Interview interview : interviews) {
            InterviewDTO dto = new InterviewDTO();
            dto.setInterviewId(interview.getId());
            dto.setStatus(interview.getStatus().name());
            dto.setType(interview.getType().name());
            dto.setOverallScore(interview.getOverallScore());
            dto.setStartedAt(interview.getStartedAt());
            dto.setCompletedAt(interview.getCompletedAt());

            // Include job role title
            if (interview.getJobRole() != null) {
                dto.setJobRoleTitle(interview.getJobRole().getTitle());
            }

            // Include question count (lightweight)
            Long questionCount = questionRepository.countByInterviewId(interview.getId());
            dto.setQuestions(null); // Don't load full questions for history list

            history.add(dto);
        }

        return history;
    }

    // ============================================================
    // DTO Mapping
    // ============================================================

    private InterviewDTO mapToInterviewDTO(Interview interview, List<Question> questions) {
        InterviewDTO dto = new InterviewDTO();
        dto.setInterviewId(interview.getId());
        dto.setStatus(interview.getStatus().name());
        dto.setType(interview.getType().name());
        dto.setOverallScore(interview.getOverallScore());
        dto.setStartedAt(interview.getStartedAt());
        dto.setCompletedAt(interview.getCompletedAt());

        if (interview.getJobRole() != null) {
            dto.setJobRoleTitle(interview.getJobRole().getTitle());
        }

        // Map questions
        List<InterviewQuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            InterviewQuestionDTO qDto = new InterviewQuestionDTO();
            qDto.setQuestionId(question.getId());
            qDto.setQuestionNumber(question.getQuestionNumber());
            qDto.setQuestionText(question.getQuestionText());
            qDto.setCategory(question.getCategory());
            qDto.setDifficulty(question.getDifficulty());
            qDto.setAvatarVideoUrl(question.getAvatarVideoUrl());

            // Check if response exists for this question
            Optional<Response> responseOpt = responseRepository.findByQuestionId(question.getId());
            qDto.setAnswered(responseOpt.isPresent());

            questionDTOs.add(qDto);
        }

        dto.setQuestions(questionDTOs);
        return dto;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/config/AWSS3Config.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/AWSS3Config.java
package com.interview.platform.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSS3Config {

    @Value("${aws.access.key.id}")
    private String accessKeyId;

    @Value("${aws.secret.access.key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Bean
    public AmazonS3 amazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    @Bean
    public String s3BucketName() {
        return bucketName;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/service/OpenAIService.java`**
Status: Completed

````Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/OpenAIService.java
package com.interview.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.QuestionDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OpenAIService {

    private static final Logger logger = Logger.getLogger(OpenAIService.class.getName());

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    private final RestTemplate openAIRestTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.model:gpt-4}")
    private String model;

    @Value("${openai.max-tokens:2000}")
    private int maxTokens;

    @Value("${openai.temperature:0.7}")
    private double temperature;

    public OpenAIService(@Qualifier("openAIRestTemplate") RestTemplate openAIRestTemplate,
            ObjectMapper objectMapper) {
        this.openAIRestTemplate = openAIRestTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Generate interview questions based on a resume and job role.
     *
     * @param resumeText the extracted text content from the user's resume
     * @param jobRole    the target job role title
     * @return list of generated QuestionDTOs
     */
    public List<QuestionDTO> generateInterviewQuestions(String resumeText, String jobRole) {
        logger.info("Generating interview questions for job role: " + jobRole);

        String prompt = buildPrompt(resumeText, jobRole);
        String responseContent = callOpenAIWithRetry(prompt);

        List<QuestionDTO> questions = parseQuestionsFromResponse(responseContent);
        logger.info("Successfully generated " + questions.size() + " interview questions");

        return questions;
    }

    /**
     * Build the prompt to send to OpenAI.
     */
    private String buildPrompt(String resumeText, String jobRole) {
        return String.format(
                """
                        Based on this resume and the job role '%s', generate 10 relevant interview questions. \
                        Include a mix of technical, behavioral, and situational questions. \
                        Format your response as a JSON array with objects containing these fields: \
                        questionText (string), category (one of: TECHNICAL, BEHAVIORAL, SITUATIONAL), \
                        difficulty (one of: EASY, MEDIUM, HARD).

                        Respond with ONLY the JSON array, no other text or markdown formatting.

                        Resume:
                        %s
                        """,
                jobRole, resumeText);
    }

    /**
     * Call OpenAI Chat Completion API with exponential backoff retry logic.
     */
    private String callOpenAIWithRetry(String prompt) {
        int attempt = 0;
        long delay = INITIAL_RETRY_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            try {
                return callOpenAI(prompt);
            } catch (HttpServerErrorException e) {
                // 5xx errors — retryable
                attempt++;
                logger.warning("OpenAI API server error (attempt " + attempt + "/" + MAX_RETRIES + "): "
                        + e.getStatusCode() + " - " + e.getMessage());
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("OpenAI API failed after " + MAX_RETRIES
                            + " retries due to server errors", e);
                }
                sleep(delay);
                delay *= 2; // exponential backoff
            } catch (HttpClientErrorException.TooManyRequests e) {
                // 429 rate limit — retryable
                attempt++;
                logger.warning("OpenAI API rate limited (attempt " + attempt + "/" + MAX_RETRIES + ")");
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("OpenAI API rate limit exceeded after " + MAX_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (ResourceAccessException e) {
                // Network/timeout errors — retryable
                attempt++;
                logger.warning("OpenAI API connection error (attempt " + attempt + "/" + MAX_RETRIES + "): "
                        + e.getMessage());
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("OpenAI API unreachable after " + MAX_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException e) {
                // 4xx errors (except 429) — NOT retryable
                logger.log(Level.SEVERE, "OpenAI API client error: " + e.getStatusCode()
                        + " - " + e.getResponseBodyAsString());
                throw new RuntimeException("OpenAI API request failed: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("OpenAI API failed after " + MAX_RETRIES + " retries");
    }

    /**
     * Make the actual HTTP call to the OpenAI Chat Completion endpoint.
     */
    private String callOpenAI(String prompt) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are an expert interviewer and career coach. " +
                        "Generate high-quality interview questions tailored to the candidate's resume and target role. "
                        +
                        "Always respond with valid JSON only.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        ResponseEntity<String> response = openAIRestTemplate.postForEntity(
                OPENAI_API_URL, requestBody, String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Empty response from OpenAI API");
        }

        return extractContentFromResponse(response.getBody());
    }

    /**
     * Extract the assistant's message content from the OpenAI response JSON.
     */
    private String extractContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");

            if (choices.isEmpty() || choices.isMissingNode()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            String content = choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (content == null || content.isBlank()) {
                throw new RuntimeException("Empty content in OpenAI API response");
            }

            // Log token usage for monitoring
            JsonNode usage = root.path("usage");
            if (!usage.isMissingNode()) {
                logger.info("OpenAI token usage — prompt: " + usage.path("prompt_tokens").asInt()
                        + ", completion: " + usage.path("completion_tokens").asInt()
                        + ", total: " + usage.path("total_tokens").asInt());
            }

            return content.trim();
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Failed to parse OpenAI response JSON", e);
            throw new RuntimeException("Failed to parse OpenAI API response", e);
        }
    }

    /**
     * Parse a JSON array string into a list of QuestionDTOs.
     * Handles cases where the response might be wrapped in markdown code fences.
     */
    private List<QuestionDTO> parseQuestionsFromResponse(String content) {
        try {
            // Strip markdown code fences if present
            String jsonContent = content;
            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.replaceAll("^```(?:json)?\\s*", "");
                jsonContent = jsonContent.replaceAll("\\s*```$", "");
            }
            jsonContent = jsonContent.trim();

            List<QuestionDTO> questions = objectMapper.readValue(
                    jsonContent, new TypeReference<List<QuestionDTO>>() {
                    });

            // Validate and normalize each question
            List<QuestionDTO> validatedQuestions = new ArrayList<>();
            for (QuestionDTO q : questions) {
                if (q.getQuestionText() != null && !q.getQuestionText().isBlank()) {
                    // Normalize category
                    if (q.getCategory() != null) {
                        q.setCategory(q.getCategory().toUpperCase());
                    } else {
                        q.setCategory("TECHNICAL");
                    }
                    // Normalize difficulty
                    if (q.getDifficulty() != null) {
                        q.setDifficulty(q.getDifficulty().toUpperCase());
                    } else {
                        q.setDifficulty("MEDIUM");
                    }
                    validatedQuestions.add(q);
                }
            }

            if (validatedQuestions.isEmpty()) {
                throw new RuntimeException("No valid questions could be parsed from OpenAI response");
            }

            return validatedQuestions;
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Failed to parse question JSON from OpenAI response: " + content, e);
            throw new RuntimeException("Failed to parse interview questions from AI response. "
                    + "The response was not in the expected JSON format.", e);
        }
    }

    /**
     * Sleep helper that handles InterruptedException.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", e);
        }
    }
}

````


**Tool Call: Read file `backend/src/main/java/com/interview/platform/config/AsyncConfig.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/AsyncConfig.java
package com.interview.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "avatarTaskExecutor")
    public Executor avatarTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("avatar-video-");
        executor.initialize();
        return executor;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/service/VideoStorageService.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/VideoStorageService.java
package com.interview.platform.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class VideoStorageService {

    private static final Logger logger = Logger.getLogger(VideoStorageService.class.getName());

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public VideoStorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * Upload video response to S3
     */
    public String uploadVideo(MultipartFile file, Long userId, Long interviewId, Long questionId) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String key = String.format("interviews/%d/%d/response_%d_%s.webm",
                userId, interviewId, questionId, timestamp);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata("user-id", String.valueOf(userId));
            metadata.addUserMetadata("interview-id", String.valueOf(interviewId));
            metadata.addUserMetadata("question-id", String.valueOf(questionId));

            PutObjectRequest request = new PutObjectRequest(
                    bucketName,
                    key,
                    file.getInputStream(),
                    metadata).withCannedAcl(CannedAccessControlList.Private);

            amazonS3.putObject(request);
            logger.info("Successfully uploaded video: " + key + " for user: " + userId);

            return generatePresignedUrl(key, 7);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to upload video for user: " + userId + ", interview: " + interviewId
                    + ", question: " + questionId, e);
            throw new RuntimeException("Failed to upload video: " + e.getMessage(), e);
        } catch (AmazonServiceException e) {
            logger.log(Level.SEVERE, "AWS S3 error while uploading video: " + e.getMessage(), e);
            throw new RuntimeException("Failed to upload video to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Upload resume file to S3
     */
    public String uploadResumeFile(MultipartFile file, Long userId) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".pdf";
        String key = String.format("resumes/%d/resume_%s%s", userId, timestamp, extension);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata("user-id", String.valueOf(userId));
            metadata.addUserMetadata("original-filename", originalFilename != null ? originalFilename : "resume");

            PutObjectRequest request = new PutObjectRequest(
                    bucketName,
                    key,
                    file.getInputStream(),
                    metadata).withCannedAcl(CannedAccessControlList.Private);

            amazonS3.putObject(request);
            logger.info("Successfully uploaded resume: " + key + " for user: " + userId);

            return generatePresignedUrl(key, 7);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to upload resume for user: " + userId, e);
            throw new RuntimeException("Failed to upload resume: " + e.getMessage(), e);
        } catch (AmazonServiceException e) {
            logger.log(Level.SEVERE, "AWS S3 error while uploading resume: " + e.getMessage(), e);
            throw new RuntimeException("Failed to upload resume to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Upload raw audio bytes to S3 (used by TextToSpeechService).
     *
     * @param audioData   the audio file bytes
     * @param key         the S3 object key
     * @param contentType the MIME type (e.g. "audio/mpeg")
     * @return pre-signed URL to the uploaded file
     */
    public String uploadAudioBytes(byte[] audioData, String key, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(audioData.length);

            PutObjectRequest request = new PutObjectRequest(
                    bucketName,
                    key,
                    new ByteArrayInputStream(audioData),
                    metadata).withCannedAcl(CannedAccessControlList.Private);

            amazonS3.putObject(request);
            logger.info("Successfully uploaded audio: " + key);

            return generatePresignedUrl(key, 7);
        } catch (AmazonServiceException e) {
            logger.log(Level.SEVERE, "AWS S3 error while uploading audio: " + e.getMessage(), e);
            throw new RuntimeException("Failed to upload audio to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Generate pre-signed URL for accessing a file
     */
    public String generatePresignedUrl(String key, int validDays) {
        try {
            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += (long) validDays * 24 * 60 * 60 * 1000;
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);

            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            logger.fine("Generated pre-signed URL for key: " + key);
            return url.toString();
        } catch (AmazonServiceException e) {
            logger.log(Level.SEVERE, "Failed to generate pre-signed URL for key: " + key, e);
            throw new RuntimeException("Failed to generate pre-signed URL: " + e.getMessage(), e);
        }
    }

    /**
     * Delete file from S3
     */
    public void deleteFile(String key) {
        try {
            amazonS3.deleteObject(bucketName, key);
            logger.info("Successfully deleted file: " + key);
        } catch (AmazonServiceException e) {
            logger.log(Level.SEVERE, "Failed to delete file: " + key, e);
            // Don't throw exception, just log the error
        }
    }

    /**
     * Check if file exists in S3
     */
    public boolean fileExists(String key) {
        try {
            amazonS3.getObjectMetadata(bucketName, key);
            return true;
        } catch (AmazonServiceException e) {
            return false;
        }
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/service/TextToSpeechService.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/TextToSpeechService.java
package com.interview.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TextToSpeechService {

    private static final Logger logger = Logger.getLogger(TextToSpeechService.class.getName());

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    private final RestTemplate restTemplate;
    private final VideoStorageService videoStorageService;
    private final ObjectMapper objectMapper;

    @Value("${elevenlabs.api.key}")
    private String apiKey;

    @Value("${elevenlabs.api.url:https://api.elevenlabs.io/v1}")
    private String apiBaseUrl;

    @Value("${elevenlabs.voice.id:21m00Tcm4TlvDq8ikWAM}")
    private String voiceId;

    @Value("${elevenlabs.model.id:eleven_monolingual_v1}")
    private String modelId;

    @Value("${elevenlabs.voice.stability:0.5}")
    private double stability;

    @Value("${elevenlabs.voice.similarity-boost:0.75}")
    private double similarityBoost;

    public TextToSpeechService(RestTemplate restTemplate, VideoStorageService videoStorageService,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.videoStorageService = videoStorageService;
        this.objectMapper = objectMapper;
    }

    /**
     * Convert text to speech using ElevenLabs API, upload the audio to S3,
     * and return the S3 pre-signed URL.
     *
     * @param text       the text to convert to speech
     * @param questionId a unique identifier used for the S3 key
     * @return the S3 URL of the generated audio file
     */
    public String generateSpeech(String text, Long questionId) {
        logger.info("Generating speech for question ID: " + questionId
                + " (text length: " + text.length() + " chars)");

        byte[] audioData = callElevenLabsWithRetry(text);

        // Upload to S3
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String s3Key = String.format("tts/question_%d_%s.mp3", questionId, timestamp);

        String audioUrl = videoStorageService.uploadAudioBytes(audioData, s3Key, "audio/mpeg");
        logger.info("Speech audio uploaded to S3: " + s3Key);

        return audioUrl;
    }

    /**
     * Overloaded method when no specific question ID is available.
     */
    public String generateSpeech(String text) {
        long syntheticId = Math.abs(text.hashCode());
        return generateSpeech(text, syntheticId);
    }

    /**
     * Call ElevenLabs TTS API with exponential backoff retry logic.
     */
    private byte[] callElevenLabsWithRetry(String text) {
        int attempt = 0;
        long delay = INITIAL_RETRY_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            try {
                return callElevenLabs(text);
            } catch (HttpServerErrorException e) {
                attempt++;
                logger.warning("ElevenLabs API server error (attempt " + attempt + "/" + MAX_RETRIES
                        + "): " + e.getStatusCode());
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException(
                            "ElevenLabs API failed after " + MAX_RETRIES + " retries due to server errors", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempt++;
                logger.warning("ElevenLabs API rate limited (attempt " + attempt + "/" + MAX_RETRIES + ")");
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException(
                            "ElevenLabs API rate limit exceeded after " + MAX_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (ResourceAccessException e) {
                attempt++;
                logger.warning("ElevenLabs API connection error (attempt " + attempt + "/" + MAX_RETRIES
                        + "): " + e.getMessage());
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException(
                            "ElevenLabs API unreachable after " + MAX_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException e) {
                // 4xx errors (except 429) — NOT retryable
                logger.log(Level.SEVERE, "ElevenLabs API client error: " + e.getStatusCode()
                        + " - " + e.getResponseBodyAsString());
                throw new RuntimeException("ElevenLabs API request failed: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("ElevenLabs API failed after " + MAX_RETRIES + " retries");
    }

    /**
     * Make the actual HTTP call to ElevenLabs text-to-speech endpoint.
     * Returns the raw MP3 audio bytes.
     */
    private byte[] callElevenLabs(String text) {
        String url = apiBaseUrl + "/text-to-speech/" + voiceId;

        // Build request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        // Build request body
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("text", text);
        requestBody.put("model_id", modelId);

        Map<String, Object> voiceSettings = new LinkedHashMap<>();
        voiceSettings.put("stability", stability);
        voiceSettings.put("similarity_boost", similarityBoost);
        requestBody.put("voice_settings", voiceSettings);

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize TTS request body", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, byte[].class);

        byte[] audioData = response.getBody();
        if (audioData == null || audioData.length == 0) {
            throw new RuntimeException("Empty audio response from ElevenLabs API");
        }

        logger.info("Received audio data: " + audioData.length + " bytes from ElevenLabs");
        return audioData;
    }

    /**
     * Sleep helper that handles InterruptedException.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", e);
        }
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/config/SecurityConfig.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/SecurityConfig.java
package com.interview.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/service/SpeechToTextService.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/SpeechToTextService.java
package com.interview.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.TranscriptionResult;
import com.interview.platform.model.Response;
import com.interview.platform.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SpeechToTextService {

    private static final Logger logger = Logger.getLogger(SpeechToTextService.class.getName());

    private static final int MAX_POLL_RETRIES = 60;
    private static final long POLL_INTERVAL_MS = 3000;
    private static final int MAX_API_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ResponseRepository responseRepository;

    @Value("${assemblyai.api.key}")
    private String apiKey;

    @Value("${assemblyai.api.url:https://api.assemblyai.com/v2}")
    private String apiBaseUrl;

    public SpeechToTextService(RestTemplate restTemplate, ObjectMapper objectMapper,
            ResponseRepository responseRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.responseRepository = responseRepository;
    }

    // ============================================================
    // Public API
    // ============================================================

    /**
     * Transcribe a video/audio file from a URL (synchronous).
     *
     * @param videoUrl the publicly accessible URL of the media file
     * @return TranscriptionResult with text and confidence
     */
    public TranscriptionResult transcribeVideo(String videoUrl) {
        logger.info("Starting transcription for: " + videoUrl);

        // Step 1: Submit transcription job
        String transcriptId = submitTranscriptionJob(videoUrl);
        logger.info("Transcription job submitted with ID: " + transcriptId);

        // Step 2: Poll for completion
        TranscriptionResult result = pollForTranscription(transcriptId);
        logger.info("Transcription completed — confidence: " + result.getConfidence());

        return result;
    }

    /**
     * Async transcription that also persists the result to the Response entity.
     *
     * @param videoUrl   the URL of the video to transcribe
     * @param questionId the question ID to look up the Response entity
     * @return CompletableFuture containing the transcription result
     */
    @Async("avatarTaskExecutor")
    @Transactional
    public CompletableFuture<TranscriptionResult> transcribeVideoAsync(String videoUrl, Long questionId) {
        try {
            TranscriptionResult result = transcribeVideo(videoUrl);

            // Persist transcription to Response entity
            Optional<Response> responseOpt = responseRepository.findByQuestionId(questionId);
            if (responseOpt.isPresent()) {
                Response response = responseOpt.get();
                response.setTranscription(result.getText());
                response.setTranscriptionConfidence(result.getConfidence());
                responseRepository.save(response);
                logger.info("Transcription saved to Response for question ID: " + questionId);
            } else {
                logger.warning("No Response entity found for question ID: " + questionId
                        + " — transcription not persisted");
            }

            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Async transcription failed for question ID: " + questionId, e);
            CompletableFuture<TranscriptionResult> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // ============================================================
    // Submit Transcription Job
    // ============================================================

    /**
     * Submit a transcription job to AssemblyAI with retry logic.
     */
    private String submitTranscriptionJob(String videoUrl) {
        int attempt = 0;
        long delay = INITIAL_RETRY_DELAY_MS;

        while (attempt < MAX_API_RETRIES) {
            try {
                return callSubmitTranscription(videoUrl);
            } catch (HttpServerErrorException e) {
                attempt++;
                logger.warning("AssemblyAI server error (attempt " + attempt + "/" + MAX_API_RETRIES
                        + "): " + e.getStatusCode());
                if (attempt >= MAX_API_RETRIES) {
                    throw new RuntimeException(
                            "AssemblyAI failed after " + MAX_API_RETRIES + " retries due to server errors", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempt++;
                logger.warning("AssemblyAI rate limited (attempt " + attempt + "/" + MAX_API_RETRIES + ")");
                if (attempt >= MAX_API_RETRIES) {
                    throw new RuntimeException(
                            "AssemblyAI rate limit exceeded after " + MAX_API_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (ResourceAccessException e) {
                attempt++;
                logger.warning("AssemblyAI connection error (attempt " + attempt + "/" + MAX_API_RETRIES
                        + "): " + e.getMessage());
                if (attempt >= MAX_API_RETRIES) {
                    throw new RuntimeException(
                            "AssemblyAI unreachable after " + MAX_API_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException e) {
                logger.log(Level.SEVERE, "AssemblyAI client error: " + e.getStatusCode()
                        + " - " + e.getResponseBodyAsString());
                throw new RuntimeException("AssemblyAI request failed: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("AssemblyAI failed after " + MAX_API_RETRIES + " retries");
    }

    private String callSubmitTranscription(String videoUrl) {
        String url = apiBaseUrl + "/transcript";

        HttpHeaders headers = buildHeaders();

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("audio_url", videoUrl);
        requestBody.put("language_code", "en");

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize AssemblyAI request body", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String transcriptId = root.path("id").asText();
            if (transcriptId == null || transcriptId.isBlank()) {
                throw new RuntimeException("No transcript ID returned from AssemblyAI");
            }
            return transcriptId;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed to parse AssemblyAI submit response", e);
        }
    }

    // ============================================================
    // Poll for Transcription Completion
    // ============================================================

    /**
     * Poll AssemblyAI /transcript/{id} until the transcription is complete.
     *
     * @param transcriptId the AssemblyAI transcript ID
     * @return TranscriptionResult with text and confidence
     */
    private TranscriptionResult pollForTranscription(String transcriptId) {
        String url = apiBaseUrl + "/transcript/" + transcriptId;
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        for (int attempt = 0; attempt < MAX_POLL_RETRIES; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, String.class);

                JsonNode root = objectMapper.readTree(response.getBody());
                String status = root.path("status").asText();

                switch (status) {
                    case "completed":
                        String text = root.path("text").asText("");
                        double confidence = root.path("confidence").asDouble(0.0);

                        logger.info("Transcription " + transcriptId + " completed after "
                                + (attempt + 1) + " polls (confidence: " + confidence + ")");

                        return new TranscriptionResult(text, confidence);

                    case "error":
                        String errorMsg = root.path("error").asText("Unknown transcription error");
                        throw new RuntimeException("AssemblyAI transcription failed: " + errorMsg);

                    case "queued":
                    case "processing":
                        logger.fine("Transcription " + transcriptId + " status: " + status
                                + " (poll " + (attempt + 1) + "/" + MAX_POLL_RETRIES + ")");
                        break;

                    default:
                        logger.warning("Transcription " + transcriptId + " unexpected status: " + status);
                        break;
                }

            } catch (HttpClientErrorException e) {
                logger.log(Level.SEVERE, "AssemblyAI poll error for transcript "
                        + transcriptId + ": " + e.getMessage());
                throw new RuntimeException("Failed to poll AssemblyAI transcript status: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                if (e.getMessage() != null && e.getMessage().startsWith("AssemblyAI transcription")) {
                    throw e;
                }
                logger.warning("Error polling transcript " + transcriptId + ": " + e.getMessage());
            } catch (Exception e) {
                logger.warning("Error parsing AssemblyAI poll response for " + transcriptId + ": " + e.getMessage());
            }

            sleep(POLL_INTERVAL_MS);
        }

        throw new RuntimeException("AssemblyAI transcription timed out after "
                + (MAX_POLL_RETRIES * POLL_INTERVAL_MS / 1000) + " seconds for transcript: " + transcriptId);
    }

    // ============================================================
    // Helpers
    // ============================================================

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", apiKey);
        return headers;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/model/Question.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Question.java
package com.interview.platform.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(nullable = false)
    private Integer questionNumber;

    @Column(length = 50)
    private String category;

    @Column(length = 20)
    private String difficulty;

    @Column(name = "avatar_video_url")
    private String avatarVideoUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Question() {
    }

    public Question(Long id, Interview interview, String questionText, Integer questionNumber, String category,
            String difficulty, String avatarVideoUrl, LocalDateTime createdAt) {
        this.id = id;
        this.interview = interview;
        this.questionText = questionText;
        this.questionNumber = questionNumber;
        this.category = category;
        this.difficulty = difficulty;
        this.avatarVideoUrl = avatarVideoUrl;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getAvatarVideoUrl() {
        return avatarVideoUrl;
    }

    public void setAvatarVideoUrl(String avatarVideoUrl) {
        this.avatarVideoUrl = avatarVideoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/controller/InterviewController.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/controller/InterviewController.java
package com.interview.platform.controller;

import com.interview.platform.dto.InterviewDTO;
import com.interview.platform.dto.StartInterviewRequest;
import com.interview.platform.model.Feedback;
import com.interview.platform.model.InterviewStatus;
import com.interview.platform.repository.FeedbackRepository;
import com.interview.platform.repository.InterviewRepository;
import com.interview.platform.service.InterviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "http://localhost:3000")
public class InterviewController {

    private static final Logger logger = Logger.getLogger(InterviewController.class.getName());

    private final InterviewService interviewService;
    private final FeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;

    public InterviewController(InterviewService interviewService,
            FeedbackRepository feedbackRepository,
            InterviewRepository interviewRepository) {
        this.interviewService = interviewService;
        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
    }

    // ============================================================
    // 1. POST /api/interviews/start
    // ============================================================

    @PostMapping("/start")
    public ResponseEntity<InterviewDTO> startInterview(
            @Valid @RequestBody StartInterviewRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromRequest(httpRequest);
        logger.info("Starting interview — user: " + userId + ", resume: " + request.getResumeId()
                + ", jobRole: " + request.getJobRoleId());

        InterviewDTO interview = interviewService.startInterview(
                userId, request.getResumeId(), request.getJobRoleId());

        return new ResponseEntity<>(interview, HttpStatus.CREATED);
    }

    // ============================================================
    // 2. POST /api/interviews/{interviewId}/response
    // ============================================================

    @PostMapping("/{interviewId}/response")
    public ResponseEntity<Map<String, Object>> submitResponse(
            @PathVariable Long interviewId,
            @RequestParam("questionId") Long questionId,
            @RequestParam("video") MultipartFile videoFile,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromRequest(httpRequest);
        logger.info("Submitting response — user: " + userId + ", interview: " + interviewId
                + ", question: " + questionId);

        if (videoFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Video file is required",
                    "status", "BAD_REQUEST"));
        }

        String result = interviewService.submitResponse(userId, interviewId, questionId, videoFile);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", result);
        response.put("interviewId", interviewId);
        response.put("questionId", questionId);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 3. GET /api/interviews/{interviewId}
    // ============================================================

    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewDTO> getInterview(
            @PathVariable Long interviewId,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromRequest(httpRequest);
        InterviewDTO interview = interviewService.getInterview(interviewId, userId);

        return ResponseEntity.ok(interview);
    }

    // ============================================================
    // 4. POST /api/interviews/{interviewId}/complete
    // ============================================================

    @PostMapping("/{interviewId}/complete")
    public ResponseEntity<Map<String, String>> completeInterview(
            @PathVariable Long interviewId,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromRequest(httpRequest);
        logger.info("Completing interview — user: " + userId + ", interview: " + interviewId);

        String result = interviewService.completeInterview(interviewId, userId);

        return ResponseEntity.ok(Map.of(
                "message", result,
                "interviewId", String.valueOf(interviewId),
                "status", "PROCESSING"));
    }

    // ============================================================
    // 5. GET /api/interviews/history
    // ============================================================

    @GetMapping("/history")
    public ResponseEntity<List<InterviewDTO>> getInterviewHistory(HttpServletRequest httpRequest) {

        Long userId = getUserIdFromRequest(httpRequest);
        List<InterviewDTO> history = interviewService.getInterviewHistory(userId);

        return ResponseEntity.ok(history);
    }

    // ============================================================
    // 6. GET /api/interviews/{interviewId}/feedback
    // ============================================================

    @GetMapping("/{interviewId}/feedback")
    public ResponseEntity<Object> getInterviewFeedback(
            @PathVariable Long interviewId,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromRequest(httpRequest);

        // Validate ownership
        interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Interview not found or does not belong to user: " + interviewId));

        // Check feedback availability
        Optional<Feedback> feedbackOpt = feedbackRepository.findByInterviewId(interviewId);

        if (feedbackOpt.isPresent()) {
            Feedback feedback = feedbackOpt.get();
            Map<String, Object> feedbackResponse = new LinkedHashMap<>();
            feedbackResponse.put("status", "COMPLETED");
            feedbackResponse.put("overallScore", feedback.getOverallScore());
            feedbackResponse.put("strengths", feedback.getStrengths());
            feedbackResponse.put("weaknesses", feedback.getWeaknesses());
            feedbackResponse.put("recommendations", feedback.getRecommendations());
            feedbackResponse.put("detailedAnalysis", feedback.getDetailedAnalysis());
            feedbackResponse.put("generatedAt", feedback.getGeneratedAt());

            return ResponseEntity.ok(feedbackResponse);
        }

        // Check if interview is still processing
        var interview = interviewRepository.findById(interviewId);
        if (interview.isPresent() && interview.get().getStatus() == InterviewStatus.PROCESSING) {
            Map<String, String> processingResponse = new LinkedHashMap<>();
            processingResponse.put("status", "PROCESSING");
            processingResponse.put("message", "Feedback is being generated. Please check back shortly.");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(processingResponse);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", "NOT_FOUND",
                        "message", "No feedback available. Complete the interview first."));
    }

    // ============================================================
    // Helper
    // ============================================================

    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr == null) {
            throw new RuntimeException("User not authenticated");
        }
        return (Long) userIdAttr;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/service/AIFeedbackService.java`**
Status: Completed

````Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/AIFeedbackService.java
package com.interview.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.FeedbackDTO;
import com.interview.platform.model.Feedback;
import com.interview.platform.model.Interview;
import com.interview.platform.model.Response;
import com.interview.platform.repository.FeedbackRepository;
import com.interview.platform.repository.InterviewRepository;
import com.interview.platform.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AIFeedbackService {

    private static final Logger logger = Logger.getLogger(AIFeedbackService.class.getName());

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    private final RestTemplate openAIRestTemplate;
    private final ObjectMapper objectMapper;
    private final ResponseRepository responseRepository;
    private final InterviewRepository interviewRepository;
    private final FeedbackRepository feedbackRepository;

    @Value("${openai.model:gpt-4}")
    private String model;

    @Value("${openai.max-tokens:2000}")
    private int maxTokens;

    @Value("${openai.temperature:0.7}")
    private double temperature;

    public AIFeedbackService(@Qualifier("openAIRestTemplate") RestTemplate openAIRestTemplate,
            ObjectMapper objectMapper,
            ResponseRepository responseRepository,
            InterviewRepository interviewRepository,
            FeedbackRepository feedbackRepository) {
        this.openAIRestTemplate = openAIRestTemplate;
        this.objectMapper = objectMapper;
        this.responseRepository = responseRepository;
        this.interviewRepository = interviewRepository;
        this.feedbackRepository = feedbackRepository;
    }

    // ============================================================
    // Public API
    // ============================================================

    /**
     * Generate AI feedback for a completed interview (synchronous).
     *
     * @param interviewId the interview to generate feedback for
     * @return the persisted Feedback entity
     */
    @Transactional
    public Feedback generateFeedback(Long interviewId) {
        logger.info("Generating AI feedback for interview ID: " + interviewId);

        // Step 1: Fetch interview
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found: " + interviewId));

        // Step 2: Check if feedback already exists
        Optional<Feedback> existingFeedback = feedbackRepository.findByInterviewId(interviewId);
        if (existingFeedback.isPresent()) {
            logger.info("Feedback already exists for interview ID: " + interviewId);
            return existingFeedback.get();
        }

        // Step 3: Fetch all responses with transcriptions
        List<Response> responses = responseRepository.findByInterviewIdOrderByQuestionId(interviewId);
        if (responses.isEmpty()) {
            throw new RuntimeException("No responses found for interview: " + interviewId);
        }

        // Step 4: Build prompt from Q&A pairs
        String prompt = buildFeedbackPrompt(responses);

        // Step 5: Call OpenAI
        String aiResponse = callOpenAIWithRetry(prompt);

        // Step 6: Parse feedback
        FeedbackDTO feedbackDTO = parseFeedbackResponse(aiResponse);

        // Step 7: Create and save Feedback entity
        Feedback feedback = createFeedbackEntity(interview, feedbackDTO);

        // Step 8: Update interview with overall score
        interview.setOverallScore(feedbackDTO.getScore());
        interviewRepository.save(interview);

        logger.info("AI feedback generated for interview ID: " + interviewId
                + " — score: " + feedbackDTO.getScore());

        return feedback;
    }

    /**
     * Async version for background processing.
     */
    @Async("avatarTaskExecutor")
    public CompletableFuture<Feedback> generateFeedbackAsync(Long interviewId) {
        try {
            Feedback feedback = generateFeedback(interviewId);
            return CompletableFuture.completedFuture(feedback);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Async feedback generation failed for interview ID: " + interviewId, e);
            CompletableFuture<Feedback> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // ============================================================
    // Prompt Building
    // ============================================================

    private String buildFeedbackPrompt(List<Response> responses) {
        StringBuilder qaPairs = new StringBuilder();
        int questionNumber = 1;

        for (Response response : responses) {
            String questionText = response.getQuestion() != null
                    ? response.getQuestion().getQuestionText()
                    : "Question " + questionNumber;

            String transcription = response.getTranscription();
            if (transcription == null || transcription.isBlank()) {
                transcription = "[No response transcription available]";
            }

            qaPairs.append(String.format("[Q%d: %s, A%d: %s]\n",
                    questionNumber, questionText, questionNumber, transcription));
            questionNumber++;
        }

        return String.format(
                """
                        Analyze this interview performance based on the following Q&A pairs:
                        %s

                        Provide:
                        1. Overall score (0-100)
                        2. Top 3-5 strengths
                        3. Top 3-5 weaknesses
                        4. Specific actionable recommendations

                        Return as JSON only, no markdown:
                        {
                          "score": <number 0-100>,
                          "strengths": ["strength1", "strength2", ...],
                          "weaknesses": ["weakness1", "weakness2", ...],
                          "recommendations": ["recommendation1", "recommendation2", ...],
                          "detailedAnalysis": "A paragraph summarizing the overall performance"
                        }
                        """,
                qaPairs.toString());
    }

    // ============================================================
    // OpenAI API Call with Retry
    // ============================================================

    private String callOpenAIWithRetry(String prompt) {
        int attempt = 0;
        long delay = INITIAL_RETRY_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            try {
                return callOpenAI(prompt);
            } catch (HttpServerErrorException e) {
                attempt++;
                logger.warning("OpenAI server error (attempt " + attempt + "/" + MAX_RETRIES
                        + "): " + e.getStatusCode());
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException(
                            "OpenAI failed after " + MAX_RETRIES + " retries for feedback generation", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempt++;
                logger.warning("OpenAI rate limited (attempt " + attempt + "/" + MAX_RETRIES + ")");
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException(
                            "OpenAI rate limit exceeded after " + MAX_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (ResourceAccessException e) {
                attempt++;
                logger.warning("OpenAI connection error (attempt " + attempt + "/" + MAX_RETRIES
                        + "): " + e.getMessage());
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException(
                            "OpenAI unreachable after " + MAX_RETRIES + " retries", e);
                }
                sleep(delay);
                delay *= 2;
            } catch (HttpClientErrorException e) {
                logger.log(Level.SEVERE, "OpenAI client error: " + e.getStatusCode()
                        + " - " + e.getResponseBodyAsString());
                throw new RuntimeException("OpenAI request failed: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("OpenAI failed after " + MAX_RETRIES + " retries");
    }

    private String callOpenAI(String prompt) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are an expert interview coach and career advisor. "
                        + "Analyze interview responses and provide constructive, actionable feedback. "
                        + "Always respond with valid JSON only.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        ResponseEntity<String> response = openAIRestTemplate.postForEntity(
                OPENAI_API_URL, requestBody, String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Empty response from OpenAI API");
        }

        return extractContentFromResponse(response.getBody());
    }

    private String extractContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");

            if (choices.isEmpty() || choices.isMissingNode()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            String content = choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (content == null || content.isBlank()) {
                throw new RuntimeException("Empty content in OpenAI API response");
            }

            // Log token usage
            JsonNode usage = root.path("usage");
            if (!usage.isMissingNode()) {
                logger.info("Feedback OpenAI tokens — prompt: " + usage.path("prompt_tokens").asInt()
                        + ", completion: " + usage.path("completion_tokens").asInt()
                        + ", total: " + usage.path("total_tokens").asInt());
            }

            return content.trim();
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Failed to parse OpenAI response JSON", e);
            throw new RuntimeException("Failed to parse OpenAI API response", e);
        }
    }

    // ============================================================
    // Response Parsing
    // ============================================================

    private FeedbackDTO parseFeedbackResponse(String content) {
        try {
            // Strip markdown code fences if present
            String jsonContent = content;
            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.replaceAll("^```(?:json)?\\s*", "");
                jsonContent = jsonContent.replaceAll("\\s*```$", "");
            }
            jsonContent = jsonContent.trim();

            FeedbackDTO dto = objectMapper.readValue(jsonContent, FeedbackDTO.class);

            // Validate score range
            if (dto.getScore() < 0) {
                dto.setScore(0);
            } else if (dto.getScore() > 100) {
                dto.setScore(100);
            }

            // Ensure lists are not null
            if (dto.getStrengths() == null) {
                dto.setStrengths(Collections.emptyList());
            }
            if (dto.getWeaknesses() == null) {
                dto.setWeaknesses(Collections.emptyList());
            }
            if (dto.getRecommendations() == null) {
                dto.setRecommendations(Collections.emptyList());
            }
            if (dto.getDetailedAnalysis() == null) {
                dto.setDetailedAnalysis("");
            }

            return dto;
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Failed to parse feedback JSON: " + content, e);
            throw new RuntimeException(
                    "Failed to parse AI feedback. The response was not in the expected JSON format.", e);
        }
    }

    // ============================================================
    // Entity Creation
    // ============================================================

    private Feedback createFeedbackEntity(Interview interview, FeedbackDTO dto) {
        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setUser(interview.getUser());
        feedback.setOverallScore(dto.getScore());

        // Store lists as JSON strings
        try {
            feedback.setStrengths(objectMapper.writeValueAsString(dto.getStrengths()));
            feedback.setWeaknesses(objectMapper.writeValueAsString(dto.getWeaknesses()));
            feedback.setRecommendations(objectMapper.writeValueAsString(dto.getRecommendations()));
        } catch (JsonProcessingException e) {
            logger.log(Level.WARNING, "Failed to serialize feedback lists to JSON, using toString()", e);
            feedback.setStrengths(dto.getStrengths().toString());
            feedback.setWeaknesses(dto.getWeaknesses().toString());
            feedback.setRecommendations(dto.getRecommendations().toString());
        }

        feedback.setDetailedAnalysis(dto.getDetailedAnalysis());

        return feedbackRepository.save(feedback);
    }

    // ============================================================
    // Helpers
    // ============================================================

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", e);
        }
    }
}

````


**Tool Call: Read file `backend/src/main/java/com/interview/platform/model/Response.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Response.java
package com.interview.platform.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String transcription;

    @Column(name = "transcription_confidence")
    private Double transcriptionConfidence;

    @Column(name = "video_duration")
    private Integer videoDuration;

    @Column(name = "responded_at", nullable = false, updatable = false)
    private LocalDateTime respondedAt;

    public Response() {
    }

    public Response(Long id, Question question, Interview interview, User user, String videoUrl, String transcription,
            Double transcriptionConfidence, Integer videoDuration, LocalDateTime respondedAt) {
        this.id = id;
        this.question = question;
        this.interview = interview;
        this.user = user;
        this.videoUrl = videoUrl;
        this.transcription = transcription;
        this.transcriptionConfidence = transcriptionConfidence;
        this.videoDuration = videoDuration;
        this.respondedAt = respondedAt;
    }

    @PrePersist
    protected void onCreate() {
        respondedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public Double getTranscriptionConfidence() {
        return transcriptionConfidence;
    }

    public void setTranscriptionConfidence(Double transcriptionConfidence) {
        this.transcriptionConfidence = transcriptionConfidence;
    }

    public Integer getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Integer videoDuration) {
        this.videoDuration = videoDuration;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/config/JwtTokenProvider.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/JwtTokenProvider.java
package com.interview.platform.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication, Long userId) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            System.out.println("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/model/Interview.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/model/Interview.java
package com.interview.platform.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_status", columnList = "status")
})
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_role_id", nullable = false)
    private JobRole jobRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private InterviewType type;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Interview() {
    }

    public Interview(Long id, User user, Resume resume, JobRole jobRole, InterviewStatus status, InterviewType type,
            Integer overallScore, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.user = user;
        this.resume = resume;
        this.jobRole = jobRole;
        this.status = status;
        this.type = type;
        this.overallScore = overallScore;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        if (status == null) {
            status = InterviewStatus.IN_PROGRESS;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public InterviewType getType() {
        return type;
    }

    public void setType(InterviewType type) {
        this.type = type;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

```


**Tool Call: Read file `backend/src/main/java/com/interview/platform/config/OpenAIConfig.java`**
Status: Completed

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/OpenAIConfig.java
package com.interview.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    /**
     * Default plain RestTemplate for general HTTP calls (e.g. ElevenLabs).
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * RestTemplate pre-configured with OpenAI Bearer auth header.
     */
    @Bean
    public RestTemplate openAIRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}

```


**Tool Call: Thinking**
Status: Completed


Now I have a comprehensive understanding of the entire codebase. Let me synthesize my findings:

**Critical Issues Found:**

1. **Java Version Discrepancy**: 
   - `pom.xml` says `<java.version>17</java.version>`
   - README Prerequisites says "Java 21 or higher"
   - TECHNICAL_REFERENCE.md says "Java 17, Spring Boot 3"
   - This is a real conflict.

2. **Security Vulnerabilities (CRITICAL)**:
   - `app.jwtSecret=ap7W6mpqkLxJgRRvZIAiABFLkQGS5LK+vrG3pI/ImO4=` is HARDCODED in application.properties
   - `openai.api.key=sk-placeholder` is hardcoded (even though it says placeholder)
   - `did.api.key=placeholder` hardcoded
   - DB credentials hardcoded: `spring.datasource.username=user`, `spring.datasource.password=password`
   - AWS credentials use env vars with fallback to `test` — but the approach is fragile
   - `AWSS3Config.java` uses deprecated `AWSStaticCredentialsProvider` with `BasicAWSCredentials` — should use IAM roles
   - Actuator endpoints exposed at `/actuator/**` with `permitAll()` — health details exposed

3. **S3 Upload Architecture Problem**:
   - `VideoStorageService.uploadVideo()` accepts `MultipartFile` — this means the video file is uploaded to the Spring Boot server FIRST (consuming server memory and bandwidth), then forwarded to S3
   - `spring.servlet.multipart.max-file-size=100MB` — a 100MB file will be held in server memory
   - No presigned URL implementation exists. The `generatePresignedUrl` method only generates GET URLs for downloading, never PUT URLs for uploading.

4. **Blocking Architecture Problems**:
   - `AvatarVideoService.generateAvatarVideo()` is blocking: it calls ElevenLabs -> then D-ID -> then polls D-ID for 60 polls * 3 seconds = up to 180 seconds. All while holding a thread.
   - `Thread.sleep()` in polling loops blocks platform threads. With Java 21 virtual threads, this would be fine, but currently on Java 17 platform threads.
   - `AsyncConfig` has a pool of only core=2, max=5 threads. If 5 interviews start simultaneously, each generating 10 questions, that's 50 avatar video tasks competing for 5 threads (queue cap = 25). The 26th+ tasks will be rejected.
   - The `@Async` calls in `InterviewService.startInterview()` use `.thenAccept()` on `CompletableFuture` — but this runs within a `@Transactional` method. The transaction may close before the async callback completes, causing detached entity issues.

5. **Race Conditions**:
   - `SpeechToTextService.transcribeVideoAsync()` is annotated `@Transactional` and `@Async("avatarTaskExecutor")`. The `@Transactional` on an `@Async` method in Spring creates a new transaction in the async thread. But the entity may have been modified by the caller already. The `findByQuestionId` may race with the save from `submitResponse`.
   - In `InterviewService.completeInterview()`, the feedback async callback calls `interviewRepository.findById()` and saves — but this is outside any transaction boundary (running in the async executor). And it races with the status update in the caller.
   
6. **Cost Traps**:
   - D-ID generates a video for EVERY question EVERY time. No caching at all.
   - OpenAI is called to generate questions AND evaluate answers — no caching of similar questions.
   - ElevenLabs converts the same question text to speech every time, even if the same question text was used before.
   - `openai.model=gpt-4` — GPT-4 is much more expensive than GPT-4-turbo or GPT-4o.

7. **Missing Dependencies**:
   - No circuit breaker library (Resilience4j is not in pom.xml)
   - No caching library (no Redis, no Spring Cache)
   - No WebSocket support (no spring-boot-starter-websocket)
   - Uses old AWS SDK v1 (`com.amazonaws:aws-java-sdk-s3:1.12.529`) instead of v2

8. **Frontend Issues**:
   - The `frontend/` directory is EMPTY in the repository! No actual frontend code exists.
   - Based on TECHNICAL_REFERENCE.md, the frontend architecture is described but not implemented.

Let me organize my response into the four phases as requested.


## Assistant

Now I have a complete picture of every file in the codebase. Here is the execution plan, grounded in the actual code I just read.

---

# Foolproof Technical Execution Plan
## AI Interview Preparation Platform — Prototype → Production

---

## PHASE 1: Critical Architecture & Security (The Foundation)

### 1.1 The Java Version Discrepancy — Real Problem, Real Impact

I found a three-way contradiction across your documentation and build config:

| Source | Java Version |
|:---|:---|
| `pom.xml` → `<java.version>17</java.version>` | **Java 17** (actual build target) |
| README.md → Prerequisites | **Java 21** |
| TECHNICAL_REFERENCE.md → Tech Stack | **Java 17** |

Your `pom.xml` is the source of truth, and it compiles to **Java 17**. Here's why upgrading to 21 isn't just cosmetic — it's architecturally critical for *this specific application*.

**Why Virtual Threads Matter Here:**

Look at your `AvatarVideoService.pollForVideoCompletion()`:

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/AvatarVideoService.java#L188-L237
    private String pollForVideoCompletion(String talkId) {
        // ...
        for (int attempt = 0; attempt < MAX_POLL_RETRIES; attempt++) {
            // ... HTTP call, JSON parse ...
            sleep(POLL_INTERVAL_MS); // Thread.sleep(3000) — BLOCKS a platform thread for 3 seconds
        }
    }
```

And your `AsyncConfig`:

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/AsyncConfig.java#L10-L25
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "avatarTaskExecutor")
    public Executor avatarTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("avatar-video-");
        executor.initialize();
        return executor;
    }
}
```

**The math is brutal:** Each interview generates 10 questions (your prompt says "generate 10"). Each avatar video generation takes up to 180 seconds (60 polls × 3s) of `Thread.sleep()`. Your executor has **5 max threads**. So **a single interview occupies all 5 threads** while the remaining 5 question tasks queue up. **A second simultaneous user gets all their tasks queued or rejected.**

With Java 21 Virtual Threads, every `Thread.sleep()` yields the underlying carrier thread back to the pool. You can run thousands of polling operations concurrently because sleeping virtual threads cost essentially zero resources.

**Fix — Step 1: Update `pom.xml`:**

```Ai-interview-preparation--main/backend/pom.xml#L16-L17
	<properties>
		<java.version>21</java.version>
```

**Fix — Step 2: Replace the thread pool with Virtual Threads:**

```/dev/null/AsyncConfig.java#L1-L31
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Virtual-thread executor for AI service calls (avatar gen, transcription, feedback).
     * Each virtual thread can block on Thread.sleep() or HTTP I/O without consuming
     * a platform thread. No pool sizing needed — JVM manages carrier threads.
     */
    @Bean(name = "avatarTaskExecutor")
    public Executor avatarTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Separate executor for lightweight async tasks if needed.
     */
    @Bean(name = "generalTaskExecutor")
    public Executor generalTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

**Fix — Step 3: Enable Virtual Threads for Tomcat** in `application.properties`:

```/dev/null/application.properties#L1
spring.threads.virtual.enabled=true
```

This single property makes Tomcat handle every incoming HTTP request on a virtual thread instead of a platform thread. Your 100MB video upload that blocks while streaming to S3? Now it doesn't pin a Tomcat thread.

---

### 1.2 Security Audit — Hardcoded Secrets (Critical Severity)

Your `application.properties` has **hardcoded production-sensitive values**:

```Ai-interview-preparation--main/backend/src/main/resources/application.properties#L28-L38
# JWT Configuration
app.jwtSecret=ap7W6mpqkLxJgRRvZIAiABFLkQGS5LK+vrG3pI/ImO4=
app.jwtExpirationInMs=3600000

# OpenAI Configuration
openai.api.key=sk-placeholder
openai.model=gpt-4
openai.max-tokens=2000
openai.temperature=${OPENAI_TEMPERATURE:0.7}

# ...
did.api.key=placeholder
```

**Issue 1: The JWT secret is committed to Git.** Anyone who clones this repo can forge valid tokens for any user. This is the same class of vulnerability that caused the 2023 Microsoft Storm-0558 breach.

**Issue 2: Inconsistent env var strategy.** Some values use `${ENV_VAR:default}` syntax (like `aws.access.key.id`), but others like `openai.api.key` and `app.jwtSecret` are naked hardcoded values. This means even if you set environment variables, they won't override the hardcoded ones for JWT.

**Issue 3: Actuator exposed without auth:**

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/SecurityConfig.java#L37-L39
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
```

With `management.endpoint.health.show-details=always`, anyone hitting `/actuator/health` sees database connection status, disk space, and other internal details.

**Issue 4: AWS credentials via static keys:**

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/config/AWSS3Config.java#L23-L35
    @Bean
    public AmazonS3 amazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
```

This uses the deprecated AWS SDK v1 with hardcoded static credentials. In production on EC2/ECS/EKS, you should use **IAM Roles** — no credentials in code at all.

**Production Secrets Strategy:**

For a first deployment, use **Spring Boot Profiles + Environment Variables** rigorously. For a mature deployment, use **AWS Secrets Manager**.

**Rewritten `application.properties` (safe defaults, env-var-driven):**

```/dev/null/application.properties#L1-L40
# ─── Server ───
server.port=${SERVER_PORT:8080}

# ─── Database ───
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:interview_platform}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ─── JPA ───
spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:validate}
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# ─── JWT (NO DEFAULT — must be provided) ───
app.jwtSecret=${JWT_SECRET}
app.jwtExpirationInMs=${JWT_EXPIRATION:3600000}

# ─── AWS (prefer IAM roles; these are fallback for local dev) ───
aws.region=${AWS_REGION:us-east-1}
aws.s3.bucket.name=${AWS_S3_BUCKET_NAME}

# ─── AI API Keys (NO DEFAULTS — fail fast if missing) ───
openai.api.key=${OPENAI_API_KEY}
openai.model=${OPENAI_MODEL:gpt-4o}
elevenlabs.api.key=${ELEVENLABS_API_KEY}
did.api.key=${DID_API_KEY}
assemblyai.api.key=${ASSEMBLYAI_API_KEY}

# ─── Actuator (locked down) ───
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=when_authorized
management.endpoint.health.roles=ADMIN

# ─── Virtual Threads ───
spring.threads.virtual.enabled=true
```

Key change: **No default values for secrets.** Spring Boot will fail to start if `JWT_SECRET` is not set. This is intentional — you *want* a loud failure rather than silently running with a compromised key.

**Modernized S3 Config (AWS SDK v2 + IAM Role support):**

Add to `pom.xml`:
```/dev/null/pom.xml#L1-L5
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.25.60</version>
</dependency>
```

```/dev/null/AWSS3Config.java#L1-L38
@Configuration
public class AWSS3Config {

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    /**
     * AWS SDK v2 S3 client.
     * In production (EC2/ECS/EKS), uses IAM role automatically via DefaultCredentialsProvider.
     * Locally, falls back to ~/.aws/credentials or env vars AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY.
     * No hardcoded credentials ever.
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public String s3BucketName() {
        return bucketName;
    }
}
```

---

### 1.3 S3 Presigned Upload URLs — Eliminating the Server Bottleneck

Your current upload flow is a **server-proxied upload**:

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/VideoStorageService.java#L39-L66
    public String uploadVideo(MultipartFile file, Long userId, Long interviewId, Long questionId) {
        // ... 
        PutObjectRequest request = new PutObjectRequest(
                bucketName, key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.Private);
        amazonS3.putObject(request);
        // ...
    }
```

And your controller receives the `MultipartFile` directly:

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/controller/InterviewController.java#L58-L61
    public ResponseEntity<Map<String, Object>> submitResponse(
            @PathVariable Long interviewId,
            @RequestParam("questionId") Long questionId,
            @RequestParam("video") MultipartFile videoFile,
```

**The problem:** A 100MB video file is uploaded from the browser → Spring Boot server (held in memory/disk via `spring.servlet.multipart.max-file-size=100MB`) → then streamed to S3. Your server becomes a bandwidth bottleneck and a memory bomb.

**The fix — Presigned URL Architecture:**

```/dev/null/architecture.md#L1-L11
Current Flow (Bad):
  Browser → [100MB] → Spring Boot → [100MB] → S3

New Flow (Good):
  1. Browser → Spring Boot: "I need to upload a video for question 42"
  2. Spring Boot → Browser: "Here's a presigned PUT URL valid for 15 min"
  3. Browser → [100MB] → S3 (direct, no server involvement)
  4. Browser → Spring Boot: "Upload done, S3 key is X"
  5. Spring Boot → saves Response entity + triggers transcription
```

**Backend — New Presigned URL Endpoint + Service Method:**

```/dev/null/VideoStorageService.java#L1-L58
// Add to VideoStorageService.java

/**
 * Generate a presigned PUT URL for direct browser-to-S3 upload.
 * The URL is short-lived (15 min) and scoped to a specific S3 key.
 */
public PresignedUploadResult generateUploadUrl(Long userId, Long interviewId, Long questionId) {
    String timestamp = String.valueOf(Instant.now().toEpochMilli());
    String key = String.format("interviews/%d/%d/response_%d_%s.webm",
            userId, interviewId, questionId, timestamp);

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("video/webm")
            .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .putObjectRequest(putObjectRequest)
            .signatureDuration(Duration.ofMinutes(15))
            .build();

    PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

    return new PresignedUploadResult(
            presigned.url().toString(),
            key,
            presigned.expiration()
    );
}

// DTO for the result
public record PresignedUploadResult(
    String uploadUrl,
    String s3Key,
    Instant expiresAt
) {}
```

**New Controller Endpoints:**

```/dev/null/InterviewController.java#L1-L48
// --- Step 1: Get presigned URL ---
@PostMapping("/{interviewId}/upload-url")
public ResponseEntity<Map<String, Object>> getUploadUrl(
        @PathVariable Long interviewId,
        @RequestParam("questionId") Long questionId,
        HttpServletRequest httpRequest) {

    Long userId = getUserIdFromRequest(httpRequest);

    // Validate interview + question ownership (same checks as before)
    // ...

    var result = videoStorageService.generateUploadUrl(userId, interviewId, questionId);

    return ResponseEntity.ok(Map.of(
            "uploadUrl", result.uploadUrl(),
            "s3Key", result.s3Key(),
            "expiresAt", result.expiresAt().toString()
    ));
}

// --- Step 2: Confirm upload + trigger transcription ---
@PostMapping("/{interviewId}/confirm-upload")
public ResponseEntity<Map<String, Object>> confirmUpload(
        @PathVariable Long interviewId,
        @RequestParam("questionId") Long questionId,
        @RequestParam("s3Key") String s3Key,
        HttpServletRequest httpRequest) {

    Long userId = getUserIdFromRequest(httpRequest);

    // Verify the file actually exists in S3
    if (!videoStorageService.fileExists(s3Key)) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Video not found in S3. Upload may have failed."));
    }

    String videoUrl = videoStorageService.generatePresignedUrl(s3Key, 7);

    // Save Response entity + trigger async transcription
    // (refactored from submitResponse to accept a URL instead of MultipartFile)
    interviewService.confirmVideoUpload(userId, interviewId, questionId, videoUrl);

    return ResponseEntity.ok(Map.of(
            "message", "Upload confirmed. Transcription started.",
            "interviewId", interviewId,
            "questionId", questionId));
}
```

**Frontend — TypeScript Upload with Presigned URL:**

```/dev/null/videoUpload.service.ts#L1-L55
interface PresignedUploadResponse {
  uploadUrl: string;
  s3Key: string;
  expiresAt: string;
}

export async function uploadVideoToS3(
  interviewId: number,
  questionId: number,
  videoBlob: Blob
): Promise<void> {
  // Step 1: Get presigned URL from our backend
  const { data } = await api.post<PresignedUploadResponse>(
    `/api/interviews/${interviewId}/upload-url`,
    null,
    { params: { questionId } }
  );

  // Step 2: Upload directly to S3 (no auth header — S3 presigned URL has its own auth)
  await axios.put(data.uploadUrl, videoBlob, {
    headers: {
      'Content-Type': 'video/webm',
    },
    // Track upload progress for UI
    onUploadProgress: (progressEvent) => {
      const percent = Math.round(
        (progressEvent.loaded * 100) / (progressEvent.total ?? 1)
      );
      console.log(`Upload progress: ${percent}%`);
      // Dispatch to state management for progress bar
    },
  });

  // Step 3: Confirm upload with backend (triggers transcription)
  await api.post(`/api/interviews/${interviewId}/confirm-upload`, null, {
    params: { questionId, s3Key: data.s3Key },
  });
}
```

**S3 CORS Configuration** (must be set on the bucket for browser direct uploads):

```/dev/null/s3-cors.json#L1-L15
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["PUT"],
    "AllowedOrigins": [
      "http://localhost:3000",
      "https://yourdomain.com"
    ],
    "ExposeHeaders": ["ETag"],
    "MaxAgeSeconds": 3600
  }
]
```

---

## PHASE 2: AI Orchestration & Latency Management

### 2.1 The Latency Problem — Quantified

Let me trace the actual blocking chain in your `startInterview()` flow:

```Ai-interview-preparation--main/backend/src/main/java/com/interview/platform/service/InterviewService.java#L69-L127
    @Transactional
    public InterviewDTO startInterview(Long userId, Long resumeId, Long jobRoleId) {
        // ... validation ...
        
        // BLOCKING CALL — 5-15 seconds to GPT-4
        List<QuestionDTO> generatedQuestions = openAIService.generateInterviewQuestions(
                resumeText, jobRole.getTitle());

        for (QuestionDTO dto : generatedQuestions) {
            // Save question...
            
            // ASYNC fire — but within a @Transactional boundary
            avatarVideoService.generateAvatarVideoAsync(questionText, questionId)
                    .thenAccept(videoUrl -> {
                        // This callback runs OUTSIDE the original transaction
                        questionRepository.findById(questionId).ifPresent(q -> {
                            q.setAvatarVideoUrl(videoUrl);
                            questionRepository.save(q); // New transaction here
                        });
                    });
        }
        return mapToInterviewDTO(interview, savedQuestions);
    }
```

**Hidden Bug:** The `@Transactional` on `startInterview()` commits when the method returns. But the `@Async` `generateAvatarVideoAsync` tasks are fired off using the `avatarTaskExecutor` — these are *detached from the transaction*. The `.thenAccept()` callback runs in a random thread with no transaction. The `questionRepository.save()` inside the callback works because Spring Data creates an implicit transaction, but there's a **race condition**: if two avatar generations complete simultaneously and both try to look up and update question state, you could get stale reads.

**The real latency budget per avatar video:**
1. **ElevenLabs TTS**: ~2-5 seconds
2. **S3 upload** (TTS audio): ~1-2 seconds
3. **D-ID talk creation**: ~1-3 seconds
4. **D-ID polling**: 30-180 seconds (60 polls × 3s interval)
5. **D-ID video download + S3 reupload**: ~3-10 seconds

**Total per question: 37-200 seconds. For 10 questions: 6-33 minutes** (even with 5 parallel threads).

### 2.2 Asynchronous Event-Driven Architecture

The fix has two parts: (A) decouple the HTTP response from the AI processing, and (B) use a proper state machine for tracking progress.

**Part A — Interview Status State Machine:**

```/dev/null/InterviewStatus.java#L1-L14
public enum InterviewStatus {
    CREATED,          // Interview entity saved, questions saved to DB
    GENERATING_VIDEOS,// Avatar videos being generated async
    IN_PROGRESS,      // All videos ready, user can start answering
    PROCESSING,       // User completed, feedback being generated
    COMPLETED,        // Feedback ready
    FAILED            // Something went wrong
}
```

**Part B — Decoupled Orchestration Service:**

```/dev/null/InterviewOrchestrationService.java#L1-L94
@Service
public class InterviewOrchestrationService {

    private final AvatarVideoService avatarVideoService;
    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;

    /**
     * Fire-and-forget: generate all avatar videos for an interview.
     * Updates question entities as each video completes.
     * Updates interview status when ALL videos are done (or some fail).
     *
     * Runs on a virtual thread — Thread.sleep() in polling is free.
     */
    @Async("avatarTaskExecutor")
    @Transactional
    public void generateAllAvatarVideos(Long interviewId) {
        var interview = interviewRepository.findById(interviewId).orElseThrow();
        var questions = questionRepository.findByInterviewIdOrderByQuestionNumber(interviewId);

        // Launch all video generations concurrently using structured concurrency
        // (Java 21 feature, or use CompletableFuture.allOf)
        List<CompletableFuture<Void>> futures = questions.stream()
                .map(question -> CompletableFuture.runAsync(() -> {
                    try {
                        String videoUrl = avatarVideoService.generateAvatarVideo(
                                question.getQuestionText(), question.getId());

                        // Each update in its own transaction
                        updateQuestionVideoUrl(question.getId(), videoUrl);

                    } catch (Exception e) {
                        logger.log(Level.SEVERE,
                                "Avatar video failed for question " + question.getId(), e);
                        // Mark question as failed but don't kill the whole batch
                    }
                }, Executors.newVirtualThreadPerTaskExecutor()))
                .toList();

        // Wait for all to complete (virtual threads make this efficient)
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .join();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Some avatar videos failed for interview " + interviewId, e);
        }

        // Transition interview to IN_PROGRESS (ready for user)
        updateInterviewStatus(interviewId, InterviewStatus.IN_PROGRESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void updateQuestionVideoUrl(Long questionId, String videoUrl) {
        questionRepository.findById(questionId).ifPresent(q -> {
            q.setAvatarVideoUrl(videoUrl);
            questionRepository.save(q);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void updateInterviewStatus(Long interviewId, InterviewStatus status) {
        interviewRepository.findById(interviewId).ifPresent(i -> {
            i.setStatus(status);
            interviewRepository.save(i);
        });
    }
}
```

Now `startInterview()` returns immediately with status `GENERATING_VIDEOS`:

```/dev/null/InterviewService.java#L1-L30
@Transactional
public InterviewDTO startInterview(Long userId, Long resumeId, Long jobRoleId) {
    // ... validation, entity creation ...

    // Synchronous — generate questions with GPT (5-15s, acceptable for user wait)
    List<QuestionDTO> generatedQuestions = openAIService.generateInterviewQuestions(
            resumeText, jobRole.getTitle());

    // Save questions to DB
    List<Question> savedQuestions = saveQuestions(interview, generatedQuestions);

    // Set status to GENERATING_VIDEOS
    interview.setStatus(InterviewStatus.GENERATING_VIDEOS);
    interviewRepository.save(interview);

    // Fire-and-forget: avatar generation in background
    orchestrationService.generateAllAvatarVideos(interview.getId());

    // Return immediately — frontend will poll for video readiness
    return mapToInterviewDTO(interview, savedQuestions);
}
```

### 2.3 Frontend Status Tracking — SSE vs. Polling

For this use case, **Server-Sent Events (SSE)** is better than WebSockets (one-directional server→client) and simpler than polling (no wasted requests).

However, given your current stack doesn't include WebSocket support and SSE requires keeping connections open, **polling is the pragmatic first choice** with an upgrade path to SSE.

**Backend — Polling Endpoint (already partially exists):**

Your existing `GET /api/interviews/{interviewId}` already returns question data with `avatarVideoUrl`. The frontend just needs to poll it.

**Frontend — Intelligent Polling Hook (TypeScript):**

```/dev/null/useInterviewPolling.ts#L1-L72
import { useCallback, useEffect, useRef, useState } from 'react';
import { InterviewDTO } from '../types';
import { api } from '../services/api.service';

interface UseInterviewPollingOptions {
  interviewId: number;
  enabled: boolean;
  /** Stop polling when this returns true */
  isComplete: (data: InterviewDTO) => boolean;
  intervalMs?: number;
  maxAttempts?: number;
}

interface UseInterviewPollingResult {
  data: InterviewDTO | null;
  isLoading: boolean;
  error: Error | null;
  videosReady: number;
  totalQuestions: number;
}

export function useInterviewPolling({
  interviewId,
  enabled,
  isComplete,
  intervalMs = 5000,
  maxAttempts = 120, // 10 minutes at 5s intervals
}: UseInterviewPollingOptions): UseInterviewPollingResult {
  const [data, setData] = useState<InterviewDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const attemptRef = useRef(0);
  const timerRef = useRef<ReturnType<typeof setTimeout>>();

  const poll = useCallback(async () => {
    if (attemptRef.current >= maxAttempts) {
      setError(new Error('Interview preparation timed out. Please try again.'));
      setIsLoading(false);
      return;
    }

    try {
      const { data: interview } = await api.get<InterviewDTO>(
        `/api/interviews/${interviewId}`
      );
      setData(interview);
      attemptRef.current += 1;

      if (isComplete(interview)) {
        setIsLoading(false);
        return; // Stop polling
      }

      // Schedule next poll
      timerRef.current = setTimeout(poll, intervalMs);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Polling failed'));
      setIsLoading(false);
    }
  }, [interviewId, intervalMs, maxAttempts, isComplete]);

  useEffect(() => {
    if (enabled) poll();
    return () => clearTimeout(timerRef.current);
  }, [enabled, poll]);

  const videosReady = data?.questions?.filter((q) => q.avatarVideoUrl).length ?? 0;
  const totalQuestions = data?.questions?.length ?? 0;

  return { data, isLoading, error, videosReady, totalQuestions };
}
```

**Usage in the Interview Room component:**

```/dev/null/InterviewRoom.tsx#L1-L28
function InterviewRoom({ interviewId }: { interviewId: number }) {
  const { data, isLoading, videosReady, totalQuestions } = useInterviewPolling({
    interviewId,
    enabled: true,
    isComplete: (interview) =>
      interview.status === 'IN_PROGRESS' &&
      interview.questions?.every((q) => q.avatarVideoUrl != null) === true,
  });

  if (isLoading) {
    return (
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <CircularProgress variant="determinate" value={(videosReady / totalQuestions) * 100} />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Preparing your interview...
        </Typography>
        <Typography color="text.secondary">
          {videosReady} of {totalQuestions} questions ready
        </Typography>
      </Box>
    );
  }

  // Render the interview room with avatar videos
  return <QuestionPlayer questions={data!.questions} />;
}
```

---

## PHASE 3: Cost Optimization & Caching

### 3.1 Cost Analysis — Where the Money Goes

Let me quantify the cost per interview session based on your current code:

| API | Operation | Cost/Interview (10 Qs) | Notes |
|:---|:---|:---|:---|
| **OpenAI GPT-4** | Question generation | ~$0.30 | ~2000 tokens in/out |
| **OpenAI GPT-4** | Feedback evaluation | ~$0.20 | ~1500 tokens |
| **ElevenLabs** | 10 × TTS | ~$0.15 | ~200 chars/question |
| **D-ID** | 10 × avatar video | **~$5.00** | $0.50/video on paid plan |
| **AssemblyAI** | 10 × transcription | ~$0.36 | ~2 min/answer × $0.0036/15s |
| **AWS S3** | Storage + transfers | ~$0.02 | Negligible |
| **Total** | | **~$6.03/interview** | |

**D-ID is 83% of your cost.** And here's the insight: many interview questions are generic. "Tell me about a challenging project" will be asked to multiple users. The avatar video for that question is **identical** regardless of who's watching.

### 3.2 Caching Strategy — Two-Tier Cache

**Tier 1: Question-Text-to-Video Cache (Exact Match)**

Before generating a new avatar video, hash the question text and check if we've already generated a video for it.

Add the dependency to `pom.xml`:
```/dev/null/pom.xml#L1-L4
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

**The cache layer wraps `AvatarVideoService`:**

```/dev/null/CachedAvatarVideoService.java#L1-L72
@Service
public class CachedAvatarVideoService {

    private final AvatarVideoService avatarVideoService;
    private final AvatarVideoCacheRepository cacheRepository;

    /**
     * Check if we've already generated an avatar video for this exact question text.
     * Key: SHA-256 hash of normalized question text.
     * Value: S3 URL of the existing video.
     *
     * This saves ~$0.50 per cache hit (D-ID) + ~$0.015 (ElevenLabs).
     */
    public String getOrGenerateAvatarVideo(String questionText, Long questionId) {
        String cacheKey = computeCacheKey(questionText);

        // Check DB cache
        Optional<AvatarVideoCache> cached = cacheRepository.findByCacheKey(cacheKey);
        if (cached.isPresent() && !isExpired(cached.get())) {
            logger.info("CACHE HIT for question ID: " + questionId
                    + " — reusing video: " + cached.get().getVideoUrl());
            return cached.get().getVideoUrl();
        }

        // Cache miss — generate new video
        logger.info("CACHE MISS for question ID: " + questionId + " — generating new video");
        String videoUrl = avatarVideoService.generateAvatarVideo(questionText, questionId);

        // Store in cache
        AvatarVideoCache entry = new AvatarVideoCache();
        entry.setCacheKey(cacheKey);
        entry.setQuestionTextHash(cacheKey);
        entry.setVideoUrl(videoUrl);
        entry.setCreatedAt(LocalDateTime.now());
        entry.setExpiresAt(LocalDateTime.now().plusDays(30)); // Videos valid for 30 days
        cacheRepository.save(entry);

        return videoUrl;
    }

    /**
     * Normalize and hash the question text.
     * "What is polymorphism?" and "what is polymorphism?" hit the same cache entry.
     */
    private String computeCacheKey(String questionText) {
        String normalized = questionText.toLowerCase().strip()
                .replaceAll("\\s+", " ")          // collapse whitespace
                .replaceAll("[^a-z0-9 ]", "");     // remove punctuation
        return DigestUtils.sha256Hex(normalized);
    }

    private boolean isExpired(AvatarVideoCache entry) {
        return entry.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
```

**Cache Entity:**

```/dev/null/AvatarVideoCache.java#L1-L22
@Entity
@Table(name = "avatar_video_cache", indexes = {
    @Index(name = "idx_cache_key", columnList = "cache_key", unique = true)
})
public class AvatarVideoCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", nullable = false, unique = true, length = 64)
    private String cacheKey;

    @Column(name = "question_text_hash", nullable = false, length = 64)
    private String questionTextHash;

    @Column(name = "video_url", nullable = false, length = 2048)
    private String videoUrl;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // getters, setters...
}
```

**Tier 2: OpenAI Response Caching (Semantic)**

For question generation, you can't do exact caching because resumes differ. But for *feedback generation*, if a user retakes the same interview and gives a very similar answer, you could cache feedback. However, this is lower priority since OpenAI costs are only ~$0.50/interview vs. D-ID's ~$5.00.

**More impactful OpenAI optimization — switch to `gpt-4o`:**

Your current config uses `gpt-4`:

```Ai-interview-preparation--main/backend/src/main/resources/application.properties#L34
openai.model=gpt-4
```

GPT-4o is **~75% cheaper** and **2× faster** than GPT-4 with comparable quality for structured output tasks like question generation and feedback. Change to:

```/dev/null/application.properties#L1
openai.model=gpt-4o
```

**Cost Impact of Both Changes:**

| Scenario | Before | After (w/ cache + gpt-4o) | Savings |
|:---|:---|:---|:---|
| First unique interview | $6.03 | $1.40 | 77% |
| Repeat with similar Qs (80% hit rate) | $6.03 | $0.52 | 91% |

---

## PHASE 4: Frontend Robustness & State Management

### 4.1 Context API — Where It Will Break

Your technical reference describes using `AuthContext` for global auth state. This is fine for authentication. The problem will emerge in the **Interview Room**, where you have:

1. **Video recording state** (MediaRecorder, Blob, stream)
2. **Upload progress state** (0-100%)
3. **Per-question state** (answered/not, transcription status, avatar video ready)
4. **Interview-level state** (current question index, overall status)
5. **Timer state** (recording duration)

If you put all of this into a single Context, every state update (like upload progress ticking from 47% to 48%) will **re-render every component** consuming that context — including the video player, the question list, and the recording UI.

**Recommendation: Stay with Context API for auth, use Zustand for interview state.**

Zustand is 1KB, has no boilerplate, and supports selector-based subscriptions (only re-render when the specific slice of state you care about changes).

```/dev/null/interviewStore.ts#L1-L95
import { create } from 'zustand';

// ── Types ──
interface QuestionState {
  questionId: number;
  questionText: string;
  avatarVideoUrl: string | null;
  isAnswered: boolean;
  uploadProgress: number;
  transcriptionStatus: 'pending' | 'processing' | 'completed' | 'failed';
}

interface InterviewState {
  interviewId: number | null;
  status: string;
  currentQuestionIndex: number;
  questions: QuestionState[];
  isRecording: boolean;
  recordingDuration: number;

  // Actions
  setInterview: (id: number, questions: QuestionState[]) => void;
  setStatus: (status: string) => void;
  goToNextQuestion: () => void;
  goToPreviousQuestion: () => void;
  setRecording: (recording: boolean) => void;
  setUploadProgress: (questionId: number, progress: number) => void;
  setQuestionAnswered: (questionId: number) => void;
  updateAvatarVideoUrl: (questionId: number, url: string) => void;
  setTranscriptionStatus: (questionId: number, status: QuestionState['transcriptionStatus']) => void;
  reset: () => void;
}

const initialState = {
  interviewId: null,
  status: '',
  currentQuestionIndex: 0,
  questions: [],
  isRecording: false,
  recordingDuration: 0,
};

export const useInterviewStore = create<InterviewState>((set, get) => ({
  ...initialState,

  setInterview: (id, questions) =>
    set({ interviewId: id, questions, currentQuestionIndex: 0 }),

  setStatus: (status) => set({ status }),

  goToNextQuestion: () =>
    set((state) => ({
      currentQuestionIndex: Math.min(
        state.currentQuestionIndex + 1,
        state.questions.length - 1
      ),
    })),

  goToPreviousQuestion: () =>
    set((state) => ({
      currentQuestionIndex: Math.max(state.currentQuestionIndex - 1, 0),
    })),

  setRecording: (recording) => set({ isRecording: recording }),

  // Only re-renders components that subscribe to this specific question's progress
  setUploadProgress: (questionId, progress) =>
    set((state) => ({
      questions: state.questions.map((q) =>
        q.questionId === questionId ? { ...q, uploadProgress: progress } : q
      ),
    })),

  setQuestionAnswered: (questionId) =>
    set((state) => ({
      questions: state.questions.map((q) =>
        q.questionId === questionId ? { ...q, isAnswered: true } : q
      ),
    })),

  updateAvatarVideoUrl: (questionId, url) =>
    set((state) => ({
      questions: state.questions.map((q) =>
        q.questionId === questionId ? { ...q, avatarVideoUrl: url } : q
      ),
    })),

  setTranscriptionStatus: (questionId, status) =>
    set((state) => ({
      questions: state.questions.map((q) =>
        q.questionId === questionId ? { ...q, transcriptionStatus: status } : q
      ),
    })),

  reset: () => set(initialState),
}));
```

**Selector-based subscription prevents unnecessary re-renders:**

```/dev/null/UploadProgress.tsx#L1-L15
// This component ONLY re-renders when this specific question's progress changes
function UploadProgressBar({ questionId }: { questionId: number }) {
  const progress = useInterviewStore(
    (state) => state.questions.find((q) => q.questionId === questionId)?.uploadProgress ?? 0
  );

  return (
    <LinearProgress
      variant="determinate"
      value={progress}
      sx={{ width: '100%', mt: 1 }}
    />
  );
}
```

### 4.2 Error Handling — Graceful Degradation for Third-Party API Failures

Your system depends on 4 external APIs. Any of them can fail. The strategy: **define fallback behaviors per API, wrap them in a typed error boundary system.**

**TypeScript API Error Types:**

```/dev/null/apiErrors.ts#L1-L75
// ── Error Classification ──
export enum ApiErrorCode {
  // AI Service Errors
  AVATAR_VIDEO_GENERATION_FAILED = 'AVATAR_VIDEO_GENERATION_FAILED',
  SPEECH_SYNTHESIS_FAILED = 'SPEECH_SYNTHESIS_FAILED',
  TRANSCRIPTION_FAILED = 'TRANSCRIPTION_FAILED',
  QUESTION_GENERATION_FAILED = 'QUESTION_GENERATION_FAILED',
  FEEDBACK_GENERATION_FAILED = 'FEEDBACK_GENERATION_FAILED',

  // Infrastructure Errors
  VIDEO_UPLOAD_FAILED = 'VIDEO_UPLOAD_FAILED',
  NETWORK_ERROR = 'NETWORK_ERROR',
  AUTH_EXPIRED = 'AUTH_EXPIRED',
  SERVER_ERROR = 'SERVER_ERROR',
}

export interface ApiError {
  code: ApiErrorCode;
  message: string;
  retryable: boolean;
  fallbackAvailable: boolean;
}

// ── Fallback Strategy Map ──
export const FALLBACK_STRATEGIES: Record<ApiErrorCode, FallbackStrategy> = {
  [ApiErrorCode.AVATAR_VIDEO_GENERATION_FAILED]: {
    userMessage: 'AI avatar is temporarily unavailable. Showing text question instead.',
    action: 'SHOW_TEXT_ONLY',
    retryable: true,
  },
  [ApiErrorCode.SPEECH_SYNTHESIS_FAILED]: {
    userMessage: 'Voice synthesis unavailable. Question displayed as text.',
    action: 'SHOW_TEXT_ONLY',
    retryable: true,
  },
  [ApiErrorCode.TRANSCRIPTION_FAILED]: {
    userMessage: 'Auto-transcription failed. You can type your response summary instead.',
    action: 'ENABLE_MANUAL_TRANSCRIPTION',
    retryable: true,
  },
  [ApiErrorCode.QUESTION_GENERATION_FAILED]: {
    userMessage: 'Could not generate personalized questions. Using standard questions for this role.',
    action: 'USE_FALLBACK_QUESTIONS',
    retryable: false,
  },
  [ApiErrorCode.FEEDBACK_GENERATION_FAILED]: {
    userMessage: 'AI feedback is being generated. We will email you when ready.',
    action: 'DEFER_FEEDBACK',
    retryable: true,
  },
  [ApiErrorCode.VIDEO_UPLOAD_FAILED]: {
    userMessage: 'Video upload failed. Please check your connection and try again.',
    action: 'RETRY_UPLOAD',
    retryable: true,
  },
  [ApiErrorCode.NETWORK_ERROR]: {
    userMessage: 'Connection lost. Your progress is saved. Reconnecting...',
    action: 'AUTO_RETRY',
    retryable: true,
  },
  [ApiErrorCode.AUTH_EXPIRED]: {
    userMessage: 'Session expired. Please log in again.',
    action: 'REDIRECT_LOGIN',
    retryable: false,
  },
  [ApiErrorCode.SERVER_ERROR]: {
    userMessage: 'Something went wrong on our end. Please try again.',
    action: 'RETRY',
    retryable: true,
  },
};

interface FallbackStrategy {
  userMessage: string;
  action: string;
  retryable: boolean;
}
```

**Graceful Degradation Component — "D-ID Down? Show Text.":**

```/dev/null/QuestionDisplay.tsx#L1-L46
interface QuestionDisplayProps {
  question: QuestionState;
}

function QuestionDisplay({ question }: QuestionDisplayProps) {
  const [videoError, setVideoError] = useState(false);

  // Case 1: Avatar video is ready and loads fine
  if (question.avatarVideoUrl && !videoError) {
    return (
      <Box>
        <video
          src={question.avatarVideoUrl}
          controls
          autoPlay
          onError={() => setVideoError(true)}
          style={{ width: '100%', borderRadius: 8 }}
        />
      </Box>
    );
  }

  // Case 2: Fallback — text-only question with visual styling
  return (
    <Paper elevation={2} sx={{ p: 4, textAlign: 'center' }}>
      {question.avatarVideoUrl === null && !videoError && (
        <Alert severity="info" sx={{ mb: 2 }}>
          AI avatar is being generated. Question shown as text for now.
        </Alert>
      )}
      {videoError && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          {FALLBACK_STRATEGIES[ApiErrorCode.AVATAR_VIDEO_GENERATION_FAILED].userMessage}
        </Alert>
      )}
      <Avatar sx={{ width: 80, height: 80, mx: 'auto', mb: 2, bgcolor: 'primary.main' }}>
        <SmartToyIcon sx={{ fontSize: 40 }} />
      </Avatar>
      <Typography variant="h5" gutterBottom>
        {question.questionText}
      </Typography>
    </Paper>
  );
}
```

**Backend — Circuit Breaker with Resilience4j:**

Add to `pom.xml`:
```/dev/null/pom.xml#L1-L4
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.2.0</version>
</dependency>
```

Add to `application.properties`:
```/dev/null/application.properties#L1-L18
# ── Circuit Breaker Config ──
# D-ID: open circuit after 3 failures in 10 calls, stay open for 60s
resilience4j.circuitbreaker.instances.did-api.sliding-window-size=10
resilience4j.circuitbreaker.instances.did-api.failure-rate-threshold=30
resilience4j.circuitbreaker.instances.did-api.wait-duration-in-open-state=60s
resilience4j.circuitbreaker.instances.did-api.permitted-number-of-calls-in-half-open-state=3

# ElevenLabs: similar config
resilience4j.circuitbreaker.instances.elevenlabs-api.sliding-window-size=10
resilience4j.circuitbreaker.instances.elevenlabs-api.failure-rate-threshold=30
resilience4j.circuitbreaker.instances.elevenlabs-api.wait-duration-in-open-state=60s

# OpenAI: more tolerant (critical path)
resilience4j.circuitbreaker.instances.openai-api.sliding-window-size=20
resilience4j.circuitbreaker.instances.openai-api.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.openai-api.wait-duration-in-open-state=30s
```

**Apply to AvatarVideoService:**

```/dev/null/AvatarVideoService.java#L1-L24
@Service
public class AvatarVideoService {

    // ... existing fields ...

    @CircuitBreaker(name = "did-api", fallbackMethod = "avatarVideoFallback")
    @Retry(name = "did-api", fallbackMethod = "avatarVideoFallback")
    public String generateAvatarVideo(String questionText, Long questionId) {
        // ... existing implementation — remove manual retry loop,
        //     Resilience4j handles retries now ...
        String audioUrl = textToSpeechService.generateSpeech(questionText, questionId);
        String talkId = createTalk(audioUrl);
        String resultUrl = pollForVideoCompletion(talkId);
        return downloadAndUploadToS3(resultUrl, questionId);
    }

    /**
     * Fallback: if D-ID circuit is open, return null.
     * The frontend will show text-only question.
     */
    private String avatarVideoFallback(String questionText, Long questionId, Exception ex) {
        logger.warning("D-ID circuit breaker OPEN — returning null for question " + questionId);
        return null; // Question entity will have avatarVideoUrl = null → frontend shows text
    }
}
```

---

## Summary — Priority-Ordered Execution Roadmap

| Priority | Task | Impact | Effort |
|:---|:---|:---|:---|
| 🔴 P0 | Remove hardcoded JWT secret + API keys from `application.properties` | Security | 1 hour |
| 🔴 P0 | Restrict actuator endpoints | Security | 15 min |
| 🔴 P0 | Fix Java version to 21, enable virtual threads | Scalability | 30 min |
| 🟠 P1 | Implement presigned S3 upload URLs | Server load, memory | 4 hours |
| 🟠 P1 | Upgrade AWS SDK v1 → v2 with IAM roles | Security | 2 hours |
| 🟠 P1 | Add avatar video cache (DB-backed) | **Cost: -83%** | 3 hours |
| 🟡 P2 | Switch OpenAI model to `gpt-4o` | **Cost: -75% on OpenAI** | 5 min |
| 🟡 P2 | Add Resilience4j circuit breakers | Reliability | 3 hours |
| 🟡 P2 | Implement interview status state machine + polling | UX | 4 hours |
| 🟢 P3 | Frontend Zustand store for interview state | Performance | 2 hours |
| 🟢 P3 | Graceful degradation (text fallback for avatar) | UX | 2 hours |
| 🟢 P3 | SSE for real-time progress (replaces polling) | UX polish | 4 hours |

The P0 items should be done before any deployment. The P1 items should be done before any real users touch the system. P2 and P3 can be iterated on once the foundation is solid.

