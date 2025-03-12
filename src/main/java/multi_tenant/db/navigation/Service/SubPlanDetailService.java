package multi_tenant.db.navigation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional; 
import multi_tenant.db.navigation.DTO.SubPlanRequest;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.SubPlanDetail;
import multi_tenant.db.navigation.Entity.Global.SubscriptionPlan;
import multi_tenant.db.navigation.Exception.NoDataFoundException;
import multi_tenant.db.navigation.Repository.Global.SubPlanDetailRepository;

@Service
public class SubPlanDetailService {

	@Autowired
	private SubPlanDetailRepository subPlanDetailRepository;
	
	@Autowired
	private SubscriptionPlanService subPlanService;  
	
	public SubPlanDetail getSubPlanDetailById(long id) {
		return subPlanDetailRepository.findById(id)
				.orElseThrow(() -> new NoDataFoundException("No plan details found for this id: " + id));
	}
	
	@Transactional(transactionManager = "globalTransactionManager")
	public SubPlanDetail saveInitialPlanDetail(SubPlanRequest request, Owner owner, SubscriptionPlan plan) {		
		SubPlanDetail planDetail = new SubPlanDetail();		
		planDetail.setOwner(owner);
		planDetail.setSubscriptionPlan(plan);
		planDetail.setAdditionalTenantCount(request.getAdditionalTenantCount());	
		planDetail.setMaxTenant(plan.getTenantLimit() + request.getAdditionalTenantCount());
		planDetail.setAdditionalAdminCount(request.getAdditionalAdminCount());
		planDetail.setStatus(SubPlanDetail.PlanDetailStatus.INACTIVE);
			
		return subPlanDetailRepository.save(planDetail);		
	}
	
}
