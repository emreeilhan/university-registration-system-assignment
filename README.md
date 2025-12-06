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
- **GUI Interface:** Modern Swing-based graphical interface with color-coded status indicators
- **Architecture:**
    - Model: Core entities (Student, Course, Section, etc.)
    - Repository: In-memory storage using Repository Pattern
    - Service: Business logic returns `Result<T>` instead of throwing exceptions
    - CLI: Command-line interface
    - GUI: Graphical user interface with role-based dashboards

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
├── gui/            # Graphical User Interface (Swing)
└── Main.java       # Application entry point (DI & Seeding)
```

## How to Run

### Prerequisites
- Java JDK 11+
- Maven

### Compile & Run

#### Using Maven (Recommended)

```bash
# Compile the project
mvn clean compile

# Run with Maven
mvn exec:java -Dexec.mainClass="edu.uni.registration.Main"

# When prompted, choose interface:
# 1 for CLI (Command Line Interface)
# 2 for GUI (Graphical User Interface)
```

#### Using Java directly

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

### GUI Features

The GUI provides three role-based dashboards:

**Student Dashboard:**
- My Schedule tab: View enrolled and waitlisted sections with color-coded status
  - Green: Enrolled
  - Yellow: Waitlisted
  - Red: Dropped
- Transcript tab: View completed courses, grades, and GPA
- Search & Enroll tab: Search courses and enroll in sections with detailed information

**Instructor Dashboard:**
- My Sections tab: View assigned sections
- Class Roster tab: View enrolled students and post grades

**Admin Dashboard:**
- New Course tab: Create new courses
- New Section tab: Create new sections
- Assign Instructor tab: Assign instructors to sections
- Override Capacity tab: Override section capacity with audit logging

## Demo Scenario

Pre-loaded test data:
- **Students:** S1-S6, **Instructors:** I1-I3, **Admin:** A1
- **Courses:** CS101 → CS102 → CS201, MATH101 → MATH102, PHYS101
- **Sections:** 10 sections (Fall 2023, Spring 2024)

### Demo Script (Step-by-Step)

Follow these steps to demonstrate the system's core functionality:

#### ✅ Case 1: Successful Enrollment

```
1. Start the application: mvn exec:java -Dexec.mainClass="edu.uni.registration.Main"
2. Choose: 1 (CLI)
3. Choose: 1 (Student)
4. Enter ID: S3
5. Select: 2 (Enroll in Section)
6. Enter Section ID: CS102-01
7. Expected: "Success! Status: ENROLLED"
   (S3 passed CS101 with grade B, so prerequisite is satisfied)
```

#### ❌ Case 2: Prerequisite Failure

```
1. Choose: 1 (Student)
2. Enter ID: S1
3. Select: 2 (Enroll in Section)
4. Enter Section ID: CS102-01
5. Expected: "Failed: Prereqs not met"
   (S1 has no transcript history, hasn't completed CS101)
```

#### ❌ Case 3: Schedule Conflict Failure

```
1. Choose: 1 (Student)
2. Enter ID: S1
3. Select: 2 (Enroll in Section)
4. Enter Section ID: CS101-01
5. Expected: "Success! Status: ENROLLED"
6. Select: 2 (Enroll in Section)
7. Enter Section ID: CS102-01 (Spring 2024, Mon 10:00-11:30)
   -- First need to satisfy prereq, so let's try a different conflict --
8. Enter Section ID: MATH101-01 (Fall 2023, Mon 11:00-12:30)
   -- This doesn't conflict with CS101-01 (Mon 9:00-10:30), so try: --
9. For conflict demo, use S3 who is already in CS102-01:
   - Login as S3
   - Try to enroll in CS101-01 (Mon/Wed 9:00-10:30)
   - Then try CS102-01 (Mon 10:00-11:30) - OVERLAPS!
   - Expected: "Failed: Time conflict with CS101-01"
```

#### ❌ Case 4: Capacity/Waitlist Failure

```
1. Choose: 1 (Student)
2. Enter ID: S1
3. Select: 2 (Enroll in Section)
4. Enter Section ID: CS101-SMALL (capacity: 1, waitlist: 2)
5. Expected: "Success! Status: ENROLLED" (first student)

6. Logout, login as S2
7. Enroll in CS101-SMALL
8. Expected: "Success! Status: WAITLISTED" (waitlist spot 1)

9. Logout, login as S5
10. Enroll in CS101-SMALL
11. Expected: "Success! Status: WAITLISTED" (waitlist spot 2)

12. Logout, login as S6
13. Enroll in CS101-SMALL
14. Expected: "Failed: Section/Waitlist full"
```

#### 🔧 Case 5: Admin Override

```
1. Choose: 3 (Admin)
2. Enter ID: A1
3. Select: 5 (Override Enrollment - Force Add)
4. Enter Student ID: S1
5. Enter Section ID: CS102-01
6. Enter Reason: "Department head approval"
7. Expected: "Student forcefully enrolled. Status: ENROLLED"
   (Bypasses prerequisite check, logged for audit)
```

### Summary Table

| Case | User | Action | Expected Result |
|------|------|--------|-----------------|
| 1 | S3 | Enroll CS102-01 | ✅ Success (prereq met) |
| 2 | S1 | Enroll CS102-01 | ❌ Prereqs not met |
| 3 | S3 | Enroll overlapping section | ❌ Time conflict |
| 4 | S6 | Enroll full section | ❌ Section/Waitlist full |
| 5 | A1 | Force enroll S1 | ✅ Admin override success |

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
    
    class CourseQuery {
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
    CourseQuery ..|> Specification
    
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

## Recent Improvements (December 2025)

### GUI Enhancements

1. **Fixed Schedule Display:**
   - Now correctly shows both ENROLLED and WAITLISTED sections
   - Previously only ENROLLED sections were visible

2. **Enhanced Drop Functionality:**
   - Added confirmation dialog before dropping
   - Shows current enrollment status in confirmation
   - Better error handling

3. **Improved Section Selection:**
   - Displays term, meeting times, and capacity information
   - Shows enrolled/capacity ratio (e.g., "15/30 enrolled")
   - Clear distinction between enrolled and waitlisted status

4. **Visual Status Indicators:**
   - Color-coded schedule table:
     - Light green: Enrolled
     - Light yellow: Waitlisted
     - Light red: Dropped
   - Makes status immediately visible

5. **Better Transcript Handling:**
   - User-friendly message for empty transcripts
   - Clear display of GPA and total credits

6. **Admin Features:**
   - Added Instructor Assignment tab
   - Added Capacity Override tab with audit logging
   - Input validation and clear success/error messages

## Notes

- **No persistence:** Everything is in-memory. I didn't implement a database because this was meant to be a demo project. Adding JDBC or JPA would be straightforward though.

- **CLI could be better:** Error messages are pretty basic. Could add input validation and clearer prompts, but it works for testing the core logic.

- **Waitlist logic:** The auto-promotion when someone drops was tricky. Had to make sure it only promotes the first waitlisted student and updates their status correctly.

- **GUI vs CLI:** Both interfaces use the same service layer, demonstrating proper separation of concerns.

