/*
 Navicat Premium Data Transfer

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3308
 Source Schema         : librarymanagement

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 24/04/2025 21:46:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `book_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `publish_date` date NULL DEFAULT NULL,
  `stock` int(11) NULL DEFAULT 0,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` enum('AVAILABLE','UNAVAILABLE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'AVAILABLE',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`book_id`) USING BTREE,
  UNIQUE INDEX `isbn`(`isbn` ASC) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_author`(`author` ASC) USING BTREE,
  INDEX `idx_isbn`(`isbn` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (1, 'The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Scribner', '1925-04-10', 10, 'Fiction', 'classic,american,novel', 'A novel about the mysterious millionaire Jay Gatsby and his obsession with the beautiful Daisy Buchanan.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (2, 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'HarperCollins', '1960-07-11', 8, 'Fiction', 'classic,american,southern', 'The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (3, '1984', 'George Orwell', '9780451524935', 'Signet Classic', '1949-06-08', 10, 'Fiction', 'dystopian,political,classic', 'A dystopian novel set in Airstrip One, a province of the superstate Oceania in a world of perpetual war.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-23 15:02:38');
INSERT INTO `books` VALUES (4, 'The Catcher in the Rye', 'J.D. Salinger', '9780316769488', 'Little, Brown and Company', '1951-07-16', 5, 'Fiction', 'classic,coming-of-age', 'The story of teenage protagonist Holden Caulfield and his experiences in New York City.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (5, 'Pride and Prejudice', 'Jane Austen', '9780141439518', 'Penguin Classics', '1813-01-28', 7, 'Fiction', 'classic,romance,british', 'A romantic novel of manners that follows the character development of Elizabeth Bennet.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (6, 'The Hobbit', 'J.R.R. Tolkien', '9780618260300', 'Houghton Mifflin', '1937-09-21', 15, 'Fantasy', 'adventure,classic,quest', 'A fantasy novel about the quest of Bilbo Baggins to win a share of the treasure guarded by the dragon, Smaug.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (7, 'Brave New World', 'Aldous Huxley', '9780060850524', 'Harper Perennial', '1932-10-05', 9, 'Fiction', 'dystopian,science-fiction,classic', 'A dystopian novel set in a futuristic World State, whose citizens are environmentally engineered.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (8, 'The Lord of the Rings', 'J.R.R. Tolkien', '9780618640157', 'Mariner Books', '1954-07-29', 6, 'Fantasy', 'epic,adventure,classic', 'An epic high-fantasy novel set in Middle-earth, a fictional world populated by humans and other beings.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (9, 'Animal Farm', 'George Orwell', '9780451526342', 'Signet Classics', '1945-08-17', 11, 'Fiction', 'political,allegory,classic', 'An allegorical novella that reflects events leading up to the Russian Revolution of 1917.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 11:20:08');
INSERT INTO `books` VALUES (10, 'The Da Vinci Code', 'Dan Brown', '9780307474278', 'Anchor', '2003-03-18', 3, 'Thriller', 'mystery,conspiracy,art', 'A mystery thriller novel that follows symbologist Robert Langdon as he investigates a murder in Paris.', 'AVAILABLE', '2025-04-22 11:20:08', '2025-04-22 05:08:43');
INSERT INTO `books` VALUES (12, 'c123456', 'xiao', '1234567891', 'c2', '2025-04-14', 1, 'cs', 'ds', '22', 'AVAILABLE', '2025-04-23 15:04:40', '2025-04-23 15:04:54');

-- ----------------------------
-- Table structure for borrow_records
-- ----------------------------
DROP TABLE IF EXISTS `borrow_records`;
CREATE TABLE `borrow_records`  (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `book_id` bigint(20) NOT NULL,
  `borrow_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `due_date` datetime NOT NULL,
  `return_date` datetime NULL DEFAULT NULL,
  `status` enum('BORROWED','RETURNED','OVERDUE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'BORROWED',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_due_date`(`due_date` ASC) USING BTREE,
  CONSTRAINT `borrow_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `borrow_records_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow_records
-- ----------------------------
INSERT INTO `borrow_records` VALUES (1, 3, 3, '2025-04-22 04:47:35', '2025-05-06 04:47:35', NULL, 'BORROWED', '2025-04-22 04:47:35', '2025-04-22 04:47:35');
INSERT INTO `borrow_records` VALUES (2, 3, 3, '2025-04-22 04:47:47', '2025-05-06 04:47:47', '2025-04-22 04:47:51', 'RETURNED', '2025-04-22 04:47:47', '2025-04-22 04:47:51');
INSERT INTO `borrow_records` VALUES (3, 3, 10, '2025-04-22 05:08:43', '2025-05-06 05:08:43', NULL, 'BORROWED', '2025-04-22 05:08:43', '2025-04-22 05:08:43');
INSERT INTO `borrow_records` VALUES (4, 4, 3, '2025-04-23 15:02:38', '2025-05-07 15:02:38', NULL, 'BORROWED', '2025-04-23 15:02:38', '2025-04-23 15:02:38');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` enum('ROLE_USER','ROLE_ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ROLE_USER',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (2, 'xiaowei', '$2a$10$.BK6lDPgIumtw577jPwKa.Vm3p6L4E1XGYNh5zJWdxnlfQw4kdzHC', '420632511@qq.com', '19978883579', 'ROLE_ADMIN', '2025-04-21 22:15:16', '2025-04-22 10:36:49');
INSERT INTO `users` VALUES (3, '420632511@qq.com', '$2a$10$Z59eI5AND5KCeuuLaxJ1UeX77J8qGogqxABa1WNWCwtLOC8CpaU8i', '420632311@qq.com', '19978883553', 'ROLE_USER', '2025-04-22 04:47:24', '2025-04-22 05:45:55');
INSERT INTO `users` VALUES (4, '1920', '$2a$10$wDGQUrOPZj5wUeKoWN4kqeUa7XaKZFz9ejieYDPSJZp7kVi07TmLy', '420@qq.com', '19978888888', 'ROLE_ADMIN', '2025-04-23 15:02:04', '2025-04-23 15:05:10');

SET FOREIGN_KEY_CHECKS = 1;
