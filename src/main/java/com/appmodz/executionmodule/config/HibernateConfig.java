package com.appmodz.executionmodule.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        HikariDataSource ds;
        config.setDriverClassName( env.getProperty("JDBC_DRIVER"));
        config.setJdbcUrl( env.getProperty("JDBC_URL"));
        config.setUsername( env.getProperty("POSTGRES_USERNAME") );
        config.setPassword( env.getProperty("POSTGRES_PASSWORD") );
        ds = new HikariDataSource( config );
        return ds;
    }

    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(@Qualifier("dataSource") final DataSource dataSource) throws IOException {
        LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
        bean.setDataSource(dataSource);

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        hibernateProperties.setProperty("hibernate.show_sql", "false");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto","update");
        hibernateProperties.setProperty("javax.persistence.validation.mode", "none"); // Added Property for testings bean validation
        bean.setHibernateProperties(hibernateProperties);
        bean.setPackagesToScan("com.appmodz.executionmodule");
        bean.afterPropertiesSet();

        return bean.getObject();
    }

    @Bean(name = "transactionManager")
    public HibernateTransactionManager getHibernateTransactionManager(@Qualifier("sessionFactory") final SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

}