# Test Results

## Test Execution Summary

**Test Date:** November 24, 2025, 23:49:09 +03:00

**Test Framework:** JUnit 5 (Jupiter)

**Build Status:** ✅ BUILD SUCCESS

**Total Test Classes:** 9
- AdminWorkflowTest (3 tests)
- CapacityValidatorTest (4 tests)
- PrerequisiteValidatorTest (5 tests)
- RepositoryTest (4 tests)
- ScheduleConflictCheckerTest (3 tests)
- StudentTranscriptTest (2 tests)
- TimeSlotTest (5 tests)
- TranscriptTest (3 tests)
- AppTest (org.example) (1 test)

**Total Test Methods:** 30

**Test Results:**
- ✅ **Tests run:** 30
- ❌ **Failures:** 0
- ⚠️ **Errors:** 0
- ⏭️ **Skipped:** 0

**Total Execution Time:** 3.228 seconds

## Maven Test Output

### Command Executed

```bash
mvn test
```

### Actual Test Output

```
WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
WARNING: sun.misc.Unsafe::staticFieldBase has been called by com.google.inject.internal.aop.HiddenClassDefiner (file:/opt/homebrew/Cellar/maven/3.9.11/libexec/lib/guice-5.1.0-classes.jar)
WARNING: Please consider reporting this to the maintainers of class com.google.inject.internal.aop.HiddenClassDefiner
WARNING: sun.misc.Unsafe::staticFieldBase will be removed in a future release

[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------< edu.uni:university-registration-system >---------------
[INFO] Building university-registration-system 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ university-registration-system ---
[INFO] skip non existing resourceDirectory /Users/emreilhan/IdeaProjects/university-registration-system/src/main/resources
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ university-registration-system ---
[INFO] Recompiling the module because of added or removed source files.
[INFO] Compiling 38 source files with javac [debug release 17] to target/classes
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ university-registration-system ---
[INFO] skip non existing resourceDirectory /Users/emreilhan/IdeaProjects/university-registration-system/src/test/resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ university-registration-system ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 9 source files with javac [debug release 17] to target/test-classes
[INFO] 
[INFO] --- surefire:3.3.0:test (default-test) @ university-registration-system ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running edu.uni.registration.RepositoryTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.021 s -- in edu.uni.registration.RepositoryTest
[INFO] Running edu.uni.registration.CapacityValidatorTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in edu.uni.registration.CapacityValidatorTest
[INFO] Running edu.uni.registration.AdminWorkflowTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.020 s -- in edu.uni.registration.AdminWorkflowTest
[INFO] Running edu.uni.registration.PrerequisiteValidatorTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.004 s -- in edu.uni.registration.PrerequisiteValidatorTest
[INFO] Running edu.uni.registration.TranscriptTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in edu.uni.registration.TranscriptTest
[INFO] Running edu.uni.registration.TimeSlotTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in edu.uni.registration.TimeSlotTest
[INFO] Running edu.uni.registration.ScheduleConflictCheckerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in edu.uni.registration.ScheduleConflictCheckerTest
[INFO] Running edu.uni.registration.StudentTranscriptTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s -- in edu.uni.registration.StudentTranscriptTest
[INFO] Running org.example.AppTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s -- in org.example.AppTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.228 s
[INFO] Finished at: 2025-11-24T23:49:09+03:00
[INFO] ------------------------------------------------------------------------
```

## Test Coverage

The test suite covers the following areas:

1. **Admin Workflow Tests** - Admin override functionality and capacity management
2. **Capacity Validator Tests** - Section capacity and waitlist validation
3. **Prerequisite Validator Tests** - Course prerequisite checking logic
4. **Repository Tests** - CRUD operations for all repository implementations
5. **Schedule Conflict Checker Tests** - Time slot overlap detection
6. **Student Transcript Tests** - Transcript retrieval and GPA calculation
7. **Time Slot Tests** - TimeSlot model validation and overlap detection
8. **Transcript Tests** - Transcript entry management and GPA calculation

## Instructions to Run Tests

### Prerequisites

1. Install Maven (if not already installed):
   - **macOS:** `brew install maven`
   - **Linux:** `sudo apt-get install maven` or `sudo yum install maven`
   - **Windows:** Download from [Apache Maven](https://maven.apache.org/download.cgi)

2. Ensure Java JDK 17+ is installed

### Running Tests

```bash
# Navigate to project root
cd /path/to/university-registration-system

# Run all tests
mvn test

# Run tests with verbose output
mvn test -X

# Run specific test class
mvn test -Dtest=TimeSlotTest

# Run tests and generate coverage report (if configured)
mvn test jacoco:report
```

### Alternative: Run Tests via IDE

Most IDEs (IntelliJ IDEA, Eclipse, VS Code) can run JUnit tests directly:
- Right-click on `src/test` folder → "Run All Tests"
- Or run individual test classes

## Test Execution Details

### Individual Test Class Results

| Test Class | Tests Run | Failures | Errors | Skipped | Time (s) |
|------------|-----------|----------|--------|---------|----------|
| RepositoryTest | 4 | 0 | 0 | 0 | 0.021 |
| CapacityValidatorTest | 4 | 0 | 0 | 0 | 0.005 |
| AdminWorkflowTest | 3 | 0 | 0 | 0 | 0.020 |
| PrerequisiteValidatorTest | 5 | 0 | 0 | 0 | 0.004 |
| TranscriptTest | 3 | 0 | 0 | 0 | 0.005 |
| TimeSlotTest | 5 | 0 | 0 | 0 | 0.003 |
| ScheduleConflictCheckerTest | 3 | 0 | 0 | 0 | 0.002 |
| StudentTranscriptTest | 2 | 0 | 0 | 0 | 0.001 |
| AppTest | 1 | 0 | 0 | 0 | 0.001 |
| **TOTAL** | **30** | **0** | **0** | **0** | **0.062** |

### Build Information

- **Maven Version:** 3.9.11
- **Java Version:** 17 (debug release)
- **Source Files Compiled:** 38
- **Test Files Compiled:** 9
- **Total Build Time:** 3.228 seconds

### Notes

- All tests passed successfully ✅
- No failures or errors detected
- Build completed successfully
- Warnings about deprecated `sun.misc.Unsafe` methods are from Maven dependencies and do not affect test execution

