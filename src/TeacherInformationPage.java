import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TeacherInformationPage extends JFrame {
    private JTextField searchField;
    private JTable teacherTable;
    private DefaultTableModel tableModel;

    public TeacherInformationPage() {
        setTitle("Teacher Information");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 250));

        // Search panel with styled search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 245, 250));
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setToolTipText("Search by ID, Name, or Department");
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add icon to search field (optional, if you have an icon)
        // JLabel searchIcon = new JLabel(new ImageIcon("icons/search.png"));
        // searchField.add(searchIcon, BorderLayout.WEST);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // Table setup with enhanced styles
        String[] columnNames = {"Teacher ID", "First Name", "Last Name", "Department", "Email"};
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
        scrollPane.getViewport().setBackground(new Color(250, 250, 255));
        add(scrollPane, BorderLayout.CENTER);

        // Load data from the database
        loadTeacherDataFromDatabase();

        // Enable search functionality
        enableSearchFunctionality();
    }

    private void styleTable() {
        teacherTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        teacherTable.setRowHeight(30);
        teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        teacherTable.getTableHeader().setBackground(new Color(60, 130, 180));
        teacherTable.getTableHeader().setForeground(Color.WHITE);
        teacherTable.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(60, 130, 180), 1));

        teacherTable.setShowGrid(false);
        teacherTable.setIntercellSpacing(new Dimension(0, 0));

        teacherTable.setSelectionBackground(new Color(230, 240, 255));
        teacherTable.setRowSorter(new TableRowSorter<>(tableModel));

        teacherTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    cell.setBackground(new Color(210, 230, 255));
                    cell.setForeground(Color.BLACK);
                } else {
                    cell.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 245, 255));
                    cell.setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return cell;
            }
        });
    }

    private void loadTeacherDataFromDatabase() {
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
                JOptionPane.showMessageDialog(this, "Error loading teacher data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enableSearchFunctionality() {
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        teacherTable.setRowSorter(rowSorter);

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
            TeacherInformationPage frame = new TeacherInformationPage();
            frame.setVisible(true);
        });
    }
}
