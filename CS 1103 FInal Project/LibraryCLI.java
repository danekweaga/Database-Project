import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryCLI {
    // Main application
    private static DatabaseManager dbManager;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Initialize database connection
            dbManager = new DatabaseManager("jdbc:mysql://localhost:3306/university_library", "root", "12345");
            
            boolean exit = false;
            while (!exit) {
                System.out.println("\n=== University Library Management System ===");
                System.out.println("1. Books Management");
                System.out.println("2. Students Management");
                System.out.println("3. Faculty Management");
                System.out.println("4. Librarians Management");
                System.out.println("5. Borrow Transactions");
                System.out.println("6. Fines Management");
                System.out.println("7. Reports and Analytics");
                System.out.println("0. Exit");
                System.out.print("Select an option: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        bookMenu();
                        break;
                    case 2:
                        studentMenu();
                        break;
                    case 3:
                        facultyMenu();
                        break;
                    case 4:
                        librarianMenu();
                        break;
                    case 5:
                        transactionMenu();
                        break;
                    case 6:
                        fineMenu();
                        break;
                    case 7:
                        reportsMenu();
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
            
            dbManager.close();
            System.out.println("Goodbye!");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void bookMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Book Management ===");
            System.out.println("1. View all books");
            System.out.println("2. View book details");
            System.out.println("3. Add new book");
            System.out.println("4. Update book availability");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllBooks();
                    break;
                case 2:
                    viewBookDetails();
                    break;
                case 3:
                    addNewBook();
                    break;
                case 4:
                    updateBookAvailability();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllBooks() throws SQLException {
        List<Book> books = dbManager.getBooks();
        System.out.println("\n=== All Books ===");
        System.out.printf("%-10s %-40s %-20s %-15s %-10s%n", 
            "ID", "Title", "Author", "Category", "Available");
        System.out.println("-------------------------------------------------------------------------------");
        for (Book book : books) {
            System.out.printf("%-10s %-40s %-20s %-15s %-10s%n",
                book.getBookId(),
                book.getTitle().length() > 35 ? book.getTitle().substring(0, 35) + "..." : book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.isAvailable() ? "Yes" : "No");
        }
    }

    private static void viewBookDetails() throws SQLException {
        System.out.print("\nEnter book ID: ");
        String bookId = scanner.nextLine();
        Book book = dbManager.getBookById(bookId);
        
        if (book != null) {
            System.out.println("\n=== Book Details ===");
            System.out.println("ID:           " + book.getBookId());
            System.out.println("Title:        " + book.getTitle());
            System.out.println("Author:       " + book.getAuthor());
            System.out.println("ISBN:         " + book.getIsbn());
            System.out.println("Publisher:    " + book.getPublisher());
            System.out.println("Year:         " + book.getYear());
            System.out.println("Category:     " + book.getCategory());
            System.out.println("Availability: " + (book.isAvailable() ? "Available" : "Checked Out"));
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void addNewBook() throws SQLException {
        System.out.println("\n=== Add New Book ===");
        System.out.print("Book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Publisher: ");
        String publisher = scanner.nextLine();
        System.out.print("Year: ");
        int year = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        
        Book newBook = new Book(bookId, title, author, isbn, publisher, year, category, true);
        if (dbManager.addBook(newBook)) {
            System.out.println("Book added successfully.");
        } else {
            System.out.println("Failed to add book.");
        }
    }

    private static void updateBookAvailability() throws SQLException {
        System.out.print("\nEnter book ID to update availability: ");
        String bookId = scanner.nextLine();
        System.out.print("Is the book available now? (true/false): ");
        boolean available = scanner.nextBoolean();
        scanner.nextLine();
        
        if (dbManager.updateBookAvailability(bookId, available)) {
            System.out.println("Book availability updated successfully.");
        } else {
            System.out.println("Failed to update book availability. Book may not exist.");
        }
    }

    private static void studentMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Student Management ===");
            System.out.println("1. View all students");
            System.out.println("2. View student details");
            System.out.println("3. Add new student");
            System.out.println("4. View student's borrowing history");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllStudents();
                    break;
                case 2:
                    viewStudentDetails();
                    break;
                case 3:
                    addNewStudent();
                    break;
                case 4:
                    viewStudentBorrowingHistory();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllStudents() throws SQLException {
        List<Student> students = dbManager.getStudents();
        System.out.println("\n=== All Students ===");
        System.out.printf("%-10s %-20s %-20s %-30s%n", "ID", "Name", "Major", "Contact Info");
        System.out.println("------------------------------------------------------------------");
        for (Student student : students) {
            System.out.printf("%-10s %-20s %-20s %-30s%n",
                student.getStudentId(),
                student.getName(),
                student.getMajor(),
                student.getContactInfo());
        }
    }

    private static void viewStudentDetails() throws SQLException {
        System.out.print("\nEnter student ID: ");
        String studentId = scanner.nextLine();
        Student student = dbManager.getStudentById(studentId);
        
        if (student != null) {
            System.out.println("\n=== Student Details ===");
            System.out.println("ID:           " + student.getStudentId());
            System.out.println("Name:         " + student.getName());
            System.out.println("Major:        " + student.getMajor());
            System.out.println("Contact Info: " + student.getContactInfo());
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void addNewStudent() throws SQLException {
        System.out.println("\n=== Add New Student ===");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Major: ");
        String major = scanner.nextLine();
        System.out.print("Contact Info: ");
        String contactInfo = scanner.nextLine();
        
        Student newStudent = new Student(studentId, name, major, contactInfo);
        if (dbManager.addStudent(newStudent)) {
            System.out.println("Student added successfully.");
        } else {
            System.out.println("Failed to add student.");
        }
    }

    private static void viewStudentBorrowingHistory() throws SQLException {
        System.out.print("\nEnter student ID: ");
        String studentId = scanner.nextLine();
        
        System.out.println("\n=== Borrowing History ===");
        List<String> history = dbManager.getBorrowingHistory(studentId, "student");
        if (history.isEmpty()) {
            System.out.println("No borrowing history found for this student.");
        } else {
            for (String record : history) {
                System.out.println(record);
            }
        }
    }

    private static void facultyMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Faculty Management ===");
            System.out.println("1. View all faculty");
            System.out.println("2. View faculty's borrowing history");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllFaculty();
                    break;
                case 2:
                    viewFacultyBorrowingHistory();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllFaculty() throws SQLException {
        List<Faculty> facultyList = dbManager.getFaculty();
        System.out.println("\n=== All Faculty ===");
        System.out.printf("%-10s %-20s %-20s %-30s%n", "ID", "Name", "Department", "Contact Info");
        System.out.println("------------------------------------------------------------------");
        for (Faculty faculty : facultyList) {
            System.out.printf("%-10s %-20s %-20s %-30s%n",
                faculty.getFacultyId(),
                faculty.getName(),
                faculty.getDepartment(),
                faculty.getContactInfo());
        }
    }

    private static void viewFacultyBorrowingHistory() throws SQLException {
        System.out.print("\nEnter faculty ID: ");
        String facultyId = scanner.nextLine();
        
        System.out.println("\n=== Borrowing History ===");
        List<String> history = dbManager.getBorrowingHistory(facultyId, "faculty");
        if (history.isEmpty()) {
            System.out.println("No borrowing history found for this faculty member.");
        } else {
            for (String record : history) {
                System.out.println(record);
            }
        }
    }

    private static void librarianMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Librarian Management ===");
            System.out.println("1. View all librarians");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllLibrarians();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllLibrarians() throws SQLException {
        List<Librarian> librarians = dbManager.getLibrarians();
        System.out.println("\n=== All Librarians ===");
        System.out.printf("%-10s %-20s %-30s %-20s%n", "ID", "Name", "Contact Info", "Role");
        System.out.println("------------------------------------------------------------------");
        for (Librarian librarian : librarians) {
            System.out.printf("%-10s %-20s %-30s %-20s%n",
                librarian.getLibrarianId(),
                librarian.getName(),
                librarian.getContactInfo(),
                librarian.getRole());
        }
    }

    private static void transactionMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Borrow Transactions ===");
            System.out.println("1. View all transactions");
            System.out.println("2. Add new transaction");
            System.out.println("3. Return a book");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllTransactions();
                    break;
                case 2:
                    addNewTransaction();
                    break;
                case 3:
                    returnBook();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllTransactions() throws SQLException {
        List<BorrowTransaction> transactions = dbManager.getBorrowTransactions();
        System.out.println("\n=== All Borrow Transactions ===");
        System.out.printf("%-15s %-10s %-10s %-10s %-12s %-12s %-12s %-10s%n", 
            "Transaction", "Book ID", "Type", "Borrower", "Borrow Date", "Due Date", "Return Date", "Status");
        System.out.println("--------------------------------------------------------------------------------------------");
        for (BorrowTransaction t : transactions) {
            System.out.printf("%-15s %-10s %-10s %-10s %-12s %-12s %-12s %-10s%n",
                t.getTransactionId(),
                t.getBookId(),
                t.getBorrowerType(),
                t.getBorrowerId(),
                t.getBorrowDate(),
                t.getDueDate(),
                t.getReturnDate() != null ? t.getReturnDate() : "N/A",
                t.getStatus());
        }
    }

    private static void addNewTransaction() throws SQLException {
        System.out.println("\n=== Add New Borrow Transaction ===");
        System.out.print("Transaction ID: ");
        String transactionId = scanner.nextLine();
        System.out.print("Book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Borrower Type (student/faculty): ");
        String borrowerType = scanner.nextLine();
        System.out.print("Borrower ID: ");
        String borrowerId = scanner.nextLine();
        System.out.print("Librarian ID: ");
        String librarianId = scanner.nextLine();
        System.out.print("Borrow Date (YYYY-MM-DD): ");
        Date borrowDate = Date.valueOf(scanner.nextLine());
        System.out.print("Due Date (YYYY-MM-DD): ");
        Date dueDate = Date.valueOf(scanner.nextLine());
        
        BorrowTransaction newTransaction = new BorrowTransaction(
            transactionId, bookId, borrowerType, borrowerId, librarianId, 
            borrowDate, dueDate, null, "active");
        
        if (dbManager.addBorrowTransaction(newTransaction)) {
            // Update book availability
            dbManager.updateBookAvailability(bookId, false);
            System.out.println("Transaction added successfully.");
        } else {
            System.out.println("Failed to add transaction.");
        }
    }

    private static void returnBook() throws SQLException {
        System.out.print("\nEnter transaction ID: ");
        String transactionId = scanner.nextLine();
        System.out.print("Return Date (YYYY-MM-DD): ");
        Date returnDate = Date.valueOf(scanner.nextLine());
        
        // First get the book ID from the transaction
        String bookId = null;
        String query = "SELECT book_id FROM borrow_transactions WHERE transaction_id = ?";
        try (PreparedStatement stmt = dbManager.connection.prepareStatement(query)) {
            stmt.setString(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bookId = rs.getString("book_id");
                }
            }
        }
        
        if (bookId != null) {
            if (dbManager.returnBook(transactionId, returnDate)) {
                // Update book availability
                dbManager.updateBookAvailability(bookId, true);
                System.out.println("Book returned successfully.");
                
                // Check if the book was returned late
                query = "SELECT due_date FROM borrow_transactions WHERE transaction_id = ?";
                try (PreparedStatement stmt = dbManager.connection.prepareStatement(query)) {
                    stmt.setString(1, transactionId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            Date dueDate = rs.getDate("due_date");
                            if (returnDate.after(dueDate)) {
                                long daysLate = (returnDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
                                double fineAmount = daysLate * 0.50; // $0.50 per day late
                                
                                System.out.printf("Book was returned %d days late. Fine: $%.2f%n", daysLate, fineAmount);
                                
                                System.out.print("Issue fine? (yes/no): ");
                                String issueFine = scanner.nextLine();
                                if (issueFine.equalsIgnoreCase("yes")) {
                                    String fineId = "F" + System.currentTimeMillis();
                                    Fine newFine = new Fine(
                                        fineId, transactionId, "student", // Assume student for simplicity
                                        "S001", fineAmount, new Date(System.currentTimeMillis()), 
                                        null, "pending");
                                    
                                    if (dbManager.addFine(newFine)) {
                                        System.out.println("Fine issued successfully.");
                                    } else {
                                        System.out.println("Failed to issue fine.");
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Failed to return book.");
            }
        } else {
            System.out.println("Transaction not found.");
        }
    }

    private static void fineMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Fines Management ===");
            System.out.println("1. View all fines");
            System.out.println("2. Pay a fine");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllFines();
                    break;
                case 2:
                    payFine();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllFines() throws SQLException {
        List<Fine> fines = dbManager.getFines();
        System.out.println("\n=== All Fines ===");
        System.out.printf("%-15s %-15s %-10s %-10s %-8s %-12s %-12s %-10s%n", 
            "Fine ID", "Transaction", "Type", "Borrower", "Amount", "Issue Date", "Payment Date", "Status");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Fine fine : fines) {
            System.out.printf("%-15s %-15s %-10s %-10s $%-7.2f %-12s %-12s %-10s%n",
                fine.getFineId(),
                fine.getTransactionId(),
                fine.getBorrowerType(),
                fine.getBorrowerId(),
                fine.getAmount(),
                fine.getIssueDate(),
                fine.getPaymentDate() != null ? fine.getPaymentDate() : "N/A",
                fine.getPaymentStatus());
        }
    }

    private static void payFine() throws SQLException {
        System.out.print("\nEnter fine ID: ");
        String fineId = scanner.nextLine();
        System.out.print("Payment Date (YYYY-MM-DD): ");
        Date paymentDate = Date.valueOf(scanner.nextLine());
        
        if (dbManager.payFine(fineId, paymentDate)) {
            System.out.println("Fine paid successfully.");
        } else {
            System.out.println("Failed to pay fine. Fine may not exist.");
        }
    }

    private static void reportsMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Reports and Analytics ===");
            System.out.println("1. Currently borrowed books");
            System.out.println("2. Overdue books");
            System.out.println("3. Popular books");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewCurrentlyBorrowedBooks();
                    break;
                case 2:
                    viewOverdueBooks();
                    break;
                case 3:
                    viewPopularBooks();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewCurrentlyBorrowedBooks() throws SQLException {
        System.out.println("\n=== Currently Borrowed Books ===");
        List<String> borrowedBooks = dbManager.getCurrentlyBorrowedBooks();
        if (borrowedBooks.isEmpty()) {
            System.out.println("No books are currently borrowed.");
        } else {
            for (String book : borrowedBooks) {
                System.out.println(book);
            }
        }
    }

    private static void viewOverdueBooks() throws SQLException {
        System.out.println("\n=== Overdue Books ===");
        List<String> overdueBooks = dbManager.getOverdueBooks();
        if (overdueBooks.isEmpty()) {
            System.out.println("No books are currently overdue.");
        } else {
            for (String book : overdueBooks) {
                System.out.println(book);
            }
        }
    }

    private static void viewPopularBooks() throws SQLException {
        System.out.print("\nEnter number of top books to show: ");
        int limit = scanner.nextInt();
        scanner.nextLine();
        
        System.out.println("\n=== Most Popular Books ===");
        List<String> popularBooks = dbManager.getPopularBooks(limit);
        if (popularBooks.isEmpty()) {
            System.out.println("No borrowing data available.");
        } else {
            for (String book : popularBooks) {
                System.out.println(book);
            }
        }
    }
}