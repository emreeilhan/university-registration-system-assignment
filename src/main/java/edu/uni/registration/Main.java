package edu.uni.registration;

import edu.uni.registration.cli.CommandLineInterface;
import edu.uni.registration.model.*;
import edu.uni.registration.repository.*;
import edu.uni.registration.service.*;
import edu.uni.registration.service.impl.*;
import edu.uni.registration.validation.*;
import edu.uni.registration.util.Result;

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

        // 4. Seed Data (Örnek Veriler)
        seedData(studentRepo, courseRepo, sectionRepo, personRepo);

        // 5. Launch CLI
        CommandLineInterface cli = new CommandLineInterface(regService, catalogService, gradingService);
        cli.start();
    }

    private static void seedData(StudentRepository sRepo, CourseRepository cRepo, SectionRepository secRepo, PersonRepository pRepo) {
        System.out.println("Seeding data...");

        // Students
        Student s1 = new Student("S1", "Ali", "Yilmaz", "ali@uni.edu", "CS", 1);
        Student s2 = new Student("S2", "Ayse", "Demir", "ayse@uni.edu", "Math", 2);
        sRepo.save(s1);
        sRepo.save(s2);
        pRepo.save(s1); // Also save as Person if needed for login checks
        pRepo.save(s2);

        // Admin
        Admin a1 = new Admin("A1", "Super", "Admin", "admin@uni.edu");
        pRepo.save(a1);

        // Courses
        Course c1 = new Course("CS101", "Intro to CS", 3);
        Course c2 = new Course("CS102", "Data Structures", 4);
        c2.addPrerequisite("CS101");
        cRepo.save(c1);
        cRepo.save(c2);

        // Sections
        Section sec1 = new Section("SEC101-A", c1, "Fall", 30); // CS101
        Section sec2 = new Section("SEC102-A", c2, "Fall", 30); // CS102
        secRepo.save(sec1);
        secRepo.save(sec2);

        System.out.println("Data loaded. Users: S1 (Student), A1 (Admin). Sections: SEC101-A, SEC102-A.");
    }
}

