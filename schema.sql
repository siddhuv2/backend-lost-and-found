-- SQL Setup Script for College Lost and Found Portal
-- Database: MySQL 8+

CREATE DATABASE IF NOT EXISTS college_lost_and_found
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE college_lost_and_found;

-- Tables will be automatically created and kept updated by Hibernate (spring.jpa.hibernate.ddl-auto=update)
-- Optional manual DDL reference:
/*
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    college_id_number VARCHAR(50) NOT NULL,
    department VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(60) NOT NULL,
    description TEXT NOT NULL,
    campus_location VARCHAR(150) NOT NULL,
    incident_date DATE NOT NULL,
    additional_details TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_items_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    claimant_id BIGINT NOT NULL,
    explanation TEXT NOT NULL,
    identifying_details TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    response_note TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_claims_item FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_claims_claimant FOREIGN KEY (claimant_id) REFERENCES users (id) ON DELETE CASCADE
);
*/
