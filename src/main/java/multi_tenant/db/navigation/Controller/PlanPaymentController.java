package multi_tenant.db.navigation.Controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.validation.Valid;
import multi_tenant.db.navigation.DTO.PaymentIntentDTO;
import multi_tenant.db.navigation.DTO.SubPlanRequest;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.SubPlanDetail;
import multi_tenant.db.navigation.Entity.Global.SubscriptionPlan;
import multi_tenant.db.navigation.Exception.NoDataFoundException;
import multi_tenant.db.navigation.Service.OwnerService;
import multi_tenant.db.navigation.Service.PaymentService;
import multi_tenant.db.navigation.Service.StripeService;
import multi_tenant.db.navigation.Service.SubPlanDetailService;
import multi_tenant.db.navigation.Service.SubscriptionPlanService;

@RestController
@RequestMapping("/api/owner")
public class PlanPaymentController {
	@Autowired
	private SubscriptionPlanService subPlanService;
	@Autowired
	private SubPlanDetailService subPlanDetailService;
	
	@Autowired
	private OwnerService ownerService;

	@Autowired
	private StripeService stripeService;
	
	@Autowired
	private PaymentService paymentService;	

	private final static Logger logger = LoggerFactory.getLogger(PlanPaymentController.class);

	@PostMapping("/create-plan-payment")
	public ResponseEntity<Object> createPlanPayment(@Valid @RequestBody SubPlanRequest request, Principal principal) {
		try {
			//check owner current plan -> later
			
			Owner owner = ownerService.getOwnerByEmail(principal.getName());
			
			SubscriptionPlan plan = subPlanService.getPlanById(request.getSubPlanId());
			
			//calculate total plan fee
			BigDecimal totalPlanFee = subPlanService.calculatePlanFee(request, plan);
			
			//create stripe paymentIntent
			PaymentIntentDTO paymentIntentDTO = stripeService.createPaymentIntent(totalPlanFee, principal.getName(), "First subscription payment.");
			
			//save to Subsciption_plan_detail
			SubPlanDetail planDetail = subPlanDetailService.saveInitialPlanDetail(request, owner, plan);
			
			//save to payment and payment detail
			paymentService.saveInitPayment(owner, planDetail, totalPlanFee, paymentIntentDTO.getId(), request, plan);
			
			return new ResponseEntity<>(Map.of("PaymentIntentDTO", paymentIntentDTO), HttpStatus.OK);
			
		} catch (StripeException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);		
			
		}catch(NoDataFoundException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		}catch(IllegalStateException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		}
		
	}
}
