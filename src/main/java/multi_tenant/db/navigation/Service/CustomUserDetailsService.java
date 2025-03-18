package multi_tenant.db.navigation.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import multi_tenant.db.navigation.Entity.Global.Developer;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.Tenant;
import multi_tenant.db.navigation.Entity.Tenant.User;
import multi_tenant.db.navigation.Utils.TenantContext;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	@Autowired
	private UserService userService;
	
	@Autowired
	private OwnerService ownerService;
	
	@Autowired
	private DeveloperService developerService;	

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {		
		
		String globalUser = TenantContext.getCurrentGlobalUserType(); // either OWNER or DEVELOPER
		//from Context
		Object user = findUserByEmail(email, (globalUser != null) ? globalUser : "USER");			
		
//		if(globalUser != null) {
//			 user = findUserByEmail(email, globalUser);			
//		}else {
//			System.out.println("user login");
//			 user = findUserByEmail(email, "USER"); //WRONG HERE, lazy fetching
//			 System.out.println("user");
//		}
		if (user == null) {
			throw new UsernameNotFoundException("User not found for email: " + email);
		}
		
		return new  org.springframework.security.core.userdetails.User(
				email, getPasswordFromUser(user), getRolesFromUser(user));
	}
	
	  private Object findUserByEmail(String email, String userType) {
			return switch (userType) {
			case "OWNER" -> ownerService.getOwnerByEmail(email);			
			case "DEVELOPER" -> developerService.getDeveloperByEmail(email);				
			case "USER" ->  userService.getUserByEmail(email);				
			default -> null; 
			};
	  }
	  
	  private String getPasswordFromUser(Object user) {
		  if( user instanceof Owner) return ((Owner) user).getPassword();
		  if( user instanceof Developer) return ((Developer) user).getPassword();
		  if( user instanceof User) return ((User) user).getPassword();
		  return null;
	  }
	  
	  
	  private List<GrantedAuthority> getRolesFromUser(Object user){
		  if (user instanceof Owner) {
			 return Arrays.stream(Tenant.Role.values()) //get roles from enum
			  .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
			  .collect(Collectors.toList());			 
		  }
	        if (user instanceof Developer && ((Developer) user).getRole() != null) 
	        	return List.of(new SimpleGrantedAuthority("ROLE_" + ((Developer) user).getRole()));
	        
	        if (user instanceof User && ((User) user).getRole().getName() != null)
	        	return List.of(new SimpleGrantedAuthority("ROLE_" + ((User) user).getRole().getName()));
		  
		  return List.of(); //empty list
	  }
}
