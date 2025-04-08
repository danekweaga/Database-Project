import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Fine {
        private String fineId;
        private String transactionId;
        private String borrowerType;
        private String borrowerId;
        private double amount;
        private Date issueDate;
        private Date paymentDate;
        private String paymentStatus;

        public Fine(String fineId, String transactionId, String borrowerType, String borrowerId, 
                   double amount, Date issueDate, Date paymentDate, String paymentStatus) {
            this.fineId = fineId;
            this.transactionId = transactionId;
            this.borrowerType = borrowerType;
            this.borrowerId = borrowerId;
            this.amount = amount;
            this.issueDate = issueDate;
            this.paymentDate = paymentDate;
            this.paymentStatus = paymentStatus;
        }

        // Getters and setters
        public String getFineId() { return fineId; }
        public String getTransactionId() { return transactionId; }
        public String getBorrowerType() { return borrowerType; }
        public String getBorrowerId() { return borrowerId; }
        public double getAmount() { return amount; }
        public Date getIssueDate() { return issueDate; }
        public Date getPaymentDate() { return paymentDate; }
        public String getPaymentStatus() { return paymentStatus; }
    }
