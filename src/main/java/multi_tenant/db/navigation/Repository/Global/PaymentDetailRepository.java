package multi_tenant.db.navigation.Repository.Global;

import org.springframework.data.jpa.repository.JpaRepository;

import multi_tenant.db.navigation.Entity.Global.PaymentDetail;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long>{

}
