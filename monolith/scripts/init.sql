CREATE DATABASE IF NOT EXISTS boardgame_club CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs;
USE boardgame_club;
CREATE TABLE IF NOT EXISTS activities (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(100) NOT NULL,
 location VARCHAR(120) NOT NULL,
 starts_at DATETIME(6) NOT NULL,
 capacity INT NOT NULL,
 CONSTRAINT ck_activity_capacity CHECK (capacity BETWEEN 1 AND 200)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS registrations (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 activity_id BIGINT NOT NULL,
 member_no VARCHAR(30) NOT NULL,
 member_name VARCHAR(50) NOT NULL,
 registered_at DATETIME(6) NOT NULL,
 CONSTRAINT uk_activity_member UNIQUE (activity_id, member_no),
 CONSTRAINT fk_registration_activity FOREIGN KEY (activity_id) REFERENCES activities(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS courses (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 code VARCHAR(30) NOT NULL UNIQUE,
 name VARCHAR(100) NOT NULL
) ENGINE=InnoDB;
INSERT INTO courses(code, name) VALUES ('MS04', '微服务架构与实践')
ON DUPLICATE KEY UPDATE name = VALUES(name);
