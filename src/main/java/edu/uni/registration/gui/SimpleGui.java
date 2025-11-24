package edu.uni.registration.gui;

import edu.uni.registration.model.*;
import edu.uni.registration.service.CatalogService;
import edu.uni.registration.service.GradingService;
import edu.uni.registration.service.RegistrationService;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SimpleGui extends JFrame {

    private final RegistrationService registrationService;
    private final CatalogService catalogService;
    private final GradingService gradingService;
    
    private String currentUserId;
    private String currentUserRole; // STUDENT, INSTRUCTOR, ADMIN
    
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Radio buttons for login
    private JRadioButton rbStudent;
    private JRadioButton rbInstructor;
    private JRadioButton rbAdmin;

    public SimpleGui(RegistrationService registrationService, CatalogService catalogService, GradingService gradingService) {
        this.registrationService = registrationService;
        this.catalogService = catalogService;
        this.gradingService = gradingService;
        
        setTitle("University Registration System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add screens (Cards)
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createStudentDashboard(), "STUDENT");
        mainPanel.add(createInstructorDashboard(), "INSTRUCTOR");
        mainPanel.add(createAdminDashboard(), "ADMIN");

        add(mainPanel);
        
        // Start at Login
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("University System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel userLabel = new JLabel("User ID:");
        JTextField userField = new JTextField(15);
        
        // Role Selection
        JPanel rolePanel = new JPanel(new FlowLayout());
        rbStudent = new JRadioButton("Student");
        rbInstructor = new JRadioButton("Instructor");
        rbAdmin = new JRadioButton("Admin");
        
        // Group them
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbStudent);
        bg.add(rbInstructor);
        bg.add(rbAdmin);
        rbStudent.setSelected(true); // Default
        
        rolePanel.add(rbStudent);
        rolePanel.add(rbInstructor);
        rolePanel.add(rbAdmin);

        JButton loginBtn = new JButton("Login");
        
        loginBtn.addActionListener(e -> {
            String userId = userField.getText().trim();
            if (userId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID");
                return;
            }
            
            this.currentUserId = userId;
            
            if (rbStudent.isSelected()) {
                this.currentUserRole = "STUDENT";
                refreshStudentData();
                cardLayout.show(mainPanel, "STUDENT");
            } else if (rbInstructor.isSelected()) {
                this.currentUserRole = "INSTRUCTOR";
                refreshInstructorData();
                cardLayout.show(mainPanel, "INSTRUCTOR");
            } else if (rbAdmin.isSelected()) {
                this.currentUserRole = "ADMIN";
                cardLayout.show(mainPanel, "ADMIN");
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(rolePanel, gbc);
        
        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        return panel;
    }
    
    private void logout() {
        currentUserId = null;
        currentUserRole = null;
        cardLayout.show(mainPanel, "LOGIN");
    }

    // =================================================================================
    // STUDENT DASHBOARD
    // =================================================================================
    
    private JTable scheduleTable;
    private DefaultTableModel scheduleModel;
    
    private JTable transcriptTable;
    private DefaultTableModel transcriptModel;
    private JLabel gpaLabel;
    
    private JTable searchTable;
    private DefaultTableModel searchModel;

    private JPanel createStudentDashboard() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // TAB 1: My Schedule
        JPanel schedulePanel = new JPanel(new BorderLayout());
        String[] scheduleCols = {"Section ID", "Course", "Term", "Instructor", "Status"};
        scheduleModel = new DefaultTableModel(scheduleCols, 0);
        scheduleTable = new JTable(scheduleModel);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshStudentData());
        
        JButton dropBtn = new JButton("Drop Selected Section");
        dropBtn.addActionListener(e -> dropSelectedSection());

        JPanel btnPanel = new JPanel();
        btnPanel.add(refreshBtn);
        btnPanel.add(dropBtn);
        
        schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        schedulePanel.add(btnPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("My Schedule", schedulePanel);

        // TAB 2: Transcript (NEW)
        JPanel transcriptPanel = new JPanel(new BorderLayout());
        String[] transCols = {"Course", "Term", "Credits", "Grade"};
        transcriptModel = new DefaultTableModel(transCols, 0);
        transcriptTable = new JTable(transcriptModel);
        
        gpaLabel = new JLabel("GPA: 0.00 | Total Credits: 0");
        gpaLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        transcriptPanel.add(new JScrollPane(transcriptTable), BorderLayout.CENTER);
        transcriptPanel.add(gpaLabel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Transcript", transcriptPanel);

        // TAB 3: Search & Enroll
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel topSearch = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search Title");
        JButton enrollBtn = new JButton("Enroll in Selected");
        
        topSearch.add(new JLabel("Title:"));
        topSearch.add(searchField);
        topSearch.add(searchBtn);
        
        String[] searchCols = {"Course Code", "Title", "Credits", "Prereqs"};
        searchModel = new DefaultTableModel(searchCols, 0);
        searchTable = new JTable(searchModel);
        
        searchBtn.addActionListener(e -> {
            CourseQuery q = new CourseQuery();
            q.setTitle(searchField.getText());
            Result<List<Course>> res = catalogService.search(q);
            searchModel.setRowCount(0);
            if (res.isOk()) {
                for (Course c : res.get()) {
                    searchModel.addRow(new Object[]{c.getCode(), c.getTitle(), c.getCredits(), c.getPrerequisites()});
                }
            }
        });
        
        enrollBtn.addActionListener(e -> enrollSelectedCourse());

        searchPanel.add(topSearch, BorderLayout.NORTH);
        searchPanel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        searchPanel.add(enrollBtn, BorderLayout.SOUTH);

        tabbedPane.addTab("Search & Enroll", searchPanel);
        
        return createHeaderWrapper("Student Dashboard", tabbedPane);
    }

    private void refreshStudentData() {
        if (currentUserId == null || !"STUDENT".equals(currentUserRole)) return;
        
        // 1. Schedule
        scheduleModel.setRowCount(0);
        Result<List<Section>> res = registrationService.getCurrentSchedule(currentUserId, null);
        if (res.isOk()) {
            for (Section s : res.get()) {
                String status = "ENROLLED";
                for(Enrollment e : s.getRoster()) {
                     if(e.getStudent().getId().equals(currentUserId)) {
                         status = e.getStatus().toString();
                         break;
                     }
                }
                scheduleModel.addRow(new Object[]{
                    s.getId(), s.getCourse().getCode(), s.getTerm(),
                    s.getInstructor() != null ? s.getInstructor().getFullName() : "TBA",
                    status
                });
            }
        }
        
        // 2. Transcript
        transcriptModel.setRowCount(0);
        Result<Transcript> tRes = registrationService.getTranscript(currentUserId);
        if (tRes.isOk()) {
            Transcript t = tRes.get();
            for (TranscriptEntry e : t.getEntries()) {
                transcriptModel.addRow(new Object[]{
                    e.getSection().getCourse().getCode(),
                    e.getSection().getTerm(),
                    e.getCredits(),
                    e.getGrade()
                });
            }
            gpaLabel.setText(String.format("GPA: %.2f | Total Credits: %d", t.getGpa(), t.getTotalCredits()));
        } else {
             gpaLabel.setText("GPA: N/A (Error: " + tRes.getError() + ")");
        }
    }
    
    // =================================================================================
    // INSTRUCTOR DASHBOARD
    // =================================================================================
    
    private JTable insSectionTable;
    private DefaultTableModel insSectionModel;
    
    private JTable rosterTable;
    private DefaultTableModel rosterModel;
    
    private JPanel createInstructorDashboard() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // TAB 1: My Sections
        JPanel secPanel = new JPanel(new BorderLayout());
        String[] secCols = {"Section ID", "Course", "Term", "Capacity", "Enrolled"};
        insSectionModel = new DefaultTableModel(secCols, 0);
        insSectionTable = new JTable(insSectionModel);
        
        JButton loadRosterBtn = new JButton("View Roster of Selected");
        loadRosterBtn.addActionListener(e -> loadSelectedRoster());
        
        secPanel.add(new JScrollPane(insSectionTable), BorderLayout.CENTER);
        secPanel.add(loadRosterBtn, BorderLayout.SOUTH);
        
        tabbedPane.addTab("My Sections", secPanel);
        
        // TAB 2: Roster & Grading
        JPanel rosterPanel = new JPanel(new BorderLayout());
        String[] rostCols = {"Student ID", "Name", "Status", "Grade"};
        rosterModel = new DefaultTableModel(rostCols, 0);
        rosterTable = new JTable(rosterModel);
        
        JPanel gradingPanel = new JPanel(new FlowLayout());
        gradingPanel.add(new JLabel("Assign Grade (Select Student):"));
        String[] grades = {"A", "B", "C", "D", "F", "I", "W"};
        JComboBox<String> gradeCombo = new JComboBox<>(grades);
        gradingPanel.add(gradeCombo);
        JButton postGradeBtn = new JButton("Post Grade");
        
        postGradeBtn.addActionListener(e -> postGrade(gradeCombo.getSelectedItem().toString()));
        
        gradingPanel.add(postGradeBtn);
        
        rosterPanel.add(new JScrollPane(rosterTable), BorderLayout.CENTER);
        rosterPanel.add(gradingPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Class Roster", rosterPanel);
        
        return createHeaderWrapper("Instructor Dashboard", tabbedPane);
    }
    
    private void refreshInstructorData() {
        insSectionModel.setRowCount(0);
        Result<List<Section>> res = catalogService.getInstructorSections(currentUserId);
        if (res.isOk()) {
            for (Section s : res.get()) {
                insSectionModel.addRow(new Object[]{
                    s.getId(), s.getCourse().getTitle(), s.getTerm(),
                    s.getCapacity(), s.getRoster().size()
                });
            }
        }
    }
    
    private void loadSelectedRoster() {
        int row = insSectionTable.getSelectedRow();
        if (row == -1) return;
        
        String secId = (String) insSectionModel.getValueAt(row, 0);
        // Find section object
        Result<List<Section>> res = catalogService.getInstructorSections(currentUserId);
        if (res.isOk()) {
            Section target = null;
            // Find the section manually instead of using streams
            for (Section s : res.get()) {
                if (s.getId().equals(secId)) {
                    target = s;
                    break;
                }
            }
            
            if (target != null) {
                rosterModel.setRowCount(0);
                for (Enrollment e : target.getRoster()) {
                    rosterModel.addRow(new Object[]{
                        e.getStudent().getId(), e.getStudent().getFullName(),
                        e.getStatus(), e.getGrade().map(Grade::toString).orElse("-")
                    });
                }
                // Switch to roster tab
                // Hacky way to access JTabbedPane, usually we keep reference but for simple GUI:
                // ... assuming user clicks tab manually or we automate.
                JOptionPane.showMessageDialog(this, "Roster loaded in 'Class Roster' tab.");
            }
        }
    }
    
    private void postGrade(String gradeStr) {
        int row = rosterTable.getSelectedRow();
        if (row == -1) {
             JOptionPane.showMessageDialog(this, "Select a student from roster first.");
             return;
        }
        
        String studentId = (String) rosterModel.getValueAt(row, 0);
        
        // We need sectionId. Since roster is loaded from a selection, we need to track "currentSectionId".
        // For simplicity, we can ask user or store it. Let's ask Section ID again or store it in field?
        // Storing in field is better.
        int secRow = insSectionTable.getSelectedRow();
        if (secRow == -1) {
            JOptionPane.showMessageDialog(this, "Select the section in 'My Sections' tab again to confirm context.");
            return;
        }
        String sectionId = (String) insSectionModel.getValueAt(secRow, 0);
        
        Result<Void> res = gradingService.postGrade(currentUserId, sectionId, studentId, Grade.valueOf(gradeStr));
        if (res.isOk()) {
            JOptionPane.showMessageDialog(this, "Grade posted!");
            loadSelectedRoster(); // Refresh
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + res.getError());
        }
    }

    // =================================================================================
    // ADMIN DASHBOARD
    // =================================================================================

    private JPanel createAdminDashboard() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Create Course
        JPanel coursePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField cCode = new JTextField();
        JTextField cTitle = new JTextField();
        JTextField cCredits = new JTextField();
        JButton createCourseBtn = new JButton("Create Course");
        
        coursePanel.add(new JLabel("Code:")); coursePanel.add(cCode);
        coursePanel.add(new JLabel("Title:")); coursePanel.add(cTitle);
        coursePanel.add(new JLabel("Credits:")); coursePanel.add(cCredits);
        coursePanel.add(new JLabel("")); coursePanel.add(createCourseBtn);
        
        createCourseBtn.addActionListener(e -> {
            try {
                int cr = Integer.parseInt(cCredits.getText());
                Result<Course> res = catalogService.createCourse(cCode.getText(), cTitle.getText(), cr);
                if (res.isOk()) JOptionPane.showMessageDialog(this, "Course created!");
                else JOptionPane.showMessageDialog(this, "Error: " + res.getError());
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid input"); }
        });
        
        JPanel p1Wrapper = new JPanel(new FlowLayout()); p1Wrapper.add(coursePanel);
        tabbedPane.addTab("New Course", p1Wrapper);
        
        // Tab 2: Create Section
        JPanel secPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField sId = new JTextField();
        JTextField sCourseCode = new JTextField();
        JTextField sTerm = new JTextField();
        JTextField sCap = new JTextField();
        JButton createSecBtn = new JButton("Create Section");
        
        secPanel.add(new JLabel("Section ID:")); secPanel.add(sId);
        secPanel.add(new JLabel("Course Code:")); secPanel.add(sCourseCode);
        secPanel.add(new JLabel("Term:")); secPanel.add(sTerm);
        secPanel.add(new JLabel("Capacity:")); secPanel.add(sCap);
        secPanel.add(new JLabel("")); secPanel.add(createSecBtn);
        
        createSecBtn.addActionListener(e -> {
             try {
                int cap = Integer.parseInt(sCap.getText());
                // Find course first
                CourseQuery q = new CourseQuery(); q.setCode(sCourseCode.getText());
                Result<List<Course>> search = catalogService.search(q);
                Course c = null;
                if (search.isOk() && !search.get().isEmpty()) c = search.get().get(0); // Take first
                
                if (c == null) { JOptionPane.showMessageDialog(this, "Course not found"); return; }
                
                Result<Section> res = catalogService.createSection(sId.getText(), c, sTerm.getText(), cap);
                if (res.isOk()) JOptionPane.showMessageDialog(this, "Section created!");
                else JOptionPane.showMessageDialog(this, "Error: " + res.getError());
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid input"); }
        });
        
        JPanel p2Wrapper = new JPanel(new FlowLayout()); p2Wrapper.add(secPanel);
        tabbedPane.addTab("New Section", p2Wrapper);
        
        return createHeaderWrapper("Admin Dashboard", tabbedPane);
    }

    // --- Helpers ---
    
    private JPanel createHeaderWrapper(String title, JComponent content) {
        JPanel container = new JPanel(new BorderLayout());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        
        topBar.add(lbl, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        
        container.add(topBar, BorderLayout.NORTH);
        container.add(content, BorderLayout.CENTER);
        return container;
    }
    
    private void dropSelectedSection() {
        int row = scheduleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section to drop.");
            return;
        }
        String sectionId = (String) scheduleModel.getValueAt(row, 0);
        
        Result<Void> res = registrationService.dropStudentInSection(currentUserId, sectionId);
        if (res.isOk()) {
            JOptionPane.showMessageDialog(this, "Dropped successfully!");
            refreshStudentData();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + res.getError());
        }
    }
    
    private void enrollSelectedCourse() {
        String sectionId = JOptionPane.showInputDialog(this, "Enter Section ID to enroll (e.g. CS101-01):");
        if (sectionId != null && !sectionId.isBlank()) {
            Result<Enrollment> res = registrationService.enrollStudentInSection(currentUserId, sectionId);
            if (res.isOk()) {
                JOptionPane.showMessageDialog(this, "Enrolled! Status: " + res.get().getStatus());
                refreshStudentData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed: " + res.getError());
            }
        }
    }
}
