INSERT IGNORE INTO users (id, name, username, email, password, role, banned) 
VALUES 
(1, 'Admin User', 'admin', 'admin@library.com', 'admin123', 'ADMIN', false), 
(2, 'Jane Student', 'student', 'student@library.com', 'student123', 'STUDENT', false);