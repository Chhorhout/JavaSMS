import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentCourseAssignment extends JFrame {
    private JComboBox<ComboItem> studentComboBox;
    private JComboBox<ComboItem> courseComboBox;
    private JTextField scoreField;
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;
    private JButton assignButton, removeButton, updateScoreButton;

    public StudentCourseAssignment() {
        setTitle("Student Course Assignment");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 245));

        // Modify the input panel to have two sections
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(250, 250, 255));

        // Assignment Panel
        JPanel assignmentPanel = new JPanel(new GridBagLayout());
        assignmentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 115, 220), 2),
                "Assign Course",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(50, 115, 220)
        ));
        assignmentPanel.setBackground(new Color(250, 250, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Student and Course dropdowns in Assignment Panel
        addLabelAndComboBox(assignmentPanel, gbc, "Select Student:", studentComboBox = new JComboBox<>(), 0);
        addLabelAndComboBox(assignmentPanel, gbc, "Select Course:", courseComboBox = new JComboBox<>(), 1);

        // Assignment buttons
        JPanel assignButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        assignButtonPanel.setOpaque(false);
        assignButton = createStyledButton("Assign Course", new Color(50, 115, 220));
        removeButton = createStyledButton("Remove Assignment", new Color(220, 53, 69));
        assignButtonPanel.add(assignButton);
        assignButtonPanel.add(removeButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        assignmentPanel.add(assignButtonPanel, gbc);

        // Score Panel
        JPanel scorePanel = new JPanel(new GridBagLayout());
        scorePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 115, 220), 2),
                "Update Score",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(50, 115, 220)
        ));
        scorePanel.setBackground(new Color(250, 250, 255));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Score input in Score Panel
        JLabel scoreLabel = new JLabel("Score:");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreField = new JTextField(10);
        scoreField.setPreferredSize(new Dimension(100, 30));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        scorePanel.add(scoreLabel, gbc);
        
        gbc.gridx = 1;
        scorePanel.add(scoreField, gbc);

        updateScoreButton = createStyledButton("Update Score", new Color(40, 167, 69));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        scorePanel.add(updateScoreButton, gbc);

        // Add panels to main panel
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setOpaque(false);
        topPanel.add(assignmentPanel);
        topPanel.add(scorePanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Student ID", "Student Name", "Course ID", "Course Name", "Score", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Load data and setup listeners
        loadStudents();
        loadCourses();
        loadEnrollments();
        setupActionListeners();
    }

    private void setupActionListeners() {
        assignButton.addActionListener(e -> {
            ComboItem student = (ComboItem) studentComboBox.getSelectedItem();
            ComboItem course = (ComboItem) courseComboBox.getSelectedItem();
            
            if (student == null || course == null) {
                JOptionPane.showMessageDialog(this, "Please select both student and course");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)")) {
                
                pstmt.setString(1, student.getId());
                pstmt.setString(2, course.getId());
                pstmt.executeUpdate();
                
                loadEnrollments();
                clearFields();
                JOptionPane.showMessageDialog(this, "Course assigned successfully!");
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(this, "Student is already enrolled in this course!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error assigning course: " + ex.getMessage());
                }
            }
        });

        updateScoreButton.addActionListener(e -> {
            int selectedRow = enrollmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an enrollment to update score");
                return;
            }

            try {
                double score = Double.parseDouble(scoreField.getText());
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this, "Score must be between 0 and 100");
                    return;
                }

                String studentId = tableModel.getValueAt(selectedRow, 0).toString();
                String courseId = tableModel.getValueAt(selectedRow, 2).toString();
                String grade = calculateGrade(score);

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE enrollments SET score = ?, grade = ? WHERE student_id = ? AND course_id = ?")) {
                    
                    pstmt.setDouble(1, score);
                    pstmt.setString(2, grade);
                    pstmt.setString(3, studentId);
                    pstmt.setString(4, courseId);
                    pstmt.executeUpdate();
                    
                    loadEnrollments();
                    clearFields();
                    JOptionPane.showMessageDialog(this, "Score updated successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid score");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating score: " + ex.getMessage());
            }
        });

        removeButton.addActionListener(e -> {
            int selectedRow = enrollmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an enrollment to remove");
                return;
            }

            String studentId = tableModel.getValueAt(selectedRow, 0).toString();
            String courseId = tableModel.getValueAt(selectedRow, 2).toString();

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?")) {
                
                pstmt.setString(1, studentId);
                pstmt.setString(2, courseId);
                pstmt.executeUpdate();
                
                loadEnrollments();
                JOptionPane.showMessageDialog(this, "Enrollment removed successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error removing enrollment: " + ex.getMessage());
            }
        });

        // Add table selection listener
        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = enrollmentTable.getSelectedRow();
                if (selectedRow != -1) {
                    Object score = tableModel.getValueAt(selectedRow, 4);
                    scoreField.setText(score != null ? score.toString() : "");
                }
            }
        });
    }

    private String calculateGrade(double score) {
        if (score >= 95) return "A+";
        if (score >= 90) return "A";
        if (score >= 85) return "A-";
        if (score >= 80) return "B+";
        if (score >= 75) return "B";
        if (score >= 70) return "B-";
        if (score >= 65) return "C+";
        if (score >= 60) return "C";
        if (score >= 55) return "C-";
        if (score >= 50) return "D";
        return "F";
    }

    private void clearFields() {
        studentComboBox.setSelectedIndex(-1);
        courseComboBox.setSelectedIndex(-1);
        scoreField.setText("");
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
    }

    private void styleTable() {
        enrollmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        enrollmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        enrollmentTable.setRowHeight(25);
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void loadStudents() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, first_name, last_name FROM students")) {
            
            studentComboBox.removeAllItems();
            while (rs.next()) {
                String id = rs.getString("student_id");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                studentComboBox.addItem(new ComboItem(id, name));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void loadCourses() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, course_name FROM courses")) {
            
            courseComboBox.removeAllItems();
            while (rs.next()) {
                String id = rs.getString("course_id");
                String name = rs.getString("course_name");
                courseComboBox.addItem(new ComboItem(id, name));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
        }
    }

    private void loadEnrollments() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT e.student_id, CONCAT(s.first_name, ' ', s.last_name) as student_name, " +
                "e.course_id, c.course_name, e.score, e.grade " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN courses c ON e.course_id = c.course_id")) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getObject("score"),
                    rs.getString("grade")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage());
        }
    }

    private static class ComboItem {
        private String id;
        private String name;

        public ComboItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String toString() { return name; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentCourseAssignment().setVisible(true);
        });
    }

    private void addLabelAndComboBox(JPanel panel, GridBagConstraints gbc, String labelText, JComboBox<ComboItem> comboBox, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        
        gbc.gridx = 0;
        gbc.gridy = gridy;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        comboBox.setPreferredSize(new Dimension(200, 30));
        panel.add(comboBox, gbc);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        styleButton(button, color);
        return button;
    }
} 