DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       email VARCHAR(100),
                       first_name VARCHAR(50),
                       last_name VARCHAR(50)
);

INSERT INTO users (username, password, role, email, first_name, last_name) VALUES
-- обычные пользователи (пароль: password1, password2, password3, password4)
('user1', '$2a$10$fDgLgClLQrO/0Jb6LK2BEeckIgaCcGk7KjFFma4vdceVpBEJc5cBC', 'ROLE_USER', 'useremail1@example.com', 'Ivan', 'Petrov'),
('user2', '$2a$10$VeuQigyJLcmlbQE6g/49fePi.v4/AlGcDuFSS.SZW6/UxZB6ZU3ti', 'ROLE_USER', 'useremail2@example.com', 'Petr', 'Ivanov'),
('user3', '$2a$10$kjinH3e0RuoMUAXU6WxQ/.DsD4F16xz9QpNNsmOulipKlzFr8l4e6', 'ROLE_USER', 'useremail3@example.com', 'Svetlana', 'Sidorova'),
('user4', '$2a$10$nM/kefOD4BU73HL1JkrLwuiSwwTKfffuGG2HJf39CKW4QjUoHHR..', 'ROLE_USER', 'useremail4@example.com', 'Olga', 'Kuznetsova'),

-- администраторы (пароль: adminpass1, adminpass2, adminpass3, adminpass4)
('admin1', '$2a$10$WdynO8RrnxIxUr0TO3/R3.NSyz9fj9Qj9sDum6RWwiCNhGvoLl0QG', 'ROLE_ADMIN', 'adminemail1@example.com', 'Olga', 'Ivanova'),
('admin2', '$2a$10$pcNy8qRWlLmwrcjXHd2SvOtWC3JTsmObJXAnaXLkvxXi6jTYTGS9u', 'ROLE_ADMIN', 'adminemail2@example.com', 'Sergey', 'Petrov'),
('admin3', '$2a$10$HVVEg.Sx/TylD6bI.0KkBu5tbVaNa8gooS7Oh3aLUAqY8RuAZNSBi', 'ROLE_ADMIN', 'adminemail3@example.com', 'Anna', 'Smirnova'),
('admin4', '$2a$10$STGR4jRMCMzn1jPq3WqLXO2zYGme5FbE7tCrKS1tu4TJnu91MVkaS', 'ROLE_ADMIN', 'adminemail4@example.com', 'Mikhail', 'Volkov');
