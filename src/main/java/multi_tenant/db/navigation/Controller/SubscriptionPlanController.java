package multi_tenant.db.navigation.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import multi_tenant.db.navigation.Entity.Global.SubscriptionPlan;
import multi_tenant.db.navigation.Exception.NoDataFoundException;
import multi_tenant.db.navigation.Service.SubscriptionPlanService;

@RestController
@RequestMapping("/api/owner")
public class SubscriptionPlanController {
	@Autowired
	private SubscriptionPlanService subPlanService;

	@GetMapping("/subscription-plans")
	public ResponseEntity<Object> getAllSubcriptionPlans() {
		try {
			List<SubscriptionPlan> plans = subPlanService.getSubscriptionPlanList();
			return ResponseEntity.ok(plans);

		} catch (NoDataFoundException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
}
