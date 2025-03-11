package multi_tenant.db.navigation.Entity.Global;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_type", schema = "db_navigation_global_multi_tenant")
public class PaymentType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String description;
	
//	@JsonIgnore
//	@OneToMany(mappedBy = "paymentType") //On delete restrict
//	private List<PaymentDetail> paymentDetails = new ArrayList<>();
//	
//	@JsonIgnore
//	@OneToMany(mappedBy = "paymentType") //On delete restrict
//	private List<ProrataDetail> prorataDetail = new ArrayList<>();
}
