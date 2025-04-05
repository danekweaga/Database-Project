import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class LibraryCLI {
    private static Connection conn;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Initialize database connection
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:library.db");
            conn.setAutoCommit(false);
            
            // Create tables if they don't exist
            initializeDatabase();
            
            // Insert sample data if tables are empty
            insertSampleData();
            
            // Main menu loop
            boolean running = true;
            while (running) {
                System.out.println("\n=== University Library System ===");
                System.out.println("1. Book Management");
                System.out.println("2. User Management");
                System.out.println("3. Transaction Management");
                System.out.println("4. Fine Management");
                System.out.println("5. Reports");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1:
                        bookManagement();
                        break;
                    case 2:
                        userManagement();
                        break;
                    case 3:
                        transactionManagement();
                        break;
                    case 4:
                        fineManagement();
                        break;
                    case 5:
                        generateReports();
                        break;
                    case 6:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
            
            // Close connection
            conn.close();
            System.out.println("Thank you for using the Library System!");
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Create tables if they don't exist (same as before)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS books (" +
            "book_id VARCHAR(10) PRIMARY KEY, " +
            "title VARCHAR(100) NOT NULL, " +
            "author VARCHAR(50), " +
            "isbn VARCHAR(20), " +
            "publisher VARCHAR(50), " +
            "year NUMERIC(4,0) CHECK (year > 1000 AND year < 2100), " +
            "category VARCHAR(30), " +
            "availability BOOLEAN NOT NULL DEFAULT TRUE)");
            
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS students (" +
            "student_id VARCHAR(10) PRIMARY KEY, " +
            "name VARCHAR(50) NOT NULL, " +
            "major VARCHAR(30), " +
            "contact_info VARCHAR(100))");
            
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS faculty (" +
            "faculty_id VARCHAR(10) PRIMARY KEY, " +
            "name VARCHAR(50) NOT NULL, " +
            "department VARCHAR(30), " +
            "contact_info VARCHAR(100))");
            
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS librarians (" +
            "librarian_id VARCHAR(10) PRIMARY KEY, " +
            "name VARCHAR(50) NOT NULL, " +
            "contact_info VARCHAR(100), " +
            "role VARCHAR(30))");
            
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS borrow_transactions (" +
            "transaction_id VARCHAR(15) PRIMARY KEY, " +
            "book_id VARCHAR(10), " +
            "borrower_type VARCHAR(10) CHECK (borrower_type IN ('student', 'faculty')), " +
            "borrower_id VARCHAR(10), " +
            "librarian_id VARCHAR(10), " +
            "borrow_date DATE NOT NULL, " +
            "due_date DATE NOT NULL, " +
            "return_date DATE, " +
            "status VARCHAR(20) CHECK (status IN ('active', 'returned', 'overdue')), " +
            "FOREIGN KEY (book_id) REFERENCES books (book_id) ON DELETE CASCADE, " +
            "FOREIGN KEY (librarian_id) REFERENCES librarians (librarian_id) ON DELETE SET NULL)");
            
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS fines (" +
            "fine_id VARCHAR(15) PRIMARY KEY, " +
            "transaction_id VARCHAR(15), " +
            "borrower_type VARCHAR(10) CHECK (borrower_type IN ('student', 'faculty')), " +
            "borrower_id VARCHAR(10), " +
            "amount NUMERIC(8,2) CHECK (amount >= 0), " +
            "issue_date DATE NOT NULL, " +
            "payment_date DATE, " +
            "payment_status VARCHAR(20) CHECK (payment_status IN ('pending', 'paid', 'waived')), " +
            "FOREIGN KEY (transaction_id) REFERENCES borrow_transactions (transaction_id) ON DELETE CASCADE)");
            
        conn.commit();
        stmt.close();
    }

    private static void insertSampleData() throws SQLException {
        // Check if tables are empty and insert sample data if needed
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
        if (rs.getInt(1) == 0) {
            // Insert sample books
            stmt.executeUpdate("INSERT INTO books VALUES ('B0001', 'Database System Concepts', 'Abraham Silberschatz', '9780073523323', 'McGraw-Hill', 2019, 'Computer Science', true)");
            stmt.executeUpdate("INSERT INTO books VALUES ('B0002', 'Introduction to Algorithms', 'Thomas H. Cormen', '9780262033848', 'MIT Press', 2009, 'Computer Science', true)");
            stmt.executeUpdate("INSERT INTO books VALUES ('B0003', 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'HarperCollins', 1960, 'Fiction', true)");
            
            // Insert sample students
            stmt.executeUpdate("INSERT INTO students VALUES ('S0001', 'John Smith', 'Computer Science', 'jsmith@university.edu')");
            stmt.executeUpdate("INSERT INTO students VALUES ('S0002', 'Emma Johnson', 'Biology', 'ejohnson@university.edu')");
            
            // Insert sample faculty
            stmt.executeUpdate("INSERT INTO faculty VALUES ('F0001', 'Dr. James Wilson', 'Computer Science', 'jwilson@university.edu')");
            stmt.executeUpdate("INSERT INTO faculty VALUES ('F0002', 'Dr. Sarah Miller', 'Biology', 'smiller@university.edu')");
            
            // Insert sample librarians
            stmt.executeUpdate("INSERT INTO librarians VALUES ('L0001', 'Patricia Garcia', 'pgarcia@university.edu', 'Head Librarian')");
            stmt.executeUpdate("INSERT INTO librarians VALUES ('L0002', 'David Martinez', 'dmartinez@university.edu', 'Assistant Librarian')");
            
            // Insert sample transactions
            stmt.executeUpdate("INSERT INTO borrow_transactions VALUES ('T0001', 'B0001', 'student', 'S0001', 'L0001', '2025-03-15', '2025-03-29', null, 'active')");
            stmt.executeUpdate("INSERT INTO borrow_transactions VALUES ('T0002', 'B0003', 'faculty', 'F0002', 'L0002', '2025-03-10', '2025-04-10', '2025-03-25', 'returned')");
            
            // Insert sample fines
            stmt.executeUpdate("INSERT INTO fines VALUES ('F0001', 'T0003', 'student', 'S0003', 25.50, '2025-03-07', null, 'pending')");
            
            conn.commit();
        }
        rs.close();
        stmt.close();
    }

    // ========== BOOK MANAGEMENT ==========
    private static void bookManagement() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Book Management ===");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Books");
            System.out.println("4. Update Book Information");
            System.out.println("5. Delete Book");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    viewAllBooks();
                    break;
                case 3:
                    searchBooks();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    deleteBook();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addBook() throws SQLException {
        System.out.println("\n=== Add New Book ===");
        
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();
        
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        
        System.out.print("Enter Publisher: ");
        String publisher = scanner.nextLine();
        
        System.out.print("Enter Publication Year: ");
        int year = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        
        String sql = "INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, bookId);
        pstmt.setString(2, title);
        pstmt.setString(3, author);
        pstmt.setString(4, isbn);
        pstmt.setString(5, publisher);
        pstmt.setInt(6, year);
        pstmt.setString(7, category);
        pstmt.setBoolean(8, true);
        
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Book added successfully!");
            conn.commit();
        } else {
            System.out.println("Failed to add book.");
        }
        pstmt.close();
    }

    private static void viewAllBooks() throws SQLException {
        System.out.println("\n=== All Books in Library ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM books ORDER BY title");
        
        System.out.printf("%-10s %-30s %-20s %-15s %-10s %-20s %-15s %-10s\n",
            "ID", "Title", "Author", "ISBN", "Year", "Publisher", "Category", "Available");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-10s %-30s %-20s %-15s %-10d %-20s %-15s %-10s\n",
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("year"),
                rs.getString("publisher"),
                rs.getString("category"),
                rs.getBoolean("availability") ? "Yes" : "No");
        }
        
        rs.close();
        stmt.close();
    }

    private static void searchBooks() throws SQLException {
        System.out.println("\n=== Search Books ===");
        System.out.println("Search by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Category");
        System.out.println("4. ISBN");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        String searchTerm;
        String sql;
        
        switch (choice) {
            case 1:
                System.out.print("Enter title to search: ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";
                break;
            case 2:
                System.out.print("Enter author to search: ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY author";
                break;
            case 3:
                System.out.print("Enter category to search: ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM books WHERE category LIKE ? ORDER BY category";
                break;
            case 4:
                System.out.print("Enter ISBN to search: ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM books WHERE isbn = ?";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        if (choice != 4) {
            pstmt.setString(1, "%" + searchTerm + "%");
        } else {
            pstmt.setString(1, searchTerm);
        }
        
        ResultSet rs = pstmt.executeQuery();
        
        System.out.printf("%-10s %-30s %-20s %-15s %-10s %-20s %-15s %-10s\n",
            "ID", "Title", "Author", "ISBN", "Year", "Publisher", "Category", "Available");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-10s %-30s %-20s %-15s %-10d %-20s %-15s %-10s\n",
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("year"),
                rs.getString("publisher"),
                rs.getString("category"),
                rs.getBoolean("availability") ? "Yes" : "No");
        }
        
        if (!found) {
            System.out.println("No books found matching your criteria.");
        }
        
        rs.close();
        pstmt.close();
    }

    private static void updateBook() throws SQLException {
        System.out.println("\n=== Update Book Information ===");
        System.out.print("Enter Book ID to update: ");
        String bookId = scanner.nextLine();
        
        // Check if book exists
        PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM books WHERE book_id = ?");
        checkStmt.setString(1, bookId);
        ResultSet rs = checkStmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println("Book not found with ID: " + bookId);
            checkStmt.close();
            return;
        }
        
        // Display current information
        System.out.println("\nCurrent Book Information:");
        System.out.printf("%-10s %-30s %-20s %-15s %-10s %-20s %-15s\n",
            "ID", "Title", "Author", "ISBN", "Year", "Publisher", "Category");
        System.out.printf("%-10s %-30s %-20s %-15s %-10d %-20s %-15s\n",
            rs.getString("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getInt("year"),
            rs.getString("publisher"),
            rs.getString("category"));
        
        // Get updated information
        System.out.println("\nEnter new information (leave blank to keep current value):");
        
        System.out.print("Title [" + rs.getString("title") + "]: ");
        String title = scanner.nextLine();
        if (title.isEmpty()) title = rs.getString("title");
        
        System.out.print("Author [" + rs.getString("author") + "]: ");
        String author = scanner.nextLine();
        if (author.isEmpty()) author = rs.getString("author");
        
        System.out.print("ISBN [" + rs.getString("isbn") + "]: ");
        String isbn = scanner.nextLine();
        if (isbn.isEmpty()) isbn = rs.getString("isbn");
        
        System.out.print("Publisher [" + rs.getString("publisher") + "]: ");
        String publisher = scanner.nextLine();
        if (publisher.isEmpty()) publisher = rs.getString("publisher");
        
        System.out.print("Year [" + rs.getInt("year") + "]: ");
        String yearStr = scanner.nextLine();
        int year = yearStr.isEmpty() ? rs.getInt("year") : Integer.parseInt(yearStr);
        
        System.out.print("Category [" + rs.getString("category") + "]: ");
        String category = scanner.nextLine();
        if (category.isEmpty()) category = rs.getString("category");
        
        // Update book
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, publisher = ?, " +
                     "year = ?, category = ? WHERE book_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, title);
        pstmt.setString(2, author);
        pstmt.setString(3, isbn);
        pstmt.setString(4, publisher);
        pstmt.setInt(5, year);
        pstmt.setString(6, category);
        pstmt.setString(7, bookId);
        
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Book updated successfully!");
            conn.commit();
        } else {
            System.out.println("Failed to update book.");
        }
        
        pstmt.close();
        rs.close();
        checkStmt.close();
    }

    private static void deleteBook() throws SQLException {
        System.out.println("\n=== Delete Book ===");
        System.out.print("Enter Book ID to delete: ");
        String bookId = scanner.nextLine();
        
        // Check if book exists and is not borrowed
        PreparedStatement checkStmt = conn.prepareStatement(
            "SELECT b.*, COUNT(t.transaction_id) as active_loans " +
            "FROM books b LEFT JOIN borrow_transactions t ON b.book_id = t.book_id AND t.status = 'active' " +
            "WHERE b.book_id = ? GROUP BY b.book_id");
        checkStmt.setString(1, bookId);
        ResultSet rs = checkStmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println("Book not found with ID: " + bookId);
            checkStmt.close();
            return;
        }
        
        if (rs.getInt("active_loans") > 0) {
            System.out.println("Cannot delete book. There are active loans for this book.");
            rs.close();
            checkStmt.close();
            return;
        }
        
        // Confirm deletion
        System.out.println("Book to delete:");
        System.out.println("Title: " + rs.getString("title"));
        System.out.println("Author: " + rs.getString("author"));
        System.out.print("Are you sure you want to delete this book? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            String sql = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book deleted successfully!");
                conn.commit();
            } else {
                System.out.println("Failed to delete book.");
            }
            pstmt.close();
        } else {
            System.out.println("Deletion cancelled.");
        }
        
        rs.close();
        checkStmt.close();
    }

    // ========== USER MANAGEMENT ==========
    private static void userManagement() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== User Management ===");
            System.out.println("1. Manage Students");
            System.out.println("2. Manage Faculty");
            System.out.println("3. Manage Librarians");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    manageStudents();
                    break;
                case 2:
                    manageFaculty();
                    break;
                case 3:
                    manageLibrarians();
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void manageStudents() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Student Management ===");
            System.out.println("1. Add New Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Students");
            System.out.println("4. Update Student Information");
            System.out.println("5. Delete Student");
            System.out.println("6. View Student's Borrowing History");
            System.out.println("7. Back to User Management");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    searchStudents();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    viewStudentBorrowingHistory();
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addStudent() throws SQLException {
        System.out.println("\n=== Add New Student ===");
        
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Major: ");
        String major = scanner.nextLine();
        
        System.out.print("Enter Contact Info: ");
        String contactInfo = scanner.nextLine();
        
        String sql = "INSERT INTO students VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, studentId);
        pstmt.setString(2, name);
        pstmt.setString(3, major);
        pstmt.setString(4, contactInfo);
        
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Student added successfully!");
            conn.commit();
        } else {
            System.out.println("Failed to add student.");
        }
        pstmt.close();
    }

    private static void viewAllStudents() throws SQLException {
        System.out.println("\n=== All Students ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY name");
        
        System.out.printf("%-10s %-20s %-20s %-30s\n", "ID", "Name", "Major", "Contact Info");
        System.out.println("------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-10s %-20s %-20s %-30s\n",
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("major"),
                rs.getString("contact_info"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void searchStudents() throws SQLException {
        System.out.println("\n=== Search Students ===");
        System.out.println("Search by:");
        System.out.println("1. Name");
        System.out.println("2. Major");
        System.out.println("3. Student ID");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        String searchTerm;
        String sql;
        
        switch (choice) {
            case 1:
                System.out.print("Enter name to search: ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM students WHERE name LIKE ? ORDER BY name";
                break;
            case 2:
                System.out.print("Enter major to search: ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM students WHERE major LIKE ? ORDER BY major";
                break;
            case 3:
                System.out.print("Enter student ID to search: ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM students WHERE student_id = ?";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        if (choice != 3) {
            pstmt.setString(1, "%" + searchTerm + "%");
        } else {
            pstmt.setString(1, searchTerm);
        }
        
        ResultSet rs = pstmt.executeQuery();
        
        System.out.printf("%-10s %-20s %-20s %-30s\n", "ID", "Name", "Major", "Contact Info");
        System.out.println("------------------------------------------------------------------");
        
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-10s %-20s %-20s %-30s\n",
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("major"),
                rs.getString("contact_info"));
        }
        
        if (!found) {
            System.out.println("No students found matching your criteria.");
        }
        
        rs.close();
        pstmt.close();
    }

    private static void updateStudent() throws SQLException {
        System.out.println("\n=== Update Student Information ===");
        System.out.print("Enter Student ID to update: ");
        String studentId = scanner.nextLine();
        
        // Check if student exists
        PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM students WHERE student_id = ?");
        checkStmt.setString(1, studentId);
        ResultSet rs = checkStmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println("Student not found with ID: " + studentId);
            checkStmt.close();
            return;
        }
        
        // Display current information
        System.out.println("\nCurrent Student Information:");
        System.out.printf("%-10s %-20s %-20s %-30s\n", "ID", "Name", "Major", "Contact Info");
        System.out.printf("%-10s %-20s %-20s %-30s\n",
            rs.getString("student_id"),
            rs.getString("name"),
            rs.getString("major"),
            rs.getString("contact_info"));
        
        // Get updated information
        System.out.println("\nEnter new information (leave blank to keep current value):");
        
        System.out.print("Name [" + rs.getString("name") + "]: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = rs.getString("name");
        
        System.out.print("Major [" + rs.getString("major") + "]: ");
        String major = scanner.nextLine();
        if (major.isEmpty()) major = rs.getString("major");
        
        System.out.print("Contact Info [" + rs.getString("contact_info") + "]: ");
        String contactInfo = scanner.nextLine();
        if (contactInfo.isEmpty()) contactInfo = rs.getString("contact_info");
        
        // Update student
        String sql = "UPDATE students SET name = ?, major = ?, contact_info = ? WHERE student_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, major);
        pstmt.setString(3, contactInfo);
        pstmt.setString(4, studentId);
        
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Student updated successfully!");
            conn.commit();
        } else {
            System.out.println("Failed to update student.");
        }
        
        pstmt.close();
        rs.close();
        checkStmt.close();
    }

    private static void deleteStudent() throws SQLException {
        System.out.println("\n=== Delete Student ===");
        System.out.print("Enter Student ID to delete: ");
        String studentId = scanner.nextLine();
        
        // Check if student exists and has no active loans
        PreparedStatement checkStmt = conn.prepareStatement(
            "SELECT s.*, COUNT(t.transaction_id) as active_loans " +
            "FROM students s LEFT JOIN borrow_transactions t ON s.student_id = t.borrower_id AND t.status = 'active' " +
            "WHERE s.student_id = ? GROUP BY s.student_id");
        checkStmt.setString(1, studentId);
        ResultSet rs = checkStmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println("Student not found with ID: " + studentId);
            checkStmt.close();
            return;
        }
        
        if (rs.getInt("active_loans") > 0) {
            System.out.println("Cannot delete student. There are active loans for this student.");
            rs.close();
            checkStmt.close();
            return;
        }
        
        // Confirm deletion
        System.out.println("Student to delete:");
        System.out.println("Name: " + rs.getString("name"));
        System.out.println("Major: " + rs.getString("major"));
        System.out.print("Are you sure you want to delete this student? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            String sql = "DELETE FROM students WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student deleted successfully!");
                conn.commit();
            } else {
                System.out.println("Failed to delete student.");
            }
            pstmt.close();
        } else {
            System.out.println("Deletion cancelled.");
        }
        
        rs.close();
        checkStmt.close();
    }

    private static void viewStudentBorrowingHistory() throws SQLException {
        System.out.println("\n=== Student Borrowing History ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        
        // Check if student exists
        PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM students WHERE student_id = ?");
        checkStmt.setString(1, studentId);
        ResultSet studentRs = checkStmt.executeQuery();
        
        if (!studentRs.next()) {
            System.out.println("Student not found with ID: " + studentId);
            checkStmt.close();
            return;
        }
        
        System.out.println("\nBorrowing history for: " + studentRs.getString("name"));
        
        // Get borrowing history
        String sql = "SELECT t.*, b.title FROM borrow_transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.borrower_id = ? AND t.borrower_type = 'student' " +
                     "ORDER BY t.borrow_date DESC";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, studentId);
        ResultSet rs = pstmt.executeQuery();
        
        System.out.printf("%-15s %-10s %-30s %-15s %-15s %-15s %-10s\n",
            "Transaction", "Book ID", "Title", "Borrow Date", "Due Date", "Return Date", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-15s %-10s %-30s %-15s %-15s %-15s %-10s\n",
                rs.getString("transaction_id"),
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getDate("borrow_date"),
                rs.getDate("due_date"),
                rs.getDate("return_date"),
                rs.getString("status"));
        }
        
        if (!found) {
            System.out.println("No borrowing history found for this student.");
        }
        
        rs.close();
        pstmt.close();
        studentRs.close();
        checkStmt.close();
    }

    // Similar methods would be implemented for faculty and librarian management
    // (manageFaculty, addFaculty, viewAllFaculty, etc.)
    // The implementations would be very similar to the student methods

    // ========== TRANSACTION MANAGEMENT ==========
    private static void transactionManagement() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Transaction Management ===");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. View All Transactions");
            System.out.println("4. View Active Transactions");
            System.out.println("5. View Overdue Transactions");
            System.out.println("6. Check for Overdue Books");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    borrowBook();
                    break;
                case 2:
                    returnBook();
                    break;
                case 3:
                    viewAllTransactions();
                    break;
                case 4:
                    viewActiveTransactions();
                    break;
                case 5:
                    viewOverdueTransactions();
                    break;
                case 6:
                    checkOverdueBooks();
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void borrowBook() throws SQLException {
        System.out.println("\n=== Borrow Book ===");
        
        System.out.print("Enter Transaction ID: ");
        String transactionId = scanner.nextLine();
        
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();
        
        System.out.print("Borrower Type (student/faculty): ");
        String borrowerType = scanner.nextLine();
        if (!borrowerType.equalsIgnoreCase("student") && !borrowerType.equalsIgnoreCase("faculty")) {
            System.out.println("Invalid borrower type. Must be 'student' or 'faculty'.");
            return;
        }
        
        System.out.print("Enter Borrower ID: ");
        String borrowerId = scanner.nextLine();
        
        // Verify borrower exists
        String borrowerTable = borrowerType.equalsIgnoreCase("student") ? "students" : "faculty";
        PreparedStatement borrowerStmt = conn.prepareStatement(
            "SELECT * FROM " + borrowerTable + " WHERE " + borrowerTable.substring(0, 1) + "_id = ?");
        borrowerStmt.setString(1, borrowerId);
        ResultSet borrowerRs = borrowerStmt.executeQuery();
        
        if (!borrowerRs.next()) {
            System.out.println(borrowerType + " not found with ID: " + borrowerId);
            borrowerStmt.close();
            return;
        }
        
        System.out.print("Enter Librarian ID: ");
        String librarianId = scanner.nextLine();
        
        // Verify librarian exists
        PreparedStatement librarianStmt = conn.prepareStatement("SELECT * FROM librarians WHERE librarian_id = ?");
        librarianStmt.setString(1, librarianId);
        ResultSet librarianRs = librarianStmt.executeQuery();
        
        if (!librarianRs.next()) {
            System.out.println("Librarian not found with ID: " + librarianId);
            librarianStmt.close();
            borrowerStmt.close();
            return;
        }
        
        // Check if book is available
        PreparedStatement bookStmt = conn.prepareStatement("SELECT * FROM books WHERE book_id = ?");
        bookStmt.setString(1, bookId);
        ResultSet bookRs = bookStmt.executeQuery();
        
        if (!bookRs.next()) {
            System.out.println("Book not found with ID: " + bookId);
            bookStmt.close();
            librarianStmt.close();
            borrowerStmt.close();
            return;
        }
        
        if (!bookRs.getBoolean("availability")) {
            System.out.println("Book is not available for borrowing.");
            bookStmt.close();
            librarianStmt.close();
            borrowerStmt.close();
            return;
        }
        
        // Calculate due date (14 days for students, 30 days for faculty)
        int loanPeriod = borrowerType.equalsIgnoreCase("student") ? 14 : 30;
        
        // Create transaction
        String sql = "INSERT INTO borrow_transactions VALUES (?, ?, ?, ?, ?, CURRENT_DATE, DATE(CURRENT_DATE, ?), NULL, 'active')";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, transactionId);
        pstmt.setString(2, bookId);
        pstmt.setString(3, borrowerType.toLowerCase());
        pstmt.setString(4, borrowerId);
        pstmt.setString(5, librarianId);
        pstmt.setString(6, "+" + loanPeriod + " days");
        
        int rows = pstmt.executeUpdate();
        PreparedStatement updateStmt = conn.prepareStatement("UPDATE books SET availability = FALSE WHERE book_id = ?");

        if (rows > 0) {
                        // Update book availability
                        updateStmt.setString(1, bookId);
            updateStmt.executeUpdate();
            
            conn.commit();
            System.out.println("Book borrowed successfully! Due date: " + 
                LocalDate.now().plusDays(loanPeriod).toString());
        } else {
            System.out.println("Failed to borrow book.");
            conn.rollback();
        }
        
        pstmt.close();
        updateStmt.close();
        bookStmt.close();
        librarianStmt.close();
        borrowerStmt.close();
    }
    
    private static void returnBook() throws SQLException {
        System.out.println("\n=== Return Book ===");
        System.out.print("Enter Transaction ID: ");
        String transactionId = scanner.nextLine();
        
        // Get transaction details
        PreparedStatement transStmt = conn.prepareStatement(
            "SELECT t.*, b.title FROM borrow_transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "WHERE t.transaction_id = ? AND t.status = 'active'");
        transStmt.setString(1, transactionId);
        ResultSet transRs = transStmt.executeQuery();
        
        if (!transRs.next()) {
            System.out.println("No active transaction found with ID: " + transactionId);
            transStmt.close();
            return;
        }
        
        // Display transaction details
        System.out.println("\nTransaction Details:");
        System.out.println("Book: " + transRs.getString("title") + " (" + transRs.getString("book_id") + ")");
        System.out.println("Borrower: " + transRs.getString("borrower_id") + " (" + transRs.getString("borrower_type") + ")");
        System.out.println("Borrow Date: " + transRs.getDate("borrow_date"));
        System.out.println("Due Date: " + transRs.getDate("due_date"));
        
        // Check if overdue
        LocalDate dueDate = transRs.getDate("due_date").toLocalDate();
        LocalDate returnDate = LocalDate.now();
        boolean isOverdue = returnDate.isAfter(dueDate);
        
        if (isOverdue) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
            System.out.println("This book is " + daysOverdue + " days overdue!");
            
            // Calculate fine ($1 per day for students, $0.50 per day for faculty)
            double fineAmount = transRs.getString("borrower_type").equals("student") ? 
                daysOverdue * 1.0 : daysOverdue * 0.5;
            
            System.out.printf("Fine amount: $%.2f%n", fineAmount);
            
            // Create fine record
            String fineId = "F" + System.currentTimeMillis() % 10000;
            PreparedStatement fineStmt = conn.prepareStatement(
                "INSERT INTO fines VALUES (?, ?, ?, ?, ?, CURRENT_DATE, NULL, 'pending')");
            fineStmt.setString(1, fineId);
            fineStmt.setString(2, transactionId);
            fineStmt.setString(3, transRs.getString("borrower_type"));
            fineStmt.setString(4, transRs.getString("borrower_id"));
            fineStmt.setDouble(5, fineAmount);
            fineStmt.executeUpdate();
            fineStmt.close();
        }
        
        // Update transaction
        PreparedStatement updateStmt = conn.prepareStatement(
            "UPDATE borrow_transactions SET return_date = CURRENT_DATE, status = 'returned' " +
            "WHERE transaction_id = ?");
        updateStmt.setString(1, transactionId);
        updateStmt.executeUpdate();
        
        // Update book availability
        PreparedStatement bookStmt = conn.prepareStatement(
            "UPDATE books SET availability = TRUE WHERE book_id = ?");
        bookStmt.setString(1, transRs.getString("book_id"));
        bookStmt.executeUpdate();
        
        System.out.println("Book returned successfully!");
        conn.commit();
        
        bookStmt.close();
        updateStmt.close();
        transRs.close();
        transStmt.close();
    }

    private static void viewAllTransactions() throws SQLException {
        System.out.println("\n=== All Transactions ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT t.*, b.title, " +
            "CASE WHEN t.borrower_type = 'student' THEN s.name ELSE f.name END as borrower_name " +
            "FROM borrow_transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON t.borrower_type = 'student' AND t.borrower_id = s.student_id " +
            "LEFT JOIN faculty f ON t.borrower_type = 'faculty' AND t.borrower_id = f.faculty_id " +
            "ORDER BY t.borrow_date DESC");
        
        System.out.printf("%-15s %-10s %-30s %-15s %-20s %-15s %-15s %-15s %-10s\n",
            "Transaction", "Book ID", "Title", "Borrower", "Borrower Name", "Borrow Date", "Due Date", "Return Date", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-15s %-10s %-30s %-15s %-20s %-15s %-15s %-15s %-10s\n",
                rs.getString("transaction_id"),
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("borrower_type") + ":" + rs.getString("borrower_id"),
                rs.getString("borrower_name"),
                rs.getDate("borrow_date"),
                rs.getDate("due_date"),
                rs.getDate("return_date"),
                rs.getString("status"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void viewActiveTransactions() throws SQLException {
        System.out.println("\n=== Active Transactions ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT t.*, b.title, " +
            "CASE WHEN t.borrower_type = 'student' THEN s.name ELSE f.name END as borrower_name " +
            "FROM borrow_transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON t.borrower_type = 'student' AND t.borrower_id = s.student_id " +
            "LEFT JOIN faculty f ON t.borrower_type = 'faculty' AND t.borrower_id = f.faculty_id " +
            "WHERE t.status = 'active' " +
            "ORDER BY t.due_date");
        
        System.out.printf("%-15s %-10s %-30s %-15s %-20s %-15s %-15s\n",
            "Transaction", "Book ID", "Title", "Borrower", "Borrower Name", "Borrow Date", "Due Date");
        System.out.println("-------------------------------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-15s %-10s %-30s %-15s %-20s %-15s %-15s\n",
                rs.getString("transaction_id"),
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("borrower_type") + ":" + rs.getString("borrower_id"),
                rs.getString("borrower_name"),
                rs.getDate("borrow_date"),
                rs.getDate("due_date"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void viewOverdueTransactions() throws SQLException {
        System.out.println("\n=== Overdue Transactions ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT t.*, b.title, " +
            "CASE WHEN t.borrower_type = 'student' THEN s.name ELSE f.name END as borrower_name, " +
            "julianday('now') - julianday(t.due_date) as days_overdue " +
            "FROM borrow_transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON t.borrower_type = 'student' AND t.borrower_id = s.student_id " +
            "LEFT JOIN faculty f ON t.borrower_type = 'faculty' AND t.borrower_id = f.faculty_id " +
            "WHERE t.status = 'active' AND t.due_date < date('now') " +
            "ORDER BY t.due_date");
        
        System.out.printf("%-15s %-10s %-30s %-15s %-20s %-15s %-15s %-10s\n",
            "Transaction", "Book ID", "Title", "Borrower", "Borrower Name", "Due Date", "Days Overdue", "Fine");
        System.out.println("------------------------------------------------------------------------------------------------------");
        
        while (rs.next()) {
            int daysOverdue = rs.getInt("days_overdue");
            double fineAmount = rs.getString("borrower_type").equals("student") ? 
                daysOverdue * 1.0 : daysOverdue * 0.5;
            
            System.out.printf("%-15s %-10s %-30s %-15s %-20s %-15s %-15d $%-8.2f\n",
                rs.getString("transaction_id"),
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("borrower_type") + ":" + rs.getString("borrower_id"),
                rs.getString("borrower_name"),
                rs.getDate("due_date"),
                daysOverdue,
                fineAmount);
        }
        
        rs.close();
        stmt.close();
    }

    private static void checkOverdueBooks() throws SQLException {
        System.out.println("\n=== Checking for Overdue Books ===");
        Statement stmt = conn.createStatement();
        
        // Find all active transactions with due dates in the past
        int updated = stmt.executeUpdate(
            "UPDATE borrow_transactions SET status = 'overdue' " +
            "WHERE status = 'active' AND due_date < date('now')");
        
        if (updated > 0) {
            System.out.println("Marked " + updated + " transactions as overdue.");
            conn.commit();
        } else {
            System.out.println("No new overdue books found.");
        }
        
        stmt.close();
    }

    // ========== FINE MANAGEMENT ==========
    private static void fineManagement() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Fine Management ===");
            System.out.println("1. View All Fines");
            System.out.println("2. View Pending Fines");
            System.out.println("3. Record Fine Payment");
            System.out.println("4. Waive Fine");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    viewAllFines();
                    break;
                case 2:
                    viewPendingFines();
                    break;
                case 3:
                    recordFinePayment();
                    break;
                case 4:
                    waiveFine();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void viewAllFines() throws SQLException {
        System.out.println("\n=== All Fines ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT f.*, t.book_id, b.title, " +
            "CASE WHEN f.borrower_type = 'student' THEN s.name ELSE f2.name END as borrower_name " +
            "FROM fines f " +
            "JOIN borrow_transactions t ON f.transaction_id = t.transaction_id " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON f.borrower_type = 'student' AND f.borrower_id = s.student_id " +
            "LEFT JOIN faculty f2 ON f.borrower_type = 'faculty' AND f.borrower_id = f2.faculty_id " +
            "ORDER BY f.issue_date DESC");
        
        System.out.printf("%-10s %-15s %-10s %-30s %-20s %-10s %-15s %-15s %-10s\n",
            "Fine ID", "Transaction", "Book ID", "Title", "Borrower Name", "Amount", "Issue Date", "Payment Date", "Status");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-10s %-15s %-10s %-30s %-20s $%-9.2f %-15s %-15s %-10s\n",
                rs.getString("fine_id"),
                rs.getString("transaction_id"),
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("borrower_name"),
                rs.getDouble("amount"),
                rs.getDate("issue_date"),
                rs.getDate("payment_date"),
                rs.getString("payment_status"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void viewPendingFines() throws SQLException {
        System.out.println("\n=== Pending Fines ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT f.*, t.book_id, b.title, " +
            "CASE WHEN f.borrower_type = 'student' THEN s.name ELSE f2.name END as borrower_name " +
            "FROM fines f " +
            "JOIN borrow_transactions t ON f.transaction_id = t.transaction_id " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON f.borrower_type = 'student' AND f.borrower_id = s.student_id " +
            "LEFT JOIN faculty f2 ON f.borrower_type = 'faculty' AND f.borrower_id = f2.faculty_id " +
            "WHERE f.payment_status = 'pending' " +
            "ORDER BY f.issue_date");
        
        System.out.printf("%-10s %-15s %-10s %-30s %-20s %-10s %-15s\n",
            "Fine ID", "Transaction", "Book ID", "Title", "Borrower Name", "Amount", "Issue Date");
        System.out.println("------------------------------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-10s %-15s %-10s %-30s %-20s $%-9.2f %-15s\n",
                rs.getString("fine_id"),
                rs.getString("transaction_id"),
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("borrower_name"),
                rs.getDouble("amount"),
                rs.getDate("issue_date"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void recordFinePayment() throws SQLException {
        System.out.println("\n=== Record Fine Payment ===");
        System.out.print("Enter Fine ID: ");
        String fineId = scanner.nextLine();
        
        // Get fine details
        PreparedStatement fineStmt = conn.prepareStatement(
            "SELECT f.*, t.book_id, b.title, " +
            "CASE WHEN f.borrower_type = 'student' THEN s.name ELSE f2.name END as borrower_name " +
            "FROM fines f " +
            "JOIN borrow_transactions t ON f.transaction_id = t.transaction_id " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON f.borrower_type = 'student' AND f.borrower_id = s.student_id " +
            "LEFT JOIN faculty f2 ON f.borrower_type = 'faculty' AND f.borrower_id = f2.faculty_id " +
            "WHERE f.fine_id = ? AND f.payment_status = 'pending'");
        fineStmt.setString(1, fineId);
        ResultSet rs = fineStmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println("No pending fine found with ID: " + fineId);
            fineStmt.close();
            return;
        }
        
        // Display fine details
        System.out.println("\nFine Details:");
        System.out.println("Transaction: " + rs.getString("transaction_id"));
        System.out.println("Book: " + rs.getString("title") + " (" + rs.getString("book_id") + ")");
        System.out.println("Borrower: " + rs.getString("borrower_name") + 
                          " (" + rs.getString("borrower_type") + ":" + rs.getString("borrower_id") + ")");
        System.out.println("Amount: $" + rs.getDouble("amount"));
        System.out.println("Issued: " + rs.getDate("issue_date"));
        
        System.out.print("\nConfirm payment? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            // Update fine
            PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE fines SET payment_date = CURRENT_DATE, payment_status = 'paid' " +
                "WHERE fine_id = ?");
            updateStmt.setString(1, fineId);
            updateStmt.executeUpdate();
            conn.commit();
            System.out.println("Payment recorded successfully!");
            updateStmt.close();
        } else {
            System.out.println("Payment cancelled.");
        }
        
        rs.close();
        fineStmt.close();
    }

    private static void waiveFine() throws SQLException {
        System.out.println("\n=== Waive Fine ===");
        System.out.print("Enter Fine ID: ");
        String fineId = scanner.nextLine();
        
        // Get fine details
        PreparedStatement fineStmt = conn.prepareStatement(
            "SELECT f.*, t.book_id, b.title, " +
            "CASE WHEN f.borrower_type = 'student' THEN s.name ELSE f2.name END as borrower_name " +
            "FROM fines f " +
            "JOIN borrow_transactions t ON f.transaction_id = t.transaction_id " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON f.borrower_type = 'student' AND f.borrower_id = s.student_id " +
            "LEFT JOIN faculty f2 ON f.borrower_type = 'faculty' AND f.borrower_id = f2.faculty_id " +
            "WHERE f.fine_id = ? AND f.payment_status = 'pending'");
        fineStmt.setString(1, fineId);
        ResultSet rs = fineStmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println("No pending fine found with ID: " + fineId);
            fineStmt.close();
            return;
        }
        
        // Display fine details
        System.out.println("\nFine Details:");
        System.out.println("Transaction: " + rs.getString("transaction_id"));
        System.out.println("Book: " + rs.getString("title") + " (" + rs.getString("book_id") + ")");
        System.out.println("Borrower: " + rs.getString("borrower_name") + 
                          " (" + rs.getString("borrower_type") + ":" + rs.getString("borrower_id") + ")");
        System.out.println("Amount: $" + rs.getDouble("amount"));
        System.out.println("Issued: " + rs.getDate("issue_date"));
        
        System.out.print("\nConfirm waive this fine? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            // Update fine
            PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE fines SET payment_status = 'waived' " +
                "WHERE fine_id = ?");
            updateStmt.setString(1, fineId);
            updateStmt.executeUpdate();
            conn.commit();
            System.out.println("Fine waived successfully!");
            updateStmt.close();
        } else {
            System.out.println("Waive cancelled.");
        }
        
        rs.close();
        fineStmt.close();
    }

    // ========== REPORTS ==========
    private static void generateReports() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Reports ===");
            System.out.println("1. Books Currently Borrowed");
            System.out.println("2. Overdue Books");
            System.out.println("3. Most Popular Books");
            System.out.println("4. User Borrowing History");
            System.out.println("5. Fine Summary");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    booksCurrentlyBorrowed();
                    break;
                case 2:
                    overdueBooksReport();
                    break;
                case 3:
                    popularBooksReport();
                    break;
                case 4:
                    userBorrowingHistory();
                    break;
                case 5:
                    fineSummaryReport();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void booksCurrentlyBorrowed() throws SQLException {
        System.out.println("\n=== Books Currently Borrowed ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT b.book_id, b.title, " +
            "CASE WHEN t.borrower_type = 'student' THEN s.name ELSE f.name END as borrower_name, " +
            "t.borrower_type, t.borrower_id, t.due_date " +
            "FROM borrow_transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON t.borrower_type = 'student' AND t.borrower_id = s.student_id " +
            "LEFT JOIN faculty f ON t.borrower_type = 'faculty' AND t.borrower_id = f.faculty_id " +
            "WHERE t.status = 'active' " +
            "ORDER BY t.due_date");
        
        System.out.printf("%-10s %-30s %-20s %-15s %-15s\n",
            "Book ID", "Title", "Borrower Name", "Borrower ID", "Due Date");
        System.out.println("------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-10s %-30s %-20s %-15s %-15s\n",
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("borrower_name"),
                rs.getString("borrower_type") + ":" + rs.getString("borrower_id"),
                rs.getDate("due_date"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void overdueBooksReport() throws SQLException {
        System.out.println("\n=== Overdue Books Report ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT b.book_id, b.title, " +
            "CASE WHEN t.borrower_type = 'student' THEN s.name ELSE f.name END as borrower_name, " +
            "t.borrower_type, t.borrower_id, t.due_date, " +
            "julianday('now') - julianday(t.due_date) as days_overdue, " +
            "CASE WHEN t.borrower_type = 'student' THEN " +
            "  (julianday('now') - julianday(t.due_date)) * 1.0 " +
            "ELSE (julianday('now') - julianday(t.due_date)) * 0.5 END as fine_amount " +
            "FROM borrow_transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "LEFT JOIN students s ON t.borrower_type = 'student' AND t.borrower_id = s.student_id " +
            "LEFT JOIN faculty f ON t.borrower_type = 'faculty' AND t.borrower_id = f.faculty_id " +
            "WHERE t.status = 'active' AND t.due_date < date('now') " +
            "ORDER BY t.due_date");
        
        System.out.printf("%-10s %-30s %-20s %-15s %-15s %-10s %-10s\n",
            "Book ID", "Title", "Borrower Name", "Borrower ID", "Due Date", "Days Over", "Fine");
        System.out.println("------------------------------------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-10s %-30s %-20s %-15s %-15s %-10d $%-8.2f\n",
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("borrower_name"),
                rs.getString("borrower_type") + ":" + rs.getString("borrower_id"),
                rs.getDate("due_date"),
                rs.getInt("days_overdue"),
                rs.getDouble("fine_amount"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void popularBooksReport() throws SQLException {
        System.out.println("\n=== Most Popular Books ===");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT b.book_id, b.title, b.author, COUNT(t.transaction_id) as borrow_count " +
            "FROM books b LEFT JOIN borrow_transactions t ON b.book_id = t.book_id " +
            "GROUP BY b.book_id, b.title, b.author " +
            "ORDER BY borrow_count DESC, b.title " +
            "LIMIT 10");
        
        System.out.printf("%-10s %-30s %-20s %-10s\n",
            "Book ID", "Title", "Author", "Borrows");
        System.out.println("----------------------------------------------------");
        
        while (rs.next()) {
            System.out.printf("%-10s %-30s %-20s %-10d\n",
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("borrow_count"));
        }
        
        rs.close();
        stmt.close();
    }

    private static void userBorrowingHistory() throws SQLException {
        System.out.println("\n=== User Borrowing History ===");
        System.out.print("Enter User ID (student or faculty): ");
        String userId = scanner.nextLine();
        
        // Determine if user is student or faculty
        PreparedStatement userStmt = conn.prepareStatement(
            "SELECT 'student' as type, name FROM students WHERE student_id = ? " +
            "UNION ALL " +
            "SELECT 'faculty' as type, name FROM faculty WHERE faculty_id = ?");
        userStmt.setString(1, userId);
        userStmt.setString(2, userId);
        ResultSet userRs = userStmt.executeQuery();
        
        if (!userRs.next()) {
            System.out.println("No user found with ID: " + userId);
            userStmt.close();
            return;
        }
        
        String userType = userRs.getString("type");
        String userName = userRs.getString("name");
        
        System.out.println("\nBorrowing history for: " + userName + " (" + userType + ":" + userId + ")");
        
        // Get borrowing history
        PreparedStatement histStmt = conn.prepareStatement(
            "SELECT t.*, b.title FROM borrow_transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "WHERE t.borrower_id = ? AND t.borrower_type = ? " +
            "ORDER BY t.borrow_date DESC");
        histStmt.setString(1, userId);
        histStmt.setString(2, userType);
        ResultSet rs = histStmt.executeQuery();
        
        System.out.printf("%-15s %-10s %-30s %-15s %-15s %-15s %-10s\n",
            "Transaction", "Book ID", "Title", "Borrow Date", "Due Date", "Return Date", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-15s %-10s %-30s %-15s %-15s %-15s %-10s\n",
                rs.getString("transaction_id"),
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getDate("borrow_date"),
                rs.getDate("due_date"),
                rs.getDate("return_date"),
                rs.getString("status"));
        }
        
        if (!found) {
            System.out.println("No borrowing history found for this user.");
        }
        
        // Get fines history
        PreparedStatement fineStmt = conn.prepareStatement(
            "SELECT f.*, b.title FROM fines f " +
            "JOIN borrow_transactions t ON f.transaction_id = t.transaction_id " +
            "JOIN books b ON t.book_id = b.book_id " +
            "WHERE f.borrower_id = ? AND f.borrower_type = ? " +
            "ORDER BY f.issue_date DESC");
        fineStmt.setString(1, userId);
        fineStmt.setString(2, userType);
        ResultSet fineRs = fineStmt.executeQuery();
        
        System.out.println("\nFines history:");
        System.out.printf("%-10s %-15s %-10s %-30s %-10s %-15s %-15s %-10s\n",
            "Fine ID", "Transaction", "Book ID", "Title", "Amount", "Issue Date", "Payment Date", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        boolean finesFound = false;
        while (fineRs.next()) {
            finesFound = true;
            System.out.printf("%-10s %-15s %-10s %-30s $%-9.2f %-15s %-15s %-10s\n",
                fineRs.getString("fine_id"),
                fineRs.getString("transaction_id"),
                fineRs.getString("book_id"),
                fineRs.getString("title"),
                fineRs.getDouble("amount"),
                fineRs.getDate("issue_date"),
                fineRs.getDate("payment_date"),
                fineRs.getString("payment_status"));
        }
        
        if (!finesFound) {
            System.out.println("No fines history found for this user.");
        }
        
        fineRs.close();
        fineStmt.close();
        rs.close();
        histStmt.close();
        userRs.close();
        userStmt.close();
    }

    private static void fineSummaryReport() throws SQLException {
        System.out.println("\n=== Fine Summary Report ===");
        Statement stmt = conn.createStatement();
        
        // Total fines
        ResultSet totalRs = stmt.executeQuery(
            "SELECT SUM(amount) as total_fines, " +
            "SUM(CASE WHEN payment_status = 'paid' THEN amount ELSE 0 END) as total_paid, " +
            "SUM(CASE WHEN payment_status = 'pending' THEN amount ELSE 0 END) as total_pending, " +
            "SUM(CASE WHEN payment_status = 'waived' THEN amount ELSE 0 END) as total_waived " +
            "FROM fines");
        
        if (totalRs.next()) {
            System.out.println("Total fines issued: $" + totalRs.getDouble("total_fines"));
            System.out.println("Total paid: $" + totalRs.getDouble("total_paid"));
            System.out.println("Total pending: $" + totalRs.getDouble("total_pending"));
            System.out.println("Total waived: $" + totalRs.getDouble("total_waived"));
        }
        
        // Fines by borrower type
        System.out.println("\nFines by borrower type:");
        ResultSet typeRs = stmt.executeQuery(
            "SELECT borrower_type, SUM(amount) as total_fines, " +
            "SUM(CASE WHEN payment_status = 'paid' THEN amount ELSE 0 END) as paid, " +
            "SUM(CASE WHEN payment_status = 'pending' THEN amount ELSE 0 END) as pending, " +
            "SUM(CASE WHEN payment_status = 'waived' THEN amount ELSE 0 END) as waived " +
            "FROM fines " +
            "GROUP BY borrower_type");
        
        System.out.printf("%-10s %-15s %-15s %-15s %-15s\n",
            "Type", "Total", "Paid", "Pending", "Waived");
        System.out.println("----------------------------------------------------");
        
        while (typeRs.next()) {
            System.out.printf("%-10s $%-14.2f $%-14.2f $%-14.2f $%-14.2f\n",
                typeRs.getString("borrower_type"),
                typeRs.getDouble("total_fines"),
                typeRs.getDouble("paid"),
                typeRs.getDouble("pending"),
                typeRs.getDouble("waived"));
        }
        
        // Top fine payers
        System.out.println("\nTop fine payers:");
        ResultSet topRs = stmt.executeQuery(
            "SELECT borrower_type, borrower_id, " +
            "CASE WHEN borrower_type = 'student' THEN s.name ELSE f.name END as name, " +
            "SUM(amount) as total_fines " +
            "FROM fines " +
            "LEFT JOIN students s ON borrower_type = 'student' AND borrower_id = s.student_id " +
            "LEFT JOIN faculty f ON borrower_type = 'faculty' AND borrower_id = f.faculty_id " +
            "GROUP BY borrower_type, borrower_id, name " +
            "ORDER BY total_fines DESC " +
            "LIMIT 5");
        
        System.out.printf("%-10s %-10s %-20s %-15s\n",
            "Type", "ID", "Name", "Total Fines");
        System.out.println("---------------------------------------------");
        
        while (topRs.next()) {
            System.out.printf("%-10s %-10s %-20s $%-14.2f\n",
                topRs.getString("borrower_type"),
                topRs.getString("borrower_id"),
                topRs.getString("name"),
                topRs.getDouble("total_fines"));
        }
        
        topRs.close();
        typeRs.close();
        totalRs.close();
        stmt.close();
    }
    private static void manageFaculty() throws SQLException {
    boolean back = false;
    while (!back) {
        System.out.println("\n=== Faculty Management ===");
        System.out.println("1. Add New Faculty");
        System.out.println("2. View All Faculty");
        System.out.println("3. Search Faculty");
        System.out.println("4. Update Faculty Information");
        System.out.println("5. Delete Faculty");
        System.out.println("6. View Faculty's Borrowing History");
        System.out.println("7. Back to User Management");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                addFaculty();
                break;
            case 2:
                viewAllFaculty();
                break;
            case 3:
                searchFaculty();
                break;
            case 4:
                updateFaculty();
                break;
            case 5:
                deleteFaculty();
                break;
            case 6:
                viewFacultyBorrowingHistory();
                break;
            case 7:
                back = true;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
}
private static void searchFaculty() throws SQLException {
    System.out.println("\n=== Search Faculty ===");
    System.out.println("Search by:");
    System.out.println("1. Name");
    System.out.println("2. Department");
    System.out.println("3. Faculty ID");
    System.out.print("Enter your choice: ");
    
    int choice = scanner.nextInt();
    scanner.nextLine();
    
    String searchTerm;
    String sql;
    
    switch (choice) {
        case 1:
            System.out.print("Enter name to search: ");
            searchTerm = scanner.nextLine();
            sql = "SELECT * FROM faculty WHERE name LIKE ? ORDER BY name";
            break;
        case 2:
            System.out.print("Enter department to search: ");
            searchTerm = scanner.nextLine();
            sql = "SELECT * FROM faculty WHERE department LIKE ? ORDER BY department";
            break;
        case 3:
            System.out.print("Enter faculty ID to search: ");
            searchTerm = scanner.nextLine();
            sql = "SELECT * FROM faculty WHERE faculty_id = ?";
            break;
        default:
            System.out.println("Invalid choice.");
            return;
    }
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    if (choice != 3) {
        pstmt.setString(1, "%" + searchTerm + "%");
    } else {
        pstmt.setString(1, searchTerm);
    }
    
    ResultSet rs = pstmt.executeQuery();
    
    System.out.printf("%-10s %-20s %-20s %-30s\n", "ID", "Name", "Department", "Contact Info");
    System.out.println("------------------------------------------------------------------");
    
    boolean found = false;
    while (rs.next()) {
        found = true;
        System.out.printf("%-10s %-20s %-20s %-30s\n",
            rs.getString("faculty_id"),
            rs.getString("name"),
            rs.getString("department"),
            rs.getString("contact_info"));
    }
    
    if (!found) {
        System.out.println("No faculty found matching your criteria.");
    }
    
    rs.close();
    pstmt.close();
}
private static void updateFaculty() throws SQLException {
    System.out.println("\n=== Update Faculty Information ===");
    System.out.print("Enter Faculty ID to update: ");
    String facultyId = scanner.nextLine();
    
    // Check if faculty exists
    PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM faculty WHERE faculty_id = ?");
    checkStmt.setString(1, facultyId);
    ResultSet rs = checkStmt.executeQuery();
    
    if (!rs.next()) {
        System.out.println("Faculty not found with ID: " + facultyId);
        checkStmt.close();
        return;
    }
    
    // Display current information
    System.out.println("\nCurrent Faculty Information:");
    System.out.printf("%-10s %-20s %-20s %-30s\n", "ID", "Name", "Department", "Contact Info");
    System.out.printf("%-10s %-20s %-20s %-30s\n",
        rs.getString("faculty_id"),
        rs.getString("name"),
        rs.getString("department"),
        rs.getString("contact_info"));
    
    // Get updated information
    System.out.println("\nEnter new information (leave blank to keep current value):");
    
    System.out.print("Name [" + rs.getString("name") + "]: ");
    String name = scanner.nextLine();
    if (name.isEmpty()) name = rs.getString("name");
    
    System.out.print("Department [" + rs.getString("department") + "]: ");
    String department = scanner.nextLine();
    if (department.isEmpty()) department = rs.getString("department");
    
    System.out.print("Contact Info [" + rs.getString("contact_info") + "]: ");
    String contactInfo = scanner.nextLine();
    if (contactInfo.isEmpty()) contactInfo = rs.getString("contact_info");
    
    // Update faculty
    String sql = "UPDATE faculty SET name = ?, department = ?, contact_info = ? WHERE faculty_id = ?";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, name);
    pstmt.setString(2, department);
    pstmt.setString(3, contactInfo);
    pstmt.setString(4, facultyId);
    
    int rows = pstmt.executeUpdate();
    if (rows > 0) {
        System.out.println("Faculty updated successfully!");
        conn.commit();
    } else {
        System.out.println("Failed to update faculty.");
    }
    
    pstmt.close();
    rs.close();
    checkStmt.close();
}
private static void manageLibrarians() throws SQLException {
    boolean back = false;
    while (!back) {
        System.out.println("\n=== Librarian Management ===");
        System.out.println("1. Add New Librarian");
        System.out.println("2. View All Librarians");
        System.out.println("3. Search Librarians");
        System.out.println("4. Update Librarian Information");
        System.out.println("5. Delete Librarian");
        System.out.println("6. Back to User Management");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                addLibrarian();
                break;
            case 2:
                viewAllLibrarians();
                break;
            case 3:
                searchLibrarians();
                break;
            case 4:
                updateLibrarian();
                break;
            case 5:
                deleteLibrarian();
                break;
            case 6:
                back = true;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
}
private static void deleteLibrarian() throws SQLException {
    System.out.println("\n=== Delete Librarian ===");
    System.out.print("Enter Librarian ID to delete: ");
    String librarianId = scanner.nextLine();
    
    // Check if librarian exists
    PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM librarians WHERE librarian_id = ?");
    checkStmt.setString(1, librarianId);
    ResultSet rs = checkStmt.executeQuery();
    
    if (!rs.next()) {
        System.out.println("Librarian not found with ID: " + librarianId);
        checkStmt.close();
        return;
    }
    
    // Confirm deletion
    System.out.println("Librarian to delete:");
    System.out.println("Name: " + rs.getString("name"));
    System.out.println("Contact Info: " + rs.getString("contact_info"));
    System.out.print("Are you sure you want to delete this librarian? (y/n): ");
    String confirm = scanner.nextLine();
    
    if (confirm.equalsIgnoreCase("y")) {
        String sql = "DELETE FROM librarians WHERE librarian_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, librarianId);
        
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Librarian deleted successfully!");
            conn.commit();
        } else {
            System.out.println("Failed to delete librarian.");
        }
        pstmt.close();
    } else {
        System.out.println("Deletion cancelled.");
    }
    
    rs.close();
    checkStmt.close();
}
private static void viewAllLibrarians() throws SQLException {
    System.out.println("\n=== All Librarians ===");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM librarians ORDER BY name");
    
    System.out.printf("%-10s %-20s %-30s %-15s\n", "ID", "Name", "Contact Info", "Role");
    System.out.println("------------------------------------------------------------------");
    
    while (rs.next()) {
        System.out.printf("%-10s %-20s %-30s %-15s\n",
            rs.getString("librarian_id"),
            rs.getString("name"),
            rs.getString("contact_info"),
            rs.getString("role"));
    }
    
    rs.close();
    stmt.close();
}
private static void searchLibrarians() throws SQLException {
    System.out.println("\n=== Search Librarians ===");
    System.out.println("Search by:");
    System.out.println("1. Name");
    System.out.println("2. Contact Info");
    System.out.println("3. Librarian ID");
    System.out.print("Enter your choice: ");
    
    int choice = scanner.nextInt();
    scanner.nextLine();
    
    String searchTerm;
    String sql;
    
    switch (choice) {
        case 1:
            System.out.print("Enter name to search: ");
            searchTerm = scanner.nextLine();
            sql = "SELECT * FROM librarians WHERE name LIKE ? ORDER BY name";
            break;
        case 2:
            System.out.print("Enter contact info to search: ");
            searchTerm = scanner.nextLine();
            sql = "SELECT * FROM librarians WHERE contact_info LIKE ? ORDER BY contact_info";
            break;
        case 3:
            System.out.print("Enter librarian ID to search: ");
            searchTerm = scanner.nextLine();
            sql = "SELECT * FROM librarians WHERE librarian_id = ?";
            break;
        default:
            System.out.println("Invalid choice.");
            return;
    }
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    if (choice != 3) {
        pstmt.setString(1, "%" + searchTerm + "%");
    } else {
        pstmt.setString(1, searchTerm);
    }
    
    ResultSet rs = pstmt.executeQuery();
    
    System.out.printf("%-10s %-20s %-30s %-15s\n", "ID", "Name", "Contact Info", "Role");
    System.out.println("------------------------------------------------------------------");
    
    boolean found = false;
    while (rs.next()) {
        found = true;
        System.out.printf("%-10s %-20s %-30s %-15s\n",
            rs.getString("librarian_id"),
            rs.getString("name"),
            rs.getString("contact_info"),
            rs.getString("role"));
    }
    
    if (!found) {
        System.out.println("No librarians found matching your criteria.");
    }
    
    rs.close();
    pstmt.close();
}
private static void updateLibrarian() throws SQLException {
    System.out.println("\n=== Update Librarian Information ===");
    System.out.print("Enter Librarian ID to update: ");
    String librarianId = scanner.nextLine();
    
    // Check if librarian exists
    PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM librarians WHERE librarian_id = ?");
    checkStmt.setString(1, librarianId);
    ResultSet rs = checkStmt.executeQuery();
    
    if (!rs.next()) {
        System.out.println("Librarian not found with ID: " + librarianId);
        checkStmt.close();
        return;
    }
    
    // Display current information
    System.out.println("\nCurrent Librarian Information:");
    System.out.printf("%-10s %-20s %-30s %-15s\n", "ID", "Name", "Contact Info", "Role");
    System.out.printf("%-10s %-20s %-30s %-15s\n",
        rs.getString("librarian_id"),
        rs.getString("name"),
        rs.getString("contact_info"),
        rs.getString("role"));
    
    // Get updated information
    System.out.println("\nEnter new information (leave blank to keep current value):");
    
    System.out.print("Name [" + rs.getString("name") + "]: ");
    String name = scanner.nextLine();
    if (name.isEmpty()) name = rs.getString("name");
    
    System.out.print("Contact Info [" + rs.getString("contact_info") + "]: ");
    String contactInfo = scanner.nextLine();
    if (contactInfo.isEmpty()) contactInfo = rs.getString("contact_info");
    
    System.out.print("Role [" + rs.getString("role") + "]: ");
    String role = scanner.nextLine();
    if (role.isEmpty()) role = rs.getString("role");
    
    // Update librarian
    String sql = "UPDATE librarians SET name = ?, contact_info = ?, role = ? WHERE librarian_id = ?";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, name);
    pstmt.setString(2, contactInfo);
    pstmt.setString(3, role);
    pstmt.setString(4, librarianId);
    
    int rows = pstmt.executeUpdate();
    if (rows > 0) {
        System.out.println("Librarian updated successfully!");
        conn.commit();
    } else {
        System.out.println("Failed to update librarian.");
    }
    
    pstmt.close();
    rs.close();
    checkStmt.close();
}
private static void viewFacultyBorrowingHistory() throws SQLException {
    System.out.println("\n=== Faculty Borrowing History ===");
    System.out.print("Enter Faculty ID: ");
    String facultyId = scanner.nextLine();
    
    // Check if faculty exists
    PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM faculty WHERE faculty_id = ?");
    checkStmt.setString(1, facultyId);
    ResultSet facultyRs = checkStmt.executeQuery();
    
    if (!facultyRs.next()) {
        System.out.println("Faculty not found with ID: " + facultyId);
        checkStmt.close();
        return;
    }
    
    System.out.println("\nBorrowing history for: " + facultyRs.getString("name"));
    
    // Get borrowing history
    String sql = "SELECT t.*, b.title FROM borrow_transactions t " +
                 "JOIN books b ON t.book_id = b.book_id " +
                 "WHERE t.borrower_id = ? AND t.borrower_type = 'faculty' " +
                 "ORDER BY t.borrow_date DESC";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, facultyId);
    ResultSet rs = pstmt.executeQuery();
    
    System.out.printf("%-15s %-10s %-30s %-15s %-15s %-15s %-10s\n",
        "Transaction", "Book ID", "Title", "Borrow Date", "Due Date", "Return Date", "Status");
    System.out.println("--------------------------------------------------------------------------------------------------------");
    
    boolean found = false;
    while (rs.next()) {
        found = true;
        System.out.printf("%-15s %-10s %-30s %-15s %-15s %-15s %-10s\n",
            rs.getString("transaction_id"),
            rs.getString("book_id"),
            rs.getString("title"),
            rs.getDate("borrow_date"),
            rs.getDate("due_date"),
            rs.getDate("return_date"),
            rs.getString("status"));
    }
    
    if (!found) {
        System.out.println("No borrowing history found for this faculty.");
    }
    
    rs.close();
    pstmt.close();
    facultyRs.close();
    checkStmt.close();
}
private static void deleteFaculty() throws SQLException {
    System.out.println("\n=== Delete Faculty ===");
    System.out.print("Enter Faculty ID to delete: ");
    String facultyId = scanner.nextLine();
    
    // Check if faculty exists and has no active loans
    PreparedStatement checkStmt = conn.prepareStatement(
        "SELECT f.*, COUNT(t.transaction_id) as active_loans " +
        "FROM faculty f LEFT JOIN borrow_transactions t ON f.faculty_id = t.borrower_id AND t.status = 'active' " +
        "WHERE f.faculty_id = ? GROUP BY f.faculty_id");
    checkStmt.setString(1, facultyId);
    ResultSet rs = checkStmt.executeQuery();
    
    if (!rs.next()) {
        System.out.println("Faculty not found with ID: " + facultyId);
        checkStmt.close();
        return;
    }
    
    if (rs.getInt("active_loans") > 0) {
        System.out.println("Cannot delete faculty. There are active loans for this faculty.");
        rs.close();
        checkStmt.close();
        return;
    }
    
    // Confirm deletion
    System.out.println("Faculty to delete:");
    System.out.println("Name: " + rs.getString("name"));
    System.out.println("Department: " + rs.getString("department"));
    System.out.print("Are you sure you want to delete this faculty? (y/n): ");
    String confirm = scanner.nextLine();
    
    if (confirm.equalsIgnoreCase("y")) {
        String sql = "DELETE FROM faculty WHERE faculty_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, facultyId);
        
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Faculty deleted successfully!");
            conn.commit();
        } else {
            System.out.println("Failed to delete faculty.");
        }
        pstmt.close();
    } else {
        System.out.println("Deletion cancelled.");
    }
    
    rs.close();
    checkStmt.close();
}
private static void addFaculty() throws SQLException {
    System.out.println("\n=== Add New Faculty ===");
    
    System.out.print("Enter Faculty ID: ");
    String facultyId = scanner.nextLine();
    
    System.out.print("Enter Name: ");
    String name = scanner.nextLine();
    
    System.out.print("Enter Department: ");
    String department = scanner.nextLine();
    
    System.out.print("Enter Contact Info: ");
    String contactInfo = scanner.nextLine();
    
    String sql = "INSERT INTO faculty VALUES (?, ?, ?, ?)";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, facultyId);
    pstmt.setString(2, name);
    pstmt.setString(3, department);
    pstmt.setString(4, contactInfo);
    
    int rows = pstmt.executeUpdate();
    if (rows > 0) {
        System.out.println("Faculty added successfully!");
        conn.commit();
    } else {
        System.out.println("Failed to add faculty.");
    }
    pstmt.close();
}
private static void viewAllFaculty() throws SQLException {
    System.out.println("\n=== All Faculty ===");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM faculty ORDER BY name");
    
    System.out.printf("%-10s %-20s %-20s %-30s\n", "ID", "Name", "Department", "Contact Info");
    System.out.println("------------------------------------------------------------------");
    
    while (rs.next()) {
        System.out.printf("%-10s %-20s %-20s %-30s\n",
            rs.getString("faculty_id"),
            rs.getString("name"),
            rs.getString("department"),
            rs.getString("contact_info"));
    }
    
    rs.close();
    stmt.close();
}
private static void addLibrarian() throws SQLException {
    System.out.println("\n=== Add New Librarian ===");
    
    System.out.print("Enter Librarian ID: ");
    String librarianId = scanner.nextLine();
    
    System.out.print("Enter Name: ");
    String name = scanner.nextLine();
    
    System.out.print("Enter Contact Info: ");
    String contactInfo = scanner.nextLine();
    
    System.out.print("Enter Role: ");
    String role = scanner.nextLine();
    
    String sql = "INSERT INTO librarians VALUES (?, ?, ?, ?)";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, librarianId);
    pstmt.setString(2, name);
    pstmt.setString(3, contactInfo);
    pstmt.setString(4, role);
    
    int rows = pstmt.executeUpdate();
    if (rows > 0) {
        System.out.println("Librarian added successfully!");
        conn.commit();
    } else {
        System.out.println("Failed to add librarian.");
    }
    pstmt.close();
}
}