USE testing;


DROP TABLE StudentAttendance;
DROP TABLE StudentCourseGrades;
DROP TABLE StudentAssignmentGrades;
DROP TABLE StudentGPA;
DROP TABLE CourseAssignment;
DROP TABLE Courses;
DROP TABLE Assignments;
DROP TABLE Students;


CREATE TABLE Students(
	student_ID INTEGER PRIMARY KEY AUTO_INCREMENT,
	firstName  VARCHAR(255) NOT NULL,
	middleName VARCHAR(255) NOT NULL,
	lastName VARCHAR(255) NOT NULL,
        gradeLevel INTEGER NOT NULL,
        major VARCHAR(255)
        
);


CREATE TABLE StudentGPA (
	student_ID INTEGER NOT NULL,
	gradeLevel INTEGER NOT NULL,
        studentGPA FLOAT(3,2) DEFAULT 0.00,
        studentRank INTEGER,
        FOREIGN KEY (student_ID) REFERENCES Students(student_ID) ON DELETE CASCADE
        
);


CREATE TABLE Courses(
	course_ID INTEGER PRIMARY KEY AUTO_INCREMENT,
	courseSubject VARCHAR(255) NOT NULL,
        gradeLevel INTEGER NOT NULL,
        courseNumber INTEGER NOT NULL,
	semester VARCHAR(255) NOT NULL
        
);

CREATE TABLE Assignments(
	assignment_ID INTEGER PRIMARY KEY AUTO_INCREMENT,
	assignmentName VARCHAR(255) NOT NULL,
	assignmentType VARCHAR(255) NOT NULL,
	dueDate DATE NOT NULL,
	points FLOAT(5,2) NOT NULL,
        weight FLOAT(5,2) NOT NULL
);


CREATE TABLE StudentCourseGrades(
	student_ID INTEGER NOT NULL,
	course_ID INTEGER NOT NULL,
	grade FLOAT (5,2),
	FOREIGN KEY (student_ID) REFERENCES Students(student_ID) ON DELETE CASCADE,
	FOREIGN KEY (course_ID) REFERENCES Courses(course_ID) ON DELETE CASCADE
);

CREATE TABLE StudentAssignmentGrades(
	student_ID INTEGER NOT NULL,
	assignment_ID INTEGER NOT NULL,
	grade FLOAT (5,2),
	FOREIGN KEY (student_ID) REFERENCES Students(student_ID) ON DELETE CASCADE,
	FOREIGN KEY (assignment_ID) REFERENCES Assignments(assignment_ID) ON DELETE CASCADE
);


CREATE TABLE StudentAttendance(
	student_ID INTEGER NOT NULL,
	course_ID INTEGER NOT NULL,
	attendanceDate DATE NOT NULL,
	attendance INTEGER,
	FOREIGN KEY (student_ID) REFERENCES Students(student_ID) ON DELETE CASCADE,
	FOREIGN KEY (course_ID) REFERENCES Courses(course_ID) ON DELETE CASCADE
);


CREATE TABLE CourseAssignment(
	course_ID INTEGER NOT NULL,
        assignment_ID INTEGER NOT NULL,
	FOREIGN KEY (course_ID) REFERENCES Courses(course_ID) ON DELETE CASCADE,
	FOREIGN KEY (assignment_ID) REFERENCES Assignments(assignment_ID) ON DELETE CASCADE
);




