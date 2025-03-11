
package multi_tenant.db.navigation.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import multi_tenant.db.navigation.DTO.RegisterRequest;
import multi_tenant.db.navigation.DTO.ResetPasswordRequest;
import multi_tenant.db.navigation.JWT.JwtTokenProvider;
import multi_tenant.db.navigation.Service.AuthService;
import multi_tenant.db.navigation.Service.CustomUserDetailsService;
import multi_tenant.db.navigation.Service.RegisterService;
import multi_tenant.db.navigation.Service.ResetPasswordService;

@RestController
@RequestMapping("api/")
public class AuthController {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RegisterService registerService;

	@Autowired
	private ResetPasswordService resetPasswordService;
	
	@Autowired
	private AuthService authService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestParam String email, @RequestParam String password) {
		try {
			System.out.println("hey");
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));
			List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
			System.out.println("login roles" + roles.toString());
			String accessToken = jwtTokenProvider.generateAccessToken(email, roles);
			String refreshToken = jwtTokenProvider.generateRefreshToken(email, roles);

			logger.info("Successfully login and generate tokens for: {}", email);
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Login successfully.");
			response.put("accessToken", accessToken);
			response.put("refreshToken", refreshToken);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>(Map.of("error", "Invalid Username or Email."), HttpStatus.BAD_REQUEST);
		}
	}

	/*
	 * DONE = DTO: registerBody interceptor: connect to correct db jwt check role -
	 * Principal create new user send email to change password password: -
	 * reset-token & expired token -
	 **/

	@PostMapping("global/register")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN)")
	public ResponseEntity<Object> registerGlobal(@RequestBody RegisterRequest request, Principal principal,
			HttpServletRequest httpRequest) {
		
		String role = authService.getCurrentUserRole(principal);		
		if(role.equals("ADMIN") && !request.getUserType().equalsIgnoreCase("OWNER")){
			return new ResponseEntity<>(Map.of("error", "Admin (Developer) can only register Owner."), HttpStatus.UNAUTHORIZED);
		}
		
		try {
			registerService.registerNewUser(request, principal, null);
			return new ResponseEntity<>(Map.of("message", "Please check your email to reset password."), HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	

	@PostMapping("admin/register")
	@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
	public ResponseEntity<Object> registerTenant(@RequestBody RegisterRequest request, Principal principal,
			HttpServletRequest httpRequest) {
		//fe will attach userType:USER to DTO, not showing on screen
		String shopName = httpRequest.getHeader("shop-name");
		if(!"USER".equalsIgnoreCase(request.getUserType())) {
			return new ResponseEntity<>(Map.of("error", "Only 'USER' type is allowed for tenant registration"), HttpStatus.UNAUTHORIZED);
		}
		
		try {
			registerService.registerNewUser(request, principal, shopName);
			return new ResponseEntity<>(Map.of("message", "Please check your email to reset password."), HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request,
			HttpServletRequest httpRequest) {
		String shopName = httpRequest.getHeader("shop-name");
		String globalUser = httpRequest.getHeader("global-user");
		try {
			resetPasswordService.resetUserPassword(request.getToken(), request.getNewPassword(), shopName, globalUser);
			return new ResponseEntity<>(Map.of("message", "Successfully reset new password."), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
		}

	}

}
