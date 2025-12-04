package com.locadora.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration config = new Configuration();
            config.configure(); // lê hibernate.properties automaticamente

            sessionFactory = config.buildSessionFactory(
                    new StandardServiceRegistryBuilder()
                            .applySettings(config.getProperties())
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
