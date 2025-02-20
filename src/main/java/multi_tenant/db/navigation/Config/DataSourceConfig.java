package multi_tenant.db.navigation.Config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import multi_tenant.db.navigation.Utils.DataSourceUtil;

@Configuration
@EnableJpaRepositories(
    basePackages = "multi_tenant.db.navigation.Repository.Global",
    entityManagerFactoryRef = "globalEntityManagerFactory",
    transactionManagerRef = "globalTransactionManager"
)
public class DataSourceConfig {

    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Bean(name = "globalDataSource")
    public DataSource globalDataSource() {
        return dataSourceUtil.createDataSource("global_multi_tenant");
    }
    
    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(
                new HibernateJpaVendorAdapter(),
                new HashMap<>(), 
                null
        );
    }

    @Bean(name = "globalEntityManagerFactory")
//    @DependsOn("flyway") // Flyway must run before Hibernate
    public LocalContainerEntityManagerFactoryBean globalEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("globalDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("multi_tenant.db.navigation.Entity.Global")
                .persistenceUnit("globalPU")
                .build();
    }

    @Bean(name = "globalTransactionManager")
    public PlatformTransactionManager globalTransactionManager(
            @Qualifier("globalEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @PostConstruct
    public void runFlywayMigration() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSourceUtil.createDataSource("global_multi_tenant"))
                .locations("classpath:db/migration/global")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
    }
}
