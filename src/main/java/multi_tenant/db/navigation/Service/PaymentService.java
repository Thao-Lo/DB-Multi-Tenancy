package multi_tenant.db.navigation.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional; 
import multi_tenant.db.navigation.DTO.SubPlanRequest;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.Payment;
import multi_tenant.db.navigation.Entity.Global.PaymentDetail;
import multi_tenant.db.navigation.Entity.Global.SubPlanDetail;
import multi_tenant.db.navigation.Entity.Global.SubscriptionPlan;
import multi_tenant.db.navigation.Repository.Global.PaymentDetailRepository;
import multi_tenant.db.navigation.Repository.Global.PaymentRepository;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentDetailService paymentDetailService;
	
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;
	
	@Transactional(transactionManager = "globalTransactionManager")
	public void saveInitPayment(Owner owner, SubPlanDetail planDetail, BigDecimal amount, String paymentIntentId,
			SubPlanRequest request, SubscriptionPlan plan) {
		// save to payment table
		Payment payment = new Payment();
		payment.setOwner(owner);
		payment.setSubPlanDetail(planDetail);
		payment.setAmount(amount);
		payment.setPaymentIntentId(paymentIntentId);
		payment.setPaymentStatus(Payment.PaymentStatus.PENDING);

		payment = paymentRepository.save(payment);
		
		List<PaymentDetail> paymentDetails = new ArrayList<>();
		// each payment break down to different type
		paymentDetails.add(paymentDetailService.createPaymentDetail(payment, 1,  plan.getBaseCost(), 1, plan.getBaseCost()));

		if (request.getAdditionalAdminCount() > 0) {
			BigDecimal additionalAdminAmount = plan.getAdditionalAmindFee()
					.multiply(BigDecimal.valueOf(request.getAdditionalAdminCount()));
			
			paymentDetails.add(paymentDetailService.createPaymentDetail(
					payment, 2, additionalAdminAmount,
					request.getAdditionalAdminCount(), plan.getAdditionalAmindFee()));

		}
		if (request.getAdditionalTenantCount() > 0) {
			BigDecimal additionalTenantAmount = plan.getAdditionalTenantFee()
					.multiply(BigDecimal.valueOf(request.getAdditionalTenantCount()));
			
			
			paymentDetails.add(paymentDetailService.createPaymentDetail(
					payment, 3, additionalTenantAmount,
					request.getAdditionalTenantCount(), plan.getAdditionalTenantFee()));
		}
		if (paymentDetails.isEmpty()) {
		    throw new IllegalStateException("No payment details found to save.");
		}
		paymentDetailRepository.saveAll(paymentDetails);
	}
}
