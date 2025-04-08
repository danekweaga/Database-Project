# University Library Management System - CLI Setup Guide

## Quick Setup Instructions

1. **Install MySQL** on your system if not already installed

2. **Run these SQL scripts** in order:
   - First run `DDL schema script.txt` to create the database structure
   - Then run `DML instance data script.txt` to add sample data (optional)

3. **Update database credentials** if needed:
   - Open `LibraryCLI.java`
   - Find 'dbManager'
   - Change `user_name` and `password` constants in 'dbManager' variable to the credentials used by your MySQL setup.

4. **Run the application**:
   - Compile and execute `LibraryCLI.java`
   - The main menu will appear when the program starts

That's it! The system is ready to use. The CLI interface will guide you through all available operations.
