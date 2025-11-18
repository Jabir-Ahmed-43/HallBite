package HallBite;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class DashboardDesign extends JFrame {
    private final String username;
    private final JPanel mainPanel;
    private final JLabel welcomeLabel;

    public DashboardDesign(String username) {
        this.username = username;

        setTitle("HallBite Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // =========================
        // 🔶 Top bar
        // =========================
        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(78, 59, 49));
        topBar.setPreferredSize(new Dimension(1000, 60));
        JLabel titleLabel = new JLabel("HALLBITE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(230, 230, 230));
        topBar.add(titleLabel);
        add(topBar, BorderLayout.NORTH);

        // =========================
        // 🔶 Sidebar
        // =========================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(236, 185, 146));
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JButton profileBtn = new JButton("PROFILE");
        JButton applicationBtn = new JButton("APPLICATION");
        JButton logoutBtn = new JButton("LOGOUT");

        Font btnFont = new Font("SansSerif", Font.BOLD, 15);
        profileBtn.setFont(btnFont);
        applicationBtn.setFont(btnFont);
        logoutBtn.setFont(btnFont);

        profileBtn.setBackground(new Color(230, 240, 255));
        applicationBtn.setBackground(new Color(230, 240, 255));
        logoutBtn.setBackground(new Color(255, 230, 230));
        logoutBtn.setForeground(Color.RED);

        gbc.gridy = 0;
        sidebar.add(profileBtn, gbc);
        gbc.gridy = 1;
        sidebar.add(applicationBtn, gbc);
        gbc.gridy = 2;
        sidebar.add(logoutBtn, gbc);
        add(sidebar, BorderLayout.WEST);

        // =========================
        // 🔶 Main card panel
        // =========================
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Dashboard card
        JPanel dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBackground(new Color(191, 211, 185));
        welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        dashboardPanel.add(welcomeLabel);
        mainPanel.add(dashboardPanel, "Dashboard");

        // Profile card
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(new Color(191, 211, 185));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JLabel nameLabel = new JLabel("Name: ...");
        JLabel deptLabel = new JLabel("Department: ...");
        JLabel sessionLabel = new JLabel("Session: ...");
        JLabel hallLabel = new JLabel("Hall: ...");
        JLabel regLabel = new JLabel("Reg. No: ...");

        Font infoFont = new Font("Segoe UI", Font.PLAIN, 18);
        for (JLabel lbl : new JLabel[]{nameLabel, deptLabel, sessionLabel, hallLabel, regLabel}) {
            lbl.setFont(infoFont);
            lbl.setForeground(new Color(40, 40, 40));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            profilePanel.add(lbl);
            profilePanel.add(Box.createVerticalStrut(15));
        }
        mainPanel.add(profilePanel, "Profile");

        // Application card
        JPanel applicationPanel = new JPanel(new BorderLayout());
        applicationPanel.setBackground(new Color(191, 211, 185));
        JLabel chooseLabel = new JLabel("Choose the Floor", SwingConstants.CENTER);
        chooseLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        chooseLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        applicationPanel.add(chooseLabel, BorderLayout.NORTH);

        JPanel floorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 40));
        floorPanel.setOpaque(false);
        String[] floors = {"1st", "2nd", "3rd", "4th", "5th"};
        Font floorFont = new Font("Segoe UI", Font.BOLD, 18);

        for (String f : floors) {
            JButton floorBtn = new JButton(f + " Floor");
            floorBtn.setFont(floorFont);
            floorBtn.setBackground(new Color(230, 240, 255));
            floorBtn.setFocusPainted(false);
            floorBtn.setPreferredSize(new Dimension(120, 60));
            floorPanel.add(floorBtn);

            floorBtn.addActionListener(e -> {
                dispose();
                if (f.equals("1st")) {
                    SwingUtilities.invokeLater(() -> new HallRoomLayout1(username, f).setVisible(true));
                } else if (f.equals("2nd")) {
                    SwingUtilities.invokeLater(() -> new HallRoomLayout2(username, f).setVisible(true));
                } else if (f.equals("3rd")) {
                    SwingUtilities.invokeLater(() -> new HallRoomLayout3(username, f).setVisible(true));
                } else if (f.equals("4th")) {
                    SwingUtilities.invokeLater(() -> new HallRoomLayout4(username, f).setVisible(true));
                } else if (f.equals("5th")) {
                    SwingUtilities.invokeLater(() -> new HallRoomLayout5(username, f).setVisible(true));
                }
            });
        }
        applicationPanel.add(floorPanel, BorderLayout.CENTER);
        mainPanel.add(applicationPanel, "Application");

        // =========================
        // 🔶 Button actions
        // =========================
        profileBtn.addActionListener(e -> {
            switchPanel("Profile");
            loadStudentInfo(nameLabel, deptLabel, sessionLabel, hallLabel, regLabel);
        });

        applicationBtn.addActionListener(e -> switchPanel("Application"));

        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                // Optionally go back to Login:
                // new Login().setVisible(true);
            }
        });

        // Load welcome text from DB
        loadWelcomeName();
    }

    private void switchPanel(String panelName) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, panelName);
    }

    private void loadWelcomeName() {
        welcomeLabel.setText("Welcome, loading...");
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String studentName = null;
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/hallbite", "root", "0000");
                     PreparedStatement pst = conn.prepareStatement(
                             "SELECT name FROM students WHERE username = ?")) {
                    pst.setString(1, username);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            studentName = rs.getString("name");
                        }
                    }
                }
                return studentName;
            }

            @Override
            protected void done() {
                try {
                    String studentName = get();
                    welcomeLabel.setText("Welcome, " +
                            (studentName != null ? studentName : username) + "!");
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(DashboardDesign.this,
                            "Error: " + e.getMessage());
                    welcomeLabel.setText("Welcome, " + username + "!");
                }
            }
        };
        worker.execute();
    }

    private void loadStudentInfo(JLabel name, JLabel dept,
                                 JLabel session, JLabel hall, JLabel reg) {
        SwingWorker<StudentData, Void> worker = new SwingWorker<StudentData, Void>() {
            @Override
            protected StudentData doInBackground() throws Exception {
                StudentData data = null;
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/hallbite", "root", "0000");
                     PreparedStatement pst = conn.prepareStatement(
                             "SELECT name, department, session, hall, reg_no " +
                                     "FROM students WHERE username = ?")) {
                    pst.setString(1, username);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            data = new StudentData(
                                    rs.getString("name"),
                                    rs.getString("department"),
                                    rs.getString("session"),
                                    rs.getString("hall"),
                                    rs.getString("reg_no"));
                        }
                    }
                }
                return data;
            }

            @Override
            protected void done() {
                try {
                    StudentData data = get();
                    if (data != null) {
                        name.setText("Name: " + data.name);
                        dept.setText("Department: " + data.department);
                        session.setText("Session: " + data.session);
                        hall.setText("Hall: " + data.hall);
                        reg.setText("Reg. No: " + data.regNo);
                    } else {
                        JOptionPane.showMessageDialog(DashboardDesign.this,
                                "No student data found for " + username);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(DashboardDesign.this,
                            "Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private static class StudentData {
        final String name, department, session, hall, regNo;

        StudentData(String name, String department,
                    String session, String hall, String regNo) {
            this.name = name;
            this.department = department;
            this.session = session;
            this.hall = hall;
            this.regNo = regNo;
        }
    }

    // For testing only; real app should start from Login
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new DashboardDesign("testUser").setVisible(true));
    }
}
