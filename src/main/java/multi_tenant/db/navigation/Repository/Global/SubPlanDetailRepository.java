package multi_tenant.db.navigation.Repository.Global;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.SubPlanDetail;

public interface SubPlanDetailRepository extends JpaRepository<SubPlanDetail, Long>{
	//findTopBy...OrderBy...Desc/Asc
	Optional<SubPlanDetail> findTopByOwnerOrderBySubscriptionStartDesc(Owner owner);
	
	@Query("SELECT s FROM SubPlanDetail s WHERE s.nextBillingDate BETWEEN :start AND :end AND s.status = :status")
	List<SubPlanDetail> findByNextBillingDateAndStatus(
			@Param("start") LocalDateTime startOfDay,
			@Param("end") LocalDateTime endOfDay,
			@Param("status") SubPlanDetail.PlanDetailStatus status);
	
	
}
