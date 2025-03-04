package multi_tenant.db.navigation.Repository.Tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import multi_tenant.db.navigation.Entity.Tenant.User;

public interface UserRepository extends JpaRepository<User, Long>{
//	User findByEmail(String email);
	
	@Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.permissions WHERE u.email = :email")
	User findByEmailWithPermissions(@Param("email") String email);
	
	@Query("SELECT u FROM User u JOIN FETCH u.role r WHERE u.email = :email")
	User findByEmail(@Param("email") String email);
	
}
