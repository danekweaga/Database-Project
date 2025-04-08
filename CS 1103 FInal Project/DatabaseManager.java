import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class DatabaseManager {
        public static Connection connection;

        public DatabaseManager(String url, String user, String password) throws SQLException {
            this.connection = DriverManager.getConnection(url, user, password);
        }

        // Book operations
        public List<Book> getBooks() throws SQLException {
            List<Book> books = new ArrayList<>();
            String query = "SELECT * FROM books ORDER BY title";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    books.add(new Book(
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("publisher"),
                        rs.getInt("year"),
                        rs.getString("category"),
                        rs.getBoolean("availability")
                    ));
                }
            }
            return books;
        }

        public Book getBookById(String bookId) throws SQLException {
            String query = "SELECT * FROM books WHERE book_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Book(
                            rs.getString("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getString("publisher"),
                            rs.getInt("year"),
                            rs.getString("category"),
                            rs.getBoolean("availability")
                        );
                    }
                }
            }
            return null;
        }

        public boolean addBook(Book book) throws SQLException {
            String query = "INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, book.getBookId());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getAuthor());
                stmt.setString(4, book.getIsbn());
                stmt.setString(5, book.getPublisher());
                stmt.setInt(6, book.getYear());
                stmt.setString(7, book.getCategory());
                stmt.setBoolean(8, book.isAvailable());
                return stmt.executeUpdate() > 0;
            }
        }

        public boolean updateBookAvailability(String bookId, boolean available) throws SQLException {
            String query = "UPDATE books SET availability = ? WHERE book_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setBoolean(1, available);
                stmt.setString(2, bookId);
                return stmt.executeUpdate() > 0;
            }
        }

        // Student operations
        public List<Student> getStudents() throws SQLException {
            List<Student> students = new ArrayList<>();
            String query = "SELECT * FROM students ORDER BY name";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    students.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("major"),
                        rs.getString("contact_info")
                    ));
                }
            }
            return students;
        }

        public Student getStudentById(String studentId) throws SQLException {
            String query = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, studentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Student(
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("major"),
                            rs.getString("contact_info")
                        );
                    }
                }
            }
            return null;
        }

        public boolean addStudent(Student student) throws SQLException {
            String query = "INSERT INTO students VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, student.getStudentId());
                stmt.setString(2, student.getName());
                stmt.setString(3, student.getMajor());
                stmt.setString(4, student.getContactInfo());
                return stmt.executeUpdate() > 0;
            }
        }

        // Faculty operations
        public List<Faculty> getFaculty() throws SQLException {
            List<Faculty> facultyList = new ArrayList<>();
            String query = "SELECT * FROM faculty ORDER BY name";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    facultyList.add(new Faculty(
                        rs.getString("faculty_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("contact_info")
                    ));
                }
            }
            return facultyList;
        }

        // Librarian operations
        public List<Librarian> getLibrarians() throws SQLException {
            List<Librarian> librarians = new ArrayList<>();
            String query = "SELECT * FROM librarians ORDER BY name";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    librarians.add(new Librarian(
                        rs.getString("librarian_id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("role")
                    ));
                }
            }
            return librarians;
        }

        // Borrow transaction operations
        public List<BorrowTransaction> getBorrowTransactions() throws SQLException {
            List<BorrowTransaction> transactions = new ArrayList<>();
            String query = "SELECT * FROM borrow_transactions ORDER BY borrow_date DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    transactions.add(new BorrowTransaction(
                        rs.getString("transaction_id"),
                        rs.getString("book_id"),
                        rs.getString("borrower_type"),
                        rs.getString("borrower_id"),
                        rs.getString("librarian_id"),
                        rs.getDate("borrow_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")
                    ));
                }
            }
            return transactions;
        }

        public boolean addBorrowTransaction(BorrowTransaction transaction) throws SQLException {
            String query = "INSERT INTO borrow_transactions VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, transaction.getTransactionId());
                stmt.setString(2, transaction.getBookId());
                stmt.setString(3, transaction.getBorrowerType());
                stmt.setString(4, transaction.getBorrowerId());
                stmt.setString(5, transaction.getLibrarianId());
                stmt.setDate(6, transaction.getBorrowDate());
                stmt.setDate(7, transaction.getDueDate());
                stmt.setDate(8, transaction.getReturnDate());
                stmt.setString(9, transaction.getStatus());
                return stmt.executeUpdate() > 0;
            }
        }

        public boolean returnBook(String transactionId, Date returnDate) throws SQLException {
            String query = "UPDATE borrow_transactions SET return_date = ?, status = 'returned' WHERE transaction_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setDate(1, returnDate);
                stmt.setString(2, transactionId);
                return stmt.executeUpdate() > 0;
            }
        }

        // Fine operations
        public List<Fine> getFines() throws SQLException {
            List<Fine> fines = new ArrayList<>();
            String query = "SELECT * FROM fines ORDER BY issue_date DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    fines.add(new Fine(
                        rs.getString("fine_id"),
                        rs.getString("transaction_id"),
                        rs.getString("borrower_type"),
                        rs.getString("borrower_id"),
                        rs.getDouble("amount"),
                        rs.getDate("issue_date"),
                        rs.getDate("payment_date"),
                        rs.getString("payment_status")
                    ));
                }
            }
            return fines;
        }

        public boolean addFine(Fine fine) throws SQLException {
            String query = "INSERT INTO fines VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, fine.getFineId());
                stmt.setString(2, fine.getTransactionId());
                stmt.setString(3, fine.getBorrowerType());
                stmt.setString(4, fine.getBorrowerId());
                stmt.setDouble(5, fine.getAmount());
                stmt.setDate(6, fine.getIssueDate());
                stmt.setDate(7, fine.getPaymentDate());
                stmt.setString(8, fine.getPaymentStatus());
                return stmt.executeUpdate() > 0;
            }
        }

        public boolean payFine(String fineId, Date paymentDate) throws SQLException {
            String query = "UPDATE fines SET payment_date = ?, payment_status = 'paid' WHERE fine_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setDate(1, paymentDate);
                stmt.setString(2, fineId);
                return stmt.executeUpdate() > 0;
            }
        }

        // Advanced operations with JOINs
        public List<String> getCurrentlyBorrowedBooks() throws SQLException {
            List<String> results = new ArrayList<>();
            String query = "SELECT b.title, s.name as borrower_name, bt.due_date " +
                          "FROM borrow_transactions bt " +
                          "JOIN books b ON bt.book_id = b.book_id " +
                          "JOIN students s ON bt.borrower_id = s.student_id " +
                          "WHERE bt.status = 'active' AND bt.borrower_type = 'student' " +
                          "UNION " +
                          "SELECT b.title, f.name as borrower_name, bt.due_date " +
                          "FROM borrow_transactions bt " +
                          "JOIN books b ON bt.book_id = b.book_id " +
                          "JOIN faculty f ON bt.borrower_id = f.faculty_id " +
                          "WHERE bt.status = 'active' AND bt.borrower_type = 'faculty' " +
                          "ORDER BY due_date";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    results.add(String.format("%-40s %-20s Due: %s", 
                        rs.getString("title"),
                        rs.getString("borrower_name"),
                        rs.getDate("due_date")));
                }
            }
            return results;
        }

        public List<String> getOverdueBooks() throws SQLException {
            List<String> results = new ArrayList<>();
            String query = "SELECT b.title, s.name as borrower_name, bt.due_date, " +
                          "DATEDIFF(CURRENT_DATE, bt.due_date) as days_overdue " +
                          "FROM borrow_transactions bt " +
                          "JOIN books b ON bt.book_id = b.book_id " +
                          "JOIN students s ON bt.borrower_id = s.student_id " +
                          "WHERE bt.status = 'active' AND bt.due_date < CURRENT_DATE AND bt.borrower_type = 'student' " +
                          "UNION " +
                          "SELECT b.title, f.name as borrower_name, bt.due_date, " +
                          "DATEDIFF(CURRENT_DATE, bt.due_date) as days_overdue " +
                          "FROM borrow_transactions bt " +
                          "JOIN books b ON bt.book_id = b.book_id " +
                          "JOIN faculty f ON bt.borrower_id = f.faculty_id " +
                          "WHERE bt.status = 'active' AND bt.due_date < CURRENT_DATE AND bt.borrower_type = 'faculty' " +
                          "ORDER BY days_overdue DESC";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    results.add(String.format("%-40s %-20s Due: %s (Overdue by %d days)", 
                        rs.getString("title"),
                        rs.getString("borrower_name"),
                        rs.getDate("due_date"),
                        rs.getInt("days_overdue")));
                }
            }
            return results;
        }

        public List<String> getBorrowingHistory(String borrowerId, String borrowerType) throws SQLException {
            List<String> results = new ArrayList<>();
            String query = "SELECT b.title, bt.borrow_date, bt.due_date, bt.return_date, bt.status " +
                          "FROM borrow_transactions bt " +
                          "JOIN books b ON bt.book_id = b.book_id " +
                          "WHERE bt.borrower_id = ? AND bt.borrower_type = ? " +
                          "ORDER BY bt.borrow_date DESC";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, borrowerId);
                stmt.setString(2, borrowerType);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(String.format("%-40s Borrowed: %s Due: %s Returned: %s Status: %s", 
                            rs.getString("title"),
                            rs.getDate("borrow_date"),
                            rs.getDate("due_date"),
                            rs.getDate("return_date") != null ? rs.getDate("return_date") : "N/A",
                            rs.getString("status")));
                    }
                }
            }
            return results;
        }

        public List<String> getPopularBooks(int limit) throws SQLException {
            List<String> results = new ArrayList<>();
            String query = "SELECT b.title, b.author, COUNT(bt.transaction_id) as borrow_count " +
                          "FROM books b " +
                          "LEFT JOIN borrow_transactions bt ON b.book_id = bt.book_id " +
                          "GROUP BY b.book_id, b.title, b.author " +
                          "ORDER BY borrow_count DESC " +
                          "LIMIT ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, limit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(String.format("%-40s by %-20s (Borrowed %d times)", 
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("borrow_count")));
                    }
                }
            }
            return results;
        }

        public void close() throws SQLException {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    