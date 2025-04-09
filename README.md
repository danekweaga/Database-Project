# University Library Management System - CLI Setup Guide

## Getting Started

1. **Prerequisite: MySQL Database** Ensure MySQL is installed on your computer before proceeding.
[Link to download MySQL](https://dev.mysql.com/downloads/installer/)


3. **MySQL Connector Setup**
   - Normally, you would need to Download the `MySQL Connector/J` from the official MySQL website.
   - But it is already in the `CS 1103 final project` folder
   - Add the connector JAR file to your project's classpath or library folder.
   - If using an IDE:
        - `BlueJ`: Tools → Preferences → Libraries → Add Tool
        - `Eclipse or IntelliJ`: Add the JAR file to your project dependencies.
          
4. **Database Configuration** open the script files in `SQL Workbench`
   - Execute `DDL schema script.txt` to create the database structure.
   - Follow with `DML instance data script.txt` to add sample data (optional).

5. **Configure Connection Settings**
   - Locate the `LibraryCLI.java`
   - Find the following line of code `dbManager = new DatabaseManager("jdbc:mysql://link", "user_name", "password");`
   - Change `user_name` and `password` constants in 'dbManager' variable to match the credentials used by your MySQL setup.

6. **Launch the Application**:
   - Compile and execute `LibraryCLI.java`
   - When the program is executed,you'll be greeted with the system's main menu

The setup is now complete. You can navigate through the various library management functions using the command-line interface provided.
