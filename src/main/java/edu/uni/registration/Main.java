package edu.uni.registration;

import edu.uni.registration.cli.CommandLineInterface;
import edu.uni.registration.model.*;
import edu.uni.registration.repository.*;
import edu.uni.registration.service.*;
import edu.uni.registration.service.impl.*;
import edu.uni.registration.validation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // 1. Setup Repositories
        StudentRepository studentRepo = new StudentRepository();
        CourseRepository courseRepo = new CourseRepository();
        SectionRepository sectionRepo = new SectionRepository();
        PersonRepository personRepo = new PersonRepository();
        TranscriptRepository transcriptRepo = new TranscriptRepository();
        EnrollmentRepository enrollmentRepo = new EnrollmentRepository();

        // 2. Setup Validators
        PrerequisiteValidator prereqVal = new PrerequisiteValidator();

        // 3. Setup Services
        RegistrationService regService = new RegistrationServiceImpl(
            studentRepo, sectionRepo, prereqVal, transcriptRepo, personRepo
        );
        CatalogService catalogService = new CatalogServiceImpl(
            courseRepo, sectionRepo, personRepo
        );
        GradingService gradingService = new GradingServiceImpl(
            studentRepo, sectionRepo, enrollmentRepo, transcriptRepo
        );

        // 4. Seed Data (Minimum Requirements: 6 courses, 10 sections, 6 students, 3 instructors, 1 admin)
        seedData(studentRepo, courseRepo, sectionRepo, personRepo, transcriptRepo);

        // 5. Launch CLI
        CommandLineInterface cli = new CommandLineInterface(regService, catalogService, gradingService);
        cli.start();
    }

    private static void seedData(StudentRepository sRepo, CourseRepository cRepo, SectionRepository secRepo, PersonRepository pRepo, TranscriptRepository tRepo) {
        System.out.println("Seeding data...");

        // --- 1. Users (Admin, Instructors, Students) ---
        
        // Admin
        Admin a1 = new Admin("A1", "Super", "Admin", "admin@uni.edu");
        pRepo.save(a1);

        // Instructors (3)
        Instructor i1 = new Instructor("I1", "Alice", "Smith", "asmith@uni.edu", "CS", "OFF-101");
        Instructor i2 = new Instructor("I2", "Bob", "Jones", "bjones@uni.edu", "Math", "OFF-102");
        Instructor i3 = new Instructor("I3", "Charlie", "Brown", "cbrown@uni.edu", "Physics", "OFF-103");
        pRepo.save(i1); pRepo.save(i2); pRepo.save(i3);

        // Students (6)
        Student s1 = new Student("S1", "John", "Doe", "john@uni.edu", "CS", 1);
        Student s2 = new Student("S2", "Jane", "Roe", "jane@uni.edu", "Math", 2);
        Student s3 = new Student("S3", "Mike", "Wazowski", "mike@uni.edu", "CS", 3);
        Student s4 = new Student("S4", "Sully", "Sullivan", "sully@uni.edu", "Physics", 4);
        Student s5 = new Student("S5", "Peter", "Parker", "peter@uni.edu", "CS", 1);
        Student s6 = new Student("S6", "Tony", "Stark", "tony@uni.edu", "Engineering", 2);
        
        for (Student s : Arrays.asList(s1, s2, s3, s4, s5, s6)) {
            sRepo.save(s);
            pRepo.save(s); // Save as Person for login lookup
        }

        // --- 2. Courses (6 Courses with Prerequisites) ---
        
        // Chain 1: CS101 -> CS102 -> CS201
        Course cs101 = new Course("CS101", "Intro to CS", 3);
        Course cs102 = new Course("CS102", "Data Structures", 4);
        cs102.addPrerequisite("CS101");
        Course cs201 = new Course("CS201", "Algorithms", 4);
        cs201.addPrerequisite("CS102");

        // Chain 2: MATH101 -> MATH102
        Course math101 = new Course("MATH101", "Calculus I", 4);
        Course math102 = new Course("MATH102", "Calculus II", 4);
        math102.addPrerequisite("MATH101");

        // Chain 3: MATH101 -> PHYS101 (Physics requires Calculus)
        Course phys101 = new Course("PHYS101", "Physics I", 4);
        phys101.addPrerequisite("MATH101");

        for (Course c : Arrays.asList(cs101, cs102, cs201, math101, math102, phys101)) {
            cRepo.save(c);
        }

        // --- 3. Preload Transcripts (Past History) ---
        // We create a "dummy" past section just to record grades.
        
        Section pastCs101 = new Section("PAST-CS101", cs101, "Spring 2023", 50);
        Section pastMath101 = new Section("PAST-MATH101", math101, "Spring 2023", 50);
        Section pastCs102 = new Section("PAST-CS102", cs102, "Spring 2023", 50);

        // S3 has passed CS101 -> Can take CS102
        Transcript t3 = new Transcript(s3);
        t3.addEntry(new TranscriptEntry(pastCs101, Grade.B));
        tRepo.save(t3);

        // S4 has passed CS101 and CS102 -> Can take CS201
        Transcript t4 = new Transcript(s4);
        t4.addEntry(new TranscriptEntry(pastCs101, Grade.A));
        t4.addEntry(new TranscriptEntry(pastCs102, Grade.A));
        tRepo.save(t4);
        
        // S6 has passed MATH101 -> Can take MATH102 or PHYS101
        Transcript t6 = new Transcript(s6);
        t6.addEntry(new TranscriptEntry(pastMath101, Grade.C));
        tRepo.save(t6);

        // --- 4. Sections (10 Offerings across Fall 2023 and Spring 2024) ---

        // Fall 2023
        Section sec1 = new Section("CS101-01", cs101, "Fall 2023", 30);
        sec1.setInstructor(i1); i1.addAssignedSection(sec1);
        sec1.addMeetingTime(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "Room 101"));
        sec1.addMeetingTime(new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "Room 101"));

        Section sec2 = new Section("CS101-02", cs101, "Fall 2023", 30);
        sec2.setInstructor(i1); i1.addAssignedSection(sec2); // Same instructor, different time
        sec2.addMeetingTime(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "Room 101"));

        Section sec3 = new Section("MATH101-01", math101, "Fall 2023", 40);
        sec3.setInstructor(i2); i2.addAssignedSection(sec3);
        sec3.addMeetingTime(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "Room 202"));

        Section sec4 = new Section("PHYS101-01", phys101, "Fall 2023", 25);
        sec4.setInstructor(i3); i3.addAssignedSection(sec4);

        // Spring 2024
        Section sec5 = new Section("CS102-01", cs102, "Spring 2024", 25);
        sec5.setInstructor(i1); i1.addAssignedSection(sec5);
        sec5.addMeetingTime(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30), "Room 102"));

        Section sec6 = new Section("CS201-01", cs201, "Spring 2024", 20);
        sec6.setInstructor(i1); i1.addAssignedSection(sec6);

        Section sec7 = new Section("MATH102-01", math102, "Spring 2024", 35);
        sec7.setInstructor(i2); i2.addAssignedSection(sec7);

        Section sec8 = new Section("MATH101-02", math101, "Spring 2024", 35); // Remedial section
        sec8.setInstructor(i2); i2.addAssignedSection(sec8);

        Section sec9 = new Section("PHYS101-02", phys101, "Spring 2024", 25);
        sec9.setInstructor(i3); i3.addAssignedSection(sec9);

        // Section with limited capacity to test Waitlist
        Section sec10 = new Section("CS101-SMALL", cs101, "Spring 2024", 1); // Capacity 1!
        sec10.setWaitlistCapacity(2);
        sec10.setInstructor(i3); i3.addAssignedSection(sec10);

        for (Section s : Arrays.asList(sec1, sec2, sec3, sec4, sec5, sec6, sec7, sec8, sec9, sec10)) {
            secRepo.save(s);
        }

        System.out.println("Seed data loaded successfully.");
        System.out.println("--------------------------------------------------");
        System.out.println("Users to try:");
        System.out.println("  S1 (Freshman, no history)");
        System.out.println("  S3 (Passed CS101 -> Can take CS102)");
        System.out.println("  S4 (Passed CS101, CS102 -> Can take CS201)");
        System.out.println("  A1 (Admin)");
        System.out.println("--------------------------------------------------");
    }
}
