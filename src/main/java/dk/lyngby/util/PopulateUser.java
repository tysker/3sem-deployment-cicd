package dk.lyngby.util;


import dk.lyngby.config.HibernateConfig;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.Set;

public class PopulateUser {
    public static void main(String[] args)  {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        createUserTestData(emf);
    }
    public static void createUserTestData(EntityManagerFactory emf) {

        User user = new User("user", "user123");
        User admin = new User("admin", "admin123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");


        user.addRole(userRole);
        admin.addRole(adminRole);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }
    }
}
