
package multi_tenant.db.navigation.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import multi_tenant.db.navigation.JWT.JwtTokenProvider;
import multi_tenant.db.navigation.Service.CustomUserDetailsService;

@RestController
@RequestMapping("api/")
public class AuthController {	

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestParam String email, @RequestParam String password) {	
		try {
			System.out.println("hey");
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(email, password));
			List<String> roles = authentication.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
			System.out.println("login roles" + roles.toString());
			String accessToken = jwtTokenProvider.generateAccessToken(email, roles);
	        String refreshToken = jwtTokenProvider.generateRefreshToken(email, roles);
	        
	        Map<String, Object> response = new HashMap<>();
			response.put("message", "Login successfully.");
			response.put("accessToken", accessToken);
			response.put("refreshToken", refreshToken);			
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(BadCredentialsException e){
			return new ResponseEntity<>(Map.of("error", "Invalid Username or Email."), HttpStatus.BAD_REQUEST);
		}
	}
	
}
