import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Student Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(100, 181, 246), w, h, new Color(30, 136, 229));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel("Login for Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField emailField = createStylishTextField("Email");
        JPasswordField passwordField = createStylishPasswordField("Password");

        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(255, 255, 255, 220));
        loginButton.setForeground(new Color(30, 136, 229));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setMaximumSize(new Dimension(300, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(new Color(255, 255, 255));
            }
            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(new Color(255, 255, 255, 220));
            }
        });

        // Add action listener to login button
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (validateLogin(email, password)) {
                frame.dispose(); // Close the login window
                menu.createAndShowGUI(); // Open the menu window
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(loginButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JTextField createStylishTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(200, 200, 200));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                    g2.dispose();
                }
            }
        };
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setMaximumSize(new Dimension(300, 40));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setForeground(Color.BLACK);
        textField.setBackground(new Color(255, 255, 255, 220));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }

    private static JPasswordField createStylishPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(200, 200, 200));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                    g2.dispose();
                }
            }
        };
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(new Color(255, 255, 255, 220));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return passwordField;
    }

    private static boolean validateLogin(String email, String password) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM admins WHERE email = ? AND password = ?")) {
                pstmt.setString(1, email);
                pstmt.setString(2, password); // This should be a hashed password in production
                
                ResultSet rs = pstmt.executeQuery();
                return rs.next();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
}
