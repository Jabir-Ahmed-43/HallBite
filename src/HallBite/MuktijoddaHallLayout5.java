package HallBite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MuktijoddaHallLayout5 extends JFrame {

    public MuktijoddaHallLayout5() {
        setTitle("HallBite - 5th Floor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

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

        JLabel subTitleLabel = new JLabel("Muktijodda Hall");
        subTitleLabel.setFont(new Font("Lato", Font.PLAIN, 24));
        subTitleLabel.setForeground(new Color(220, 220, 220));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(mainTitleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subTitleLabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(180, 200, 180));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JButton backButton = createBackButton("<-- Back");
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> dispose());

        topPanel.add(backButton, BorderLayout.WEST);

        JPanel roomPanel = new JPanel();
        roomPanel.setBackground(new Color(180, 200, 180));
        roomPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE;

        // Room numbers for 5th Floor (501-509)
        int[] roomNumbers = {501, 502, 503, 504, 505, 506, 507, 508, 509};

        gbc.gridy = 0;
        for (int i = 2; i < 7; i++) {
            gbc.gridx = i - 2;
            roomPanel.add(createFlatRoomButton(roomNumbers[i]), gbc);
        }

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 3;

        JPanel centerTextPanel = new JPanel();
        centerTextPanel.setBackground(new Color(180, 200, 180));
        centerTextPanel.setLayout(new BoxLayout(centerTextPanel, BoxLayout.Y_AXIS));

        JLabel floorLabel = new JLabel("5th Floor");
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
        mainContainer.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(new Color(180, 200, 180));
        centerWrapper.add(topPanel, BorderLayout.NORTH);
        centerWrapper.add(roomPanel, BorderLayout.CENTER);

        mainContainer.add(centerWrapper, BorderLayout.CENTER);
        add(mainContainer);
        setVisible(true);
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
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 170, 130));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(230, 180, 140));
            }
        });

        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Room " + roomNumber + " selected");
        });

        return button;
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
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(170, 40, 40));
            }

//            @@@
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(200, 50, 50));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MuktijoddaHallLayout5());
    }
}
