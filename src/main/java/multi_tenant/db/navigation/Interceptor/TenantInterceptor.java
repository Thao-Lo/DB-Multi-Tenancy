package multi_tenant.db.navigation.Interceptor;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import multi_tenant.db.navigation.Entity.Global.Owner;
import multi_tenant.db.navigation.Entity.Global.Tenant;
import multi_tenant.db.navigation.JWT.JwtTokenProvider;
import multi_tenant.db.navigation.Service.OwnerService;
import multi_tenant.db.navigation.Service.TenantService;
import multi_tenant.db.navigation.Utils.TenantContext;
import multi_tenant.db.navigation.Utils.TenantRoutingDataSource;

@Component
public class TenantInterceptor implements HandlerInterceptor {
	@Autowired
	private TenantService tenantService;
	@Autowired
	private OwnerService ownerService;
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	@Autowired
	TenantRoutingDataSource tenantRoutingDataSource;
	private static final Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
		String shopName = request.getHeader("shop-name");
		String globalUser = request.getHeader("global-user");
		// get email and roles from JWT
		String authHeader = request.getHeader("Authorization");
		
		//Login without JWT 
		if (authHeader == null && shopName == null) {
			//SHOULD KEEP? CHECK AGAIN
			if (globalUser == null) {
				getGlobalTenant();
				return true;
			}

			if (globalUser.equals("DEVELOPER") || globalUser.equals("OWNER")) {
				TenantContext.setGlobalUser(globalUser);
				getGlobalTenant();
				return true;
			} else {
				sendForbiddenResponse(response, "Incorrect Global User.");
			}

		}
		// if(shopName != null && globalUser != null) -> never happend

		if (shopName != null && authHeader == null) {
			// no JWT

			System.out.println("Interceptor: " + shopName);

			String databaseName = tenantService.getDatabaseNameByShopId(shopName).getDbName();
			TenantContext.setCurrentTenant(databaseName);
			forceDatabaseConnection();
			logger.info("Tenant set to: {}", databaseName);
			return true;

		}
		// with JWT
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			Claims claims = jwtTokenProvider.validateToken(token);
			String email = claims.getSubject();
			List<String> roles = extractRoles(claims);
			System.out.println("intercepter jwt roles:" + roles.toString());
			if (roles == null || roles.isEmpty()) {
				sendForbiddenResponse(response, "Role is not found");
				return false;
			}

			System.out.println("Owner roles" + roles.toString());
			if(shopName == null) {
				getGlobalTenant();
				return true;
			}
			if (roles.contains("ROLE_OWNER")) {
				Owner owner = ownerService.getOwnerByEmail(email);
				// check if Owners has ShopName
				List<Tenant> tenants = tenantService.getTenantsByOwnerId(owner.getId());
				for (Tenant tenant : tenants) {
					if (tenant.getName().equals(shopName)) {
						TenantContext.setCurrentTenant(tenant.getDbName());
						return true;
					}
				}
				// wont come to controller
				sendForbiddenResponse(response, "Tenant is not found");
				return false;
			}
		}
		// authHeader != null -> user(tenant) with Jwt
		// roles not contains OWNERS
		if (shopName != null) {
			System.out.println("Interceptor: " + shopName);

			String databaseName = tenantService.getDatabaseNameByShopId(shopName).getDbName();
			TenantContext.setCurrentTenant(databaseName);
			forceDatabaseConnection();
			logger.info("Tenant set to: {}", databaseName);

		}

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		TenantContext.clear();
	}

	private void getGlobalTenant() {
		logger.warn("Shop-name header is missing");
		TenantContext.setCurrentTenant("default");
		
	}

	// convert <String,Object> roles as a object to String
	private List<String> extractRoles(Claims claims) {
		ObjectMapper objectMapper = new ObjectMapper();
		Object roleObject = claims.get("roles");

		if (roleObject == null)
			return null;
		
		// TypeReference<List<String>>(): to read comflex type, not List.class, missing String
		return objectMapper.convertValue(roleObject, new TypeReference<List<String>>() { 
		});
	}

	private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}
	private void forceDatabaseConnection() {
	    try {
	        String currentTenant = TenantContext.getCurrentTenant();	       
	        if (currentTenant != null) {
	            logger.info("Forcing database initialization for tenant: {}", currentTenant);
	            DataSource ds = tenantRoutingDataSource.determineTargetDataSource();
	            ds.getConnection().close(); // Open & store connection in pool to initialize it early
	        }
	    } catch (Exception e) {
	        logger.error("Failed to force database initialization.", e);
	    }
	}
}
