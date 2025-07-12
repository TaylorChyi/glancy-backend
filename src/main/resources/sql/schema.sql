-- Database schema for Glancy Backend

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    avatar VARCHAR(255),
    phone VARCHAR(30),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    member BOOLEAN NOT NULL DEFAULT FALSE,
    createdAt DATETIME NOT NULL,
    lastLoginAt DATETIME
);

CREATE TABLE IF NOT EXISTS faqs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    answer TEXT NOT NULL,
    createdAt DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS search_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    term VARCHAR(100) NOT NULL,
    language VARCHAR(10) NOT NULL,
    createdAt DATETIME NOT NULL,
    CONSTRAINT fk_search_record_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS words (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    term VARCHAR(100) NOT NULL UNIQUE,
    language VARCHAR(10) NOT NULL,
    phonetic VARCHAR(100),
    example VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    createdAt DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS word_definitions (
    word_id BIGINT NOT NULL,
    definition TEXT NOT NULL,
    PRIMARY KEY (word_id, definition),
    CONSTRAINT fk_word_definition_word FOREIGN KEY (word_id) REFERENCES words(id)
);

CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    createdAt DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS third_party_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    externalId VARCHAR(100) NOT NULL,
    CONSTRAINT fk_third_party_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS login_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    deviceInfo VARCHAR(255) NOT NULL,
    loginTime DATETIME NOT NULL,
    CONSTRAINT fk_login_device_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    systemLevel BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT,
    createdAt DATETIME NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS alert_recipients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS traffic_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path VARCHAR(100) NOT NULL,
    ip VARCHAR(45),
    userAgent VARCHAR(255),
    createdAt DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS system_parameters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    value VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    theme VARCHAR(20) NOT NULL,
    systemLanguage VARCHAR(20) NOT NULL,
    searchLanguage VARCHAR(20) NOT NULL,
    CONSTRAINT fk_user_pref_user FOREIGN KEY (user_id) REFERENCES users(id)
);

