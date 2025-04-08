create database if not exists university_library;
use university_library;
CREATE table books
    (book_id             varchar(10),
     title               varchar(100) not null,
     author              varchar(50),
     isbn                varchar(20),
     publisher           varchar(50),
     year                numeric(4,0) check (year > 1000 and year < 2100),
     category            varchar(30),
     availability        boolean not null default true,
     primary key (book_id)
    );

create table students
    (student_id          varchar(10),
     name                varchar(50) not null,
     major               varchar(30),
     contact_info        varchar(100),
     primary key (student_id)
    );

create table faculty
    (faculty_id          varchar(10),
     name                varchar(50) not null,
     department          varchar(30),
     contact_info        varchar(100),
     primary key (faculty_id)
    );

create table librarians
    (librarian_id        varchar(10),
     name                varchar(50) not null,
     contact_info        varchar(100),
     role                varchar(30),
     primary key (librarian_id)
    );

create table borrow_transactions
    (transaction_id      varchar(15),
     book_id             varchar(10),
     borrower_type       varchar(10) check (borrower_type in ('student', 'faculty')),
     borrower_id         varchar(10),
     librarian_id        varchar(10),
     borrow_date         date not null,
     due_date            date not null,
     return_date         date,
     status              varchar(20) check (status in ('active', 'returned', 'overdue')),
     primary key (transaction_id),
     foreign key (book_id) references books (book_id)
        on delete cascade,
     foreign key (librarian_id) references librarians (librarian_id)
        on delete set null
    );

create table fines
    (fine_id             varchar(15),
     transaction_id      varchar(15),
     borrower_type       varchar(10) check (borrower_type in ('student', 'faculty')),
     borrower_id         varchar(10),
     amount              numeric(8,2) check (amount >= 0),
     issue_date          date not null,
     payment_date        date,
     payment_status      varchar(20) check (payment_status in ('pending', 'paid', 'waived')),
     primary key (fine_id),
     foreign key (transaction_id) references borrow_transactions (transaction_id)
        on delete cascade
    );