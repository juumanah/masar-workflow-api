HELLO! HAPPY THAT U R READING THIS.. SO LETS GO 
# Masar Workflow API

A request starts with someone asking for something. Then comes the interesting part: who can see it?, who can make the decision??, and what happens if two people decide at the same time?

I built Masar to work through those questions ^^

It’s a Java and Spring Boot API where requesters submit service requests and approvers review them. The workflow is simple: submit, approve, or reject with a reason. Behind that simplicity are the rules that make it dependable.

## Why I built it?

I’m interested in how everyday work becomes software: the permissions, the handoffs, and the small details people expect a system to get right.

Masar gave me a place to turn that interest into something working. I started with requests stored in memory, then added PostgreSQL, authentication, ownership checks, tests, and db migrations as the project grew.

Each step answered a practical question:
what needs to happen for this workflow to behave correctly?

## The rules behind the workflow

- A requester can create requests and see their own.
- An approver can review all requests and make decisions.
- A rejection needs a reason.
- Once a decision is made, another decision cannot overwrite it.
- Conflicting updates are detected through optimistic locking.
- Requests and decisions remain saved after the application restarts.

I wanted these rules to be visible in the code and checked by tests.

## A small lesson from building it

One of the access tests expected a requester to see only their own request. Instead, it returned several other requests too.

The application was running. The build had succeeded. But the behavior was wrong..

That failure helped me understand why a working endpoint is only part of the job. The system also needs to respect the boundaries around that endpoint.

## Built with

Java 25 · Spring Boot · Spring Security · PostgreSQL  
Spring Data JPA · Flyway · Maven · GitHub Actions

Authentication uses sessions and CSRF protection. Passwords are stored as PBKDF2 hashes. Database changes live in versioned SQL files.

## What this repository contains

A backend API, automated tests, and instructions for running the project locally.

You can follow the examples below to register an account, submit a request, and review it through a separate approver account.

# now lets see how to run this project

## Run locally on Windows

### Requirements:

- JDK 25
- PostgreSQL 18
- Git

Maven is provided through the Maven Wrapper.

### Clone the repository

```powershell
git clone https://github.com/juumanah/masar-workflow-api.git
cd masar-workflow-api
```

### Create the database

In pgAdmin's Query Tool, run:

```sql
CREATE DATABASE masar;
```

The application defaults to:

- Host: localhost
- Port: 5432
- Database: masar
- Database user: postgres
- Application port: 8080

### Set the database password

In PowerShell:

```powershell
$dbSecret = Read-Host "PostgreSQL password" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $dbSecret).Password
```

Enter the PostgreSQL password when prompted. Typed characters are hidden.

This environment variable applies to the current PowerShell session. Set it again if you open a new terminal.

### Start the application

In that same terminal:

```powershell
.\mvnw.cmd spring-boot:run
```

Leave this terminal running. Use a second terminal for API requests.

Flyway creates the tables when the application starts against an empty database. Hibernate validates the resulting schema.

## Try the API with PowerShell

Run these commands in a second terminal.

### Register a requester

Choose a valid email and a password between 15 and 128 characters.

```powershell
$baseUrl = "http://localhost:8080"
$email = Read-Host "Email for the new account"
$secret = Read-Host "New account password (15–128 characters)" -AsSecureString
$password = [System.Net.NetworkCredential]::new("", $secret).Password

$csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -SessionVariable session
$headers = @{}
$headers[$csrf.headerName] = $csrf.token

$credentials = @{
    email = $email
    password = $password
}

Invoke-RestMethod "$baseUrl/api/auth/register" -Method Post -WebSession $session -Headers $headers -ContentType "application/json" -Body ($credentials | ConvertTo-Json)
```

All newly registered accounts receive the REQUESTER role.

### Log in and check the account

Login uses form data with email and password fields.

```powershell
Invoke-RestMethod "$baseUrl/api/auth/login" -Method Post -WebSession $session -Headers $headers -ContentType "application/x-www-form-urlencoded" -Body $credentials

Invoke-RestMethod "$baseUrl/api/auth/me" -WebSession $session
```

A successful login returns HTTP 204 with no response body.

Fetch a new CSRF token after login before making further POST requests:

```powershell
$csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -WebSession $session
$headers = @{}
$headers[$csrf.headerName] = $csrf.token
```

### Submit and retrieve a request

```powershell
$body = @{
    title = "New laptop"
    description = "A laptop for development work"
} | ConvertTo-Json

$request = Invoke-RestMethod "$baseUrl/api/requests" -Method Post -WebSession $session -Headers $headers -ContentType "application/json" -Body $body

$request | Format-List

Invoke-RestMethod "$baseUrl/api/requests" -WebSession $session
Invoke-RestMethod "$baseUrl/api/requests/$($request.id)" -WebSession $session
```

The new request starts with SUBMITTED status.

### Set up an approver

Register a separate account first. Then, using an authorized database connection in pgAdmin, replace the example email and run:

```sql
UPDATE public.app_users
SET role = 'APPROVER'
WHERE email = 'your-registered-approver@example.com'
RETURNING id, email, role;
```

Role assignment is an administrative database operation. Public registration cannot grant the APPROVER role.

Sign in with a fresh session after changing the role:

```powershell
$approverEmail = Read-Host "Approver email"
$approverSecret = Read-Host "Approver account password" -AsSecureString
$approverPassword = [System.Net.NetworkCredential]::new("", $approverSecret).Password

$csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -SessionVariable approverSession
$approverHeaders = @{}
$approverHeaders[$csrf.headerName] = $csrf.token

$approverCredentials = @{
    email = $approverEmail
    password = $approverPassword
}

Invoke-RestMethod "$baseUrl/api/auth/login" -Method Post -WebSession $approverSession -Headers $approverHeaders -ContentType "application/x-www-form-urlencoded" -Body $approverCredentials

$csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -WebSession $approverSession
$approverHeaders = @{}
$approverHeaders[$csrf.headerName] = $csrf.token

Invoke-RestMethod "$baseUrl/api/auth/me" -WebSession $approverSession
```

### Approve a submitted request

Using the request created above:

```powershell
Invoke-RestMethod "$baseUrl/api/requests/$($request.id)/approve" -Method Post -WebSession $approverSession -Headers $approverHeaders
```

To reject a different request that is still SUBMITTED:

```powershell
$requestId = Read-Host "ID of a submitted request to reject"
$rejection = @{
    reason = "Required information is missing"
} | ConvertTo-Json

Invoke-RestMethod "$baseUrl/api/requests/$requestId/reject" -Method Post -WebSession $approverSession -Headers $approverHeaders -ContentType "application/json" -Body $rejection
```

An approved or rejected request cannot receive another decision.

### Log out

```powershell
Invoke-RestMethod "$baseUrl/api/auth/logout" -Method Post -WebSession $session -Headers $headers
```

## API endpoints

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/auth/csrf` | Public |
| POST | `/api/auth/register` | Public, requires CSRF token |
| POST | `/api/auth/login` | Public, requires CSRF token |
| GET | `/api/auth/me` | Signed-in account |
| POST | `/api/auth/logout` | Requires CSRF token |
| POST | `/api/requests` | Requester |
| GET | `/api/requests` | Requester's own requests; all requests for approvers |
| GET | `/api/requests/{id}` | Owner or approver |
| POST | `/api/requests/{id}/approve` | Approver |
| POST | `/api/requests/{id}/reject` | Approver |

POST requests require a CSRF token and the session cookie associated with it.

## Validation and responses

- Email: required, valid format, maximum 254 characters.
- Password: required, between 15 and 128 characters.
- Request title and description: cannot be blank.
- Rejection reason: cannot be blank.

| Status | Meaning |
|---|---|
| 201 | Account or request created |
| 204 | Login or logout succeeded |
| 400 | Invalid input |
| 401 | Authentication required or login failed |
| 403 | Insufficient permission or invalid/missing CSRF token |
| 404 | Request not found or not accessible to the requester |
| 409 | Conflicting decision or concurrent update |

Passwords are stored as PBKDF2 hashes and are excluded from account responses.

## Run the tests

Create a separate database in pgAdmin:

```sql
CREATE DATABASE masar_test;
```

Set DB_PASSWORD in your PowerShell session, then run:

```powershell
.\mvnw.cmd "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/masar_test" test
```

Tests cover application startup, approval rules, concurrent updates, and requester access restrictions.

GitHub Actions runs the tests against a PostgreSQL service on pushes and pull requests.

## Database migrations

Migration files are stored in:

```text
src/main/resources/db/migration
```

V1 creates the users and service requests tables. Future schema changes should use new migration files, such as `V2__add_request_index.sql`. Preserve migration files that have already been applied.

The application uses:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.baseline-on-migrate=false
```

### Existing databases created before Flyway

For an existing database, first back it up and verify its schema matches V1. Then run this once:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.flyway.baseline-on-migrate=true --spring.flyway.baseline-version=1"
```

This records version 1 as the starting point. It does not execute V1 against the existing tables or verify that the full schema matches V1.

Afterward, use the normal startup command. Fresh databases do not need this baseline step.

## Configuration

The database connection can be overridden using:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

The default configuration reads the password from `DB_PASSWORD`. A `.env` file is not automatically loaded by this setup.

## Current scope

This project provides a REST API with local setup and automated CI tests. It does not include a frontend, password recovery, or a deployed production environment.