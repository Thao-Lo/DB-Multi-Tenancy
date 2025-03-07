package multi_tenant.db.navigation.Entity.Tenant;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import multi_tenant.db.navigation.Entity.BaseUser;
import multi_tenant.db.navigation.Enum.Status;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseUser{

	@ManyToOne
	@JoinColumn(name = "role_id") //have column role_id in users
	private Role role;
	
	@Enumerated(EnumType.STRING)
	@Column
	private Status status = Status.ACTIVE;
	
	@PrePersist
	public void setDefaultRoleIfNull() {
		if(this.role == null) {
			this.role = new Role();
			this.role.setId(4);
		}
	}
}
