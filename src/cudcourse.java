import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class cudcourse extends JFrame {
    private JTextField idField, nameField, teacherIdField;
    private JTextArea descriptionArea;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton createButton, deleteButton, updateButton, clearButton;

    public cudcourse() {
        setTitle("CUD Course");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 245));

        // Left panel for input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 115, 220), 2),
                "Course Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(50, 115, 220)
        ));
        inputPanel.setBackground(new Color(250, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Create input fields
        addLabelAndField(inputPanel, gbc, "Course ID:", idField = createStyledTextField());
        addLabelAndField(inputPanel, gbc, "Course Name:", nameField = createStyledTextField());
        addLabelAndField(inputPanel, gbc, "Teacher ID:", teacherIdField = createStyledTextField());

        // Description text area
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(descLabel, gbc);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        inputPanel.add(descScrollPane, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);
        createButton = createStyledButton("Create", Color.BLUE);
        deleteButton = createStyledButton("Delete", Color.RED);
        updateButton = createStyledButton("Update", Color.ORANGE);
        clearButton = createStyledButton("Clear", Color.GRAY);

        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.WEST);

        // Table setup
        String[] columnNames = {"Course ID", "Course Name", "Description", "Teacher ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadCourseData();

        // Add action listeners
        addActionListeners();

        // Table selection listener
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFields(selectedRow);
                }
            }
        });

        setupButtons();
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(5, 5, 5, 5));
        return field;
    }

    private void loadCourseData() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try {
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM courses");
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("course_description"),
                        rs.getString("teacher_id")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading course data: " + e.getMessage());
            }
        }
    }

    private void addActionListeners() {
        createButton.addActionListener(e -> {
            if (validateInputs()) {
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    try {
                        PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO courses (course_name, course_description, teacher_id) VALUES (?, ?, ?)"
                        );
                        pstmt.setString(1, nameField.getText());
                        pstmt.setString(2, descriptionArea.getText());
                        pstmt.setString(3, teacherIdField.getText());
                        pstmt.executeUpdate();

                        refreshTable();
                        clearFields();
                        JOptionPane.showMessageDialog(this, "Course created successfully!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error creating course: " + ex.getMessage());
                    }
                }
            }
        });

        // Similar implementations for delete and update buttons
        // ... (implementation details similar to cudstudent/cudteacher)
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        loadCourseData();
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        descriptionArea.setText("");
        teacherIdField.setText("");
        courseTable.clearSelection();
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty() || teacherIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course name and teacher ID are required!");
            return false;
        }
        try {
            Integer.parseInt(teacherIdField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Teacher ID must be a number!");
            return false;
        }
        return true;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    private void populateFields(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        descriptionArea.setText(tableModel.getValueAt(row, 2).toString());
        teacherIdField.setText(tableModel.getValueAt(row, 3).toString());
    }

    private void styleTable() {
        courseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        courseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        courseTable.setRowHeight(25);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void setupButtons() {
        // Add action listeners to the buttons
        deleteButton.addActionListener(e -> deleteCourse());
        updateButton.addActionListener(e -> updateCourse());
        clearButton.addActionListener(e -> clearFields());
    }

    private void deleteCourse() {
        try {
            String courseId = idField.getText();
            if (courseId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a course to delete");
                return;
            }

            String sql = "DELETE FROM courses WHERE course_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Course deleted successfully!");
                    clearFields();
                    refreshTable(); // Make sure you have this method to reload the table
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting course: " + ex.getMessage());
        }
    }

    private void updateCourse() {
        try {
            String courseId = idField.getText();
            if (courseId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a course to update");
                return;
            }

            String sql = "UPDATE courses SET course_name = ?, teacher_id = ? WHERE course_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nameField.getText());
                stmt.setString(2, teacherIdField.getText());
                stmt.setString(3, courseId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Course updated successfully!");
                    refreshTable();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating course: " + ex.getMessage());
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            cudcourse frame = new cudcourse();
            frame.setVisible(true);
        });
    }
}
