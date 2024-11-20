import javax.swing.*;
import java.awt.*;

public class home {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Create the main frame with a larger size for desktop view
        JFrame frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout());

        // Create a panel for the content
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245)); // Light gray background for a clean look

        // Load and add the logo image
        ImageIcon logoIcon = new ImageIcon("asset/MS_Logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add a welcome message with a larger, modern font
        JLabel welcomeLabel = new JLabel("Welcome to the Student Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(60, 63, 65)); // Dark gray for contrast
        welcomeLabel.setOpaque(true);
        welcomeLabel.setBackground(new Color(220, 220, 220)); // Subtle background for emphasis
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton getStartButton = new JButton("Get Started");
        getStartButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        getStartButton.setBackground(Color.BLUE); // Set solid blue background
        getStartButton.setOpaque(true); // Ensure background color is visible
        getStartButton.setBorderPainted(false); // Remove default border for a cleaner look
        getStartButton.setForeground(Color.WHITE); // White text for contrast
        getStartButton.setFocusPainted(false); // Remove focus border
        getStartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Update the action listener
        getStartButton.addActionListener(e -> {
            frame.dispose(); // Close the current frame
            login.createAndShowGUI(); // Open the login page
        });

        // Add components to the panel with spacing
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Top spacing
        panel.add(logoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Space between logo and welcome label
        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Space between welcome label and button
        panel.add(getStartButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Bottom spacing

        // Add panel to the frame
        frame.add(panel, BorderLayout.CENTER);

        // Center the frame on the screen and make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
