package HallBite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AdminShow extends javax.swing.JFrame {

    private String adminUsername;    // optional: which admin is logged in

    // Constructor used from Login (witih username)
    public AdminShow(String username) {
        this.adminUsername = username;
        initComponents();
        setLocationRelativeTo(null);

        // optional: show admin name in title
        if (adminUsername != null && !adminUsername.isEmpty()) {
            setTitle("Admin - Room Requests (" + adminUsername + ")"); // Title changed
        } else {
            setTitle("Admin - Room Requests"); // Title changed
        }

        loadAllRoomRequests(); // show room requests immediately
    }

    // Default constructor (for testing / NetBeans preview)
    public AdminShow() {
        this(null);
    }
    // --------- NEW METHOD: COMPREHENSIVE APPROVAL TRANSACTION ---------
    private String approveRoomRequest(int request_id, String username, String room_number) {
        // 1. Check if room is full
        String checkRoomSql = "SELECT current_occupancy, capacity FROM rooms WHERE room_number = ?";
        // 2. Assign room to student
        String updateStudentSql = "UPDATE students SET room_no = ? WHERE username = ?";
        // 3. Increase room occupancy
        String updateRoomSql = "UPDATE rooms SET current_occupancy = current_occupancy + 1 WHERE room_number = ?";
        // 4. Delete the request
        String deleteRequestSql = "DELETE FROM roomRequest WHERE request_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // Step 1: Check Room Capacity
            try (PreparedStatement checkStmt = conn.prepareStatement(checkRoomSql)) {
                checkStmt.setString(1, room_number);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("current_occupancy") >= rs.getInt("capacity")) {
                        conn.rollback();
                        return "ROOM_FULL";
                    }
                } else {
                    conn.rollback();
                    return "ROOM_NOT_FOUND";
                }
            }

            // Step 2: Update Student's room_no
            try (PreparedStatement studentStmt = conn.prepareStatement(updateStudentSql)) {
                studentStmt.setString(1, room_number);
                studentStmt.setString(2, username);
                if (studentStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return "STUDENT_NOT_FOUND";
                }
            }

            // Step 3: Increase Room Occupancy (Guaranteed to succeed after step 1)
            try (PreparedStatement roomStmt = conn.prepareStatement(updateRoomSql)) {
                roomStmt.setString(1, room_number);
                roomStmt.executeUpdate();
            }

            // Step 4: Delete the Request
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRequestSql)) {
                deleteStmt.setInt(1, request_id);
                deleteStmt.executeUpdate();
            }

            conn.commit(); // Commit Transaction
            return "SUCCESS";

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (Exception rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("Database Error during approval: " + e.getMessage());
            return "DB_ERROR: " + e.getMessage();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception closeEx) {
                System.err.println("DB connection close failed: " + closeEx.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable(); // Name kept for component reuse
        jTextField1 = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jButtonShowAll = new javax.swing.JButton();
        // --- NEW COMPONENT ---
        jButtonApprove = new javax.swing.JButton();
        // ---------------------

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        studentTable.setBackground(new java.awt.Color(204, 204, 204));
        studentTable.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        studentTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                }
        ));
        studentTable.setToolTipText("Room Requests"); // Tooltip changed
        studentTable.setName("requestTable"); // Name updated logically
        jScrollPane1.setViewportView(studentTable);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButtonSearch.setFont(new java.awt.Font("Helvetica", 1, 12)); // NOI18N
        jButtonSearch.setText("Search");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        jButtonShowAll.setFont(new java.awt.Font("Lato", 0, 12)); // NOI18N
        jButtonShowAll.setText("Show All");
        jButtonShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowAllActionPerformed(evt);
            }
        });

        // --- BUTTON SETUP: Text changed for clarity ---
        jButtonApprove.setFont(new java.awt.Font("Helvetica", 1, 12)); // NOI18N
        jButtonApprove.setText("Approve Request");
        jButtonApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApproveActionPerformed(evt);
            }
        });
        // --------------------------

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(34, 34, 34)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButtonSearch)
                                                // --- UPDATED LAYOUT ---
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 350, Short.MAX_VALUE)
                                                .addComponent(jButtonApprove) // Approve button added
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButtonShowAll))
                                        // ------------------------
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap(34, Short.MAX_VALUE)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1033, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButtonSearch)
                                        .addComponent(jButtonShowAll)
                                        .addComponent(jButtonApprove)) // Approve button added
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(293, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // Optional: trigger search on Enter
        // jButtonSearchActionPerformed(evt);
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        String keyword = jTextField1.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter name or reg. no to search!");
            return;
        }
        searchRoomRequests(keyword); // Calls the new search method
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowAllActionPerformed
        loadAllRoomRequests(); // Calls the new load method
    }//GEN-LAST:event_jButtonShowAllActionPerformed

    // --------- UPDATED ACTION: APPROVE BUTTON ---------
    // --------- UPDATED ACTION: APPROVE BUTTON ---------
    private void jButtonApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApproveActionPerformed
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request row to approve.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // CRITICAL ASSUMPTIONS:
            // Index 0: request_id
            // Index 5: room_number
            // Index 2: username (This is critical to update the student record!)

            // Use column index matching your roomRequest schema:
            // 0: request_id, 1: name, 2: username, 3: reg_no, 4: department, 5: room_number...

            int request_id = Integer.parseInt(studentTable.getModel().getValueAt(selectedRow, 0).toString());
            String username = studentTable.getModel().getValueAt(selectedRow, 2).toString();
            String room_number = studentTable.getModel().getValueAt(selectedRow, 5).toString();

            String status = approveRoomRequest(request_id, username, room_number);

            switch (status) {
                case "SUCCESS":
                    JOptionPane.showMessageDialog(this, "Request Approved! Room " + room_number + " assigned to " + username + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllRoomRequests(); // Refresh the table
                    break;
                case "ROOM_FULL":
                    JOptionPane.showMessageDialog(this, "Approval Failed! Room " + room_number + " is **already full**.", "Failure", JOptionPane.ERROR_MESSAGE);
                    break;
                case "ROOM_NOT_FOUND":
                    JOptionPane.showMessageDialog(this, "Approval Failed! Room " + room_number + " does not exist in the rooms table.", "Failure", JOptionPane.ERROR_MESSAGE);
                    break;
                case "STUDENT_NOT_FOUND":
                    JOptionPane.showMessageDialog(this, "Approval Failed! Student username " + username + " not found in the students table.", "Failure", JOptionPane.ERROR_MESSAGE);
                    break;
                default: // Catches DB_ERROR
                    JOptionPane.showMessageDialog(this, "An unexpected error occurred during approval: " + status, "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Error: Could not find required column data in the table (check column indices).", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing approval: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonApproveActionPerformed


    // --------- NEW METHOD: UPDATE ROOM OCCUPANCY (Changed room_id to room_number) ---------
    private boolean updateRoomOccupancy(String room_number) {
        // Assuming 'room_number' is the primary key or unique identifier in the 'rooms' table
        String sql = "UPDATE rooms SET current_occupancy = current_occupancy + 1 WHERE room_number = ? AND current_occupancy < capacity";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room_number);
            int rowsAffected = ps.executeUpdate();

            // If rowsAffected > 0, the occupancy was increased AND the capacity was not exceeded.
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Database Error updating room occupancy: " + e.getMessage());
            return false;
        }
    }
    // -----------------------------------------------------

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AdminShow.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new AdminShow().setVisible(true);
        });
    }

    // --------- LOAD ALL ROOM REQUESTS into table ---------
    private void loadAllRoomRequests() {
        try (Connection conn = DBConnection.getConnection()) {
            // Selecting all columns from roomRequest
            String sql = "SELECT * FROM roomRequest ORDER BY request_time DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                // Use column labels as headers
                model.addColumn(metaData.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            studentTable.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading room requests: " + e.getMessage());
        }
    }

    // --------- Search roomRequest by name, reg_no, or room_number ---------
    private void searchRoomRequests(String keyword) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM roomRequest WHERE name LIKE ? OR reg_no LIKE ? OR room_number LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            studentTable.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // --- NEW VARIABLE ADDED ---
    private javax.swing.JButton jButtonApprove;
    // --------------------------
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonShowAll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable studentTable;
    // End of variables declaration//GEN-END:variables
}