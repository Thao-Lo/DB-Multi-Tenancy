package multi_tenant.db.navigation.Config;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import multi_tenant.db.navigation.Entity.Global.Tenant;
import multi_tenant.db.navigation.Service.TenantService;
import multi_tenant.db.navigation.Utils.DataSourceUtil;
import multi_tenant.db.navigation.Utils.TenantRoutingDataSource;

@Configuration
@DependsOn("globalEntityManagerFactory") // Đảm bảo Global DB khởi tạo trước
@EnableJpaRepositories(
		basePackages = "multi_tenant.db.navigation.Repository.Tenant",
		entityManagerFactoryRef = "tenantEntityManagerFactory",
		transactionManagerRef = "tenantTransactionManager")
public class MultiTenantDataSourceConfig {

	@Autowired
	private TenantService tenantService;

	@Autowired
	private DataSourceUtil dataSourceUtil;
	
//	@Autowired
	private final TenantRoutingDataSource tenantRoutingDataSource;
	public MultiTenantDataSourceConfig (TenantRoutingDataSource tenantRoutingDataSource) {
		this.tenantRoutingDataSource = tenantRoutingDataSource;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MultiTenantDataSourceConfig.class);
	
	@Bean(name = "multiTenantDataSource")
	public TenantRoutingDataSource multiTenantDataSource() {
//		TenantRoutingDataSource tenantRoutingDataSource = new TenantRoutingDataSource();
		
		//add default DataSource
		tenantRoutingDataSource.addDataSource("default", dataSourceUtil.createDataSource("db_navigation_global_multi_tenant"));
		
		//add tenant DataSource List
		/* List<Tenant> tenants = tenantService.getAllTenant();		
		if (tenants.isEmpty()) {
			logger.info("No tenant found");
		} else {
			for (Tenant tenant : tenants) {
				logger.info("tenant db name: {} ", tenant.getDbName());
				tenantRoutingDataSource
				.addDataSource(tenant.getDbName(), dataSourceUtil.createDataSource(tenant.getDbName()));				
			}
		}		
		*/		
		return tenantRoutingDataSource;
	}

	@Bean(name = "tenantEntityManagerFactory")
	@DependsOn("multiTenantDataSource")
	public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("multiTenantDataSource") DataSource multiTenantDataSource) {
		return builder
				.dataSource(multiTenantDataSource)
				.packages("multi_tenant.db.navigation.Entity.Tenant") 																												
				.persistenceUnit("tenantPU")
				.build();
	}

	@Bean(name = "tenantTransactionManager")
	public PlatformTransactionManager tenantTransactionManager(
			@Qualifier("tenantEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
