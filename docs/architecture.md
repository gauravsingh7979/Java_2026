# FindIt – Smart Lost & Found Platform Architecture

## 1. System Overview
FindIt is a production-grade full-stack platform that centralizes lost and found reporting for universities, corporate offices, and local communities. It solves the fragmentation of manual lost-and-found processes by combining **role-based access control**, **heuristic weighted matching**, **confidential clue masking**, and a **cryptographic handover recovery verification workflow**.

```mermaid
flowchart TD
    subgraph Frontend ["Frontend (React 18 + Vite + Tailwind CSS)"]
        UI[Responsive SPA Web Application]
        Router[React Router DOM v6]
        Context[Auth & Notification Contexts]
        Axios[Axios Interceptors + Bearer JWT]
    end

    subgraph Backend ["Backend (Spring Boot 3.3.5 / Java 21)"]
        Filter[JwtAuthenticationFilter + SecurityFilterChain]
        Controllers[REST Controllers]
        Services[Business Logic & Transactional Services]
        MatchingEngine[Weighted Heuristic Matching Engine]
        Repositories[Spring Data JPA Repositories]
    end

    subgraph Storage ["Persistent Storage"]
        MySQL[(MySQL 8 Database)]
        FileSystem[(Secure File Upload Storage)]
    end

    UI --> Router
    Router --> Context
    Context --> Axios
    Axios -->|REST API Requests / JSON + Multipart| Filter
    Filter --> Controllers
    Controllers --> Services
    Services --> MatchingEngine
    Services --> Repositories
    Repositories --> MySQL
    Services --> FileSystem
```

---

## 2. Key Architectural Modules

### 2.1 Authentication & Role-Based Authorization
- **Stateless JWT Security**: JJWT 0.12.6 with HMAC-SHA256 signatures and claims for `sub`, `userId`, `role`, and `fullName`.
- **BCrypt Password Hashing**: Passwords stored as BCrypt salted hashes (strength 10).
- **Three-Tier RBAC**:
  - `ROLE_USER`: Can submit lost/found reports, view matches, submit claims for found items, coordinate handovers.
  - `ROLE_ADMIN`: Can moderate all reports, review ownership claims, view confidential item clues, approve/reject claims, view platform telemetry.
  - `ROLE_SUPER_ADMIN`: Administrative superpowers, manage administrator roles, review system audit trail logs.

### 2.2 Public Masking of Sensitive Clues
To eliminate fraudulent claims:
- Found item reports accept a **`publicDescription`** and **`privateDetails`**.
- The `ItemMapper` evaluates the requesting user: only the original finder or authorized administrators (`ROLE_ADMIN`, `ROLE_SUPER_ADMIN`) receive `privateDetails`.
- Unauthenticated visitors and general community members receive `privateDetails: null`.

---

## 3. Heuristic Smart Matching Engine

The core differentiator of FindIt is its **100-Point Weighted Heuristic Engine**:

$$\text{Score} = \sum_{i=1}^{n} w_i \cdot s_i$$

| Parameter | Max Points | Evaluation Algorithm |
| :--- | :---: | :--- |
| **Category Compatibility** | 25 | Strict match across compatible categories; 0 if incompatible |
| **Title Token Similarity** | 20 | Case-insensitive tokenization with Jaccard and word-overlap scoring |
| **Brand Similarity** | 15 | Levenshtein distance $\le 1$ or substring match |
| **Color Similarity** | 10 | Exact or normalized color keyword matching |
| **Location Proximity** | 15 | Building/room token overlap and campus zone proximity |
| **Date Proximity** | 10 | Date decay: 10 pts $\le 1$ day; 8 pts $\le 3$ days; 5 pts $\le 7$ days; 2 pts $\le 14$ days |
| **Keyword Description** | 5 | Description keyword and token intersection |
| **Total Possible** | **100** | Max score |

### Match Classifications:
- **Strong Match ($\ge 80$ points)**: Generates high-priority in-app and email notifications.
- **Possible Match ($60 - 79$ points)**: Displayed with amber alert badges.
- **Weak Match ($< 60$ points)**: Displayed as lower confidence reference.

---

## 4. Ownership Claim & Secure Recovery Workflow

```mermaid
sequenceDiagram
    autonumber
    actor Claimant as Claimant (User)
    actor Finder as Finder / Admin
    participant System as FindIt Backend
    participant Admin as Administrator

    Claimant->>System: Submit Claim (explanation, distinctive clues, evidence)
    System-->>Claimant: Claim Status: PENDING
    Admin->>System: Inspect Claim vs Confidential Private Details
    Admin->>System: Approve Claim (review decision notes)
    System->>System: Generate Secret Recovery Code (e.g. RCV-XXXX-XXXX)
    System-->>Claimant: Status APPROVED + Handover Recovery Code
    Claimant->>System: Schedule Handover (date, campus security location)
    Note over Claimant,Finder: In-person physical handover at designated safe spot
    Claimant->>Finder: Provide Recovery Verification Code
    Finder->>System: Confirm Handover with Verification Code
    System->>System: Transition Item Status to RECOVERED
    System-->>Claimant: In-app Notification: Handover Complete
```
