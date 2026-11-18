-- ============================================================
--  Student Transport Fee & Route Management System
--  Database script (MySQL 8.x)
--  Made by: Mohd Dayan (PIET25CR034)
--
--  Run in MySQL Workbench:  File -> Open SQL Script -> Execute (lightning icon)
--  Safe to run more than once (uses IF NOT EXISTS / INSERT IGNORE).
-- ============================================================

CREATE DATABASE IF NOT EXISTS transport_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE transport_db;

-- ---------- 1. users (login) ----------
-- password is stored as a SHA-256 hash (64 hex characters), never as plain text
CREATE TABLE IF NOT EXISTS users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(30) NOT NULL UNIQUE,
    password  CHAR(64)    NOT NULL,
    role      ENUM('Admin','User') NOT NULL DEFAULT 'User'
);

-- ---------- 2. routes ----------
CREATE TABLE IF NOT EXISTS routes (
    route_id    INT AUTO_INCREMENT PRIMARY KEY,
    route_name  VARCHAR(50)  NOT NULL UNIQUE,
    source      VARCHAR(60)  NOT NULL,
    destination VARCHAR(60)  NOT NULL,
    stops       VARCHAR(255)
);

-- ---------- 3. buses (many buses can run on one route) ----------
CREATE TABLE IF NOT EXISTS buses (
    bus_id    INT AUTO_INCREMENT PRIMARY KEY,
    bus_no    VARCHAR(15) NOT NULL UNIQUE,
    capacity  INT NOT NULL,
    route_id  INT NULL,
    CONSTRAINT chk_bus_capacity CHECK (capacity BETWEEN 1 AND 120),
    CONSTRAINT fk_bus_route FOREIGN KEY (route_id) REFERENCES routes(route_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ---------- 4. students (each student is assigned one route) ----------
CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no    VARCHAR(20) NOT NULL UNIQUE,
    name       VARCHAR(50) NOT NULL,
    course     VARCHAR(50) NOT NULL,
    route_id   INT NULL,
    CONSTRAINT fk_student_route FOREIGN KEY (route_id) REFERENCES routes(route_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ---------- 5. transport_fees (a student can have many fee records) ----------
CREATE TABLE IF NOT EXISTS transport_fees (
    fee_id     INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    amount     DECIMAL(8,2) NOT NULL,
    due_date   DATE NOT NULL,
    status     ENUM('Pending','Paid') NOT NULL DEFAULT 'Pending',
    CONSTRAINT chk_fee_amount CHECK (amount > 0),
    CONSTRAINT uq_fee UNIQUE (student_id, due_date),
    CONSTRAINT fk_fee_student FOREIGN KEY (student_id) REFERENCES students(student_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================================================
--  Sample data
--  Logins:  admin / admin123  (Admin)      user / user123  (User)
-- ============================================================
INSERT IGNORE INTO users (username, password, role) VALUES
    ('admin', SHA2('admin123', 256), 'Admin'),
    ('user',  SHA2('user123', 256),  'User');

INSERT IGNORE INTO routes (route_name, source, destination, stops) VALUES
    ('Route A', 'Jaipur Railway Station', 'PIET Campus', 'Sindhi Camp, Ajmeri Gate, Sanganer'),
    ('Route B', 'Vaishali Nagar',         'PIET Campus', 'Gopalpura, Tonk Road, Durgapura'),
    ('Route C', 'Mansarovar',             'PIET Campus', 'Sodala, Jhotwara');

INSERT IGNORE INTO buses (bus_no, capacity, route_id) VALUES
    ('RJ14-2201', 50, (SELECT route_id FROM routes WHERE route_name = 'Route A')),
    ('RJ14-2202', 45, (SELECT route_id FROM routes WHERE route_name = 'Route B')),
    ('RJ14-2203', 40, (SELECT route_id FROM routes WHERE route_name = 'Route C'));

INSERT IGNORE INTO students (roll_no, name, course, route_id) VALUES
    ('PIET25CR034', 'Mohd Dayan',  'B.Tech CSE', (SELECT route_id FROM routes WHERE route_name = 'Route A')),
    ('PIET25CR045', 'Ayesha Khan', 'B.Tech CSE', (SELECT route_id FROM routes WHERE route_name = 'Route B')),
    ('PIET25CR050', 'Rohit Verma', 'B.Tech CSE', (SELECT route_id FROM routes WHERE route_name = 'Route A'));

INSERT IGNORE INTO transport_fees (student_id, amount, due_date, status) VALUES
    ((SELECT student_id FROM students WHERE roll_no = 'PIET25CR034'), 12000.00, '2026-12-10', 'Paid'),
    ((SELECT student_id FROM students WHERE roll_no = 'PIET25CR045'), 12000.00, '2026-12-10', 'Pending'),
    ((SELECT student_id FROM students WHERE roll_no = 'PIET25CR050'), 12000.00, '2026-12-10', 'Paid');
