-- Create the database
CREATE DATABASE IF NOT EXISTS student_management_system;
USE student_management_system;

-- Create Students table
CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(10) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    major VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Create Teachers table
CREATE TABLE IF NOT EXISTS teachers (
    teacher_id VARCHAR(10) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Create Admins table
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Create Courses table
CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(255) NOT NULL,
    course_description TEXT,
    teacher_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)
);

-- Create Enrollments table
CREATE TABLE IF NOT EXISTS enrollments (
    student_id VARCHAR(10),
    course_id INT,
    score DECIMAL(5,2),
    grade VARCHAR(2),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- Insert sample Students data
INSERT INTO students (student_id, first_name, last_name, major, email) VALUES
('S001', 'John', 'Doe', 'Computer Science', 'john@example.com'),
('S002', 'Jane', 'Smith', 'Engineering', 'jane@example.com'),
('S003', 'Alice', 'Johnson', 'Mathematics', 'alice@example.com'),
('S004', 'Bob', 'Brown', 'Physics', 'bob@example.com'),
('S005', 'Emily', 'Davis', 'Chemistry', 'emily@example.com');

-- Insert sample Teachers data
INSERT INTO teachers (teacher_id, first_name, last_name, department, email) VALUES
('T001', 'Alice', 'Johnson', 'Computer Science', 'alice.j@example.com'),
('T002', 'Bob', 'Smith', 'Mathematics', 'bob.s@example.com'),
('T003', 'Carol', 'Williams', 'Physics', 'carol.w@example.com'),
('T004', 'David', 'Brown', 'Chemistry', 'david.b@example.com'),
('T005', 'Eve', 'Davis', 'Engineering', 'eve.d@example.com');

-- Insert sample Admins data
INSERT INTO admins (username, password, email) VALUES
('admin', 'password123', 'admin@example.com'),
('admin1', 'password123', 'admin');

-- Insert sample Courses data
INSERT INTO courses (course_name, course_description, teacher_id) VALUES
('Introduction to Programming', 'Basic programming concepts and principles', 'T001'),
('Advanced Mathematics', 'Complex mathematical theories and applications', 'T002'),
('Physics 101', 'Fundamental physics concepts', 'T003'),
('Organic Chemistry', 'Study of organic compounds and reactions', 'T004'),
('Engineering Design', 'Principles of engineering and design', 'T005');

-- Insert sample Enrollments data
INSERT INTO enrollments (student_id, course_id, score, grade) VALUES
('S001', 1, 85.50, 'B+'),
('S001', 2, 92.00, 'A-'),
('S002', 1, 78.50, 'C+'),
('S003', 3, 95.00, 'A'),
('S004', 4, 88.75, 'B+'),
('S005', 5, 91.25, 'A-');