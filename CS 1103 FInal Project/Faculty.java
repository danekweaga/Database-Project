public class Faculty {
        private String facultyId;
        private String name;
        private String department;
        private String contactInfo;

        public Faculty(String facultyId, String name, String department, String contactInfo) {
            this.facultyId = facultyId;
            this.name = name;
            this.department = department;
            this.contactInfo = contactInfo;
        }

        // Getters and setters
        public String getFacultyId() { return facultyId; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public String getContactInfo() { return contactInfo; }
    }