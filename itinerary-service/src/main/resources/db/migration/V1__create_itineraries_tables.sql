CREATE TABLE `itineraries` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `user_id` BIGINT NOT NULL COMMENT 'owner user id',
  `title` VARCHAR(100) NOT NULL COMMENT 'itinerary title',
  `description` VARCHAR(500) DEFAULT NULL COMMENT 'itinerary description',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='itineraries table';

CREATE TABLE `itinerary_destinations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `itinerary_id` BIGINT NOT NULL COMMENT 'itinerary id',
  `destination_id` BIGINT NOT NULL COMMENT 'destination id',
  `sort_order` INT NOT NULL COMMENT 'display order',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_itinerary_destination` (`itinerary_id`, `destination_id`),
  KEY `idx_itinerary_id` (`itinerary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='itinerary destinations table';
