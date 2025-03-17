ALTER TABLE tenant_activity_log 
DROP FOREIGN KEY fk_tenant_activity_log_tenant;

ALTER TABLE tenant_activity_log 
MODIFY COLUMN tenant_id BIGINT NULL;