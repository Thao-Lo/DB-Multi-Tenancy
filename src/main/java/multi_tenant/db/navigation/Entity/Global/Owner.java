package multi_tenant.db.navigation.Entity.Global;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
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
@Table(name = "owners", schema = "db_navigation_global_multi_tenant")
public class Owner extends BaseUser{
	
	@Enumerated(EnumType.STRING)
	@Column
	private Status status = Status.ACTIVE;	
	
	@OneToMany (mappedBy = "owner", cascade = CascadeType.ALL) //owner lowecase, field in Tenant
	@JsonIgnore
	List<Tenant> tenants = new ArrayList<>();
	
	
}
