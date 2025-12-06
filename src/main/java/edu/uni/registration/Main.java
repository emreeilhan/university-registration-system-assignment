package edu.uni.registration;

import edu.uni.registration.cli.CommandLineInterface;
import edu.uni.registration.gui.SimpleGui;
import edu.uni.registration.repository.*;
import edu.uni.registration.service.*;
import edu.uni.registration.service.impl.*;
import edu.uni.registration.util.SeedData;
import edu.uni.registration.validation.*;

import java.util.Scanner;

import javax.swing.SwingUtilities;

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
            studentRepo, sectionRepo, prereqVal, transcriptRepo, personRepo, enrollmentRepo
        );
        CatalogService catalogService = new CatalogServiceImpl(
            courseRepo, sectionRepo, personRepo
        );
        GradingService gradingService = new GradingServiceImpl(
            studentRepo, sectionRepo, enrollmentRepo, transcriptRepo
        );

        // 4. Seed Data
        SeedData.seedData(studentRepo, courseRepo, sectionRepo, personRepo, transcriptRepo, enrollmentRepo);

        // 5. Choose Interface (CLI or GUI)
        System.out.println("Choose mode: 1 for CLI, 2 for GUI");
        try (Scanner sc = new Scanner(System.in)) {
            if (sc.hasNextLine()) {
                String mode = sc.nextLine();
                if ("2".equals(mode)) {
                    System.out.println("Launching GUI...");
                    SwingUtilities.invokeLater(() -> {
                        new SimpleGui(regService, catalogService, gradingService).setVisible(true);
                    });
                } else {
                    CommandLineInterface cli = new CommandLineInterface(regService, catalogService, gradingService);
                    cli.start();
                }
            } else {
                CommandLineInterface cli = new CommandLineInterface(regService, catalogService, gradingService);
                cli.start();
            }
        }
    }
}
