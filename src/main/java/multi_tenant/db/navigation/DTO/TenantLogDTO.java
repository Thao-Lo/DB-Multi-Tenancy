package multi_tenant.db.navigation.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.SubPlanDetail;
import multi_tenant.db.navigation.Entity.Global.Tenant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantLogDTO {
	private Owner owner;
	private long tenantId;
	private SubPlanDetail planDetail;
	private String oldValue;	
	private String newValue;
	private int actionTypeId;
	private String message;
}
