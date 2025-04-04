import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class BookManager extends JPanel {
    private final Connection conn;
    private final JTable table;
    private final JTextField searchField;
    
    public BookManager(Connection connection) {
        this.conn = connection;
        setLayout(new BorderLayout());
        
        // Table setup
        table = new JTable(new BookTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.addActionListener(e -> refreshBooks());
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> refreshBooks());
        
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "Add", this::addBook);
        addButton(buttonPanel, "Edit", this::editBook);
        addButton(buttonPanel, "Delete", this::deleteBook);
        addButton(buttonPanel, "Refresh", e -> refreshBooks());
        
        // Layout
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshBooks();
    }
    
    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        panel.add(btn);
    }
    
    private void refreshBooks() {
        try {
            String search = searchField.getText().trim();
            String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, "%" + search + "%");
            st.setString(2, "%" + search + "%");
            
            ResultSet rs = st.executeQuery();
            ((BookTableModel)table.getModel()).updateData(rs);
            
            rs.close();
            st.close();
        } catch (SQLException e) {
            LibrarySystem.showError("Database Error", e);
        }
    }
    
    private void addBook(ActionEvent e) {
        // Implementation for adding a book
        JPanel form = new JPanel(new GridLayout(0, 2));
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField yearField = new JTextField();
        
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Author:"));
        form.add(authorField);
        form.add(new JLabel("ISBN:"));
        form.add(isbnField);
        form.add(new JLabel("Year:"));
        form.add(yearField);
        
        int result = JOptionPane.showConfirmDialog(
            this, form, "Add New Book", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO books (title, author, isbn, published_year) VALUES (?, ?, ?, ?)");
                st.setString(1, titleField.getText());
                st.setString(2, authorField.getText());
                st.setString(3, isbnField.getText());
                st.setInt(4, Integer.parseInt(yearField.getText()));
                st.executeUpdate();
                st.close();
                refreshBooks();
            } catch (Exception ex) {
                LibrarySystem.showError("Error Adding Book", ex);
            }
        }
    }
    
    private void editBook(ActionEvent e) {
        // Similar to addBook but with existing data
    }
    
    private void deleteBook(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int)table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(
                this, "Delete this book?", "Confirm", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement st = conn.prepareStatement(
                        "DELETE FROM books WHERE id = ?");
                    st.setInt(1, id);
                    st.executeUpdate();
                    st.close();
                    refreshBooks();
                } catch (SQLException ex) {
                    LibrarySystem.showError("Error Deleting Book", ex);
                }
            }
        }
    }
    
    private static class BookTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Title", "Author", "ISBN", "Year", "Available"};
        private List<Object[]> data = new ArrayList<>();
        
        public void updateData(ResultSet rs) throws SQLException {
            data.clear();
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("published_year"),
                    rs.getBoolean("available")
                });
            }
            fireTableDataChanged();
        }
        
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public Object getValueAt(int r, int c) { return data.get(r)[c]; }
        @Override public String getColumnName(int c) { return columns[c]; }
        @Override public Class<?> getColumnClass(int c) { 
            return getValueAt(0, c).getClass(); 
        }
    }
}