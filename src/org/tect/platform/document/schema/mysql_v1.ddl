create table if not exists `version` (`version` INTEGER NULL);
INSERT INTO `version` (version)  VALUES (1);
create table if not exists `document` (`id` INTEGER NULL PRIMARY KEY, `type` VARCHAR(255) NULL, `attribute_id_seed` INTEGER NULL);
create table if not exists `attribute` (`id` INTEGER NULL PRIMARY KEY, `type` VARCHAR(255) NULL, `document_id` INTEGER NULL, `attribute_id` INTEGER NULL, `name` VARCHAR(255) NULL, `text` TEXT NULL, `number` BIGINT NULL, `decimal` DECIMAL NULL, `date` TIMESTAMP NULL, `bool` BOOL NULL, `group_ids` VARCHAR(255) NULL);