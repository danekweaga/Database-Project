public class Book {
        private String bookId;
        private String title;
        private String author;
        private String isbn;
        private String publisher;
        private int year;
        private String category;
        private boolean availability;

        public Book(String bookId, String title, String author, String isbn, String publisher, int year, String category, boolean availability) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.publisher = publisher;
            this.year = year;
            this.category = category;
            this.availability = availability;
        }

        // Getters and setters
        public String getBookId() { return bookId; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getIsbn() { return isbn; }
        public String getPublisher() { return publisher; }
        public int getYear() { return year; }
        public String getCategory() { return category; }
        public boolean isAvailable() { return availability; }
        public void setAvailability(boolean availability) { this.availability = availability; }
    }