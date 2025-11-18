package HallBite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class HallRoomLayout2 extends JFrame {
    private final String username;

    public HallRoomLayout2(String username, String floor) {
        this.username = username;

        setTitle("HallBite - " + floor + " Floor Room Layout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // --- UI Code ---
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(180, 200, 180));
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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JButton backButton = createBackButton("<-- Back");
        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardDesign(username).setVisible(true));
        });
        topPanel.add(backButton, BorderLayout.WEST);
        JPanel roomPanel = new JPanel(new GridBagLayout());
        roomPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // ✅ --- Room numbers updated for the 2nd floor ---
        int[] roomNumbers = {201, 202, 203, 204, 205, 206, 207, 208, 209};

        gbc.gridy = 0;
        for (int i = 2; i < 7; i++) {
            gbc.gridx = i - 2;
            roomPanel.add(createFlatRoomButton(roomNumbers[i]), gbc);
        }
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JPanel centerTextPanel = new JPanel();
        centerTextPanel.setOpaque(false);
        centerTextPanel.setLayout(new BoxLayout(centerTextPanel, BoxLayout.Y_AXIS));
        JLabel floorLabel = new JLabel(floor + " Floor"); // Dynamic floor label
        floorLabel.setFont(new Font("Stencil", Font.BOLD, 38));
        floorLabel.setForeground(new Color(60, 60, 60));
        floorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel selectLabel = new JLabel("Select a room");
        selectLabel.setFont(new Font("Lato", Font.BOLD, 24));
        selectLabel.setForeground(new Color(80, 80, 80));
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerTextPanel.add(floorLabel);
        centerTextPanel.add(Box.createVerticalStrut(5));
        centerTextPanel.add(selectLabel);
        roomPanel.add(centerTextPanel, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        roomPanel.add(createFlatRoomButton(roomNumbers[1]), gbc);
        gbc.gridx = 4;
        roomPanel.add(createFlatRoomButton(roomNumbers[7]), gbc);
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
    }

    private JButton createFlatRoomButton(int roomNumber) {
        JButton button = new JButton(String.valueOf(roomNumber));
        button.setPreferredSize(new Dimension(120, 120));
        button.setBackground(new Color(230, 180, 140));
        button.setForeground(new Color(40, 40, 40));
        button.setFont(new Font("Stencil", Font.BOLD, 24));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 120, 100), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                    this,
                    "Are you sure you want to select room " + roomNumber + "?",
                    "Confirm Selection",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                assignRoomToStudent(roomNumber);
            }
        });
        return button;
    }

    // ✅ --- Added database logic method ---
    private void assignRoomToStudent(int roomNumber) {
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String roomAsString = String.valueOf(roomNumber);
                String checkRoomSql = "SELECT current_occupancy, capacity FROM rooms WHERE room_number = ? FOR UPDATE";
                String updateStudentSql = "UPDATE students SET room_no = ? WHERE username = ?";
                String updateRoomSql = "UPDATE rooms SET current_occupancy = current_occupancy + 1 WHERE room_number = ?";

                Connection conn = null;
                try {
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hallbite", "root", "0000");
                    conn.setAutoCommit(false);

                    try (PreparedStatement checkStmt = conn.prepareStatement(checkRoomSql)) {
                        checkStmt.setString(1, roomAsString);
                        ResultSet rs = checkStmt.executeQuery();

                        if (rs.next()) {
                            int occupancy = rs.getInt("current_occupancy");
                            int capacity = rs.getInt("capacity");
                            if (occupancy >= capacity) {
                                conn.rollback();
                                return "ROOM_FULL";
                            }
                        } else {
                            conn.rollback();
                            return "ROOM_NOT_FOUND";
                        }
                    }

                    try (PreparedStatement updateStudentStmt = conn.prepareStatement(updateStudentSql)) {
                        updateStudentStmt.setString(1, roomAsString);
                        updateStudentStmt.setString(2, username);
                        updateStudentStmt.executeUpdate();
                    }

                    try (PreparedStatement updateRoomStmt = conn.prepareStatement(updateRoomSql)) {
                        updateRoomStmt.setString(1, roomAsString);
                        updateRoomStmt.executeUpdate();
                    }

                    conn.commit();
                    return "SUCCESS";

                } catch (Exception e) {
                    if (conn != null) conn.rollback();
                    throw e;
                } finally {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    String status = get();
                    switch (status) {
                        case "SUCCESS":
                            JOptionPane.showMessageDialog(HallRoomLayout2.this,
                                    "Successfully assigned to room " + roomNumber + "!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case "ROOM_FULL":
                            JOptionPane.showMessageDialog(HallRoomLayout2.this,
                                    "Could not assign room. Room " + roomNumber + " is full.", "Assignment Failed",
                                    JOptionPane.WARNING_MESSAGE);
                            break;
                        case "ROOM_NOT_FOUND":
                            JOptionPane.showMessageDialog(HallRoomLayout2.this,
                                    "Could not assign room. Room " + roomNumber + " does not exist in the system.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(HallRoomLayout2.this,
                            "A database error occurred: " + e.getCause().getMessage(), "Database Error",
                            JOptionPane.ERROR_MESSAGE);
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
        // Updated main for testing purposes
        SwingUtilities.invokeLater(() -> new HallRoomLayout2("username", "2nd").setVisible(true));
    }
}
