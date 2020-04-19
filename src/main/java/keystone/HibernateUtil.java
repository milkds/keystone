package keystone;

import keystone.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static StandardServiceRegistry registry2;
    private static SessionFactory sessionFactory;
    private static SessionFactory sessionFactory2;

    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder();

                Map<String, String> settings = new HashMap<>();
                settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/keystone?useUnicode=true&" +
                        "useJDBCCompliantTimezoneShift=true&" +
                        "useLegacyDatetimeCode=false&" +
                        "serverTimezone=UTC&" +
                        "useSSL=false");
                settings.put("hibernate.connection.username", "root");
                settings.put("hibernate.connection.password", "root");
                settings.put("hibernate.show_sql", "true");
                settings.put("hibernate.hbm2ddl.auto", "none");

                registryBuilder.applySettings(settings);

                registry = registryBuilder.build();

                MetadataSources sources = new MetadataSources(registry);
                sources.addAnnotatedClass(KeyItem.class);
                sources.addAnnotatedClass(Specification.class);
                sources.addAnnotatedClass(Car.class);
                sources.addAnnotatedClass(CarAttribute.class);
                sources.addAnnotatedClass(ItemCar.class);
                sources.addAnnotatedClass(ItemCarAttribute.class);
                Metadata metadata = sources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                System.out.println("SessionFactory creation failed");
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }
    private static SessionFactory getSessionFactory2() {
        if (sessionFactory2 == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder();

                Map<String, String> settings = new HashMap<>();
                settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/keystone_full?useUnicode=true&" +
                        "useJDBCCompliantTimezoneShift=true&" +
                        "useLegacyDatetimeCode=false&" +
                        "serverTimezone=UTC&" +
                        "useSSL=false");
                settings.put("hibernate.connection.username", "root");
                settings.put("hibernate.connection.password", "root");
                settings.put("hibernate.show_sql", "true");
                settings.put("hibernate.hbm2ddl.auto", "none");

                registryBuilder.applySettings(settings);

                registry2 = registryBuilder.build();

                MetadataSources sources = new MetadataSources(registry2);
                sources.addAnnotatedClass(KeyItem.class);
                sources.addAnnotatedClass(Specification.class);
                sources.addAnnotatedClass(Car.class);
                sources.addAnnotatedClass(CarAttribute.class);
                sources.addAnnotatedClass(ItemCar.class);
                sources.addAnnotatedClass(ItemCarAttribute.class);
                Metadata metadata = sources.getMetadataBuilder().build();

                sessionFactory2 = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                System.out.println("SessionFactory creation failed");
                e.printStackTrace();
                if (registry2 != null) {
                    StandardServiceRegistryBuilder.destroy(registry2);
                }
            }
        }
        return sessionFactory2;
    } //full key db

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        if (registry2 != null) {
            StandardServiceRegistryBuilder.destroy(registry2);
        }
    }

    public static Session getSession(){
        return getSessionFactory().openSession();
    }

    //full key db
    public static Session getSession2(){
        return getSessionFactory2().openSession();
    }


}
