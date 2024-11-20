import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class cudstudent extends JFrame {
    private JTextField idField, firstNameField, lastNameField, majorField, emailField;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JButton createButton, deleteButton, updateButton, clearButton;

    public cudstudent() {
        setTitle("CUD Student");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 245));

        // Left panel for input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 115, 220), 2),
                "Student Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(50, 115, 220)
        ));
        inputPanel.setBackground(new Color(250, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addLabelAndField(inputPanel, gbc, "ID:", idField = createStyledTextField());
        addLabelAndField(inputPanel, gbc, "First Name:", firstNameField = createStyledTextField());
        addLabelAndField(inputPanel, gbc, "Last Name:", lastNameField = createStyledTextField());
        addLabelAndField(inputPanel, gbc, "Major:", majorField = createStyledTextField());
        addLabelAndField(inputPanel, gbc, "Email:", emailField = createStyledTextField());

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);
        createButton = createStyledButton("Create", Color.blue);
        deleteButton = createStyledButton("Delete", Color.RED);
        updateButton = createStyledButton("Update", Color.ORANGE);
        clearButton = createStyledButton("Clear", Color.GRAY);

        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.WEST);


        // Right panel for student table
        String[] columnNames = {"ID", "First Name", "Last Name", "Major", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Load data from database
        addSampleData();

        // Add action listeners
        addActionListeners();

        // Set up table selection listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFields(selectedRow);
                }
            }
        });
    }

    private void addSampleData() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM students");
                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String major = rs.getString("major");
                    String email = rs.getString("email");
                    tableModel.addRow(new Object[]{studentId, firstName, lastName, major, email});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
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

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void styleTable() {
        studentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        studentTable.setRowHeight(25);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        studentTable.getTableHeader().setBackground(new Color(50, 115, 220));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(230, 230, 230));
        studentTable.setSelectionBackground(new Color(220, 240, 255));
    }

    private void populateFields(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        firstNameField.setText(tableModel.getValueAt(row, 1).toString());
        lastNameField.setText(tableModel.getValueAt(row, 2).toString());
        majorField.setText(tableModel.getValueAt(row, 3).toString());
        emailField.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void clearFields() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        majorField.setText("");
        emailField.setText("");
        studentTable.clearSelection();
    }

    private void addActionListeners() {
        createButton.addActionListener(e -> {
            if (validateInputs()) {
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO students (student_id, first_name, last_name, major, email) VALUES (?, ?, ?, ?, ?)")) {
                        pstmt.setString(1, idField.getText());
                        pstmt.setString(2, firstNameField.getText());
                        pstmt.setString(3, lastNameField.getText());
                        pstmt.setString(4, majorField.getText());
                        pstmt.setString(5, emailField.getText());
                        pstmt.executeUpdate();
                        
                        tableModel.addRow(new Object[]{idField.getText(), firstNameField.getText(), lastNameField.getText(), majorField.getText(), emailField.getText()});
                        clearFields();
                        JOptionPane.showMessageDialog(this, "Student created successfully.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error creating student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                String studentId = tableModel.getValueAt(selectedRow, 0).toString();
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
                        pstmt.setString(1, studentId);
                        pstmt.executeUpdate();
                        
                        tableModel.removeRow(selectedRow);
                        clearFields();
                        JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1 && validateInputs()) {
                String studentId = idField.getText();
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement("UPDATE students SET first_name = ?, last_name = ?, major = ?, email = ? WHERE student_id = ?")) {
                        pstmt.setString(1, firstNameField.getText());
                        pstmt.setString(2, lastNameField.getText());
                        pstmt.setString(3, majorField.getText());
                        pstmt.setString(4, emailField.getText());
                        pstmt.setString(5, studentId);
                        pstmt.executeUpdate();
                        
                        tableModel.setValueAt(firstNameField.getText(), selectedRow, 1);
                        tableModel.setValueAt(lastNameField.getText(), selectedRow, 2);
                        tableModel.setValueAt(majorField.getText(), selectedRow, 3);
                        tableModel.setValueAt(emailField.getText(), selectedRow, 4);
                        JOptionPane.showMessageDialog(this, "Student updated successfully.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        clearButton.addActionListener(e -> clearFields());
    }

    private boolean validateInputs() {
        // Simple validation check for empty fields
        if (idField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() || majorField.getText().isEmpty() ||
                emailField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            cudstudent frame = new cudstudent();
            frame.setVisible(true);
        });
    }
}
