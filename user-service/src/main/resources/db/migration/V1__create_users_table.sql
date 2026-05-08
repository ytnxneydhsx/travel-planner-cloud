CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `username` VARCHAR(50) NOT NULL COMMENT 'username',
  `password` VARCHAR(100) NOT NULL COMMENT 'encoded password',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT 'nickname',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1 enabled, 0 disabled',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='users table';
