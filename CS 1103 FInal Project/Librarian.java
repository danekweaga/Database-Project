public class Librarian {
        private String librarianId;
        private String name;
        private String contactInfo;
        private String role;

        public Librarian(String librarianId, String name, String contactInfo, String role) {
            this.librarianId = librarianId;
            this.name = name;
            this.contactInfo = contactInfo;
            this.role = role;
        }

        // Getters and setters
        public String getLibrarianId() { return librarianId; }
        public String getName() { return name; }
        public String getContactInfo() { return contactInfo; }
        public String getRole() { return role; }
    }
