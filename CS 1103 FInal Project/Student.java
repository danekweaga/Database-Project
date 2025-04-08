public class Student {
        private String studentId;
        private String name;
        private String major;
        private String contactInfo;

        public Student(String studentId, String name, String major, String contactInfo) {
            this.studentId = studentId;
            this.name = name;
            this.major = major;
            this.contactInfo = contactInfo;
        }

        // Getters and setters
        public String getStudentId() { return studentId; }
        public String getName() { return name; }
        public String getMajor() { return major; }
        public String getContactInfo() { return contactInfo; }
    }