# FindIt – REST API Documentation

Base URL: `http://localhost:8080/api`  
OpenAPI / Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI Spec JSON: `http://localhost:8080/v3/api-docs`

---

## 1. Authentication APIs (`/api/auth`)

### 1.1 User Registration
- **Method**: `POST /api/auth/register`
- **Request Body**:
```json
{
  "fullName": "Jane Doe",
  "email": "jane.doe@university.edu",
  "password": "Password@123",
  "phoneNumber": "+1-555-0144",
  "organization": "Faculty of Engineering"
}
```
- **Response**: `201 Created` with JWT access token and user profile.

### 1.2 User Login
- **Method**: `POST /api/auth/login`
- **Request Body**:
```json
{
  "email": "jane.doe@university.edu",
  "password": "Password@123"
}
```
- **Response**: `200 OK` with `token` (JWT) and user summary.

### 1.3 Forgot Password
- **Method**: `POST /api/auth/forgot-password`
- **Request Body**: `{"email": "jane.doe@university.edu"}`
- **Response**: `200 OK` with status message and reset token.

### 1.4 Reset Password
- **Method**: `POST /api/auth/reset-password`
- **Request Body**: `{"token": "uuid-token", "newPassword": "NewPassword@123"}`
- **Response**: `200 OK`.

---

## 2. Lost Items APIs (`/api/lost-items`)

### 2.1 Report Lost Item
- **Method**: `POST /api/lost-items`
- **Consumes**: `multipart/form-data` (or `application/json`)
- **Headers**: `Authorization: Bearer <token>`
- **Parts**:
  - `item`: JSON string with item fields.
  - `images`: Binary file attachments (max 5, JPG/PNG/WEBP $\le$ 5MB).
- **Behavior**: Saves item, generates report ID, runs Heuristic Matching Engine against all active found items, records match pairs, and notifies matching finders/owners.

### 2.2 Search Lost Items
- **Method**: `GET /api/lost-items?search=wallet&category=WALLETS&location=Library&page=0&size=9&sortBy=createdAt&sortDir=desc`
- **Response**: Paginated results with database-side filtering.

### 2.3 Get Lost Item Details
- **Method**: `GET /api/lost-items/{id}`

---

## 3. Found Items APIs (`/api/found-items`)

### 3.1 Report Found Item
- **Method**: `POST /api/found-items`
- **Consumes**: `multipart/form-data`
- **Security Rule**: Accepts both `description` (public) and `privateDetails` (confidential).
- **Behavior**: `privateDetails` are never returned on public listings.

### 3.2 Search Found Items
- **Method**: `GET /api/found-items?category=ELECTRONICS&page=0&size=9`

### 3.3 Get Found Item Details
- **Method**: `GET /api/found-items/{id}`
- **Behavior**: Omits `privateDetails` unless requested by the original reporter or an administrator (`ROLE_ADMIN` / `ROLE_SUPER_ADMIN`).

---

## 4. Smart Matching APIs (`/api/matches`, `/api/lost-items/{id}/matches`)

### 4.1 Get Matches for Lost Item
- **Method**: `GET /api/lost-items/{lostItemId}/matches`
- **Response**: List of matches ordered by score descending, containing:
  - `score` (0.0 to 100.0)
  - `classification` (`STRONG_MATCH`, `POSSIBLE_MATCH`, `WEAK_MATCH`)
  - `matchReasons` (Array of plain-text heuristic explanations)
  - Item comparison cards.

### 4.2 Recalculate Matches (Admin)
- **Method**: `POST /api/matches/recalculate`
- **Requires**: `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`
- **Response**: `200 OK` with recalculation summary.

---

## 5. Ownership Claims APIs (`/api/claims`)

### 5.1 Submit Claim
- **Method**: `POST /api/claims`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
```json
{
  "foundItemId": 1,
  "explanation": "I left this black wallet on table 4 near the entrance around 2 PM.",
  "distinctiveCharacteristics": "Student card # 492019 inside, with a small silver lucky coin in the zipper pouch.",
  "supportingEvidence": "Driver license matching name and student enrollment proof"
}
```
- **Validation**: Rejects attempts by users to claim items they reported found (`InvalidClaimException`).

### 5.2 My Claims
- **Method**: `GET /api/claims/my-claims`
- **Response**: List of claims submitted by the authenticated user with status and recovery object if approved.

### 5.3 Approve Claim (Admin)
- **Method**: `PATCH /api/admin/claims/{id}/approve`
- **Request Body**: `{"notes": "Verified student ID and details match confidential card clues."}`
- **Behavior**: Transitions claim to `APPROVED`, automatically initializes recovery record with unique code `RCV-XXXX-XXXX`, and notifies claimant.

### 5.4 Reject Claim (Admin)
- **Method**: `PATCH /api/admin/claims/{id}/reject`
- **Request Body**: `{"notes": "Description does not match serial number or confidential wallet contents."}`

---

## 6. Secure Item Recovery APIs (`/api/recoveries`)

### 6.1 Schedule Handover
- **Method**: `POST /api/recoveries/{id}/schedule`
- **Request Body**:
```json
{
  "handoverDate": "2026-11-15T14:30:00",
  "handoverLocation": "Main Security Office, Building A",
  "notes": "Bring photo ID for verification"
}
```

### 6.2 Confirm Handover
- **Method**: `POST /api/recoveries/{id}/confirm`
- **Request Body**:
```json
{
  "recoveryCode": "RCV-A9B2-4K89",
  "handoverNotes": "Claimant verified with student card. Safe handover completed."
}
```
- **Behavior**: Validates `recoveryCode`. If correct, transitions recovery to `COMPLETED` and marks found item status as `RECOVERED`.

---

## 7. Notifications APIs (`/api/notifications`)
- `GET /api/notifications`: Retrieve user's in-app notifications.
- `GET /api/notifications/unread-count`: Returns `{ "count": n }`.
- `PATCH /api/notifications/{id}/read`: Mark notification as read.
- `PATCH /api/notifications/read-all`: Mark all notifications as read.

---

## 8. Administration APIs (`/api/admin`)
- `GET /api/admin/stats` / `GET /api/admin/dashboard`: Platform telemetry and item counts.
- `GET /api/admin/users`: List users with status and role.
- `PATCH /api/admin/users/{id}/status`: Enable/suspend user accounts.
- `PATCH /api/admin/users/{id}/role`: Super Admin role promotion/demotion.
- `GET /api/admin/audit-logs`: Access immutable audit trail.
