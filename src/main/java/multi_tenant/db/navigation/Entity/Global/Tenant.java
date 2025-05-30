package multi_tenant.db.navigation.Entity.Global;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import multi_tenant.db.navigation.Enum.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenants", schema = "db_navigation_global_multi_tenant")
public class Tenant {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;	
	
	@Column (nullable = false, unique = true, length = 45) 
	private String name;
	
	@Column(name="db_name", nullable = false, unique = true, length = 45)
	private String dbName;
	
	@Column(name="current_admin_count", nullable = false)
	private int currentAdminCount;
	
	@Column(name="max_admin_count", nullable = false)
	private int maxAdminCount;
	
	@Enumerated(EnumType.STRING)
	@Column
	private Status status;
	
	@Column(name = "created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@ManyToOne
	@JoinColumn(name="owner_id")
	private Owner owner;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "owner_role")
	private Role ownerRole;
	
	public enum Role {
		OWNER, ADMIN
	}
	
}
