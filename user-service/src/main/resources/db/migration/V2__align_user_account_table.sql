RENAME TABLE `users` TO `user_account`;

ALTER TABLE `user_account`
    MODIFY COLUMN `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    CHANGE COLUMN `created_at` `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    CHANGE COLUMN `updated_at` `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modify time',
    COMMENT = 'user account table';
