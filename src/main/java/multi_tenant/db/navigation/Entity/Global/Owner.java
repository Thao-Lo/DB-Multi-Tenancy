package multi_tenant.db.navigation.Entity.Global;



import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
	
	//CONSIDER TO REMOVE LIST AND QUERY SEPEARTELY WHEN NEEDED
	@JsonIgnore
	@OneToMany (mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY) //owner lowecase, field in Tenant	
	List<Tenant> tenants = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY) //On delete restrict
	private List<SubPlanDetail> subPlanDetails = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY) //On delete restrict
	private List<Payment> payments = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany (mappedBy = "owner", fetch = FetchType.LAZY) 	
	List<TenantActivityLog> tenantActivityLogs = new ArrayList<>();
}
