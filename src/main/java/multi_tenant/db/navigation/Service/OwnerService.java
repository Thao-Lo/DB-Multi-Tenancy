package multi_tenant.db.navigation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Repository.Global.OwnerRepository;

@Service
public class OwnerService {

	@Autowired
	private OwnerRepository ownerRepository;
	
	public Owner getOwnerByEmail(String email) {
		return ownerRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Owner not found: " + email));
	}
	public Owner getByResetToken(String token) {
		return ownerRepository.findByResetToken(token)
				.orElseThrow(() -> new RuntimeException ("Owner not found" + token));
	}
	
	public void saveOwner(Owner owner) {
		ownerRepository.save(owner);
	}
	
	// fetch SubPlanDetail List this time to avoid Lazy.Fetch in Entity
	@Transactional(transactionManager = "globalTransactionManager")
	    public Owner getOwnerWithSubPlanDetails(String email) {
	        Owner owner =getOwnerByEmail(email);	        
	        owner.getSubPlanDetails().size();	        
	        return owner;
	    }
	
	
	
}
