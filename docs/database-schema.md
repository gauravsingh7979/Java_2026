# FindIt – Database Schema Documentation

## 1. Overview
The FindIt database runs on **MySQL 8.0** (`findit_db`) using `utf8mb4` encoding. It incorporates full relational integrity, foreign key constraints, indexed lookup columns, and auditing timestamps (`created_at`, `updated_at`).

```mermaid
erDiagram
    USERS ||--o{ LOST_ITEMS : "reports"
    USERS ||--o{ FOUND_ITEMS : "reports"
    USERS ||--o{ CLAIMS : "submits"
    USERS ||--o{ NOTIFICATIONS : "receives"
    LOST_ITEMS ||--o{ MATCHES : "matches"
    FOUND_ITEMS ||--o{ MATCHES : "matches"
    FOUND_ITEMS ||--o{ CLAIMS : "receives"
    FOUND_ITEMS ||--o{ ITEM_IMAGES : "has"
    LOST_ITEMS ||--o{ ITEM_IMAGES : "has"
    CLAIMS ||--o| RECOVERIES : "generates"
    USERS ||--o{ AUDIT_LOGS : "acts"

    USERS {
        BIGINT id PK
        VARCHAR email UK
        VARCHAR password
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR phone_number
        VARCHAR organization
        VARCHAR role
        BOOLEAN active
        BOOLEAN verified
        DATETIME created_at
        DATETIME updated_at
    }

    LOST_ITEMS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR title
        VARCHAR category
        VARCHAR brand
        VARCHAR color
        VARCHAR location_lost
        DATE date_lost
        TEXT description
        TEXT distinctive_characteristics
        VARCHAR contact_preference
        VARCHAR status
        INT match_count
        DATETIME created_at
        DATETIME updated_at
    }

    FOUND_ITEMS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR title
        VARCHAR category
        VARCHAR brand
        VARCHAR color
        VARCHAR location_found
        DATE date_found
        TEXT description
        TEXT private_details
        VARCHAR contact_preference
        VARCHAR status
        INT match_count
        DATETIME created_at
        DATETIME updated_at
    }

    ITEM_IMAGES {
        BIGINT id PK
        BIGINT lost_item_id FK
        BIGINT found_item_id FK
        VARCHAR image_url
        VARCHAR file_name
        BIGINT file_size
        BOOLEAN is_primary
        DATETIME created_at
    }

    MATCHES {
        BIGINT id PK
        BIGINT lost_item_id FK
        BIGINT found_item_id FK
        DOUBLE score
        VARCHAR classification
        VARCHAR status
        JSON match_reasons
        DATETIME created_at
    }

    CLAIMS {
        BIGINT id PK
        BIGINT found_item_id FK
        BIGINT claimant_id FK
        TEXT explanation
        TEXT distinctive_characteristics
        TEXT supporting_evidence
        VARCHAR status
        BIGINT reviewer_id FK
        TEXT admin_notes
        DATETIME reviewed_at
        DATETIME created_at
    }

    RECOVERIES {
        BIGINT id PK
        BIGINT claim_id FK
        VARCHAR recovery_code UK
        VARCHAR status
        DATETIME handover_date
        VARCHAR handover_location
        TEXT notes
        DATETIME confirmed_at
        DATETIME created_at
    }

    NOTIFICATIONS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR title
        TEXT message
        VARCHAR type
        VARCHAR related_entity_type
        BIGINT related_entity_id
        BOOLEAN is_read
        DATETIME created_at
    }

    AUDIT_LOGS {
        BIGINT id PK
        BIGINT actor_id FK
        VARCHAR actor_email
        VARCHAR action
        VARCHAR entity_name
        BIGINT entity_id
        TEXT details
        VARCHAR ip_address
        DATETIME created_at
    }
```

---

## 2. Table Specifications & Constraints

### 2.1 `users`
- `email`: `VARCHAR(191)` unique index, case-insensitive collation.
- `role`: Enum mapping to `ROLE_USER`, `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`.
- `password`: BCrypt hash (60 chars).

### 2.2 `lost_items` & `found_items`
- Indexes on `(category, status)`, `date_lost`, `date_found`, and `user_id`.
- `private_details` in `found_items`: Holds sensitive confidential ownership clues, never emitted on public API responses.

### 2.3 `matches`
- Unique composite index on `(lost_item_id, found_item_id)` to prevent redundant match evaluation.
- `score`: Double value between `0.0` and `100.0`.
- `match_reasons`: Stored as serialized JSON lines.

### 2.4 `claims` & `recoveries`
- Foreign keys ensure a user cannot submit claims on items they reported found.
- `recoveries.recovery_code`: Formatted as `RCV-XXXX-XXXX`, indexed and uniquely constrained.
- Handover confirmation requires the claimant to supply this exact code.
