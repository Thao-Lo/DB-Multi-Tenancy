package multi_tenant.db.navigation.Service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import multi_tenant.db.navigation.Entity.Global.Developer;
import multi_tenant.db.navigation.Entity.Tenant.User;
import multi_tenant.db.navigation.Repository.Tenant.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRespository;
	
//	@Transactional(transactionManager = "tenantTransactionManager")
	public User getUserByEmailWithPermission(String email) {
		return userRespository.findByEmailWithPermissions(email);
	}
	
	public User getUserByEmail(String email) {
		return userRespository.findByEmail(email);
	}
	
	public List<User> getAllUsers(){
		List<User> users = userRespository.findAll();
		return users.isEmpty() ? Collections.emptyList() : users;
	}
	
	public User getByResetToken(String token) {
		return userRespository.findByResetToken(token)
				.orElseThrow(() -> new RuntimeException ("User not found" + token));
	}
}
