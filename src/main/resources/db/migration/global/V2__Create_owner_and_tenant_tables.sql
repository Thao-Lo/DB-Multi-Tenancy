-- table: owners
CREATE TABLE `db_navigation_global_multi_tenant`.`owners` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `status` ENUM('ACTIVE', 'DISABLE') NOT NULL DEFAULT 'ACTIVE',
  `created_by` VARCHAR(100) NOT NULL,
  `updated_by` VARCHAR(100) NULL DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reset_token` VARCHAR(100) NULL DEFAULT NULL,
  `reset_token_expiry` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`),  
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE);
  
  -- table: tenants
  CREATE TABLE `db_navigation_global_multi_tenant`.`tenants` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `owner_id` BIGINT NOT NULL,
  `owner_role` ENUM('OWNER','ADMIN') NOT NULL DEFAULT 'OWNER',
  `name` VARCHAR(100) NOT NULL,
  `db_name` VARCHAR(100) NOT NULL,
  `current_admin_count` INT NOT NULL DEFAULT 0,
  `max_admin_count`INT NOT NULL DEFAULT 1,
  `status` ENUM('ACTIVE', 'DISABLE') NOT NULL DEFAULT 'ACTIVE',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
  UNIQUE INDEX `db_name_UNIQUE` (`db_name` ASC) VISIBLE,
  INDEX `fk_owner_id_idx` (`owner_id` ASC) VISIBLE,
  CONSTRAINT `fk_owner_id`
    FOREIGN KEY (`owner_id`)
    REFERENCES `db_navigation_global_multi_tenant`.`owners` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

   -- table: role - owner with default role for their tenants is SUPER_ADMIN
--CREATE TABLE `global_multi_tenant`.`owner_role` (
--  `owner_id` BIGINT NOT NULL,
--  `tenant_id` BIGINT NOT NULL,
--  `role` ENUM('SUPER_ADMIN', 'ADMIN') NOT NULL DEFAULT 'SUPER_ADMIN',
--  PRIMARY KEY (`owner_id`, `tenant_id`),  -- Composite Primary Key
--  INDEX `fk_owner_id_idx` (`owner_id` ASC) VISIBLE,
--  INDEX `fk_tenant_id_idx` (`tenant_id` ASC) VISIBLE,
--  CONSTRAINT `fk_role_owner_id`
--    FOREIGN KEY (`owner_id`)
--    REFERENCES `global_multi_tenant`.`owners` (`id`)
--    ON DELETE CASCADE
--    ON UPDATE CASCADE,
--  CONSTRAINT `fk_role_tenant_id`
--    FOREIGN KEY (`tenant_id`)
--    REFERENCES `global_multi_tenant`.`tenants` (`id`)
--    ON DELETE CASCADE
--    ON UPDATE CASCADE);
