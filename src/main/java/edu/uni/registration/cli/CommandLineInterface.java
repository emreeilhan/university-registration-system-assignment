package edu.uni.registration.cli;

import edu.uni.registration.model.*;
import edu.uni.registration.service.*;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
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
        System.out.println("Uni Registration System v1.0");
        
        try {
            while (true) {
                System.out.println("\n=== LOGIN MENU ===");
                System.out.println("1. Student");
                System.out.println("2. Instructor");
                System.out.println("3. Admin");
                System.out.println("0. Exit");
                System.out.print("Choose option > ");

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
                        System.out.println("Bye.");
                        return;
                    default:
                        System.out.println("Unknown option.");
                }
            }
        } finally {
            // Purpose: Ensure Scanner is properly closed to prevent resource leak
            scanner.close();
        }
    }

    private void login(String role) {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();
        
        // Mock auth check
        this.currentUserId = id;
        this.currentUserRole = role;
        
        System.out.println("Signed in: " + role + " [" + id + "]");
        
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
            System.out.println("5. View Transcript");
            System.out.println("0. Logout");
            System.out.print("Select action: ");
            
            String choice = scanner.nextLine();
            if (choice.equals("0")) break;
            
            switch (choice) {
                case "1":
                    System.out.print("Advanced search? (y/N): ");
                    String adv = scanner.nextLine();
                    if ("y".equalsIgnoreCase(adv)) {
                        CourseQuery q = new CourseQuery();
                        System.out.print("Code (exact/contains): ");
                        String code = scanner.nextLine();
                        if (!code.isBlank()) q.setCode(code);

                        System.out.print("Title (contains): ");
                        String title = scanner.nextLine();
                        if (!title.isBlank()) q.setTitle(title);

                        System.out.print("Min Credits (blank to skip): ");
                        String minC = scanner.nextLine();
                        if (!minC.isBlank()) {
                            try { q.setMinCredits(Integer.parseInt(minC)); } catch (NumberFormatException e) { System.out.println("Ignored invalid min credits."); }
                        }

                        System.out.print("Max Credits (blank to skip): ");
                        String maxC = scanner.nextLine();
                        if (!maxC.isBlank()) {
                            try { q.setMaxCredits(Integer.parseInt(maxC)); } catch (NumberFormatException e) { System.out.println("Ignored invalid max credits."); }
                        }

                        System.out.print("Instructor name (contains, blank to skip): ");
                        String ins = scanner.nextLine();
                        if (!ins.isBlank()) q.setInstructorName(ins);

                        System.out.print("Day of week (e.g., MONDAY, blank to skip): ");
                        String day = scanner.nextLine();
                        if (!day.isBlank()) {
                            try { q.setDayOfWeek(DayOfWeek.valueOf(day.trim().toUpperCase())); }
                            catch (IllegalArgumentException e) { System.out.println("Ignored invalid day of week."); }
                        }

                        System.out.print("Start time (HH:mm, blank to skip): ");
                        String st = scanner.nextLine();
                        if (!st.isBlank()) {
                            try { q.setStartTime(LocalTime.parse(st)); }
                            catch (Exception e) { System.out.println("Ignored invalid start time."); }
                        }

                        System.out.print("End time (HH:mm, blank to skip): ");
                        String et = scanner.nextLine();
                        if (!et.isBlank()) {
                            try { q.setEndTime(LocalTime.parse(et)); }
                            catch (Exception e) { System.out.println("Ignored invalid end time."); }
                        }

                        Result<List<Course>> advRes = catalogService.search(q);
                        if (advRes.isOk()) advRes.get().forEach(System.out::println);
                        else System.out.println("Error: " + advRes.getError());
                    } else {
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
                case "5":
                    viewTranscript();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void viewTranscript() {
        Result<Transcript> res = registrationService.getTranscript(currentUserId);
        if (res.isOk()) {
            Transcript t = res.get();
            System.out.println("\n=== UNOFFICIAL TRANSCRIPT ===");
            System.out.println("Student: " + t.getStudent().getFullName() + " (" + t.getStudent().getId() + ")");
            System.out.println("Major: " + t.getStudent().getMajor());
            System.out.println("--------------------------------------------------");
            System.out.printf("%-10s %-20s %-8s %-5s%n", "Course", "Term", "Credits", "Grade");
            System.out.println("--------------------------------------------------");
            
            for (TranscriptEntry e : t.getEntries()) {
                System.out.printf("%-10s %-20s %-8d %-5s%n",
                        e.getSection().getCourse().getCode(),
                        e.getSection().getTerm(),
                        e.getCredits(),
                        e.getGrade());
            }
            System.out.println("--------------------------------------------------");
            System.out.printf("Total Credits: %d | GPA: %.2f%n", t.getTotalCredits(), t.getGpa());
        } else {
            System.out.println("Error retrieving transcript: " + res.getError());
        }
    }

    private void instructorMenu() {
        while (true) {
            System.out.println("\n=== INSTRUCTOR MENU (" + currentUserId + ") ===");
            System.out.println("1. List My Sections");
            System.out.println("2. View Roster of Section");
            System.out.println("3. Post Grade");
            System.out.println("0. Logout");
            System.out.print("Select action: ");

            String choice = scanner.nextLine();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1":
                    listInstructorSections();
                    break;
                case "2":
                    viewSectionRoster();
                    break;
                case "3":
                    postGradeForStudent();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void listInstructorSections() {
        Result<List<Section>> res = catalogService.getInstructorSections(currentUserId);
        if (res.isOk()) {
            List<Section> sections = res.get();
            if (sections.isEmpty()) {
                System.out.println("No sections assigned.");
            } else {
                System.out.println("Assigned Sections:");
                for (Section s : sections) {
                    System.out.printf(" - %s: %s (%s) [%d/%d students]%n",
                            s.getId(), s.getCourse().getTitle(), s.getTerm(),
                            s.getRoster().size(), s.getCapacity());
                }
            }
        } else {
            System.out.println("Error: " + res.getError());
        }
    }

    private void viewSectionRoster() {
        System.out.print("Enter Section ID: ");
        String sectionId = scanner.nextLine();
        
        Result<List<Section>> res = catalogService.getInstructorSections(currentUserId);
        if (res.isOk()) {
            Section target = res.get().stream()
                    .filter(s -> s.getId().equals(sectionId))
                    .findFirst()
                    .orElse(null);

            if (target != null) {
                System.out.println("Roster for " + target.getId() + ":");
                List<Enrollment> roster = target.getRoster();
                if (roster.isEmpty()) {
                    System.out.println("  (No students enrolled)");
                } else {
                    for (Enrollment e : roster) {
                        Student s = e.getStudent();
                        System.out.printf("  - %s (%s): %s [Grade: %s]%n",
                                s.getId(), s.getFullName(), e.getStatus(),
                                e.getGrade().map(Grade::toString).orElse("N/A"));
                    }
                }
            } else {
                System.out.println("Section not found or not assigned to you.");
            }
        } else {
             System.out.println("Error: " + res.getError());
        }
    }

    private void postGradeForStudent() {
        System.out.print("Enter Section ID: ");
        String sectionId = scanner.nextLine();
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Enter Grade (A, B, C, D, F, I, W): ");
        String gradeStr = scanner.nextLine();

        try {
            Grade grade = Grade.valueOf(gradeStr.toUpperCase());
            Result<Void> res = gradingService.postGrade(currentUserId, sectionId, studentId, grade);
            if (res.isOk()) {
                System.out.println("Grade posted successfully.");
            } else {
                System.out.println("Error: " + res.getError());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid grade format.");
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n=== ADMIN MENU (" + currentUserId + ") ===");
            System.out.println("1. Create Course");
            System.out.println("2. Create Section");
            System.out.println("3. Assign Instructor");
            System.out.println("4. Override Capacity");
            System.out.println("5. Override Enrollment (Force Add)");
            System.out.println("6. Edit Course (Title/Credits)");
            System.out.println("0. Logout");
            System.out.print("Select action: ");

            String choice = scanner.nextLine();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1":
                    createCourse();
                    break;
                case "2":
                    createSection();
                    break;
                case "3":
                    assignInstructor();
                    break;
                case "4":
                    overrideCapacity();
                    break;
                case "5":
                    forceEnroll();
                    break;
                case "6":
                    editCourse();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void createCourse() {
        System.out.print("Code: ");
        String code = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Credits: ");
        try {
            int credits = Integer.parseInt(scanner.nextLine());
            Result<Course> cRes = catalogService.createCourse(code, title, credits);
            if(cRes.isOk()) System.out.println("Course created.");
            else System.out.println("Error: " + cRes.getError());
        } catch (NumberFormatException e) {
            System.out.println("Invalid credits.");
        }
    }

    private void createSection() {
        System.out.print("Section ID: ");
        String secId = scanner.nextLine();
        System.out.print("Course Code: ");
        String cCode = scanner.nextLine();
        System.out.print("Term (e.g. Fall 2023): ");
        String term = scanner.nextLine();
        System.out.print("Capacity: ");
        try {
            int cap = Integer.parseInt(scanner.nextLine());
            
            // Find course by code (using first match)
            CourseQuery q = new CourseQuery();
            q.setCode(cCode);
            Result<List<Course>> search = catalogService.search(q);
            
            Course targetCourse = null;
            if(search.isOk() && !search.get().isEmpty()) {
                 targetCourse = search.get().stream()
                         .filter(c -> c.getCode().equalsIgnoreCase(cCode))
                         .findFirst()
                         .orElse(null);
            }
            
            if (targetCourse == null) {
                System.out.println("No course found for code: " + cCode);
                return;
            }

            Result<Section> sRes = catalogService.createSection(secId, targetCourse, term, cap);
            if (sRes.isOk()) System.out.println("Section created.");
            else System.out.println("Failed: " + sRes.getError());

        } catch (NumberFormatException e) {
            System.out.println("Capacity must be a number.");
        }
    }

    private void assignInstructor() {
        System.out.print("Section ID: ");
        String secId = scanner.nextLine();
        System.out.print("Instructor ID: ");
        String insId = scanner.nextLine();

        Result<Void> res = catalogService.assignInstructor(secId, insId);
        if (res.isOk()) {
            System.out.println("Instructor assigned successfully.");
        } else {
            System.out.println("Error: " + res.getError());
        }
    }

    private void overrideCapacity() {
        System.out.print("Section ID: ");
        String sId = scanner.nextLine();
        System.out.print("New Capacity: ");
        try {
            int cap = Integer.parseInt(scanner.nextLine());
            System.out.print("Reason: ");
            String reason = scanner.nextLine();
            
            Result<Void> ovrRes = catalogService.adminOverrideCapacity(sId, cap, currentUserId, reason);
            if (ovrRes.isOk()) System.out.println("Capacity updated.");
            else System.out.println("Failed: " + ovrRes.getError());
        } catch (NumberFormatException e) {
            System.out.println("Invalid capacity.");
        }
    }

    private void forceEnroll() {
        System.out.print("Student ID: ");
        String stuId = scanner.nextLine();
        System.out.print("Section ID: ");
        String secId = scanner.nextLine();
        System.out.print("Reason: ");
        String reason = scanner.nextLine();

        Result<Enrollment> res = registrationService.adminOverrideEnroll(stuId, secId, currentUserId, reason);
        if (res.isOk()) {
            System.out.println("Student forcefully enrolled. Status: " + res.get().getStatus());
        } else {
            System.out.println("Error: " + res.getError());
        }
    }

    private void editCourse() {
        System.out.print("Course Code to edit: ");
        String code = scanner.nextLine();
        if (code == null || code.isBlank()) {
            System.out.println("Code is required.");
            return;
        }
        System.out.print("New Title (leave blank to keep): ");
        String newTitle = scanner.nextLine();

        System.out.print("New Credits (leave blank to keep): ");
        String newCreditsStr = scanner.nextLine();
        Integer newCredits = null;
        if (!newCreditsStr.isBlank()) {
            try {
                newCredits = Integer.parseInt(newCreditsStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid credits. Aborting edit.");
                return;
            }
        }

        Result<Course> res = catalogService.updateCourse(code, newTitle, newCredits);
        if (res.isOk()) {
            Course c = res.get();
            System.out.printf("Updated: %s -> title='%s', credits=%d%n", c.getCode(), c.getTitle(), c.getCredits());
        } else {
            System.out.println("Error: " + res.getError());
        }
    }
}

