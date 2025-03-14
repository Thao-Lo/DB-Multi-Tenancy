package multi_tenant.db.navigation.Entity.Global;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import multi_tenant.db.navigation.Entity.BaseUser;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "developers", schema = "db_navigation_global_multi_tenant")
public class Developer extends BaseUser {

	@Enumerated(EnumType.STRING)
	@Column(name="role", nullable=false)
	private Role role = Role.ADMIN;
	
	public enum Role {
		SUPER_ADMIN, ADMIN
	}
}
