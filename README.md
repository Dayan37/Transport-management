# Student Transport Fee & Route Management System

**Made by:** Mohd Dayan (PIET25CR034)
**Tech stack:** Java, Java Swing, JDBC, MySQL, IntelliJ IDEA

A desktop management system for a college transport office: students, routes, buses and
transport fees, with Admin / User login.

---

## 1. Setup (one time)

### a) Create the database
1. Open **MySQL Workbench** and connect to your local server.
2. `File -> Open SQL Script...` -> choose `database/schema.sql` -> click the lightning icon (Execute).
   This creates the database `transport_db`, all tables, and sample data.

### b) Add the MySQL JDBC driver
1. Download **MySQL Connector/J** (Platform Independent, ZIP) from https://dev.mysql.com/downloads/connector/j/
2. Copy the file `mysql-connector-j-x.x.x.jar` into the `lib/` folder of this project.
3. In IntelliJ: `File -> Project Structure -> Libraries -> + -> Java` -> select the jar in `lib/` -> OK.

### c) Set your MySQL password
Open `db.properties` in the project folder and put your MySQL user and password:

```
db.url=jdbc:mysql://localhost:3306/transport_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_PASSWORD
```

> Do not push your real password to GitHub. Change `db.password` back to a placeholder before `git push`.

### d) Run
1. `File -> Open` -> select this project folder. Right-click `src` -> `Mark Directory as -> Sources Root`.
2. Open `src/Main.java` and press the green Run button.

(Without IntelliJ: `./run.sh` on Linux/Mac or `run.bat` on Windows - both need the jar in `lib/`.)

## 2. Login accounts

| Username | Password   | Role  | Can do                                              |
|----------|------------|-------|-----------------------------------------------------|
| admin    | admin123   | Admin | everything (add, update, delete, view)              |
| user     | user123    | User  | view/search records, export CSV, change own password |

Passwords are stored as SHA-256 hashes in MySQL, never as plain text.

## 3. Project structure

```
src/
  Main.java            start of the program -> Login
  Login.java           authentication against the users table, role check
  Dashboard.java       overview counts (from MySQL) + buttons to all modules
  AddStudent.java      add student, assign route
  RouteBus.java        add route; add bus and attach it to a route
  TransportFee.java    record a fee for a student
  SearchView.java      view all records (JTable), live search, sort, export CSV
  UpdateDelete.java    search, edit (UPDATE) and delete (DELETE with confirmation)
                       for Students, Routes, Buses and Fee records
  ChangePassword.java  verify old password, set new one
  BaseFrame.java       common Menu Bar + Toolbar for all screens after login
  DBConnection.java    JDBC connection + query()/update() helpers (PreparedStatement)
  StudentDAO / RouteDAO / FeeDAO / UserDAO   all SQL lives here (modular code)
  Validator.java       form validation rules
  Util.java            SHA-256 hashing, message boxes, friendly database error messages
  Session.java         who is logged in
  Item.java            id + label holder for combo boxes
database/schema.sql    database script (tables, keys, sample data)
db.properties          MySQL url / user / password
```

## 4. Database design

```
routes (route_id PK, route_name UNIQUE, source, destination, stops)
   |1                                   |1
   |                                    |
   *buses (bus_id PK, bus_no UNIQUE, capacity 1-120, route_id FK)
   *students (student_id PK, roll_no UNIQUE, name, course, route_id FK)
                    |1
                    *transport_fees (fee_id PK, student_id FK, amount > 0, due_date, status Pending/Paid,
                                     UNIQUE(student_id, due_date))
users (user_id PK, username UNIQUE, password SHA-256, role Admin/User)
```

Foreign keys: deleting a route keeps its students/buses (route becomes empty, `ON DELETE SET NULL`);
deleting a student also deletes that student's fee records (`ON DELETE CASCADE`).

## 5. Common modules checklist

| # | Module                  | Where                                                              |
|---|-------------------------|--------------------------------------------------------------------|
| 1 | Login (Admin/User)      | `Login.java`, `UserDAO.authenticate`                               |
| 2 | Dashboard               | `Dashboard.java`                                                   |
| 3 | Add Record              | `AddStudent`, `RouteBus`, `TransportFee`                           |
| 4 | Update Record           | `UpdateDelete` (search -> select -> edit form -> UPDATE)           |
| 5 | Delete Record           | `UpdateDelete` (confirmation dialog -> DELETE)                     |
| 6 | View All Records JTable | `SearchView` (sortable, scrollable, live search)                   |
| 7 | MySQL connectivity JDBC | `DBConnection.java` + DAO classes                                  |
| 8 | Form validation         | `Validator.java` (required, formats, ranges, dates, duplicates)    |
| 9 | Menu bar & toolbar      | `BaseFrame.java` (File / Records / Help menus + toolbar, shortcuts)|
| 10| Logout                  | menu, toolbar and dashboard button                                 |
| 11| Password change         | `ChangePassword.java`, `UserDAO.changePassword`                    |

Keyboard shortcuts (Ctrl on Windows/Linux, Cmd on Mac): D dashboard, N add student, B route/bus,
E fee, R view all, U update/delete, P change password, L logout, Q exit.

## 6. Viva notes
- Every SQL statement uses `PreparedStatement` with `?` parameters -> protects against SQL injection.
- All errors are caught with `try/catch (SQLException)` and shown by `Util.dbError()` as a readable
  message (duplicate record, cannot delete, cannot connect, etc.).
- Database code is separated from screens (DAO classes), so the screens stay short and readable.
- A normal `User` has the Add/Update/Delete menu items, toolbar buttons and pages disabled.

## 7. Putting the project on GitHub (required for the final demo)
```
git init
git add .
git commit -m "Student Transport Fee & Route Management System"
git branch -M main
git remote add origin https://github.com/<your-username>/<repo-name>.git
git push -u origin main
```
