import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class cudteacher extends JFrame {
    private JTextField idField, firstNameField, lastNameField, departmentField, emailField;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JButton createButton, deleteButton, updateButton, clearButton;

    public cudteacher() {
        setTitle("CUD Teacher");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 245));

        // Left panel for input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 115, 220), 2),
                "Teacher Information",
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
        addLabelAndField(inputPanel, gbc, "Department:", departmentField = createStyledTextField());
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

        // Right panel for teacher table
        String[] columnNames = {"ID", "First Name", "Last Name", "Department", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Load data from database
        loadTeacherData();

        // Add action listeners
        addActionListeners();

        // Set up table selection listener
        teacherTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = teacherTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFields(selectedRow);
                }
            }
        });
    }

    private void loadTeacherData() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM teachers");
                while (rs.next()) {
                    String teacherId = rs.getString("teacher_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String department = rs.getString("department");
                    String email = rs.getString("email");
                    tableModel.addRow(new Object[]{teacherId, firstName, lastName, department, email});
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
        teacherTable.setFont(new Font("Arial", Font.PLAIN, 14));
        teacherTable.setRowHeight(25);
        teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        teacherTable.getTableHeader().setBackground(new Color(50, 115, 220));
        teacherTable.getTableHeader().setForeground(Color.WHITE);
        teacherTable.setShowGrid(true);
        teacherTable.setGridColor(new Color(230, 230, 230));
        teacherTable.setSelectionBackground(new Color(220, 240, 255));
    }

    private void populateFields(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        firstNameField.setText(tableModel.getValueAt(row, 1).toString());
        lastNameField.setText(tableModel.getValueAt(row, 2).toString());
        departmentField.setText(tableModel.getValueAt(row, 3).toString());
        emailField.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void clearFields() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        departmentField.setText("");
        emailField.setText("");
        teacherTable.clearSelection();
    }

    private void addActionListeners() {
        createButton.addActionListener(e -> {
            if (validateInputs()) {
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO teachers (teacher_id, first_name, last_name, department, email) VALUES (?, ?, ?, ?, ?)")) {
                        pstmt.setString(1, idField.getText());
                        pstmt.setString(2, firstNameField.getText());
                        pstmt.setString(3, lastNameField.getText());
                        pstmt.setString(4, departmentField.getText());
                        pstmt.setString(5, emailField.getText());
                        pstmt.executeUpdate();

                        tableModel.addRow(new Object[]{idField.getText(), firstNameField.getText(), lastNameField.getText(), departmentField.getText(), emailField.getText()});
                        clearFields();
                        JOptionPane.showMessageDialog(this, "Teacher created successfully.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error creating teacher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1) {
                String teacherId = tableModel.getValueAt(selectedRow, 0).toString();
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM teachers WHERE teacher_id = ?")) {
                        pstmt.setString(1, teacherId);
                        pstmt.executeUpdate();

                        tableModel.removeRow(selectedRow);
                        clearFields();
                        JOptionPane.showMessageDialog(this, "Teacher deleted successfully.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting teacher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow != -1 && validateInputs()) {
                String teacherId = idField.getText();
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement("UPDATE teachers SET first_name = ?, last_name = ?, department = ?, email = ? WHERE teacher_id = ?")) {
                        pstmt.setString(1, firstNameField.getText());
                        pstmt.setString(2, lastNameField.getText());
                        pstmt.setString(3, departmentField.getText());
                        pstmt.setString(4, emailField.getText());
                        pstmt.setString(5, teacherId);
                        pstmt.executeUpdate();

                        tableModel.setValueAt(firstNameField.getText(), selectedRow, 1);
                        tableModel.setValueAt(lastNameField.getText(), selectedRow, 2);
                        tableModel.setValueAt(departmentField.getText(), selectedRow, 3);
                        tableModel.setValueAt(emailField.getText(), selectedRow, 4);
                        JOptionPane.showMessageDialog(this, "Teacher updated successfully.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error updating teacher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        clearButton.addActionListener(e -> clearFields());
    }

    private boolean validateInputs() {
        if (idField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() || departmentField.getText().isEmpty() ||
                emailField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            cudteacher frame = new cudteacher();
            frame.setVisible(true);
        });
    }
}
