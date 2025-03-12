package multi_tenant.db.navigation.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import multi_tenant.db.navigation.DTO.SubPlanRequest;
import multi_tenant.db.navigation.Entity.Global.SubscriptionPlan;
import multi_tenant.db.navigation.Exception.NoDataFoundException;
import multi_tenant.db.navigation.Repository.Global.SubscriptionPlanRepository;

@Service
public class SubscriptionPlanService {
	@Autowired
	private SubscriptionPlanRepository subPlanRepository;

	public List<SubscriptionPlan> getSubscriptionPlanList() {
		List<SubscriptionPlan> plans = subPlanRepository.findAll();
		if (plans.isEmpty()) {
			throw new NoDataFoundException("No subscription plans available");
		}
		return plans;
	}
	
	public SubscriptionPlan getPlanById(int id) {
		return subPlanRepository.findById(id)
				.orElseThrow(() -> new NoDataFoundException("No plan found."));
				
	}
	
	//calculate fee for 1st time subscribe the plan 
	public BigDecimal calculatePlanFee(SubPlanRequest request, SubscriptionPlan plan) {	
		
		//to avoid wrong input
		int additionalAdmins = Math.max(request.getAdditionalAdminCount(), 0);
		int additionalTenants = Math.max(request.getAdditionalTenantCount(), 0);
		
		//multiply, add
		BigDecimal baseCost = plan.getBaseCost();
		BigDecimal adminFee = BigDecimal.valueOf(additionalAdmins).multiply(plan.getAdditionalAmindFee());
		BigDecimal tenantFee = BigDecimal.valueOf(additionalTenants).multiply(plan.getAdditionalTenantFee());
		
		return baseCost.add(adminFee).add(tenantFee);
	}

}
