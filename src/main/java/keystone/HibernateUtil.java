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
    private static SessionFactory sessionFactory;

    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder();

                Map<String, String> settings = new HashMap<>();
                settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/keystone?useUnicode=true&useJDBCCompliantTimezoneShift=true&" +
                        "useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false");
                settings.put("hibernate.connection.username", "root");
                settings.put("hibernate.connection.password", "root");
                settings.put("hibernate.show_sql", "false");
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

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static Session getSession(){
        return getSessionFactory().openSession();
    }


}
