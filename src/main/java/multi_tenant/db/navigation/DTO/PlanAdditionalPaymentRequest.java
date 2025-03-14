package multi_tenant.db.navigation.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.SubPlanDetail;
import multi_tenant.db.navigation.Entity.Global.SubscriptionPlan;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanAdditionalPaymentRequest {
	
	private Owner owner;
	private SubscriptionPlan plan;
	private SubPlanDetail planDetail;
	private String additionalType;
	private int count;
	private int remainingDays;
	private BigDecimal amount;
	private String paymentIntentId;
}




