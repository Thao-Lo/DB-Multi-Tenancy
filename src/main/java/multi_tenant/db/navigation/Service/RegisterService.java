package multi_tenant.db.navigation.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import multi_tenant.db.navigation.DTO.RegisterRequest;
import multi_tenant.db.navigation.Entity.BaseUser;
import multi_tenant.db.navigation.Entity.Global.Developer;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Tenant.User;
import multi_tenant.db.navigation.Repository.Global.DeveloperRepository;
import multi_tenant.db.navigation.Repository.Global.OwnerRepository;
import multi_tenant.db.navigation.Repository.Tenant.UserRepository;
import multi_tenant.db.navigation.Utils.MultiEmailService;

@Service
public class RegisterService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DeveloperRepository developerRepository;

	@Autowired
	private OwnerRepository ownerRepository;
	
	@Autowired
	private MultiEmailService multiEmailService;
	
	private static final String DEVELOPER = "DEVELOPER";
	private static final String OWNER = "OWNER";
	private static final String USER = "USER";

	public void registerNewUser(RegisterRequest request, String retristryEmail, String shopName) {	
		String userType = request.getUserType().toUpperCase();
		String email = request.getEmail();
		
		if(!Set.of(DEVELOPER, OWNER, USER).contains(userType)) {
			throw new IllegalArgumentException("Invalid User Type: " + request.getUserType());
		}
		String globalUser = userType.equalsIgnoreCase(USER) ? null : userType;
		String shopNameOrGlobalUser = (shopName == null) ? globalUser : shopName;
		boolean isEmailExist = switch(userType) {
		case DEVELOPER -> developerRepository.existsByEmail(email);
		case OWNER -> ownerRepository.existsByEmail(email);
		case USER -> userRepository.existsByEmail(email);
		default -> false;
		};
		if(isEmailExist) {
			throw new IllegalArgumentException("Email is already existed " + email);
		}
		boolean isDeveloper = (Set.of(DEVELOPER, OWNER).contains(userType));
		
		switch (userType) {
		case DEVELOPER:
			createUser(Developer.class, request, developerRepository, retristryEmail, isDeveloper, shopNameOrGlobalUser );
			break;
		case OWNER:
			createUser(Owner.class, request, ownerRepository, retristryEmail, isDeveloper, shopNameOrGlobalUser );
			break;

		case USER:
			createUser(User.class, request, userRepository, retristryEmail, isDeveloper, shopNameOrGlobalUser );
			break;
		}
	}

	public <T extends BaseUser> T createUser (Class<T> userType, RegisterRequest request, JpaRepository<T, Long> repository, String registryEmail, boolean isDeveloper, String shopNameOrGlobalUser) {
			
				try {
					T user = userType.getDeclaredConstructor().newInstance();
					user.setFirstName(request.getFirstName());
					user.setLastName(request.getLastName());
					user.setEmail(request.getEmail());
					user.setCreatedBy(registryEmail);
					user.setResetToken(UUID.randomUUID().toString());
					user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 1 hour
					
					T savedUser = repository.save(user);
					sendPasswordSetUpEmail(user.getEmail(), user.getResetToken(), isDeveloper, shopNameOrGlobalUser);
					return savedUser;	
				} catch (InstantiationException e) {
					throw new RuntimeException("Error creating user instance: " + e.getMessage(), e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Error creating user instance: " + e.getMessage(), e);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException("Error creating user instance: " + e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException("Error creating user instance: " + e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException("Error creating user instance: " + e.getMessage(), e);
				} catch (SecurityException e) {
					throw new RuntimeException("Error creating user instance: " + e.getMessage(), e);
				}catch (MessagingException e) {
				throw new RuntimeException("Error when registering new user. " + e.getMessage(), e);				
			}			
	}
	
	private void sendPasswordSetUpEmail(String email, String token, boolean isDeveloper, String shopNameOrGlobalUser) throws MessagingException {
	    String subject = "Reset Password";

	    String body = "<h3>Set Your New Password</h3>"
	    			+ "</br>"
	                + "<p>Please send the following API request to reset your password:</p>"
	                + "<pre>"
	                + "POST api/reset-password\n"
	                + "Headers:\n"
	                + (isDeveloper ? "- global-user: " + shopNameOrGlobalUser + "\n" : "- shop-name: "  + shopNameOrGlobalUser + "\n")
	                + "Content-Type: application/json\n\n"
	                + "Body:\n"
	                + "{\n"
	                + "  \"token\": \"" + token + "\",\n"
	                + "  \"newPassword\": \"your-new-password\"\n"
	                + "}"
	                + "</pre>"
	                + "<p><b>Note:</b> The token is valid for 1 hour.</p>";

	    multiEmailService.sendEmail(email, subject, body, isDeveloper);
	}


}
