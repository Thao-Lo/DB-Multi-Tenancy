package multi_tenant.db.navigation.Entity.Global;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prorata_detail", schema = "db_navigation_global_multi_tenant")
public class ProrataDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private int quantity;

	@Column(name = "unit_price", nullable = false)
	private BigDecimal unitPrice;

	@Column(name = "days_remaining", nullable = false)
	private int daysRemaning;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	// fk
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;


	// Instead of storing the whole PaymentType, just store the ID
	@Column(name = "payment_type_id", nullable = false)
	private int paymentTypeId;
	
	// fk
//	@JsonIgnore
//	@ManyToOne
//	@JoinColumn(name = "payment_type_id", nullable = false)
//	private PaymentType paymentType;

}
