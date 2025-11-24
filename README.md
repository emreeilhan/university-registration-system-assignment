# University Course Registration System

A modular Java application modeling a university registration workflow. This system handles courses, sections, student enrollments, grading, and transcript generation with a focus on Clean Architecture and Object-Oriented principles.

## Features

- **Role-Based Access:** Distinct workflows for Students, Instructors, and Admins.
- **Registration Rules:**
    - **Capacity Check:** Prevents enrollment if section is full (supports Waitlist).
    - **Prerequisite Check:** Ensures students have passed required courses.
    - **Conflict Check:** Detects time overlaps in a student's schedule.
- **Admin Overrides:** Administrators can bypass rules (capacity/prerequisites) with mandatory logging.
- **Search Catalog:** Filter courses by code, title, credits, instructor, or time window.
- **Clean Architecture:**
    - **Model:** Core domain entities (Student, Course, Section).
    - **Repository:** In-memory data access abstraction (Generic Repository Pattern).
    - **Service:** Business logic layer returning `Result<T>` instead of throwing exceptions.
    - **CLI:** Simple command-line interface for user interaction.

## Project Structure

```
src/main/java/edu/uni/registration/
├── model/          # Entities (Student, Course, Section, etc.)
├── repository/     # Data access (Repository Pattern)
├── service/        # Business logic interfaces
├── service/impl/   # Service implementations
├── validation/     # Logic for conflicts, prerequisites, capacity
├── util/           # Helpers (Result<T>, AdminOverrideLog)
├── cli/            # Command Line Interface logic
└── Main.java       # Application entry point (DI & Seeding)
```

## How to Run

### Prerequisites
- Java JDK 11 or higher
- Maven (optional, or just use javac)

### Compilation & Execution (Manual)

1. **Compile:**
   Navigate to the project root and compile all Java files.
   ```bash
   javac -d bin -cp src/main/java src/main/java/edu/uni/registration/Main.java
   ```

2. **Run:**
   Execute the Main class from the bin directory.
   ```bash
   java -cp bin edu.uni.registration.Main
   ```

### Running Tests

The project includes JUnit tests for core business rules (Validation, Repositories, Logic).

```bash
mvn test
```
*(Or run via your IDE's test runner)*

## Design Decisions

- **Result Pattern:** Instead of cluttering logic with `try-catch` blocks, service methods return a `Result<T>` object that encapsulates either a success value or an error message.
- **Dependency Injection:** In `Main.java`, repositories and services are wired together manually, demonstrating understanding of IoC without relying on heavy frameworks like Spring.
- **Interface Segregation:** Services are defined by Interfaces (`CatalogService`, `RegistrationService`) to decouple implementation from contract.

## Demo Scenario (Pre-loaded Data)

When you run the app, the following data is automatically loaded:

- **Students:** `S1` (Ali), `S2` (Ayse)
- **Admin:** `A1`
- **Courses:** `CS101` (Intro), `CS102` (Data Structures - Prereq: CS101)
- **Sections:** `SEC101-A` (CS101), `SEC102-A` (CS102)

**Try this flow:**
1. Login as **Student (S1)**.
2. Search for "CS101".
3. Enroll in `SEC101-A`.
4. View Schedule.





