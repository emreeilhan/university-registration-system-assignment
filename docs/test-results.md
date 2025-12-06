# Test Results

## Test Execution Summary

**Test Date:** December 7, 2025, 01:33:43 +03:00

**Test Framework:** JUnit 5 (Jupiter)

**Build Status:** ✅ BUILD SUCCESS

**Total Test Classes:** 12
- AdminAuthorizationTest (3 tests)
- AdminWorkflowTest (3 tests)
- CapacityValidatorTest (4 tests)
- CourseSearchSpecificationTest (2 tests)
- GradingServiceTest (2 tests)
- PrerequisiteValidatorTest (5 tests)
- RepositoryTest (4 tests)
- ScheduleConflictCheckerTest (3 tests)
- StudentTranscriptTest (2 tests)
- TimeSlotTest (5 tests)
- TranscriptTest (3 tests)
- AppTest (org.example) (1 test)

**Total Test Methods:** 37

**Test Results:**
- ✅ **Tests run:** 37
- ❌ **Failures:** 0
- ⚠️ **Errors:** 0
- ⏭️ **Skipped:** 0

**Total Execution Time:** 1.278 seconds

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
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ university-registration-system ---
[INFO] skip non existing resourceDirectory /Users/emreilhan/IdeaProjects/university-registration-system/src/test/resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ university-registration-system ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- surefire:3.3.0:test (default-test) @ university-registration-system ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running edu.uni.registration.RepositoryTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.042 s -- in edu.uni.registration.RepositoryTest
[INFO] Running edu.uni.registration.CapacityValidatorTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.007 s -- in edu.uni.registration.CapacityValidatorTest
[INFO] Running edu.uni.registration.AdminWorkflowTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.026 s -- in edu.uni.registration.AdminWorkflowTest
[INFO] Running edu.uni.registration.AdminAuthorizationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in edu.uni.registration.AdminAuthorizationTest
[INFO] Running edu.uni.registration.PrerequisiteValidatorTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.011 s -- in edu.uni.registration.PrerequisiteValidatorTest
[INFO] Running edu.uni.registration.TranscriptTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in edu.uni.registration.TranscriptTest
[INFO] Running edu.uni.registration.TimeSlotTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in edu.uni.registration.TimeSlotTest
[INFO] Running edu.uni.registration.ScheduleConflictCheckerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in edu.uni.registration.ScheduleConflictCheckerTest
[INFO] Running edu.uni.registration.CourseSearchSpecificationTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in edu.uni.registration.CourseSearchSpecificationTest
[INFO] Running edu.uni.registration.StudentTranscriptTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in edu.uni.registration.StudentTranscriptTest
[INFO] Running edu.uni.registration.GradingServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.004 s -- in edu.uni.registration.GradingServiceTest
[INFO] Running org.example.AppTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s -- in org.example.AppTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 37, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.278 s
[INFO] Finished at: 2025-12-07T01:33:43+03:00
[INFO] ------------------------------------------------------------------------
```

## Test Coverage

The test suite covers the following areas:

1. **Admin Authorization Tests** - Role-based access control for admin operations
2. **Admin Workflow Tests** - Admin override functionality and capacity management
3. **Capacity Validator Tests** - Section capacity and waitlist validation
4. **Course Search Specification Tests** - CourseQuery specification pattern validation
5. **Grading Service Tests** - Grade posting and transcript updates
6. **Prerequisite Validator Tests** - Course prerequisite checking logic
7. **Repository Tests** - CRUD operations for all repository implementations
8. **Schedule Conflict Checker Tests** - Time slot overlap detection
9. **Student Transcript Tests** - Transcript retrieval and GPA calculation
10. **Time Slot Tests** - TimeSlot model validation and overlap detection
11. **Transcript Tests** - Transcript entry management and GPA calculation

## Test Execution Details

### Individual Test Class Results

| Test Class | Tests Run | Failures | Errors | Skipped | Time (s) |
|------------|-----------|----------|--------|---------|----------|
| RepositoryTest | 4 | 0 | 0 | 0 | 0.042 |
| CapacityValidatorTest | 4 | 0 | 0 | 0 | 0.007 |
| AdminWorkflowTest | 3 | 0 | 0 | 0 | 0.026 |
| AdminAuthorizationTest | 3 | 0 | 0 | 0 | 0.006 |
| PrerequisiteValidatorTest | 5 | 0 | 0 | 0 | 0.011 |
| TranscriptTest | 3 | 0 | 0 | 0 | 0.005 |
| TimeSlotTest | 5 | 0 | 0 | 0 | 0.006 |
| ScheduleConflictCheckerTest | 3 | 0 | 0 | 0 | 0.002 |
| CourseSearchSpecificationTest | 2 | 0 | 0 | 0 | 0.003 |
| StudentTranscriptTest | 2 | 0 | 0 | 0 | 0.003 |
| GradingServiceTest | 2 | 0 | 0 | 0 | 0.004 |
| AppTest | 1 | 0 | 0 | 0 | 0.001 |
| **TOTAL** | **37** | **0** | **0** | **0** | **0.116** |

### Build Information

- **Maven Version:** 3.9.11
- **Java Version:** 17 (debug release)
- **Source Files Compiled:** 38
- **Test Files Compiled:** 12
- **Total Build Time:** 1.278 seconds

### Notes

- All tests passed successfully ✅
- No failures or errors detected
- Build completed successfully
- 37 total tests exceeding the minimum requirement of 10
- Warnings about deprecated `sun.misc.Unsafe` methods are from Maven dependencies and do not affect test execution
