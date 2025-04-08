
-- Insert sample books
insert into books values ('B0001', 'Database System Concepts', 'Abraham Silberschatz', '9780073523323', 'McGraw-Hill', 2019, 'Computer Science', true);
insert into books values ('B0002', 'Introduction to Algorithms', 'Thomas H. Cormen', '9780262033848', 'MIT Press', 2009, 'Computer Science', true);
insert into books values ('B0003', 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'HarperCollins', 1960, 'Fiction', true);
insert into books values ('B0004', 'Pride and Prejudice', 'Jane Austen', '9780141439518', 'Penguin Classics', 1813, 'Fiction', true);
insert into books values ('B0005', 'The Elements of Style', 'William Strunk Jr.', '9780205309023', 'Longman', 1999, 'Reference', true);

-- Insert sample students
insert into students values ('S0001', 'John Smith', 'Computer Science', 'jsmith@university.edu');
insert into students values ('S0002', 'Emma Johnson', 'Biology', 'ejohnson@university.edu');
insert into students values ('S0003', 'Michael Brown', 'History', 'mbrown@university.edu');

-- Insert sample faculty
insert into faculty values ('F0001', 'Dr. James Wilson', 'Computer Science', 'jwilson@university.edu');
insert into faculty values ('F0002', 'Dr. Sarah Miller', 'Biology', 'smiller@university.edu');
insert into faculty values ('F0003', 'Dr. Robert Taylor', 'History', 'rtaylor@university.edu');

-- Insert sample librarians
insert into librarians values ('L0001', 'Patricia Garcia', 'pgarcia@university.edu', 'Head Librarian');
insert into librarians values ('L0002', 'David Martinez', 'dmartinez@university.edu', 'Assistant Librarian');

-- Insert sample borrow transactions
insert into borrow_transactions values ('T0001', 'B0001', 'student', 'S0001', 'L0001', '2025-03-15', '2025-03-29', null, 'active');
insert into borrow_transactions values ('T0002', 'B0003', 'faculty', 'F0002', 'L0002', '2025-03-10', '2025-04-10', '2025-03-25', 'returned');
insert into borrow_transactions values ('T0003', 'B0002', 'student', 'S0003', 'L0001', '2025-02-20', '2025-03-06', null, 'overdue');

-- Insert sample fines
insert into fines values ('F0001', 'T0003', 'student', 'S0003', 25.50, '2025-03-07', null, 'pending');

-- Add more books
INSERT INTO books VALUES 
('B0006', 'The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Scribner', 1925, 'Fiction', true),
('B0007', '1984', 'George Orwell', '9780451524935', 'Signet Classics', 1949, 'Fiction', true),
('B0008', 'The Art of Computer Programming', 'Donald Knuth', '9780201896831', 'Addison-Wesley', 1997, 'Computer Science', false);

-- Add more students
INSERT INTO students VALUES 
('S0004', 'Emily Davis', 'Mathematics', 'edavis@university.edu'),
('S0005', 'Daniel Wilson', 'Physics', 'dwilson@university.edu');

-- Add more faculty
INSERT INTO faculty VALUES 
('F0004', 'Dr. Lisa Anderson', 'Mathematics', 'landerson@university.edu'),
('F0005', 'Dr. Mark Thompson', 'Physics', 'mthompson@university.edu');

-- Add more librarians
INSERT INTO librarians VALUES 
('L0003', 'Jennifer Lee', 'jlee@university.edu', 'Cataloging Librarian'),
('L0004', 'Thomas Clark', 'tclark@university.edu', 'Reference Librarian');

-- Add borrow transactions (depends on books, students/faculty, and librarians)
INSERT INTO borrow_transactions VALUES 
('T0004', 'B0004', 'student', 'S0002', 'L0002', '2025-03-20', '2025-04-03', NULL, 'active'),
('T0005', 'B0006', 'faculty', 'F0001', 'L0003', '2025-03-18', '2025-04-18', NULL, 'active'),
('T0006', 'B0007', 'student', 'S0004', 'L0001', '2025-03-10', '2025-03-24', '2025-03-22', 'returned');

-- Add fines (depends on transactions)
INSERT INTO fines VALUES 
('F0002', 'T0001', 'student', 'S0001', 15.00, '2025-03-30', NULL, 'pending'),
('F0003', 'T0005', 'faculty', 'F0001', 0.00, '2025-04-19', '2025-04-19', 'paid');