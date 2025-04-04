import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class TransactionManager extends JPanel {
    private final Connection conn;
    private final JTable table;
    
    public TransactionManager(Connection connection) {
        this.conn = connection;
        setLayout(new BorderLayout());
        
        // Table setup
        table = new JTable(new TransactionTableModel());
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "Check Out", this::checkoutBook);
        addButton(buttonPanel, "Return", this::returnBook);
        addButton(buttonPanel, "Refresh", e -> refreshTransactions());
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshTransactions();
    }
    
    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        panel.add(btn);
    }
    
    private void refreshTransactions() {
        try {
            String sql = "SELECT t.id, b.title, s.name, t.checkout_date, t.due_date, t.return_date " +
                        "FROM transactions t " +
                        "JOIN books b ON t.book_id = b.id " +
                        "JOIN students s ON t.student_id = s.id " +
                        "ORDER BY t.checkout_date DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            ((TransactionTableModel)table.getModel()).updateData(rs);
            rs.close();
        } catch (SQLException e) {
            LibrarySystem.showError("Database Error", e);
        }
    }
    
    private void checkoutBook(ActionEvent e) {
        try {
            // Get available books
            ResultSet books = conn.createStatement().executeQuery(
                "SELECT id, title FROM books WHERE available = 1");
            
            // Get students
            ResultSet students = conn.createStatement().executeQuery(
                "SELECT id, name FROM students ORDER BY name");
            
            JPanel form = new JPanel(new GridLayout(0, 2));
            
            JComboBox<String> bookCombo = new JComboBox<>();
            while (books.next()) {
                bookCombo.addItem(books.getInt("id") + " - " + books.getString("title"));
            }
            
            JComboBox<String> studentCombo = new JComboBox<>();
            while (students.next()) {
                studentCombo.addItem(students.getInt("id") + " - " + students.getString("name"));
            }
            
            form.add(new JLabel("Book:"));
            form.add(bookCombo);
            form.add(new JLabel("Student:"));
            form.add(studentCombo);
            
            int result = JOptionPane.showConfirmDialog(
                this, form, "Check Out Book", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                int bookId = Integer.parseInt(bookCombo.getSelectedItem().toString().split(" - ")[0]);
                int studentId = Integer.parseInt(studentCombo.getSelectedItem().toString().split(" - ")[0]);
                
                // Create transaction
                PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO transactions (book_id, student_id, checkout_date, due_date) " +
                    "VALUES (?, ?, date('now'), date('now', '+14 days'))");
                st.setInt(1, bookId);
                st.setInt(2, studentId);
                st.executeUpdate();
                
                // Update book availability
                st = conn.prepareStatement("UPDATE books SET available = 0 WHERE id = ?");
                st.setInt(1, bookId);
                st.executeUpdate();
                
                refreshTransactions();
            }
        } catch (SQLException ex) {
            LibrarySystem.showError("Checkout Error", ex);
        }
    }
    
    private void returnBook(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int transId = (int)table.getValueAt(row, 0);
            try {
                // Get book ID for this transaction
                ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT book_id FROM transactions WHERE id = " + transId);
                int bookId = rs.getInt(1);
                
                // Mark as returned
                PreparedStatement st = conn.prepareStatement(
                    "UPDATE transactions SET return_date = date('now') WHERE id = ?");
                st.setInt(1, transId);
                st.executeUpdate();
                
                // Make book available again
                st = conn.prepareStatement("UPDATE books SET available = 1 WHERE id = ?");
                st.setInt(1, bookId);
                st.executeUpdate();
                
                refreshTransactions();
            } catch (SQLException ex) {
                LibrarySystem.showError("Return Error", ex);
            }
        }
    }
    
    private static class TransactionTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Book", "Student", "Checkout Date", "Due Date", "Return Date"};
        private List<Object[]> data = new ArrayList<>();
        
        public void updateData(ResultSet rs) throws SQLException {
            data.clear();
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getString("checkout_date"),
                    rs.getString("due_date"),
                    rs.getString("return_date")
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