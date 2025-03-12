CREATE TABLE `db_navigation_global_multi_tenant`.`subscription_plan` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `tenant_limit` INT NOT NULL,
  `admin_limit_per_tenant` INT NOT NULL,
  `base_cost` DECIMAL(10,2) NOT NULL,
  `additional_tenant_fee` DECIMAL(10,2) NOT NULL,
  `additional_admin_fee` DECIMAL(10,2) NOT NULL,
  `billing_cycle` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_subscription_plan_name` (`name` ASC) VISIBLE);
  
INSERT INTO `db_navigation_global_multi_tenant`.`subscription_plan` (`id`, `name`, `tenant_limit`, `admin_limit_per_tenant`, `base_cost`, `additional_tenant_fee`, `additional_admin_fee`, `billing_cycle`) VALUES ('1', 'Basic', '1', '1', '39', '29', '10', 'monthly');
INSERT INTO `db_navigation_global_multi_tenant`.`subscription_plan` (`id`, `name`, `tenant_limit`, `admin_limit_per_tenant`, `base_cost`, `additional_tenant_fee`, `additional_admin_fee`, `billing_cycle`) VALUES ('2', 'Business', '3', '1', '89', '29', '10', 'monthly');
INSERT INTO `db_navigation_global_multi_tenant`.`subscription_plan` (`id`, `name`, `tenant_limit`, `admin_limit_per_tenant`, `base_cost`, `additional_tenant_fee`, `additional_admin_fee`, `billing_cycle`) VALUES ('3', 'Enterprise', '10', '1', '279', '29', '10', 'monthly');
