package multi_tenant.db.navigation.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import multi_tenant.db.navigation.DTO.StripePaymentType;
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
	

	public SubPlanDetail getSubPlanDetailById(long id) {
		return subPlanDetailRepository.findById(id)
				.orElseThrow(() -> new NoDataFoundException("No plan details found for this id: " + id));
	}
	
	public SubPlanDetail getLatestPlanDetailByOwner(Owner owner) {
		return subPlanDetailRepository.findTopByOwnerOrderBySubscriptionStartDesc(owner)
				.orElseThrow(() -> new NoDataFoundException ("No Subscription Plan Detail found."));
	}

	// initial saving data to the table, Status: INACTIVE
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

	// change from INACTIVE to ACTIVE
	public void activateSubscriptionPlan(long planDetailId) {
		SubPlanDetail planDetail = getSubPlanDetailById(planDetailId);

		// update plan from INACTIVE to ACTIVE when payment success
		planDetail.setStatus(SubPlanDetail.PlanDetailStatus.ACTIVE);
		planDetail.setSubscriptionStart(LocalDateTime.now());

		subPlanDetailRepository.save(planDetail);
	}

	// change admin or tenant count
	public void updateAdditionalCount(long planDetailId, StripePaymentType paymentType, int count) {
		SubPlanDetail planDetail = getSubPlanDetailById(planDetailId);
		//ENUM is a SINGLETON, 1 object 1 reference
		if (paymentType == StripePaymentType.ADDITIONAL_ADMIN) {
			planDetail.setAdditionalAdminCount(planDetail.getAdditionalAdminCount() + count);
		}
		if (paymentType == StripePaymentType.ADDITIONAL_TENANT) {
			planDetail.setAdditionalTenantCount(planDetail.getAdditionalTenantCount() + count);		}
	}

}
