# FindIt – Smart Lost &amp; Found Platform

> **Lost something? Find it faster.**  
> An intelligent full-stack platform for universities, corporate offices, and local communities to report lost and found items, discover matches with a weighted scoring engine, communicate securely, and verify ownership before handover.

---

## 🌟 Key Features

1. **Role-Based Authorization &amp; Security (Spring Security 6 + JJWT):**
   - Three distinct roles: `USER`, `ADMIN`, `SUPER_ADMIN`.
   - BCrypt password hashing, stateless JWT authorization with claim evaluation.
   - Comprehensive system audit trail logging actor, IP, timestamp, and target entity.

2. **Smart 100-Point Weighted Heuristic Matching Engine:**
   - Evaluates category compatibility (25 pts), title token similarity (20 pts), brand match (15 pts), color similarity (10 pts), location proximity (15 pts), date difference (10 pts), and description keywords (5 pts).
   - Match classifications: **Strong Match ($\ge 80$)**, **Possible Match ($60 - 79$)**, **Weak Match ($< 60$)**.
   - Automated deduplication of match notifications and global recalculation triggers.

3. **Public Masking of Confidential Clues:**
   - Found item listings feature public descriptions for general viewing.
   - Confidential details (e.g. serial numbers, cards inside, private markings) are strictly masked and viewable only by the finder or administrators.

4. **Cryptographic Handover Recovery Workflow:**
   - Ownership claims require private evidence and distinctive characteristics.
   - Administrators approve/reject claims with formal decision notes.
   - Upon claim approval, a secret verification code (`RCV-XXXX-XXXX`) is minted.
   - Handover meeting is scheduled, and entering the verification code atomically transitions the item status to `RECOVERED`.

5. **Responsive, Polished User Experience:**
   - React 18 SPA built with Vite and Tailwind CSS.
   - Dynamic search, filters, badges, and notification drawer.
   - Administrative portal with KPI metrics, moderation queues, role switcher, and audit tables.

---

## 🛠️ Technology Stack

- **Backend:** Java 21/25, Spring Boot 3.3.5, Spring Data JPA, Hibernate, Spring Security, Spring Mail, JJWT 0.12.6, Maven
- **Database:** MySQL 8.0 (`findit_db`)
- **Frontend:** React 18, Vite 5, Tailwind CSS 3.4, React Router DOM 6, Axios, Lucide React, React Hook Form
- **API Documentation:** OpenAPI 3 / Swagger (`/swagger-ui.html`)

---

## 🚀 Quick Start Guide

### Prerequisites
- Java 21 or higher (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Node.js 18+ and npm (`npm -v`)
- MySQL 8.0 running on `localhost:3306`

### 1. Database Setup
Log into MySQL and ensure the database exists:
```sql
CREATE DATABASE IF NOT EXISTS findit_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Backend Configuration &amp; Startup
Navigate to the backend directory:
```bash
cd backend
```
Verify or update `src/main/resources/application.properties` (or set environment variables):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/findit_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Aadimal@0620
```
Build and run the Spring Boot backend:
```bash
mvn clean package -DskipTests
java -jar target/findit-backend-1.0.0.jar
```
*The backend automatically seeds initial test users, sample items, and calculates heuristic matches on first run.*
- API Base: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Frontend Startup
In a separate terminal, navigate to the frontend directory:
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## 👥 Default Demo Credentials

The platform is pre-seeded with the following test accounts:

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `superadmin@findit.org` | `SuperAdmin@123` | Full access, user role management, system audit trail |
| **Admin** | `admin@findit.org` | `Admin@123` | Claims moderation, confidential clue inspection, match recalculation |
| **User (John)** | `john.doe@university.edu` | `User@1234` | Report lost wallet, inspect 97.5% match, submit claim |
| **User (Sarah)** | `sarah.smith@university.edu` | `User@1234` | Reported found wallet with confidential student card clues |

---

## 🧪 Testing

### Backend Unit &amp; Integration Tests
Run JUnit 5 test suites covering heuristic scoring and claim authorization:
```bash
mvn test -f backend/pom.xml
```

### Frontend Production Build
```bash
cd frontend
npm run build
```

---

## 📁 Repository Structure

```
FindIt/
├── backend/
│   ├── src/main/java/com/findit/
│   │   ├── config/          # Security, WebMvc, Swagger, DatabaseSeeder
│   │   ├── controller/      # REST API Endpoints
│   │   ├── dto/             # Request & Response Data Transfer Objects
│   │   ├── entity/          # JPA Entities (User, LostItem, FoundItem, Claim, Match, etc.)
│   │   ├── enums/           # Role, ItemStatus, ItemCategory, ClaimStatus, etc.
│   │   ├── exception/       # GlobalExceptionHandler and Custom Exceptions
│   │   ├── mapper/          # Entity <-> DTO Mappers with clue masking
│   │   ├── matching/        # 100-Point Heuristic Matching Rules & Engine
│   │   ├── repository/      # Spring Data JPA Repositories
│   │   ├── security/        # JWT Authentication Filter, Token Provider, UserPrincipal
│   │   └── service/         # Business Logic Layer & Services
│   ├── src/test/java/       # JUnit 5 & Mockito Tests
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/      # Navbar, Footer, ItemCard, MatchCard, ClaimCard, Badges
│   │   ├── context/         # AuthContext, NotificationContext
│   │   ├── pages/           # Home, Login, Register, LostItems, FoundItems, AdminDashboard
│   │   ├── services/        # Axios API Client & Services
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.js
├── docs/
│   ├── architecture.md      # Architectural diagrams & scoring formula
│   ├── database-schema.md   # Entity-Relationship diagram & table specifications
│   └── api-documentation.md # Complete REST API Reference
├── .env.example
├── .gitignore
└── README.md
```

---

## 📄 License
This project is open-source under the MIT License.
