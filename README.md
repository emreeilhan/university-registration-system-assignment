# University Course Registration System

Java app for university course registration. Handles enrollments, grading, and transcripts. Built with Clean Architecture principles.

## Features

- **Roles:** Students, Instructors, and Admins have different workflows
- **Registration Rules:**
    - Capacity check (with waitlist support)
    - Prerequisite validation
    - Schedule conflict detection
- **Admin Overrides:** Admins can bypass rules, but everything gets logged
- **Course Search:** Filter by code, title, credits, instructor, or time
- **Architecture:**
    - Model: Core entities (Student, Course, Section, etc.)
    - Repository: In-memory storage using Repository Pattern
    - Service: Business logic returns `Result<T>` instead of throwing exceptions
    - CLI: Command-line interface

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
- Java JDK 11+
- Maven (or just use javac)

### Compile & Run

```bash
# Compile
javac -d bin -cp src/main/java src/main/java/edu/uni/registration/Main.java

# Run
java -cp bin edu.uni.registration.Main
```

### Tests

```bash
mvn test
```

Or run via your IDE.

## Demo Scenario

The app comes with pre-loaded test data:

- **Students:** S1-S6 (John Doe, Jane Roe, Mike Wazowski, etc.)
- **Instructors:** I1-I3 (Alice Smith, Bob Jones, Charlie Brown)
- **Admin:** A1 (Super Admin)
- **Courses:** CS101, CS102, CS201, MATH101, MATH102, PHYS101 (with prerequisites)
- **Sections:** 10 sections for Fall 2023 and Spring 2024

### Demo: Capacity, Waitlist, and Admin Override

Shows how capacity limits, waitlists, and admin overrides work. The waitlist auto-promotion was tricky to get right.

**Step 1:** Login as Student S1, enroll in `CS101-SMALL` (capacity: 1) → Success (first student)

**Step 2:** Login as Student S2, enroll in `CS101-SMALL` → Waitlisted (section full)

**Step 3:** Login as Student S3, enroll in `CS101-SMALL` → Waitlisted

**Step 4:** Login as Student S4, enroll in `CS101-SMALL` → Fails (waitlist also full)

**Step 5:** Login as Admin A1, override enrollment for S4 → Success (bypasses capacity)

**Step 6:** Login as Student S1, drop `CS101-SMALL` → S2 automatically promoted from waitlist

**Step 7:** Login as Student S2, view schedule → Should see `CS101-SMALL` (now enrolled)

### Other Scenarios

**Prerequisites:**
- S1 (no history) tries `CS102-01` → Fails (needs CS101)
- S3 (passed CS101) tries `CS102-01` → Succeeds

**Schedule Conflicts:**
- Enroll S1 in `CS101-01` (Mon/Wed 9:00-10:30)
- Try `MATH101-01` (Mon 11:00-12:30) → Succeeds (no overlap)
- Try overlapping section → Fails (time conflict)

## UML Class Diagram

Class diagram showing all entities and relationships. I used PlantUML for this - the source is in `docs/class-diagram.puml` if you want to edit it.

```mermaid
classDiagram
    class Person {
        <<abstract>>
        -String id
        -String firstName
        -String lastName
        -String email
        +getId() String
        +getFullName() String
        +role()* String
        +displayProfile()* String
    }
    
    class Student {
        -String major
        -int year
        -Transcript transcript
        -List~Enrollment~ currentEnrollments
        +getMajor() String
        +getYear() int
        +getTranscript() Transcript
    }
    
    class Instructor {
        -String department
        -String officeNumber
        -List~Section~ assignedSections
        +getDepartment() String
        +getOfficeNumber() String
    }
    
    class Admin {
    }
    
    class Course {
        -String code
        -String title
        -int credits
        -List~String~ prerequisites
        +getCode() String
        +getTitle() String
        +getCredits() int
        +matchesKeyword(String) boolean
    }
    
    class Section {
        -String id
        -Course course
        -String term
        -Instructor instructor
        -int capacity
        -List~TimeSlot~ meetingTimes
        -List~Enrollment~ roster
        +getId() String
        +getCourse() Course
        +getMeetingTimes() List~TimeSlot~
    }
    
    class Enrollment {
        -Student student
        -Section section
        -EnrollmentStatus status
        -Optional~Grade~ grade
        +getStudent() Student
        +getSection() Section
        +assignGrade(Grade) void
        +hasGrade() boolean
    }
    
    class Transcript {
        -Student student
        -List~TranscriptEntry~ entries
        +getStudent() Student
        +getGpa() double
    }
    
    class TranscriptEntry {
        -Section section
        -Grade grade
        +getSection() Section
        +getGrade() Grade
    }
    
    class TimeSlot {
        -DayOfWeek dayOfWeek
        -LocalTime start
        -LocalTime end
        -String room
        +overlaps(TimeSlot) boolean
    }
    
    class EnrollmentStatus {
        <<enumeration>>
        ENROLLED
        DROPPED
        WAITLISTED
    }
    
    class Grade {
        <<enumeration>>
        A
        B
        C
        D
        F
        I
        W
        +getPoints() double
        +countsTowardsGpa() boolean
    }
    
    class Schedulable {
        <<interface>>
        +getMeetingTimes() List~TimeSlot~
    }
    
    class Searchable {
        <<interface>>
        +matchesKeyword(String) boolean
    }
    
    class Gradable {
        <<interface>>
        +assignGrade(Grade) void
        +getGrade() Optional~Grade~
        +hasGrade() boolean
    }
    
    class Specification {
        <<interface>>
        +isSatisfiedBy(T) boolean
    }
    
    class CourseSpecification {
        -String code
        -String title
        -Integer minCredits
        -Integer maxCredits
        +isSatisfiedBy(Course) boolean
    }
    
    Person <|-- Student
    Person <|-- Instructor
    Person <|-- Admin
    
    Section ..|> Schedulable
    Course ..|> Searchable
    Enrollment ..|> Gradable
    CourseSpecification ..|> Specification
    
    Course "1" *-- "*" Section : contains
    Section "*" o-- "*" Enrollment : has
    Student "1" *-- "1" Transcript : has
    Transcript "1" *-- "*" TranscriptEntry : contains
    Section "*" o-- "*" TimeSlot : has
    Section "*" o-- "1" Instructor : assigned to
    Enrollment "*" o-- "1" Student : belongs to
    Enrollment "*" o-- "1" Section : enrolls in
    TranscriptEntry "*" o-- "1" Section : references
    Enrollment "*" o-- "1" Grade : receives
    TranscriptEntry "*" o-- "1" Grade : contains
    Enrollment "*" o-- "1" EnrollmentStatus : has
```

**Key Relationships:**
- Inheritance: `Person` → `Student`/`Instructor`/`Admin`
- Interfaces: `Section` → `Schedulable`, `Course` → `Searchable`, `Enrollment` → `Gradable`
- Associations: Course-Section (1-*), Section-Enrollment (*), Student-Transcript (1-1), etc.

## Tests

Unit tests cover business logic, validation, and repositories. See [docs/test-results.md](docs/test-results.md) for full output.

**Test Coverage:**
- Admin workflows and overrides
- Capacity and waitlist validation
- Prerequisite checking
- Repository CRUD operations
- Schedule conflict detection
- Transcript and GPA calculation
- TimeSlot overlap detection

```bash
mvn test
# Or run specific test: mvn test -Dtest=TimeSlotTest
```

## Notes

- **No persistence:** Everything is in-memory. I didn't implement a database because this was meant to be a demo project. Adding JDBC or JPA would be straightforward though.

- **CLI could be better:** Error messages are pretty basic. Could add input validation and clearer prompts, but it works for testing the core logic.

- **Waitlist logic:** The auto-promotion when someone drops was tricky. Had to make sure it only promotes the first waitlisted student and updates their status correctly.

