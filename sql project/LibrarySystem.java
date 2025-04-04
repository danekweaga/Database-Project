import javax.swing.*;
import java.sql.*;

public class LibrarySystem {
    public static void main(String[] args) {
        try {
            // Use file-based SQLite (persists between runs)
            Connection connection = DriverManager.getConnection("jdbc:sqlite:library.db");
            initializeDatabase(connection);
            
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Library Management System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 700);
                
                JTabbedPane tabs = new JTabbedPane();
                tabs.add("Books", new BookManager(connection));
                tabs.add("Students", new StudentManager(connection));
                tabs.add("Transactions", new TransactionManager(connection));
                
                frame.add(tabs);
                frame.setVisible(true);
            });
        } catch (Exception e) {
            showError("Database Error", e);
        }
    }
    
private static void initializeDatabase(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        
        // Books table with more fields
        st.execute("CREATE TABLE IF NOT EXISTS books (" +
                 "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                 "title TEXT NOT NULL," +
                 "author TEXT," +
                 "isbn TEXT UNIQUE," +
                 "published_year INTEGER," +
                 "available BOOLEAN DEFAULT 1)");
        
        // Students table with enrollment info
        st.execute("CREATE TABLE IF NOT EXISTS students (" +
                  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                  "name TEXT NOT NULL," +
                  "email TEXT UNIQUE," +
                  "major TEXT," +
                  "enrollment_date TEXT)");
        
        // Transactions table
        st.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                  "book_id INTEGER," +
                  "student_id INTEGER," +
                  "checkout_date TEXT," +
                  "due_date TEXT," +
                  "return_date TEXT," +
                  "FOREIGN KEY(book_id) REFERENCES books(id)," +
                  "FOREIGN KEY(student_id) REFERENCES students(id))");
        
        // Insert sample data if empty
        if(isTableEmpty(conn, "books")) {
            st.execute("INSERT INTO books (title, author, isbn, published_year) VALUES " +
                      "('Database Systems', 'C.J. Date', '978-0321197849', 2003)," +
                      "('Clean Code', 'Robert Martin', '978-0132350884', 2008)");
        }
        
        if(isTableEmpty(conn, "students")) {
            st.execute("INSERT INTO students (name, email, major, enrollment_date) VALUES " +
                      "('John Smith', 'john@uni.edu', 'Computer Science', '2023-09-01')," +
                      "('Alice Johnson', 'alice@uni.edu', 'Mathematics', '2023-09-01')");
        }
        
        st.close();
    }
    
    private static boolean isTableEmpty(Connection conn, String table) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM " + table);
        return rs.getInt(1) == 0;
    }
    
    public static void showError(String title, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            e.getMessage(), 
            title, 
            JOptionPane.ERROR_MESSAGE);
    }
}