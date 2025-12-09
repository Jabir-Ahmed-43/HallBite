package HallBite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HallRoomLayout1 extends JFrame {
    private final String username;
    private final int floor;          // numeric (1,2,3...)
    private final String floorLabel;  // "1st", "2nd", etc.

    // room_number (String) -> isFull (true if current_occupancy >= 4)
    private final Map<String, Boolean> roomFullMap = new HashMap<>();
    // room_number (String) -> JButton
    private final Map<String, JButton> roomButtons = new HashMap<>();

    public HallRoomLayout1(String username, String floorLabel) {
        this.username = username;
        this.floorLabel = floorLabel;
        this.floor = parseFloorFromLabel(floorLabel);

        // load room status BEFORE buttons (for red on load)
        loadRoomStatus();

        setTitle("HallBite - " + floorLabel + " Floor Room Layout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(180, 200, 180));

        // ---------------- Title ----------------
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(90, 70, 60));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel mainTitleLabel = new JLabel("HallBite");
        mainTitleLabel.setFont(new Font("Lato", Font.BOLD, 48));
        mainTitleLabel.setForeground(Color.WHITE);
        mainTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Bijoy-24 Hall");
        subTitleLabel.setFont(new Font("Lato", Font.PLAIN, 24));
        subTitleLabel.setForeground(new Color(220, 220, 220));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(mainTitleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subTitleLabel);

        // ---------------- Top bar ----------------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JButton backButton = createBackButton("<-- Back");
        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardDesign(username).setVisible(true));
        });
        topPanel.add(backButton, BorderLayout.WEST);

        JButton refreshButton = new JButton("Refresh rooms");
        refreshButton.addActionListener(e -> reloadAllRooms());
        topPanel.add(refreshButton, BorderLayout.EAST);

        // ---------------- Room grid ----------------
        JPanel roomPanel = new JPanel(new GridBagLayout());
        roomPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // For this floor, show e.g. 101–109, 201–209, etc.
        String prefix = String.valueOf(floor); // "1","2","3"...
        String[] roomNumbers = {
                prefix + "01", prefix + "02", prefix + "03",
                prefix + "04", prefix + "05", prefix + "06",
                prefix + "07", prefix + "08", prefix + "09"
        };

        // Top row (middle 5 rooms: x03-x07)
        gbc.gridy = 0;
        for (int i = 2; i < 7; i++) {
            gbc.gridx = i - 2;
            roomPanel.add(createFlatRoomButton(roomNumbers[i]), gbc);
        }

        // Center text
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 3;

        JPanel centerTextPanel = new JPanel();
        centerTextPanel.setOpaque(false);
        centerTextPanel.setLayout(new BoxLayout(centerTextPanel, BoxLayout.Y_AXIS));

        JLabel floorTextLabel = new JLabel(this.floorLabel + " Floor");
        floorTextLabel.setFont(new Font("Stencil", Font.BOLD, 38));
        floorTextLabel.setForeground(new Color(60, 60, 60));
        floorTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel selectLabel = new JLabel("Select a room");
        selectLabel.setFont(new Font("Lato", Font.BOLD, 24));
        selectLabel.setForeground(new Color(80, 80, 80));
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerTextPanel.add(floorTextLabel);
        centerTextPanel.add(Box.createVerticalStrut(5));
        centerTextPanel.add(selectLabel);

        roomPanel.add(centerTextPanel, gbc);

        gbc.gridwidth = 1;

        // Middle row side rooms: x02 and x08
        gbc.gridy = 1;
        gbc.gridx = 0;
        roomPanel.add(createFlatRoomButton(roomNumbers[1]), gbc);
        gbc.gridx = 4;
        roomPanel.add(createFlatRoomButton(roomNumbers[7]), gbc);

        // Bottom row corner rooms: x01 and x09
        gbc.gridy = 2;
        gbc.gridx = 0;
        roomPanel.add(createFlatRoomButton(roomNumbers[0]), gbc);
        gbc.gridx = 4;
        roomPanel.add(createFlatRoomButton(roomNumbers[8]), gbc);

        mainContainer.add(titlePanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(topPanel, BorderLayout.NORTH);
        centerWrapper.add(roomPanel, BorderLayout.CENTER);

        mainContainer.add(centerWrapper, BorderLayout.CENTER);

        add(mainContainer);

        // Optional: resync when window opens
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                reloadAllRooms();
            }
        });
    }

    // "1st" -> 1, "2nd" -> 2, etc.
    private int parseFloorFromLabel(String floorLabel) {
        if (floorLabel == null) return 1;
        String digits = floorLabel.replaceAll("\\D+", "");
        if (digits.isEmpty()) return 1;
        return Integer.parseInt(digits);
    }

    // --------- Load room occupancy for this floor ---------
    private void loadRoomStatus() {
        String sql = "SELECT room_number, current_occupancy FROM rooms WHERE floor = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hallbite", "root", "0000");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, floor);

            try (ResultSet rs = ps.executeQuery()) {
                roomFullMap.clear();
                while (rs.next()) {
                    String roomNo = rs.getString("room_number");
                    int occ = rs.getInt("current_occupancy");

                    // Room is FULL when current_occupancy >= 4
                    boolean isFull = occ >= 4;

                    roomFullMap.put(roomNo, isFull);

                    // Debug output (optional, remove in production)
                    System.out.println("Room: " + roomNo + ", Occupancy: " + occ + ", Is Full: " + isFull);
                }
                System.out.println("Total rooms loaded for floor " + floor + ": " + roomFullMap.size());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading room status: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --------- Reload all buttons from DB status ---------
    private void reloadAllRooms() {
        loadRoomStatus();

        for (Map.Entry<String, JButton> entry : roomButtons.entrySet()) {
            String roomNo = entry.getKey();
            JButton btn = entry.getValue();
            boolean full = roomFullMap.getOrDefault(roomNo, false);

            if (full) {
                btn.setBackground(new Color(220, 60, 60));   // red
                btn.setEnabled(false);
                btn.setToolTipText("Room " + roomNo + " is full (4/4 occupied)");
            } else {
                btn.setBackground(new Color(230, 180, 140)); // normal
                btn.setEnabled(true);
                btn.setToolTipText(null);
            }
            btn.repaint();
        }
    }

    // --------- Create button: decide color BEFORE click ---------
    private JButton createFlatRoomButton(String roomNumber) {
        JButton button = new JButton(roomNumber);
        roomButtons.put(roomNumber, button);

        button.setPreferredSize(new Dimension(120, 120));
        button.setForeground(new Color(40, 40, 40));
        button.setFont(new Font("Stencil", Font.BOLD, 24));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 120, 100), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        // Check if room is full (occupancy >= 4)
        boolean isFull = roomFullMap.getOrDefault(roomNumber, false);

        if (isFull) {
            // Full from the start: red and disabled
            button.setBackground(new Color(220, 60, 60));
            button.setEnabled(false);
            button.setToolTipText("Room " + roomNumber + " is full (4/4 occupied)");
        } else {
            // Available
            button.setBackground(new Color(230, 180, 140));

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(220, 170, 130));
                }

                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(230, 180, 140));
                }
            });

            button.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(
                        HallRoomLayout1.this,
                        "Are you sure you want to request room " + roomNumber + "?",
                        "Confirm Room Request",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    // only store request, no occupancy increment
                    createRoomRequest(roomNumber);
                }
            });
        }

        return button;
    }

    // --------- Insert into roomRequest: username, reg_no, dept, room ---------
    // --------- Insert into roomRequest: username, reg_no, dept, room ---------
    private void createRoomRequest(String roomNumber) {
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Get student data including name
                String selectStudentSql = "SELECT name, reg_no, department FROM students WHERE username = ?";

                String checkRoomSql = "SELECT current_occupancy FROM rooms WHERE room_number = ?";

                String insertRequestSql = "INSERT INTO roomRequest (name, username, reg_no, department, hall_name, room_number, floor) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/hallbite", "root", "0000")) {

                    String name = "";
                    String regNo = "";
                    String dept = "";
                    String hallName = "Bijoy-24 Hall";

                    // First, check if room is still available
                    int currentOccupancy = 0;
                    try (PreparedStatement ps = conn.prepareStatement(checkRoomSql)) {
                        ps.setString(1, roomNumber);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                currentOccupancy = rs.getInt("current_occupancy");
                                if (currentOccupancy >= 4) {
                                    return "ROOM_FULL";
                                }
                            } else {
                                return "ROOM_NOT_FOUND";
                            }
                        }
                    }

                    // Get student data - INCLUDING NAME
                    try (PreparedStatement ps = conn.prepareStatement(selectStudentSql)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                name = rs.getString("name");        // Get the student's actual name
                                regNo = rs.getString("reg_no");
                                dept = rs.getString("department");

                                // Debug: check if data is retrieved
                                System.out.println("Retrieved student data for " + username +
                                        ": Name=" + name +
                                        ", RegNo=" + regNo +
                                        ", Dept=" + dept);
                            } else {
                                return "NO_STUDENT";
                            }
                        }
                    }

                    // Check if student already has a request (without status check since table doesn't have status column)
                    String checkExistingRequestSql = "SELECT COUNT(*) FROM roomRequest WHERE username = ?";
                    try (PreparedStatement ps = conn.prepareStatement(checkExistingRequestSql)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                return "EXISTING_REQUEST";
                            }
                        }
                    }

                    // Insert request with all required fields
                    try (PreparedStatement ps = conn.prepareStatement(insertRequestSql)) {
                        ps.setString(1, name);           // Student's name
                        ps.setString(2, username);       // Username
                        ps.setString(3, regNo);          // Registration number
                        ps.setString(4, dept);           // Department
                        ps.setString(5, hallName);       // Hall name
                        ps.setString(6, roomNumber);     // Room number
                        ps.setInt(7, floor);             // Floor

                        int rowsAffected = ps.executeUpdate();
                        System.out.println("Inserted request for " + name + " in room " + roomNumber);
                    }

                    return "SUCCESS";

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }

            @Override
            protected void done() {
                try {
                    String status = get();
                    switch (status) {
                        case "SUCCESS":
                            JOptionPane.showMessageDialog(HallRoomLayout1.this,
                                    "Room request submitted for room " + roomNumber + ".\n" +
                                            "Your request will be reviewed by the hall administration.",
                                    "Request Submitted",
                                    JOptionPane.INFORMATION_MESSAGE);
                            reloadAllRooms();
                            break;

                        case "ROOM_FULL":
                            JOptionPane.showMessageDialog(HallRoomLayout1.this,
                                    "Room " + roomNumber + " is now full. Please select another room.",
                                    "Room Unavailable",
                                    JOptionPane.WARNING_MESSAGE);
                            reloadAllRooms();
                            break;

                        case "ROOM_NOT_FOUND":
                            JOptionPane.showMessageDialog(HallRoomLayout1.this,
                                    "Room " + roomNumber + " not found in the database.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            break;

                        case "NO_STUDENT":
                            JOptionPane.showMessageDialog(HallRoomLayout1.this,
                                    "Student data not found for username: " + username +
                                            "\nPlease complete your profile in the dashboard first.",
                                    "Profile Incomplete",
                                    JOptionPane.ERROR_MESSAGE);
                            break;

                        case "EXISTING_REQUEST":
                            JOptionPane.showMessageDialog(HallRoomLayout1.this,
                                    "You already have a pending room request.\n" +
                                            "Please wait for your current request to be processed.",
                                    "Existing Request Found",
                                    JOptionPane.WARNING_MESSAGE);
                            break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    String errorMessage = "A database error occurred.";
                    if (e.getCause() != null) {
                        errorMessage = e.getCause().getMessage();
                    }
                    JOptionPane.showMessageDialog(HallRoomLayout1.this,
                            errorMessage,
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JButton createBackButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(200, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Stencil", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(170, 40, 40));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(200, 50, 50));
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new HallRoomLayout1("username", "1st").setVisible(true));
    }
}