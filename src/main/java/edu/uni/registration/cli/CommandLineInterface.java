package edu.uni.registration.cli;

import edu.uni.registration.model.*;
import edu.uni.registration.repository.*;
import edu.uni.registration.service.*;
import edu.uni.registration.service.impl.*;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;
import edu.uni.registration.validation.*;

import java.util.List;
import java.util.Scanner;
import java.util.Scanner;

public class CommandLineInterface {

    private final RegistrationService registrationService;
    private final CatalogService catalogService;
    private final GradingService gradingService;
    private final Scanner scanner;

    // Temporary session state
    private String currentUserId;
    private String currentUserRole; // STUDENT, INSTRUCTOR, ADMIN

    public CommandLineInterface(RegistrationService registrationService,
                                CatalogService catalogService,
                                GradingService gradingService) {
        this.registrationService = registrationService;
        this.catalogService = catalogService;
        this.gradingService = gradingService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the University Registration System");
        
        while (true) {
            System.out.println("\n=== LOGIN MENU ===");
            System.out.println("1. Login as Student");
            System.out.println("2. Login as Instructor");
            System.out.println("3. Login as Admin");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    login("STUDENT");
                    break;
                case "2":
                    login("INSTRUCTOR");
                    break;
                case "3":
                    login("ADMIN");
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void login(String role) {
        System.out.print("Enter your ID: ");
        String id = scanner.nextLine();
        // In a real app, we would verify existence here.
        // For this demo, we assume the ID is valid if it exists in our mock data,
        // or we just proceed to show the menu.
        
        this.currentUserId = id;
        this.currentUserRole = role;
        
        System.out.println("Logged in as " + role + " (" + id + ")");
        
        switch (role) {
            case "STUDENT":
                studentMenu();
                break;
            case "INSTRUCTOR":
                instructorMenu();
                break;
            case "ADMIN":
                adminMenu();
                break;
        }
    }

    private void studentMenu() {
        while (true) {
            System.out.println("\n=== STUDENT MENU (" + currentUserId + ") ===");
            System.out.println("1. Search Courses");
            System.out.println("2. Enroll in Section");
            System.out.println("3. Drop Section");
            System.out.println("4. View Schedule");
            System.out.println("0. Logout");
            System.out.print("Select action: ");
            
            String choice = scanner.nextLine();
            if (choice.equals("0")) break;
            
            switch (choice) {
                case "1":
                    System.out.print("Enter search keyword (or press enter for all): ");
                    String queryStr = scanner.nextLine();
                    CourseQuery query = new CourseQuery();
                    query.setTitle(queryStr);
                    Result<List<Course>> searchResult = catalogService.search(query);
                    if (searchResult.isOk()) {
                        searchResult.get().forEach(System.out::println);
                    } else {
                        System.out.println("Error: " + searchResult.getError());
                    }
                    break;
                case "2":
                    System.out.print("Enter Section ID to enroll: ");
                    String sectionId = scanner.nextLine();
                    Result<Enrollment> enrollRes = registrationService.enrollStudentInSection(currentUserId, sectionId);
                    if (enrollRes.isOk()) {
                        System.out.println("Success! Status: " + enrollRes.get().getStatus());
                    } else {
                        System.out.println("Failed: " + enrollRes.getError());
                    }
                    break;
                case "3":
                    System.out.print("Enter Section ID to drop: ");
                    String dropSecId = scanner.nextLine();
                    Result<Void> dropRes = registrationService.dropStudentInSection(currentUserId, dropSecId);
                    if (dropRes.isOk()) {
                        System.out.println("Dropped successfully.");
                    } else {
                        System.out.println("Failed: " + dropRes.getError());
                    }
                    break;
                case "4":
                    Result<List<Section>> scheduleRes = registrationService.getCurrentSchedule(currentUserId, null);
                    if (scheduleRes.isOk()) {
                        System.out.println("Your Schedule:");
                        scheduleRes.get().forEach(System.out::println);
                    } else {
                        System.out.println("Error: " + scheduleRes.getError());
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void instructorMenu() {
        System.out.println("\n=== INSTRUCTOR MENU (" + currentUserId + ") ===");
        System.out.println("Not fully implemented in this demo.");
        System.out.println("Press enter to return.");
        scanner.nextLine();
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n=== ADMIN MENU (" + currentUserId + ") ===");
            System.out.println("1. Create Course");
            System.out.println("2. Create Section");
            System.out.println("3. Override Capacity");
            System.out.println("0. Logout");
            System.out.print("Select action: ");

            String choice = scanner.nextLine();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1":
                    System.out.print("Code: ");
                    String code = scanner.nextLine();
                    System.out.print("Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Credits: ");
                    int credits = Integer.parseInt(scanner.nextLine());
                    Result<Course> cRes = catalogService.createCourse(code, title, credits);
                    if(cRes.isOk()) System.out.println("Course created.");
                    else System.out.println("Error: " + cRes.getError());
                    break;
                case "2":
                    System.out.print("Section ID: ");
                    String secId = scanner.nextLine();
                    System.out.print("Course Code: ");
                    String cCode = scanner.nextLine();
                    // In real CLI we would fetch the course obj first
                    // For demo simplicity, we assume course exists or user knows flow
                    System.out.println("... (Mocking course lookup) ...");
                    // Assuming we found the course for code cCode
                    // Course c = courseRepo.findById(cCode)...
                    System.out.println("Feature simplified for demo. Please add data via DataLoader.");
                    break;
                case "3":
                    System.out.print("Section ID: ");
                    String sId = scanner.nextLine();
                    System.out.print("New Capacity: ");
                    int cap = Integer.parseInt(scanner.nextLine());
                    System.out.print("Reason: ");
                    String reason = scanner.nextLine();
                    
                    Result<Void> ovrRes = catalogService.adminOverrideCapacity(sId, cap, currentUserId, reason);
                    if (ovrRes.isOk()) System.out.println("Capacity updated.");
                    else System.out.println("Failed: " + ovrRes.getError());
                    break;
            }
        }
    }
}

