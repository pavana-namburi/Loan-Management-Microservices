# Loan Management Microservices Platform

[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot 3.x](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Git](https://img.shields.io/badge/Git-Version%20Control-F05032?style=for-the-badge&logo=git&logoColor=white)](https://git-scm.com/)

---

## 1. Project Title & Overview

The **Loan Management Microservices Platform** is an enterprise-grade digital lending solution built on a distributed microservices architecture. It automates the end-to-end lifecycle of retail credit underwriting—from identity verification and risk assessment to loan sanctioning and automated disbursement.

### System Overview & Inter-Service Communication Flow
The ecosystem is composed of four specialized microservices:
1. **`pan-service`**: Acts as the Income Tax Department / NSDL PAN verification provider. It stores official PAN registry details (name, father's name, date of birth) and validates tax identity cards.
2. **`aadhar-service`**: Acts as the UIDAI Aadhaar identity registry. It verifies resident demographic identity, address records, and linked mobile phone numbers.
3. **`report-service`**: Simulates a Credit Information Bureau (such as CIBIL / Experian). It calculates credit scores, maintains historical credit accounts, tracks repayment history, and logs hard credit enquiries.
4. **`loan_management`**: The core orchestration engine. It coordinates multi-factor identity cross-verification against PAN and Aadhaar, manages application state transitions, pulls credit reports, calculates debt-to-income (DTI) and Fixed Obligation to Income Ratio (FOIR), executes automated underwriting decisions, and handles customer disbursement consent.

### Orchestration Lifecycle
```
[Client / Borrower]
       │
       ▼
 1. Submit Basic Info (PAN, Aadhaar, Mobile)
       │
       ▼
[loan_management (Port 8080)]
       │
       ├───► [pan-service (Port 8081)]       ─── Verify PAN & legal name
       ├───► [aadhar-service (Port 8082)]    ─── Verify Aadhaar, mobile & address
       │
       ├───► Cross-validate PAN vs Aadhaar names & Aadhaar vs input phone
       ├───► Check previous application cooldown (6 months for approved, 1 month for rejected)
       │
 2. Update Application (Loan Type, Income, Amount, Tenure, Rate)
       │
       ├───► [report-service (Port 8083)]    ─── Post credit enquiry (Member: SBI Bank)
       │
 3. Evaluate Eligibility & Underwriting Decision (/loan/check/{appNum})
       │
       ├───► [report-service (Port 8083)]    ─── Generate / retrieve CIBIL report & score
       ├───► Score Evaluation: Requires CIBIL Score > 750
       ├───► Obligation Analysis: Max total EMI <= 75% of monthly income
       ├───► Calculates affordable EMI = (75% of income) - (existing active loans/credit card obligations)
       ├───► Computes approved amount = min(requestedAmount, maximumCalculatedPrincipal)
       │
 4. Process Disbursement Consent (/loan/disbursement/consent/{appNum})
       │
       ├───► If Consent = true:
       │        ├── Creates LoanDisbursement (BANK_TRANSFER, PENDING)
       │        ├── [report-service (Port 8083)] ─── Register new active loan account in CIBIL
       │        └── Updates disbursement status to SUCCESS & application to DISBURSED
       └───► If Consent = false:
                └── Application marked CANCELLED
```

---

## 2. Architecture & Microservices Breakdown

### Architectural Communication Diagram

```
 +----------------------------------------------------------------------------------------------------+
 |                                          CLIENT LAYER                                              |
 |                                  (Web / Mobile / API Consumers)                                    |
 +----------------------------------------------------------------------------------------------------+
                                                   │
                                                   │ HTTP / REST
                                                   ▼
 +----------------------------------------------------------------------------------------------------+
 |                             LOAN MANAGEMENT ORCHESTRATION SERVICE                                  |
 |                                      (Port: 8080, Context: /)                                      |
 |                                                                                                    |
 |  [LoanManagementController]                  [ExternalServiceController]                           |
 |         │                                                 │                                        |
 |         ▼                                                 │                                        |
 |  [LoanManagementServiceImpl] <────────────────────────────┘                                        |
 |         │                                                                                          |
 |         ├───► [EmiCalculatorServiceImpl]                                                           |
 |         ├───► [ExistingObligationServiceImpl]                                                      |
 |         ├───► [LoanEligibilityServiceImpl]                                                         |
 |         │                                                                                          |
 |         ├───► [RestClient] ────────────────────────────────────────────────────────┐               |
 |         │                                                                          │               |
 |         ▼                                                                          │               |
 |  [DAOs: CustomerDao, LoanApplicationDao, LoanDecisionDao, LoanDisbursementDao]     │               |
 |         │                                                                          │               |
 |         ▼                                                                          │               |
 |  [(MySQL: loan_management)]                                                        │               |
 +------------------------------------------------------------------------------------┼---------------+
                                                                                      │
                 ┌──────────────────────────────────┬─────────────────────────────────┘
                 │ HTTP (RestClient)                │ HTTP (RestClient)               │ HTTP (RestClient)
                 ▼                                  ▼                                 ▼
 +───────────────────────────────+  +───────────────────────────────+  +───────────────────────────────+
 |          PAN SERVICE          |  |        AADHAAR SERVICE        |  |        REPORT SERVICE         |
 |  (Port: 8081, /panservice)    |  |  (Port: 8082, /aadharservice) |  |  (Port: 8083, /cibilservice)  |
 |                               |  |                               |  |                               |
 |   [PanController]             |  |   [AadharController]          |  |   [CibilController]           |
 |         │                     |  |         │                     |  |         │                     |
 |         ▼                     |  |         ▼                     |  |         ▼                     |
 |   [PanServiceImpl]            |  |   [AadharServiceImpl]         |  |   [CibilServiceImpl]          |
 |         │                     |  |         │                     |  |         │                     |
 |         ▼                     |  |         ▼                     |  |         ▼                     |
 |   [PanDaoImpl]                |  |   [AadharDaoImpl]             |  |   [CibilDaoImpl]              |
 |         │                     |  |         │                     |  |         │                     |
 |         ▼                     |  |         ▼                     |  |         ▼                     |
 |  [(MySQL: pan_services)]      |  |  [(MySQL: aadhar_services)]   |  |  [(MySQL: reports)]           |
 +───────────────────────────────+  +───────────────────────────────+  +───────────────────────────────+
```

### Microservices Summary Table

| Microservice Name | Port | Servlet Context Path | Database Name | Description |
|---|---|---|---|---|
| **`pan-service`** | `8081` | `/panservice` | `pan_services` | Manages PAN card identity records, tax identity validation, active status checks, and CRUD operations. |
| **`aadhar-service`** | `8082` | `/aadharservice` | `aadhar_services` | Stores and retrieves Aadhaar demographic identity, father's name, linked phone number, and residential addresses. |
| **`report-service`** | `8083` | `/cibilservice` | `reports` | Acts as the credit bureau simulator (CIBIL); computes credit scores, maintains credit trade lines/accounts, logs hard inquiries, and compiles full credit reports. |
| **`loan_management`** | `8080` | `/` (root) | `loan_management` | Main orchestration service. Coordinates identity verification, runs eligibility & FOIR calculations, records loan underwriting decisions, and executes loan disbursement. |

---

## 3. Database Schemas & Entities

All microservices use **Spring Data JPA / Hibernate** with MySQL 8.x. Below is the precise schema layout matching the codebase entities.

### 3.1. `pan-service` (`pan_services` Database)

#### Table: `pan_details`
Mapped by: `com.pan.services.entity.Pan`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique internal identifier |
| `pan_num` | `VARCHAR(255)` | `UNIQUE`, `NOT NULL` | 10-character Permanent Account Number |
| `firstname` | `VARCHAR(255)` | `NOT NULL` | First name of the cardholder |
| `middlename` | `VARCHAR(255)` | `NOT NULL` | Middle name of the cardholder |
| `lastname` | `VARCHAR(255)` | `NOT NULL` | Last name / surname |
| `fathername` | `VARCHAR(255)` | `NOT NULL` | Father's name |
| `dob` | `DATE` | `NOT NULL` | Date of birth |
| `active_indicator` | `BOOLEAN` | `NOT NULL` | Flag indicating if the PAN is valid and active |

---

### 3.2. `aadhar-service` (`aadhar_services` Database)

#### Table: `aadhar`
Mapped by: `com.aadhar.services.entity.Aadhar`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique internal identifier |
| `aadhar_num` | `VARCHAR(255)` | `NULLABLE` | 12-digit Aadhaar UID number |
| `firstname` | `VARCHAR(255)` | `NULLABLE` | First name (mapped from field `fname`) |
| `middlename` | `VARCHAR(255)` | `NULLABLE` | Middle name (mapped from field `mname`) |
| `lastname` | `VARCHAR(255)` | `NULLABLE` | Last name (mapped from field `lname`) |
| `fathername` | `VARCHAR(255)` | `NULLABLE` | Father's name |
| `dob` | `DATE` | `NULLABLE` | Date of birth |
| `email` | `VARCHAR(255)` | `NULLABLE` | Registered email address |
| `phone` | `VARCHAR(255)` | `NULLABLE` | Registered mobile number |
| `gender` | `VARCHAR(255)` | `NULLABLE` | Gender (`MALE`, `FEMALE`, etc.) |
| `address` | `VARCHAR(255)` | `NULLABLE` | Full residential address |

---

### 3.3. `report-service` (`reports` Database)

Configured with `spring.jackson.property-naming-strategy=SNAKE_CASE`.

#### 1. Table: `customer`
Mapped by: `org.com.reports.entity.Customer`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `cust_id` | `VARCHAR(30)` | `PRIMARY KEY` | URL-safe Base64 UUID (generated via `@PrePersist`) |
| `full_name` | `VARCHAR(50)` | Max length: 50 | Borrower's full name |
| `date_of_birth` | `DATE` | `@Past` | Borrower's date of birth |
| `gender` | `VARCHAR(10)` | Max length: 10 | Gender |
| `pan` | `VARCHAR(50)` | Regex: `^[A-Z]{5}[0-9]{4}[A-Z]$` | Permanent Account Number |
| `mobile` | `VARCHAR(12)` | Regex: `^[0-9]{10,12}$` | 10 to 12 digit mobile number |

**Relationships:**
- `@OneToOne` with `Address` (mapped by `customer`)
- `@OneToOne` with `CibilScore` (mapped by `customer`)
- `@OneToMany` with `Account` (mapped by `customer`)
- `@OneToMany` with `Enquiry` (mapped by `customer`)
- `@OneToMany` with `Remark` (mapped by `customer`)
- `@OneToMany` with `Report` (mapped by `customer`)

#### 2. Table: `address`
Mapped by: `org.com.reports.entity.Address`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `add_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique address ID |
| `line1` | `VARCHAR(50)` | | Address Line 1 |
| `line2` | `VARCHAR(50)` | | Address Line 2 |
| `city` | `VARCHAR(20)` | | City |
| `state` | `VARCHAR(30)` | | State |
| `pincode` | `VARCHAR(20)` | | Postal code |
| `cust_id` | `VARCHAR(30)` | `FOREIGN KEY` -> `customer(cust_id)` | Linked customer ID |

#### 3. Table: `cibil_score`
Mapped by: `org.com.reports.entity.CibilScore`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `cibil_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Score record identifier |
| `score` | `VARCHAR(20)` | | Credit score (e.g., `"780"`) |
| `score_date` | `DATE` | | Date score was calculated |
| `risk_grade` | `VARCHAR(20)` | | Credit risk tier/grade |
| `cust_id` | `VARCHAR(30)` | `FOREIGN KEY` -> `customer(cust_id)` | Linked customer ID |

#### 4. Table: `account`
Mapped by: `org.com.reports.entity.Account`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `act_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Internal account identifier |
| `act_num` | `VARCHAR(50)` | | Account or loan number |
| `act_type` | `VARCHAR(20)` | | Account type (`PERSONAL`, `Credit Card`, etc.) |
| `member_name` | `VARCHAR(20)` | | Financial institution name (e.g., `SBI Bank`) |
| `ownership` | `VARCHAR(30)` | | Ownership type (`INDIVIDUAL`, `JOINT`) |
| `date_opened` | `DATE` | | Opening date of the credit line |
| `last_payment_date` | `DATE` | | Date of last recorded payment |
| `current_balance` | `VARCHAR(30)` | | Current balance / outstanding |
| `credit_limit` | `VARCHAR(30)` | | Total credit limit |
| `payment_history` | `VARCHAR(30)` | | Payment track record code |
| `account_status` | `VARCHAR(10)` | | Account status (e.g., `ACTIVE`, `CLOSED`) |
| `sanctioned_amount`| `VARCHAR(30)` | | Approved / sanctioned loan limit |
| `emi_amount` | `VARCHAR(30)` | | Monthly EMI obligation |
| `tenure_months` | `VARCHAR(30)` | | Loan tenure in months |
| `cust_id` | `VARCHAR(30)` | `FOREIGN KEY` -> `customer(cust_id)` | Linked customer ID |

#### 5. Table: `enquiries`
Mapped by: `org.com.reports.entity.Enquiry`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `enq_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique inquiry identifier |
| `enquiry_date` | `DATE` | | Date inquiry occurred |
| `member_name` | `VARCHAR(20)` | | Financial institution making inquiry |
| `enquiry_purpose` | `VARCHAR(20)` | | Purpose of credit search |
| `enquiry_amount` | `VARCHAR(15)` | | Credit limit requested |
| `cust_id` | `VARCHAR(30)` | `FOREIGN KEY` -> `customer(cust_id)` | Linked customer ID |

#### 6. Table: `remarks`
Mapped by: `org.com.reports.entity.Remark`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `rem_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique remark identifier |
| `remark` | `VARCHAR(100)` | | Underwriting remark or bureau note |
| `cust_id` | `VARCHAR(30)` | `FOREIGN KEY` -> `customer(cust_id)` | Linked customer ID |

#### 7. Table: `report`
Mapped by: `org.com.reports.entity.Report`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `rep_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Generated report sequence ID |
| `generated_on` | `DATE` | | Date report was generated |
| `cust_id` | `VARCHAR(30)` | `FOREIGN KEY` -> `customer(cust_id)` | Linked customer ID |

---

### 3.4. `loan_management` (`loan_management` Database)

#### 1. Table: `customers`
Mapped by: `com.loan.entity.Customer`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `customer_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique customer identifier |
| `full_name` | `VARCHAR(255)` | | Full legal name (from PAN/Aadhaar) |
| `date_of_birth` | `DATE` | | Date of birth |
| `gender` | `VARCHAR(255)` | | Gender |
| `mobile` | `VARCHAR(255)` | | Mobile number |
| `email` | `VARCHAR(255)` | | Email address |
| `pan_number` | `VARCHAR(10)` | `UNIQUE` | 10-character PAN |
| `aadhaar_last4` | `VARCHAR(255)` | | Last 4 digits of Aadhaar |
| `address` | `TEXT` | | Residential address |
| `city` | `VARCHAR(255)` | | City |
| `state` | `VARCHAR(255)` | | State |
| `pincode` | `VARCHAR(255)` | | Postal PIN code |
| `monthly_income` | `DECIMAL(38,2)`| | Monthly net salary / income |
| `occupation` | `VARCHAR(255)` | | Employment / occupation title |
| `created_at` | `DATETIME` | | Timestamp of record creation |

#### 2. Table: `loan_applications`
Mapped by: `com.loan.entity.LoanApplication`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `application_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Internal application ID |
| `application_number` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Business application reference (e.g., `LN9fa83c1b0e`) |
| `customer_id` | `INT` | `FOREIGN KEY`, `NOT NULL` | References `customers(customer_id)` |
| `loan_type` | `VARCHAR(255)` | `NOT NULL` | Enum: `PERSONAL`, `HOME`, `VEHICLE`, `BUSINESS`, `EDUCATION` |
| `requested_amount` | `DECIMAL(12,2)` | `NOT NULL` | Principal amount requested |
| `tenure_months` | `INT` | `NOT NULL` | Repayment tenure in months |
| `interest_rate` | `DECIMAL(5,2)` | | Annual interest rate (e.g., `10.50`) |
| `loan_purpose` | `TEXT` | | Purpose for the loan |
| `application_date` | `DATE` | `NOT NULL` | Date application was logged |
| `status` | `VARCHAR(255)` | `NOT NULL` | Enum: `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `CANCELLED`, `DISBURSED` |
| `created_by` | `INT` | `FOREIGN KEY` | References `users(user_id)` |

#### 3. Table: `loan_decisions`
Mapped by: `com.loan.entity.LoanDecision`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `decision_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Decision identifier |
| `application_id` | `INT` | `FOREIGN KEY`, `NOT NULL` | References `loan_applications(application_id)` |
| `decision` | `VARCHAR(255)` | `NOT NULL` | Enum: `APPROVED`, `REJECTED` |
| `approved_amount` | `DECIMAL(12,2)` | | Maximum calculated principal sanctioned |
| `approved_interest_rate`| `DECIMAL(5,2)` | | Final approved annual interest rate |
| `approved_tenure_months`| `INT` | | Final approved tenure in months |
| `decision_reason` | `TEXT` | | Explanation rationale (e.g., `"CIBIL and EMI affordability checks passed"`) |
| `decision_date` | `DATETIME` | `NOT NULL` | Timestamp of underwriting decision |
| `decided_by` | `INT` | `FOREIGN KEY` | References `users(user_id)` |

#### 4. Table: `loan_disbursements`
Mapped by: `com.loan.entity.LoanDisbursement`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `disbursement_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Disbursement transaction ID |
| `application_id` | `INT` | `FOREIGN KEY`, `NOT NULL` | References `loan_applications(application_id)` |
| `disbursement_amount` | `DECIMAL(12,2)` | `NOT NULL` | Transferred disbursement amount |
| `disbursement_date` | `DATETIME` | | Timestamp of funds settlement |
| `payment_mode` | `VARCHAR(255)` | `NOT NULL` | Enum: `NEFT`, `RTGS`, `IMPS`, `BANK_TRANSFER`, `CHEQUE` |
| `transaction_reference` | `VARCHAR(100)` | `UNIQUE` | Unique payment gateway/banking transaction UUID |
| `beneficiary_account_last4` | `VARCHAR(4)` | | Last 4 digits of beneficiary bank account |
| `status` | `VARCHAR(255)` | `NOT NULL` | Enum: `PENDING`, `SUCCESS`, `FAILED`, `CANCELLED` |
| `processed_by` | `INT` | `FOREIGN KEY` | References `users(user_id)` |
| `remarks` | `TEXT` | | Audit remarks |
| `created_at` | `DATETIME` | | Record timestamp |

#### 5. Table: `cibil_details`
Mapped by: `com.loan.entity.CibilDetails`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `cibil_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Internal CIBIL record ID |
| `application_id` | `INT` | `FOREIGN KEY`, `NOT NULL` | References `loan_applications(application_id)` |
| `credit_score` | `SMALLINT` | | Bureau credit score snapshot |
| `report_reference` | `VARCHAR(100)` | | Reference ID of the generated bureau report |
| `checked_date` | `DATETIME` | `NOT NULL` | Timestamp when credit pull was performed |
| `verification_status` | `VARCHAR(255)` | `NOT NULL` | Enum: `PENDING`, `VERIFIED`, `FAILED` |
| `remarks` | `TEXT` | | Remarks compiled from report |
| `checked_by` | `INT` | `FOREIGN KEY` | References `users(user_id)` |

#### 6. Table: `users`
Mapped by: `com.loan.entity.User`

| Column Name | SQL Type | Constraints | Description |
|---|---|---|---|
| `user_id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Internal user ID |
| `full_name` | `VARCHAR(255)` | `NOT NULL` | Staff member full name |
| `username` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Staff username |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | Secure password hash |
| `role` | `VARCHAR(255)` | `NOT NULL` | Enum: `ADMIN`, `LOAN_OFFICER`, `MANAGER` |
| `is_active` | `INT` | | Active indicator (1 = active, 0 = disabled) |
| `created_at` | `DATETIME` | | Timestamp of account creation |

---

## 4. API Endpoints Documentation

### 4.1. `pan-service` (Port: `8081`, Context Path: `/panservice`)

#### 1. Create PAN Record
- **Method:** `POST`
- **URL:** `/panservice/create`
- **Request Body:**
  ```json
  {
    "pan_num": "ABCDE1234F",
    "firstname": "Rahul",
    "middlename": "Kumar",
    "lastname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15"
  }
  ```
- **Response Body:**
  ```json
  1
  ```
- **HTTP Status:** `200 OK`

#### 2. Get PAN Record by PAN Number
- **Method:** `GET`
- **URL:** `/panservice/get/{panNum}`
- **Path Variable:** `panNum` (String, e.g., `ABCDE1234F`)
- **Request Body:** None
- **Response Body:**
  ```json
  {
    "pan_num": "ABCDE1234F",
    "firstname": "Rahul",
    "middlename": "Kumar",
    "lastname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15"
  }
  ```
- **HTTP Status:** `200 OK`

#### 3. Update PAN Record
- **Method:** `PUT`
- **URL:** `/panservice/update`
- **Request Body:**
  ```json
  {
    "pan_num": "ABCDE1234F",
    "firstname": "Rahul",
    "middlename": "Kumar",
    "lastname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15"
  }
  ```
- **Response Body:** `true` (or `false`)
- **HTTP Status:** `200 OK`

#### 4. Delete PAN Record
- **Method:** `DELETE`
- **URL:** `/panservice/delete/{panNum}`
- **Path Variable:** `panNum` (String, e.g., `ABCDE1234F`)
- **Request Body:** None
- **Response Body:**
  ```json
  {
    "id": 1,
    "pan_num": "ABCDE1234F",
    "firstname": "Rahul",
    "middlename": "Kumar",
    "lastname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15",
    "active_indicator": false
  }
  ```
- **HTTP Status:** `200 OK`

---

### 4.2. `aadhar-service` (Port: `8082`, Context Path: `/aadharservice`)

#### 1. Add Aadhaar Record
- **Method:** `POST`
- **URL:** `/aadharservice/add`
- **Request Body:**
  ```json
  {
    "aadharNum": "123456789012",
    "fname": "Rahul",
    "mname": "Kumar",
    "lname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15",
    "email": "rahul.sharma@example.com",
    "phone": "9876543210",
    "gender": "MALE",
    "address": "123 MG Road, Indiranagar, Bengaluru, Karnataka"
  }
  ```
- **Response Body:**
  ```
  Aadhar record created successfully with Id:1
  ```
  *(or `"Aadhar record already existing with the given aadhar number"`)*
- **HTTP Status:** `200 OK`

#### 2. Get Aadhaar Record by Aadhaar Number
- **Method:** `GET`
- **URL:** `/aadharservice/get/{num}`
- **Path Variable:** `num` (String, e.g., `123456789012`)
- **Request Body:** None
- **Response Body:**
  ```json
  {
    "id": 1,
    "aadharNum": "123456789012",
    "fname": "Rahul",
    "mname": "Kumar",
    "lname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15",
    "email": "rahul.sharma@example.com",
    "phone": "9876543210",
    "gender": "MALE",
    "address": "123 MG Road, Indiranagar, Bengaluru, Karnataka"
  }
  ```
- **HTTP Status:** `200 OK` (returns `null` if not found)

#### 3. Update Aadhaar Record
- **Method:** `PUT`
- **URL:** `/aadharservice/update`
- **Request Body:**
  ```json
  {
    "id": 1,
    "aadharNum": "123456789012",
    "fname": "Rahul",
    "mname": "Kumar",
    "lname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15",
    "email": "rahul.sharma@example.com",
    "phone": "9876543210",
    "gender": "MALE",
    "address": "Flat 402, Green Glen Layout, Bellandur, Bengaluru"
  }
  ```
- **Response Body:** `true` (or `false`)
- **HTTP Status:** `200 OK`

#### 4. Delete Aadhaar Record
- **Method:** `DELETE`
- **URL:** `/aadharservice/delete/{num}`
- **Path Variable:** `num` (String, e.g., `123456789012`)
- **Request Body:** None
- **Response Body:** (Empty)
- **HTTP Status:** `200 OK`

---

### 4.3. `report-service` (Port: `8083`, Context Path: `/cibilservice`)

*Note: Property naming strategy is configured to `SNAKE_CASE`.*

#### 1. Generate / Retrieve CIBIL Report
- **Method:** `POST`
- **URL:** `/cibilservice/generate/{pan}`
- **Path Variable:** `pan` (String, e.g., `ABCDE1234F`)
- **Request Body:**
  ```json
  {
    "pan": "ABCDE1234F",
    "full_name": "Rahul Kumar Sharma",
    "date_of_birth": "1990-05-15",
    "gender": "MALE",
    "mobile": "9876543210",
    "address": {
      "line1": "123 MG Road",
      "line2": "Indiranagar",
      "city": "Bengaluru",
      "state": "Karnataka",
      "pincode": "560001"
    }
  }
  ```
- **Response Body:**
  ```json
  {
    "report_id": "1",
    "generated_on": "2026-11-08T08:30:00.000Z",
    "customer": {
      "full_name": "Rahul Kumar Sharma",
      "date_of_birth": "1990-05-15",
      "gender": "MALE",
      "pan": "ABCDE1234F",
      "mobile": "9876543210",
      "address": {
        "line1": "123 MG Road",
        "line2": "Indiranagar",
        "city": "Bengaluru",
        "state": "Karnataka",
        "pincode": "560001"
      }
    },
    "cibil_score": {
      "score": 780,
      "score_date": "2026-11-08",
      "risk_grade": null
    },
    "accounts": [],
    "enquiries": [],
    "remarks": []
  }
  ```
- **HTTP Status:** `200 OK` (or `404 Not Found`)

#### 2. Add Credit Account to Profile
- **Method:** `POST`
- **URL:** `/cibilservice/accounts/{pan}`
- **Path Variable:** `pan` (String, e.g., `ABCDE1234F`)
- **Request Body:**
  ```json
  {
    "account_number": "LN9fa83c1b0e123456",
    "account_type": "PERSONAL",
    "member_name": "SBI Bank",
    "ownership": "INDIVIDUAL",
    "date_opened": "2026-11-08",
    "last_payment_date": null,
    "current_balance": "500000.00",
    "credit_limit": null,
    "payment_history": "NEW",
    "account_status": "ACTIVE",
    "sanctioned_amount": "500000.00",
    "emi_amount": "23190.52",
    "tenure_months": 24
  }
  ```
- **Response Body:** Echo of the registered `AccountDto`
- **HTTP Status:** `201 Created` (or `409 Conflict` on persistence failure)

#### 3. Log Credit Enquiry
- **Method:** `POST`
- **URL:** `/cibilservice/enquiries/{pan}`
- **Path Variable:** `pan` (String, e.g., `ABCDE1234F`)
- **Request Body:**
  ```json
  {
    "enquiry_date": "2026-11-08",
    "member_name": "SBI Bank",
    "enquiry_purpose": "Home renovation and interior design",
    "enquiry_amount": "500000.00"
  }
  ```
- **Response Body:** Echo of the registered `EnquiryDto`
- **HTTP Status:** `201 Created` (or `409 Conflict`)

---

### 4.4. `loan_management` (Port: `8080`, Context Path: `/`)

#### 4.4.1. Core Loan Orchestration Controller (`LoanManagementController`)

#### 1. Initiate Loan Application
- **Method:** `POST`
- **URL:** `/loan/create`
- **Description:** Verifies PAN against `pan-service`, verifies Aadhaar and mobile against `aadhar-service`, validates identity cross-matching, creates customer profile if new, validates reapplication cooling-off period, and initializes a loan application.
- **Request Body:**
  ```json
  {
    "panNum": "ABCDE1234F",
    "aadharNum": "123456789012",
    "mobile": "9876543210"
  }
  ```
- **Response Body:**
  ```
  LoanApplication successfully created!! with num: LN9fa83c1b0e
  ```
  *(Validation error examples: `"names in pan and aadhar doesnt match!!"`, `"Application for this user already exist!"`, `"Mobile number does not match the Aadhaar record"`)*
- **HTTP Status:** `200 OK`

#### 2. Update Application & Loan Details
- **Method:** `PUT`
- **URL:** `/loan/update`
- **Description:** Updates loan request terms and customer financial data, and automatically triggers an enquiry log in `report-service`.
- **Request Body:**
  ```json
  {
    "applicationNumber": "LN9fa83c1b0e",
    "loanType": "PERSONAL",
    "requestedAmount": 500000.00,
    "tenureMonths": 24,
    "interestRate": 10.50,
    "loanPurpose": "Home renovation and interior design",
    "email": "rahul.sharma@example.com",
    "monthlyIncome": 85000.00,
    "occupation": "Senior Software Engineer",
    "city": "Bengaluru",
    "state": "Karnataka",
    "pincode": "560001"
  }
  ```
- **Response Body:**
  ```
  Application processed successfully. Application number: LN9fa83c1b0e, status: SUBMITTED
  ```
- **HTTP Status:** `200 OK`

#### 3. Evaluate Eligibility & Underwriting Decision
- **Method:** `POST`
- **URL:** `/loan/check/{applicationNum}`
- **Path Variable:** `applicationNum` (String, e.g., `LN9fa83c1b0e`)
- **Description:** Transitions status to `UNDER_REVIEW`, requests CIBIL report from `report-service`, checks if CIBIL score > 750, computes existing obligations, calculates affordable EMI limit (75% of income minus obligations), determines maximum principal, and saves the decision (`APPROVED` / `REJECTED`).
- **Request Body:** None
- **Response Body:**
  ```json
  {
    "decisionId": 1,
    "applicationId": 1,
    "loanDecisionStatus": "APPROVED",
    "approvedAmount": 500000.00,
    "approvedInterestRate": 10.50,
    "approvedTenureMonths": 24,
    "decisionReason": "CIBIL and EMI affordability checks passed",
    "decisionDate": "2026-11-08T14:15:30.123456",
    "decidedById": null
  }
  ```
- **HTTP Status:** `200 OK`

#### 4. Submit Disbursement Consent & Finalize Disbursement
- **Method:** `POST`
- **URL:** `/loan/disbursement/consent/{applicationNumber}`
- **Path Variable:** `applicationNumber` (String, e.g., `LN9fa83c1b0e`)
- **Description:** Verifies approved status, processes borrower consent. If true, generates a disbursement record (`BANK_TRANSFER`), records the new active loan account in `report-service`, marks disbursement as `SUCCESS`, and transitions application to `DISBURSED`.
- **Request Body:**
  ```json
  {
    "consent": true
  }
  ```
- **Response Body:**
  ```
  Disbursement completed successfully. Application: LN9fa83c1b0e
  ```
  *(If `consent: false`: `"Customer declined disbursement. Application cancelled."`)*
- **HTTP Status:** `200 OK`

---

#### 4.4.2. External Gateway Proxy Controller (`ExternalServiceController`)

Provides direct pass-through lookup endpoints to external dependencies:

#### 1. Proxy Aadhaar Details
- **Method:** `GET`
- **URL:** `/api/external/aadhar/{aadharNum}`
- **Response Body:** `AadharDto`
- **HTTP Status:** `200 OK`

#### 2. Proxy PAN Details
- **Method:** `GET`
- **URL:** `/api/external/pan/{panNum}`
- **Response Body:** `PanDto`
- **HTTP Status:** `200 OK`

#### 3. Proxy CIBIL Report Generation
- **Method:** `POST`
- **URL:** `/api/external/report/{panNum}`
- **Request Body:** `AddCustomerRequestDto`
- **Response Body:** `ReportResponseDto`
- **HTTP Status:** `200 OK`

---

## 5. Environment Setup & Configuration

### Prerequisites
- **Java SE Development Kit (JDK):** Version 17
- **Apache Maven:** Version 3.8+ (or use the provided Maven wrappers)
- **MySQL Database Server:** Version 8.0+ running on port `3306`

### 5.1. Database Initialization
Log in to your MySQL terminal or database client and create the four microservice databases:

```sql
CREATE DATABASE IF NOT EXISTS pan_services;
CREATE DATABASE IF NOT EXISTS aadhar_services;
CREATE DATABASE IF NOT EXISTS reports;
CREATE DATABASE IF NOT EXISTS loan_management;
```

### 5.2. Environment Variables Configuration
All four microservices externalize database credentials using Spring environment placeholder variables with fallback defaults (`${DB_USERNAME:root}`, `${DB_PASSWORD:}`):

| Environment Variable | Description | Default Fallback |
|---|---|---|
| `DB_USERNAME` | MySQL database username | `root` |
| `DB_PASSWORD` | MySQL database password | `""` (empty password) |

#### Setting Environment Variables
- **Linux / macOS:**
  ```bash
  export DB_USERNAME=root
  export DB_PASSWORD=your_strong_password
  ```
- **Windows (PowerShell):**
  ```powershell
  $env:DB_USERNAME="root"
  $env:DB_PASSWORD="your_strong_password"
  ```
- **Windows (Command Prompt):**
  ```cmd
  set DB_USERNAME=root
  set DB_PASSWORD=your_strong_password
  ```

---

### 5.3. Step-by-Step Instructions to Build & Run Services

Open four separate terminal windows to run each microservice.

#### Terminal 1: Start `pan-service` (Port: 8081)
```bash
cd pan-service

# Linux / macOS:
./mvnw clean spring-boot:run

# Windows:
.\mvnw.cmd clean spring-boot:run
# or
mvn clean spring-boot:run
```

#### Terminal 2: Start `aadhar-service` (Port: 8082)
```bash
cd aadhar-service

# Linux / macOS:
./mvnw clean spring-boot:run

# Windows:
.\mvnw.cmd clean spring-boot:run
# or
mvn clean spring-boot:run
```

#### Terminal 3: Start `report-service` (Port: 8083)
```bash
cd report-service

# Linux / macOS / Windows:
mvn clean spring-boot:run
```

#### Terminal 4: Build & Start `loan_management` (Port: 8080)
`loan_management` is a multi-module Maven project (`data-model`, `external-services`, `data`, `web-services`). First build the modules from the root, then run `web-services`:

```bash
cd loan_management

# 1. Install all submodules to local maven repository
mvn clean install -DskipTests

# 2. Run the main spring-boot application module (web-services)
mvn spring-boot:run -pl web-services
```

---

## 6. Verification & API Testing

Follow this end-to-end verification sequence using `cURL` to test the entire loan underwriting flow.

### Step 1: Pre-populate Identity Data

#### 1.1. Seed PAN Service
```bash
curl -X POST http://localhost:8081/panservice/create \
  -H "Content-Type: application/json" \
  -d '{
    "pan_num": "ABCDE1234F",
    "firstname": "Rahul",
    "middlename": "Kumar",
    "lastname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15"
  }'
```

#### 1.2. Seed Aadhaar Service
*Note: Names and mobile number must match the PAN and loan request records.*
```bash
curl -X POST http://localhost:8082/aadharservice/add \
  -H "Content-Type: application/json" \
  -d '{
    "aadharNum": "123456789012",
    "fname": "Rahul",
    "mname": "Kumar",
    "lname": "Sharma",
    "fathername": "Ramesh Sharma",
    "dob": "1990-05-15",
    "email": "rahul.sharma@example.com",
    "phone": "9876543210",
    "gender": "MALE",
    "address": "123 MG Road, Indiranagar, Bengaluru, Karnataka"
  }'
```

---

### Step 2: Verify Seeded Records

#### 2.1. Verify PAN Lookup
```bash
curl -X GET http://localhost:8081/panservice/get/ABCDE1234F
```

#### 2.2. Verify Aadhaar Lookup
```bash
curl -X GET http://localhost:8082/aadharservice/get/123456789012
```

---

### Step 3: End-to-End Loan Application Flow

#### 3.1. Create Loan Application
Submit identity details to `loan_management`. This cross-validates records between PAN and Aadhaar.
```bash
curl -X POST http://localhost:8080/loan/create \
  -H "Content-Type: application/json" \
  -d '{
    "panNum": "ABCDE1234F",
    "aadharNum": "123456789012",
    "mobile": "9876543210"
  }'
```
**Expected Response:**
```
LoanApplication successfully created!! with num: LNXXXXXXXXXX
```
*(Copy the generated application number, e.g., `LN9fa83c1b0e`)*

---

#### 3.2. Update Application Terms & Financial Profile
Submit requested loan parameters and monthly income. This automatically posts an inquiry to the CIBIL bureau.
```bash
curl -X PUT http://localhost:8080/loan/update \
  -H "Content-Type: application/json" \
  -d '{
    "applicationNumber": "LN9fa83c1b0e",
    "loanType": "PERSONAL",
    "requestedAmount": 500000.00,
    "tenureMonths": 24,
    "interestRate": 10.50,
    "loanPurpose": "Home renovation and interior design",
    "email": "rahul.sharma@example.com",
    "monthlyIncome": 85000.00,
    "occupation": "Senior Software Engineer",
    "city": "Bengaluru",
    "state": "Karnataka",
    "pincode": "560001"
  }'
```
**Expected Response:**
```
Application processed successfully. Application number: LN9fa83c1b0e, status: SUBMITTED
```

---

#### 3.3. Execute Eligibility Evaluation & Underwriting Decision
Trigger underwriting calculations against bureau scores and debt obligations.
```bash
curl -X POST http://localhost:8080/loan/check/LN9fa83c1b0e
```
**Expected Response:**
```json
{
  "decisionId": 1,
  "applicationId": 1,
  "loanDecisionStatus": "APPROVED",
  "approvedAmount": 500000.00,
  "approvedInterestRate": 10.50,
  "approvedTenureMonths": 24,
  "decisionReason": "CIBIL and EMI affordability checks passed",
  "decisionDate": "2026-11-08T14:20:00",
  "decidedById": null
}
```

---

#### 3.4. Submit Disbursement Consent & Finalize Loan
Confirm loan acceptance to disburse the funds and log the new credit trade line in the bureau.
```bash
curl -X POST http://localhost:8080/loan/disbursement/consent/LN9fa83c1b0e \
  -H "Content-Type: application/json" \
  -d '{
    "consent": true
  }'
```
**Expected Response:**
```
Disbursement completed successfully. Application: LN9fa83c1b0e
```

---

### Step 4: Verify Post-Disbursement Bureau Update
Check the updated CIBIL report through `report-service` to confirm the newly disbursed loan account is active.
```bash
curl -X POST http://localhost:8083/cibilservice/generate/ABCDE1234F \
  -H "Content-Type: application/json" \
  -d '{
    "pan": "ABCDE1234F",
    "full_name": "Rahul Kumar Sharma",
    "date_of_birth": "1990-05-15",
    "gender": "MALE",
    "mobile": "9876543210"
  }'
```
You will observe:
- Credit score returned (default: `780`).
- Recorded inquiry for ₹500,000 from `SBI Bank`.
- New active `PERSONAL` loan account with sanctioned amount `500000.00`, calculated EMI `23190.52`, and tenure `24` months.

---

### Step 5: Test Gateway Proxy Endpoints (Optional)
Test the passthrough endpoints exposed by `loan_management`:
```bash
# Proxy PAN details:
curl -X GET http://localhost:8080/api/external/pan/ABCDE1234F

# Proxy Aadhaar details:
curl -X GET http://localhost:8080/api/external/aadhar/123456789012
```

---

## 7. Troubleshooting & Common Pitfalls

1. **Names Mismatch Error (`"names in pan and aadhar doesnt match!!"`):**
   Ensure the first name (`firstname` in PAN vs `fname` in Aadhaar) and last name (`lastname` in PAN vs `lname` in Aadhaar) match case-insensitively when seeding records.
2. **Mobile Mismatch (`"Mobile number does not match the Aadhaar record"`):**
   The phone number provided during `/loan/create` must match the phone number stored in the Aadhaar record for that resident.
3. **Multi-Module Dependency Error in `loan_management`:**
   If `web-services` fails to start due to missing local artifacts (e.g. `data`, `data-model`, `external-services`), run `mvn clean install -DskipTests` from the `loan_management` root directory before running `mvn spring-boot:run -pl web-services`.
4. **Port Conflicts:**
   Ensure ports `8080`, `8081`, `8082`, and `8083` are free and not in use by other background applications.
