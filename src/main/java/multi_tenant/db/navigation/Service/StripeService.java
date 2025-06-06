package multi_tenant.db.navigation.Service;

import java.math.BigDecimal;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;

import multi_tenant.db.navigation.DTO.PaymentIntentDTO;

@Service
public class StripeService {
	private static final Logger logger = Logger.getLogger(StripeService.class.getName());
	
	
	@Value("${stripe.api.global-secret-key}")
	private String globalApiKey;
	
	 public String getStripeApiKey() {
	        return globalApiKey;
	    }
	  public RequestOptions createRequestOptions() {
		  String apiKey = getStripeApiKey();
		  return RequestOptions.builder()
		            .setApiKey(apiKey) 
		            .build();
	  }
	
	public PaymentIntentDTO createPaymentIntent(BigDecimal amount, String email, String paymentType, int count) throws StripeException {		
		 
		 RequestOptions requestOptions = createRequestOptions();
		
		// removes the decimal part if there are only trailing .00
		long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValueExact();
		
		PaymentIntent paymentIntent =  PaymentIntent.create(
		PaymentIntentCreateParams.builder()
				.setAmount(amountInCents)
				.setCurrency("aud")
				.setReceiptEmail(email)
				.putMetadata("payment_type", paymentType)
				.putMetadata("count", String.valueOf(count)) 
				.setAutomaticPaymentMethods( //TESTING WITHOUT USING FE
			            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
			                .setEnabled(true)  // Bß║¡t chß║┐ ─æß╗Ö tß╗▒ ─æß╗Öng chß╗ìn phã░ãíng thß╗®c thanh to├ín
			                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER) // Kh├┤ng cho ph├®p redirect
			                .build()
			        )
				.build(), 
				requestOptions);
		
		return new PaymentIntentDTO(paymentIntent);
	}
	
}
