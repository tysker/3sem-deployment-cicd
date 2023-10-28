package dk.lyngby.config;

import dk.lyngby.model.Hotel;
import dk.lyngby.model.Role;
import dk.lyngby.model.Room;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HibernateConfig {

    private final static Logger logger = LoggerFactory.getLogger(HibernateConfig.class);
    private static EntityManagerFactory entityManagerFactory;
    private static Boolean isTest = false;

    private static EntityManagerFactory setupHibernateConfigurationDevelopment() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            hibernateDevelopmentConfiguration(props);
            hibernateBasicConfiguration(props);
            return getEntityManagerFactory(configuration, props);
        } catch (Throwable th) {
            logger.error("Initial EntityManagerFactory creation for development failed.", th);
            throw new ExceptionInInitializerError(th);
        }
    }

    private static EntityManagerFactory setupHibernateConfigurationIsDeployed() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            hibernateIsDeployedConfiguration(props);
            hibernateBasicConfiguration(props);
            return getEntityManagerFactory(configuration, props);
        } catch (Throwable th) {
            logger.error("Initial EntityManagerFactory creation for deployment failed.", th);
            throw new ExceptionInInitializerError(th);
        }
    }

    private static EntityManagerFactory setupHibernateConfigurationTest() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
            props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
            props.put("hibernate.connection.username", "postgres");
            props.put("hibernate.connection.password", "postgres");
            props.put("hibernate.archive.autodetection", "class");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.hbm2ddl.auto", "create-drop");
            return getEntityManagerFactory(configuration, props);
        } catch (Throwable th) {
            logger.error("Initial EntityManagerFactory creation for testing failed. %s", th);
            throw new ExceptionInInitializerError(th);
        }
    }

    private static void hibernateDevelopmentConfiguration(Properties props) throws IOException {
        props.put("hibernate.connection.url", ApplicationConfig.getProperty("db.connection.string") + ApplicationConfig.getProperty("db.name"));
        props.put("hibernate.connection.username", ApplicationConfig.getProperty("db.username"));
        props.put("hibernate.connection.password", ApplicationConfig.getProperty("db.password"));
    }

    private static void hibernateIsDeployedConfiguration(Properties props) throws IOException {
        String DB_USERNAME = System.getenv("DB_USERNAME");
        String DB_PASSWORD = System.getenv("DB_PASSWORD");
        String CONNECTION_STR = System.getenv("CONNECTION_STR") + ApplicationConfig.getProperty("db.name");
        props.setProperty("hibernate.connection.url", CONNECTION_STR);
        props.setProperty("hibernate.connection.username", DB_USERNAME);
        props.setProperty("hibernate.connection.password", DB_PASSWORD);
    }

    private static void hibernateBasicConfiguration(Properties props) {
        props.put("hibernate.show_sql", "false"); // show sql in console
        props.put("hibernate.format_sql", "false"); // format sql in console
        props.put("hibernate.use_sql_comments", "false"); // show sql comments in console
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // dialect for postgresql
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver"); // driver class for postgresql
        props.put("hibernate.archive.autodetection", "class"); // hibernate scans for annotated classes
        props.put("hibernate.current_session_context_class", "thread"); // hibernate current session context
        props.put("hibernate.hbm2ddl.auto", "update"); // hibernate creates tables based on entities
    }

    private static EntityManagerFactory getEntityManagerFactory(Configuration configuration, Properties props) {
        configuration.setProperties(props);
        getAnnotationConfiguration(configuration);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
        return sf.unwrap(EntityManagerFactory.class);
    }


    private static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(Hotel.class);
        configuration.addAnnotatedClass(Room.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
    }

    private static EntityManagerFactory getEntityManagerFactoryConfigDevelopment() {
        if (entityManagerFactory == null) entityManagerFactory = setupHibernateConfigurationDevelopment();
        return entityManagerFactory;
    }

    private static EntityManagerFactory getEntityManagerFactoryConfigTest() {
        if (entityManagerFactory == null) entityManagerFactory = setupHibernateConfigurationTest();
        return entityManagerFactory;
    }

    private static EntityManagerFactory getEntityManagerFactoryConfigIsDeployed() {
        if (entityManagerFactory == null) entityManagerFactory = setupHibernateConfigurationIsDeployed();
        return entityManagerFactory;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        boolean isDeployed = (System.getenv("DEPLOYED") != null);
        if (isDeployed) return getEntityManagerFactoryConfigIsDeployed();
        if (isTest) return getEntityManagerFactoryConfigTest();
        return getEntityManagerFactoryConfigDevelopment();
    }

    public static void setTest(Boolean test) {
        isTest = test;
    }

    public static Boolean getTest() {
        return isTest;
    }
}