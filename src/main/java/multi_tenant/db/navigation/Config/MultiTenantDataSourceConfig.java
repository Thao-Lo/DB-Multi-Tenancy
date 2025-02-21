package multi_tenant.db.navigation.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

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
@EnableJpaRepositories(basePackages = "multi_tenant.db.navigation.Repository.Tenant", entityManagerFactoryRef = "tenantEntityManagerFactory", transactionManagerRef = "tenantTransactionManager")
public class MultiTenantDataSourceConfig {

	@Autowired
	private TenantService tenantService;

	@Autowired
	private DataSourceUtil dataSourceUtil;

	@Bean
	public TenantRoutingDataSource multiTenantDataSource() {
		TenantRoutingDataSource tenantRoutingDataSource = new TenantRoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();

		List<Tenant> tenants = tenantService.getAllTenant();
		if (tenants.isEmpty()) {
			System.out.print("No tenant found");
		} else {
			for (Tenant tenant : tenants) {
				dataSourceMap.put(tenant.getDbName(), dataSourceUtil.createDataSource(tenant.getDbName()));
			}
		}

		DataSource defaultDataSource = dataSourceUtil.createDataSource("global_multi_tenant");
		dataSourceMap.put("default", defaultDataSource);

		tenantRoutingDataSource.setTargetDataSources(dataSourceMap);
		tenantRoutingDataSource.setDefaultTargetDataSource(defaultDataSource);
		tenantRoutingDataSource.afterPropertiesSet();

		System.out.println("default" + dataSourceMap.get("default"));
		
		return tenantRoutingDataSource;
	}

	@Bean(name = "tenantEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(EntityManagerFactoryBuilder builder,
			DataSource multiTenantDataSource) {
		return builder.dataSource(multiTenantDataSource).packages("multi_tenant.db.navigation.Entity.Tenant") 																												
				.persistenceUnit("tenantPU").build();
	}

	@Bean(name = "tenantTransactionManager")
	public PlatformTransactionManager tenantTransactionManager(
			@Qualifier("tenantEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
