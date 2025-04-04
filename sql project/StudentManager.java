import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.*;

public class StudentManager extends JPanel {
    private final Connection conn;
    private final JTable table;
    
    public StudentManager(Connection connection) {
        this.conn = connection;
        setLayout(new BorderLayout());
        
        // Table setup
        table = new JTable(new StudentTableModel());
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "Add", this::addStudent);
        addButton(buttonPanel, "Edit", this::editStudent);
        addButton(buttonPanel, "Delete", this::deleteStudent);
        addButton(buttonPanel, "Refresh", e -> refreshStudents());
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshStudents();
    }
    
    // Similar CRUD methods as BookManager
    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        panel.add(btn);
    }
    
    private void refreshStudents() {
        try {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM students ORDER BY name");
            ((StudentTableModel)table.getModel()).updateData(rs);
            rs.close();
        } catch (SQLException e) {
            LibrarySystem.showError("Database Error", e);
        }
    }
    
    private void addStudent(ActionEvent e) {
        // Implementation similar to addBook
    }
    
    private void editStudent(ActionEvent e) {
        // Implementation for editing
    }
    
    private void deleteStudent(ActionEvent e) {
        // Implementation similar to deleteBook
    }
    
    private static class StudentTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Name", "Email", "Major", "Enrollment Date"};
        private List<Object[]> data = new ArrayList<>();
        
        public void updateData(ResultSet rs) throws SQLException {
            data.clear();
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("major"),
                    rs.getString("enrollment_date")
                });
            }
            fireTableDataChanged();
        }
        
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public Object getValueAt(int r, int c) { return data.get(r)[c]; }
        @Override public String getColumnName(int c) { return columns[c]; }
    }
}