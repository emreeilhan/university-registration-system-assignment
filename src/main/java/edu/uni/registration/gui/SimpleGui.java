package edu.uni.registration.gui;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Section;
import edu.uni.registration.service.CatalogService;
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
    
    private String currentUserId;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public SimpleGui(RegistrationService registrationService, CatalogService catalogService) {
        this.registrationService = registrationService;
        this.catalogService = catalogService;
        
        setTitle("University Registration System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add screens (Cards)
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createStudentDashboard(), "STUDENT");

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
        
        JButton loginBtn = new JButton("Login as Student");
        
        loginBtn.addActionListener(e -> {
            String userId = userField.getText().trim();
            if (!userId.isEmpty()) {
                this.currentUserId = userId;
                // In real app verify user exists. Here we trust Input for demo.
                refreshStudentData();
                cardLayout.show(mainPanel, "STUDENT");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an ID");
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        return panel;
    }

    // --- Student Dashboard Components ---
    private JTable scheduleTable;
    private DefaultTableModel scheduleModel;
    
    private JTable searchTable;
    private DefaultTableModel searchModel;

    private JPanel createStudentDashboard() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // TAB 1: My Schedule
        JPanel schedulePanel = new JPanel(new BorderLayout());
        
        String[] scheduleCols = {"Section ID", "Course", "Term", "Instructor", "Status"};
        scheduleModel = new DefaultTableModel(scheduleCols, 0);
        scheduleTable = new JTable(scheduleModel);
        
        JButton refreshBtn = new JButton("Refresh Schedule");
        refreshBtn.addActionListener(e -> refreshStudentData());
        
        JButton dropBtn = new JButton("Drop Selected Section");
        dropBtn.addActionListener(e -> dropSelectedSection());

        JPanel btnPanel = new JPanel();
        btnPanel.add(refreshBtn);
        btnPanel.add(dropBtn);
        
        schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        schedulePanel.add(btnPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("My Schedule", schedulePanel);

        // TAB 2: Search & Enroll
        JPanel searchPanel = new JPanel(new BorderLayout());
        
        JPanel topSearch = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search Course Title");
        JButton enrollBtn = new JButton("Enroll in Selected");
        
        topSearch.add(new JLabel("Title:"));
        topSearch.add(searchField);
        topSearch.add(searchBtn);
        
        String[] searchCols = {"Course Code", "Title", "Credits", "Prereqs"};
        searchModel = new DefaultTableModel(searchCols, 0);
        searchTable = new JTable(searchModel);
        
        searchBtn.addActionListener(e -> {
            String query = searchField.getText();
            CourseQuery q = new CourseQuery();
            q.setTitle(query);
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
        
        // Logout Button at top
        JPanel container = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            currentUserId = null;
            cardLayout.show(mainPanel, "LOGIN");
        });
        
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(new JLabel("Student Dashboard"));
        topBar.add(logoutBtn);
        
        container.add(topBar, BorderLayout.NORTH);
        container.add(tabbedPane, BorderLayout.CENTER);

        return container;
    }

    private void refreshStudentData() {
        if (currentUserId == null) return;
        
        scheduleModel.setRowCount(0);
        Result<List<Section>> res = registrationService.getCurrentSchedule(currentUserId, null);
        
        if (res.isOk()) {
            for (Section s : res.get()) {
                // We need to find Enrollment status for this student in this section
                String status = "ENROLLED"; // Default
                for(Enrollment e : s.getRoster()) {
                     if(e.getStudent().getId().equals(currentUserId)) {
                         status = e.getStatus().toString();
                         break;
                     }
                }
                
                scheduleModel.addRow(new Object[]{
                    s.getId(),
                    s.getCourse().getCode(),
                    s.getTerm(),
                    s.getInstructor() != null ? s.getInstructor().getFullName() : "TBA",
                    status
                });
            }
        }
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
        int row = searchTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course from the list.");
            return;
        }
        
    
        
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

