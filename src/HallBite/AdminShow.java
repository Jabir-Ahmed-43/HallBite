package HallBite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AdminShow extends javax.swing.JFrame {

    private String adminUsername;   // optional: which admin is logged in

    // Constructor used from Login (with username)
    public AdminShow(String username) {
        this.adminUsername = username;
        initComponents();
        setLocationRelativeTo(null);

        // optional: show admin name in title
        if (adminUsername != null && !adminUsername.isEmpty()) {
            setTitle("Admin - Students (" + adminUsername + ")");
        } else {
            setTitle("Admin - Students");
        }

        loadAllStudents(); // show database immediately
    }

    // Default constructor (for testing / NetBeans preview)
    public AdminShow() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jButtonShowAll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        studentTable.setBackground(new java.awt.Color(204, 204, 204));
        studentTable.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        studentTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                }
        ));
        studentTable.setToolTipText("");
        studentTable.setName("studentTable"); // NOI18N
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
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 492, Short.MAX_VALUE)
                                                .addComponent(jButtonShowAll))
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
                                        .addComponent(jButtonShowAll))
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
        searchStudents(keyword);
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowAllActionPerformed
        loadAllStudents();
    }//GEN-LAST:event_jButtonShowAllActionPerformed

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

    // --------- Load all students into table ---------
    private void loadAllStudents() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM students";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
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
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    // --------- Search by name or reg_no ---------
    private void searchStudents(String keyword) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE name LIKE ? OR reg_no LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
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
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonShowAll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable studentTable;
    // End of variables declaration//GEN-END:variables
}
