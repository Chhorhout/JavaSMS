import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import javax.swing.border.Border;
import java.awt.event.ActionListener;

public class menu {
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 800);
        frame.setLayout(new BorderLayout());
        
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.WHITE);
        backgroundPanel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setForeground(new Color(30, 136, 229));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add image
        ImageIcon imageIcon = new ImageIcon("asset/image.png"); // Load the image
        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Scale the image
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add image and spacing
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        String[] buttonLabels = {"Manage Students", "Manage Teachers", "Manage Course", "View Student List", "View Teacher List", "Assign Courses", "Log Out"};
        for (String label : buttonLabels) {
            JButton button = createStyledButton(label);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
            contentPanel.add(button);
        }

        backgroundPanel.add(titleLabel, BorderLayout.NORTH);
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        frame.add(backgroundPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(500, 60));
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        final Color defaultColor = new Color(240, 240, 240);
        final Color hoverColor = new Color(220, 220, 220);
        final Color textColor = new Color(30, 136, 229);
        
        button.setBackground(defaultColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Make buttons round
        button.setBorder(new RoundedBorder(20)); // Use custom RoundedBorder with radius 20

        // Set logout button to red
        if (text.equals("Log Out")) {
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
        }

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!text.equals("Log Out")) {
                    button.setBackground(hoverColor);
                } else {
                    button.setBackground(new Color(220, 0, 0)); // Darker red for hover
                }
            }
            public void mouseExited(MouseEvent evt) {
                if (!text.equals("Log Out")) {
                    button.setBackground(defaultColor);
                } else {
                    button.setBackground(Color.RED);
                }
            }
        });

        // Add click effect with navigation
        button.addActionListener(createActionListener(text));

        return button;
    }

    private static ActionListener createActionListener(String buttonText) {
        return e -> {
            switch (buttonText) {
                case "Manage Students":
                    SwingUtilities.invokeLater(() -> new cudstudent().setVisible(true));
                    break;
                case "Manage Teachers":
                    SwingUtilities.invokeLater(() -> new cudteacher().setVisible(true));
                    break;
                case "Manage Course":
                    SwingUtilities.invokeLater(() -> new cudcourse().setVisible(true));
                    break;
                case "View Student List":
                    SwingUtilities.invokeLater(() -> new StudentInformationPage().setVisible(true));
                    break;
                case "View Teacher List":
                    SwingUtilities.invokeLater(() -> new TeacherInformationPage().setVisible(true));
                    break;
                case "Assign Courses":
                    SwingUtilities.invokeLater(() -> new StudentCourseAssignment().setVisible(true));
                    break;
                case "Log Out":
                    // Implement logout functionality here
                    System.exit(0); // For now, just exit the application
                    break;
                default:
                    JOptionPane.showMessageDialog(null, buttonText + " functionality not implemented yet.");
            }
        };
    }

    // Custom rounded border class
    private static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(menu::createAndShowGUI);
    }
}
