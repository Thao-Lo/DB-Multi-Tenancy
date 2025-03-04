package multi_tenant.db.navigation.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantContext {
	private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);

	private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
	private static final ThreadLocal<String> GLOBAL_USER_TYPE = new ThreadLocal<>(); //OWNER DEVELOPER

	// CURRENT_TENANT
	public static String getCurrentTenant() {
		String tenant = CURRENT_TENANT.get();		
		logger.info("TenantContext.getCurrentTenant(): {}", tenant);
		return tenant;
	}

	public static void setCurrentTenant(String databaseName) {
		logger.info("database name: {}", databaseName);
		CURRENT_TENANT.set(databaseName);
	}

	//GLOBAL USER
	public static String getCurrentGlobalUserType() {
		return GLOBAL_USER_TYPE.get();
	}

	public static void setGlobalUser(String globalUser) {
		GLOBAL_USER_TYPE.set(globalUser);
	}

	//clear all context
	public static void clear() {
		CURRENT_TENANT.remove();
		GLOBAL_USER_TYPE.remove();
	}

}
