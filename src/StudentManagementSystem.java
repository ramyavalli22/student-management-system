import java.sql.*;
import java.util.Scanner;

/**
 * Student Management System
 * ---------------------------------
 * A console-based CRUD application built with Java and SQL (JDBC + SQLite).
 *
 * Features:
 *   1. Add a new student record
 *   2. View all student records
 *   3. Search for a student by ID
 *   4. Update a student's details
 *   5. Delete a student record
 *
 * Author: Ramyavalli Kota
 */
public class StudentManagementSystem {

    // SQLite database file (created automatically in the project folder)
    private static final String DB_URL = "jdbc:sqlite:students.db";

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC"); // Explicitly load the driver
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found on classpath: " + e.getMessage());
            return;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTableIfNotExists(conn);
            runMenu(conn);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Creates the students table the first time the program runs
    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "branch TEXT NOT NULL," +
                "year INTEGER NOT NULL," +
                "cgpa REAL NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void runMenu(Connection conn) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1 -> addStudent(conn, sc);
                case 2 -> viewStudents(conn);
                case 3 -> searchStudent(conn, sc);
                case 4 -> updateStudent(conn, sc);
                case 5 -> deleteStudent(conn, sc);
                case 6 -> {
                    running = false;
                    System.out.println("Exiting. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        sc.close();
    }

    private static void addStudent(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter branch: ");
            String branch = sc.nextLine().trim();

            System.out.print("Enter year (1-4): ");
            int year = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Enter CGPA: ");
            double cgpa = Double.parseDouble(sc.nextLine().trim());

            String sql = "INSERT INTO students(name, branch, year, cgpa) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, branch);
                ps.setInt(3, year);
                ps.setDouble(4, cgpa);
                ps.executeUpdate();
            }
            System.out.println("Student added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Student not added.");
        }
    }

    private static void viewStudents(Connection conn) {
        String sql = "SELECT * FROM students ORDER BY id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nID\tName\t\tBranch\t\tYear\tCGPA");
            System.out.println("------------------------------------------------------------");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("%d\t%-12s\t%-10s\t%d\t%.2f%n",
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("branch"), rs.getInt("year"), rs.getDouble("cgpa"));
            }
            if (!any) System.out.println("No student records found.");
        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
    }

    private static void searchStudent(Connection conn, Scanner sc) {
        System.out.print("Enter student ID to search: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Branch: " + rs.getString("branch"));
                    System.out.println("Year: " + rs.getInt("year"));
                    System.out.println("CGPA: " + rs.getDouble("cgpa"));
                } else {
                    System.out.println("No student found with ID " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
    }

    private static void updateStudent(Connection conn, Scanner sc) {
        System.out.print("Enter student ID to update: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        try {
            System.out.print("Enter new CGPA: ");
            double cgpa = Double.parseDouble(sc.nextLine().trim());

            String sql = "UPDATE students SET cgpa = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, cgpa);
                ps.setInt(2, id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Student record updated successfully!");
                } else {
                    System.out.println("No student found with ID " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid CGPA value.");
        }
    }

    private static void deleteStudent(Connection conn, Scanner sc) {
        System.out.print("Enter student ID to delete: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Student record deleted successfully!");
            } else {
                System.out.println("No student found with ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }
}