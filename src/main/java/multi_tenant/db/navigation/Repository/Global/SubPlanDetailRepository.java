package multi_tenant.db.navigation.Repository.Global;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.SubPlanDetail;

public interface SubPlanDetailRepository extends JpaRepository<SubPlanDetail, Long>{
	//findTopBy...OrderBy...Desc/Asc
	Optional<SubPlanDetail> findTopByOwnerOrderBySubscriptionStartDesc(Owner owner);
}
