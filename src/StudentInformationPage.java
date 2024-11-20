import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentInformationPage extends JFrame {
    private JTextField searchField;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentInformationPage() {
        setTitle("Student Information");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 245));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 245));
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setToolTipText("Search by ID, Name, or Major");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Student ID", "First Name", "Last Name", "Major", "Email"};
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

        // Load data from the database
        loadStudentDataFromDatabase();

        // Enable search functionality
        enableSearchFunctionality();
    }

    private void styleTable() {
        studentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        studentTable.setRowHeight(25);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        studentTable.getTableHeader().setBackground(new Color(70, 130, 180));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(230, 230, 230));
    }

    private void loadStudentDataFromDatabase() {
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
                JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enableSearchFunctionality() {
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentInformationPage frame = new StudentInformationPage();
            frame.setVisible(true);
        });
    }
}
