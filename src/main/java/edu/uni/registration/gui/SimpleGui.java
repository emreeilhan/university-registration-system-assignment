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
        scheduleModel = new DefaultTableModel(scheduleCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        scheduleTable = new JTable(scheduleModel) {
            @Override
            public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                java.awt.Component c = super.prepareRenderer(renderer, row, column);
                
                if (!isRowSelected(row)) {
                    String status = (String) getValueAt(row, 4);
                    if ("WAITLISTED".equals(status)) {
                        c.setBackground(new Color(255, 250, 205)); // Light yellow for waitlist
                    } else if ("ENROLLED".equals(status)) {
                        c.setBackground(new Color(230, 255, 230)); // Light green for enrolled
                    } else if ("DROPPED".equals(status)) {
                        c.setBackground(new Color(255, 230, 230)); // Light red for dropped
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(getSelectionBackground());
                }
                return c;
            }
        };
        
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

    // Purpose: Refresh student schedule and transcript data
    private void refreshStudentData() {
        if (currentUserId == null || !"STUDENT".equals(currentUserRole)) return;
        
        // 1. Schedule - Show both ENROLLED and WAITLISTED sections
        scheduleModel.setRowCount(0);
        Result<List<Section>> res = registrationService.getCurrentSchedule(currentUserId, null);
        if (res.isOk()) {
            for (Section s : res.get()) {
                String status = "UNKNOWN";
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
        
        // 2. Transcript - Handle empty transcript gracefully
        transcriptModel.setRowCount(0);
        Result<Transcript> tRes = registrationService.getTranscript(currentUserId);
        if (tRes.isOk()) {
            Transcript t = tRes.get();
            if (t.getEntries().isEmpty()) {
                
                gpaLabel.setText("No completed courses yet. GPA: 0.00 | Total Credits: 0");
            } else {
                for (TranscriptEntry e : t.getEntries()) {
                    transcriptModel.addRow(new Object[]{
                        e.getSection().getCourse().getCode(),
                        e.getSection().getTerm(),
                        e.getCredits(),
                        e.getGrade()
                    });
                }
                gpaLabel.setText(String.format("GPA: %.2f | Total Credits: %d", t.getGpa(), t.getTotalCredits()));
            }
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
                if (res.isOk()) {
                    JOptionPane.showMessageDialog(this, "Course created successfully!");
                    cCode.setText(""); cTitle.setText(""); cCredits.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + res.getError());
                }
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
                if (res.isOk()) {
                    JOptionPane.showMessageDialog(this, "Section created successfully!");
                    sId.setText(""); sCourseCode.setText(""); sTerm.setText(""); sCap.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + res.getError());
                }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage()); }
        });
        
        JPanel p2Wrapper = new JPanel(new FlowLayout()); p2Wrapper.add(secPanel);
        tabbedPane.addTab("New Section", p2Wrapper);
        
        // Tab 3: Assign Instructor (NEW)
        JPanel assignPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField assignSecId = new JTextField();
        JTextField assignInsId = new JTextField();
        JButton assignBtn = new JButton("Assign Instructor");
        
        assignPanel.add(new JLabel("Section ID:")); assignPanel.add(assignSecId);
        assignPanel.add(new JLabel("Instructor ID:")); assignPanel.add(assignInsId);
        assignPanel.add(new JLabel("")); assignPanel.add(assignBtn);
        
        // Purpose: Allow admin to assign instructors to sections
        assignBtn.addActionListener(e -> {
            String secId = assignSecId.getText().trim();
            String insId = assignInsId.getText().trim();
            
            if (secId.isEmpty() || insId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both Section ID and Instructor ID are required");
                return;
            }
            
            Result<Void> res = catalogService.assignInstructor(secId, insId);
            if (res.isOk()) {
                JOptionPane.showMessageDialog(this, "Instructor assigned successfully!");
                assignSecId.setText(""); assignInsId.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res.getError());
            }
        });
        
        JPanel p3Wrapper = new JPanel(new FlowLayout()); p3Wrapper.add(assignPanel);
        tabbedPane.addTab("Assign Instructor", p3Wrapper);
        
        // Tab 4: Override Capacity (NEW)
        JPanel overridePanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField overrideSecId = new JTextField();
        JTextField overrideNewCap = new JTextField();
        JTextField overrideReason = new JTextField();
        JButton overrideBtn = new JButton("Override Capacity");
        
        overridePanel.add(new JLabel("Section ID:")); overridePanel.add(overrideSecId);
        overridePanel.add(new JLabel("New Capacity:")); overridePanel.add(overrideNewCap);
        overridePanel.add(new JLabel("Reason:")); overridePanel.add(overrideReason);
        overridePanel.add(new JLabel("")); overridePanel.add(overrideBtn);
        
        // Purpose: Allow admin to override section capacity with audit log
        overrideBtn.addActionListener(e -> {
            try {
                String secId = overrideSecId.getText().trim();
                int newCap = Integer.parseInt(overrideNewCap.getText().trim());
                String reason = overrideReason.getText().trim();
                
                if (secId.isEmpty() || reason.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Section ID and Reason are required");
                    return;
                }
                
                Result<Void> res = catalogService.adminOverrideCapacity(secId, newCap, currentUserId, reason);
                if (res.isOk()) {
                    JOptionPane.showMessageDialog(this, "Capacity override successful! (Logged)");
                    overrideSecId.setText(""); overrideNewCap.setText(""); overrideReason.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + res.getError());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid capacity value");
            }
        });
        
        JPanel p4Wrapper = new JPanel(new FlowLayout()); p4Wrapper.add(overridePanel);
        tabbedPane.addTab("Override Capacity", p4Wrapper);
        
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
    
    // Purpose: Drop selected section with proper status validation
    private void dropSelectedSection() {
        int row = scheduleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section to drop.");
            return;
        }
        String sectionId = (String) scheduleModel.getValueAt(row, 0);
        String status = (String) scheduleModel.getValueAt(row, 4);
        
        // Purpose: Confirm drop action with user, showing current status
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to drop this section?\nCurrent Status: " + status,
            "Confirm Drop", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        Result<Void> res = registrationService.dropStudentInSection(currentUserId, sectionId);
        if (res.isOk()) {
            JOptionPane.showMessageDialog(this, "Section dropped successfully!");
            refreshStudentData();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + res.getError());
        }
    }
    
    // Purpose: Enroll in selected course with detailed section information
    private void enrollSelectedCourse() {
        int row = searchTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return;
        }

        String courseCode = (String) searchModel.getValueAt(row, 0);
        // Purpose: Get sections for the selected course
        var secResult = catalogService.getSectionsByCourseCode(courseCode);
        if (secResult.isFail()) {
            JOptionPane.showMessageDialog(this, "Failed: " + secResult.getError());
            return;
        }
        List<Section> sections = secResult.get();
        if (sections.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sections available for " + courseCode);
            return;
        }

        Section chosen;
        if (sections.size() == 1) {
            chosen = sections.get(0);
        } else {
            // Purpose: Show detailed section information including term and meeting times
            String[] displayOptions = new String[sections.size()];
            for (int i = 0; i < sections.size(); i++) {
                Section s = sections.get(i);
                StringBuilder meetingInfo = new StringBuilder();
                for (TimeSlot ts : s.getMeetingTimes()) {
                    if (meetingInfo.length() > 0) meetingInfo.append(", ");
                    meetingInfo.append(ts.getDayOfWeek()).append(" ")
                              .append(ts.getStartTime()).append("-").append(ts.getEndTime());
                }
                int enrolled = 0;
                for (Enrollment e : s.getRoster()) {
                    if (e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED) enrolled++;
                }
                displayOptions[i] = String.format("%s [%s] - %s (%d/%d enrolled)", 
                    s.getId(), s.getTerm(), 
                    meetingInfo.length() > 0 ? meetingInfo.toString() : "TBA",
                    enrolled, s.getCapacity());
            }
            
            String selectedDisplay = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose section to enroll:",
                    "Select Section",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    displayOptions,
                    displayOptions[0]
            );
            if (selectedDisplay == null) return;
            
            // Purpose: Extract section ID from display string
            String selectedId = selectedDisplay.substring(0, selectedDisplay.indexOf(" ["));
            chosen = sections.stream().filter(s -> s.getId().equals(selectedId)).findFirst().orElse(null);
        }

        if (chosen == null) {
            JOptionPane.showMessageDialog(this, "Section selection failed.");
            return;
        }

        Result<Enrollment> res = registrationService.enrollStudentInSection(currentUserId, chosen.getId());
        if (res.isOk()) {
            String statusMsg = res.get().getStatus() == Enrollment.EnrollmentStatus.WAITLISTED ?
                "You have been added to the waitlist!" :
                "Successfully enrolled!";
            JOptionPane.showMessageDialog(this, statusMsg + "\nStatus: " + res.get().getStatus());
            refreshStudentData();
        } else {
            JOptionPane.showMessageDialog(this, "Enrollment Failed:\n" + res.getError());
        }
    }
}
