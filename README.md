# Student Management System

A console-based "CRUD application" built with "Java" and "SQL", using JDBC to connect to an embedded SQLite database. Built as a beginner project to practice Java, SQL, and database integration.

## Features
- Add a new student record
- View all student records
- Search for a student by ID
- Update a student's CGPA
- Delete a student record

## Tech Stack
- "Language:" Java (JDK 17 or higher recommended)
- "Database:" SQLite (via JDBC)
- "Concepts used:" JDBC, PreparedStatement (SQL injection-safe queries), CRUD operations, Scanner-based CLI

## How to Run

1. Install a JDK (17+)
2. Download the SQLite JDBC driver and SLF4J API jar, place them in the `lib/` folder
3. Compile: `javac StudentManagementSystem.java` (from inside `src/`)
4. Run:
