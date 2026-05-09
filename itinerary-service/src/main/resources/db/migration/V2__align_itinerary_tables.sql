RENAME TABLE `itineraries` TO `itinerary`;

ALTER TABLE `itinerary`
    MODIFY COLUMN `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    MODIFY COLUMN `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'owner user id',
    CHANGE COLUMN `created_at` `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    CHANGE COLUMN `updated_at` `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modify time',
    COMMENT = 'itinerary table';

RENAME TABLE `itinerary_destinations` TO `itinerary_destination`;

ALTER TABLE `itinerary_destination`
    MODIFY COLUMN `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    MODIFY COLUMN `itinerary_id` BIGINT UNSIGNED NOT NULL COMMENT 'itinerary id',
    MODIFY COLUMN `destination_id` BIGINT UNSIGNED NOT NULL COMMENT 'destination id',
    MODIFY COLUMN `sort_order` INT UNSIGNED NOT NULL COMMENT 'display order',
    CHANGE COLUMN `created_at` `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    COMMENT = 'itinerary destination table';
