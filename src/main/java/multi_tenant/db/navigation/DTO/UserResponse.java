package multi_tenant.db.navigation.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import multi_tenant.db.navigation.Entity.Tenant.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	private long id;
	private String firstName;
	private String email;
	private List<String> role;
}
