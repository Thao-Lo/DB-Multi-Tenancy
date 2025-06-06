package multi_tenant.db.navigation.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import multi_tenant.db.navigation.DTO.TenantLogDTO;
import multi_tenant.db.navigation.Entity.Global.TenantActivityLog;
import multi_tenant.db.navigation.Repository.Global.TenantActivityLogRepository;

@Service
public class TenantActivityLogService {
	@Autowired
	private TenantActivityLogRepository tenantActivityLogRepository;
	
	public void createTenantActivityLog(TenantLogDTO tenantLogDTO) {
		TenantActivityLog tenantActivityLog = new TenantActivityLog();
		tenantActivityLog.setOwner(tenantLogDTO.getOwner());
		tenantActivityLog.setTenantId(tenantLogDTO.getTenantId());		
		tenantActivityLog.setActionTypeId(tenantLogDTO.getActionTypeId());
		tenantActivityLog.setOldValue(tenantLogDTO.getOldValue());
		tenantActivityLog.setNewValue(tenantLogDTO.getNewValue());
		tenantActivityLog.setDetail(Map.of("message", tenantLogDTO.getMessage()));
		tenantActivityLogRepository.save(tenantActivityLog);
	}
	
}
