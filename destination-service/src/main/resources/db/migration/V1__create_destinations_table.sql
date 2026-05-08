CREATE TABLE `destinations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `name` VARCHAR(100) NOT NULL COMMENT 'destination name',
  `region_code` CHAR(6) DEFAULT NULL COMMENT 'GB/T 2260 region code',
  `address` VARCHAR(255) DEFAULT NULL COMMENT 'detail address',
  `summary` VARCHAR(255) DEFAULT NULL COMMENT 'summary',
  `description` TEXT DEFAULT NULL COMMENT 'description',
  `cover_image_url` VARCHAR(500) DEFAULT NULL COMMENT 'cover image url',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1 online, 0 offline',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='destinations table';
