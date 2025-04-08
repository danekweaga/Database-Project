import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class BorrowTransaction {
        private String transactionId;
        private String bookId;
        private String borrowerType;
        private String borrowerId;
        private String librarianId;
        private Date borrowDate;
        private Date dueDate;
        private Date returnDate;
        private String status;

        public BorrowTransaction(String transactionId, String bookId, String borrowerType, String borrowerId, 
                               String librarianId, Date borrowDate, Date dueDate, Date returnDate, String status) {
            this.transactionId = transactionId;
            this.bookId = bookId;
            this.borrowerType = borrowerType;
            this.borrowerId = borrowerId;
            this.librarianId = librarianId;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
            this.status = status;
        }

        // Getters and setters
        public String getTransactionId() { return transactionId; }
        public String getBookId() { return bookId; }
        public String getBorrowerType() { return borrowerType; }
        public String getBorrowerId() { return borrowerId; }
        public String getLibrarianId() { return librarianId; }
        public Date getBorrowDate() { return borrowDate; }
        public Date getDueDate() { return dueDate; }
        public Date getReturnDate() { return returnDate; }
        public String getStatus() { return status; }
    }
