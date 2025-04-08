# University Library Management System - CLI Setup Guide

## Getting Started

1. **Prerequisite: MySQL Database** Ensure MySQL is installed on your computer before proceeding.

2. **Database Configuration** open the script files in **SQL Workbench**
   - Execute `DDL schema script.txt` to create the database structure
   - Follow with `DML instance data script.txt` to add sample data (optional)

3. **Configure Connection Settings**
   - Locate the `LibraryCLI.java`
   - Find the following line of code **dbManager = new DatabaseManager("jdbc:mysql://link", "user_name", "password");**
   - Change `user_name` and `password` constants in 'dbManager' variable to match the credentials used by your MySQL setup.

4. **Launch the Application**:
   - Compile and execute `LibraryCLI.java`
   - When the program is executed,you'll be greeted with the system's main menu

The setup is now complete. You can navigate through the various library management functions using the command-line interface provided.
