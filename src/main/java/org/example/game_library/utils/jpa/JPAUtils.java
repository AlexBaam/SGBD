package org.example.game_library.utils.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtils {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("GameLibraryPU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if((emf != null) && (emf.isOpen())) {
            emf.close();
        }
    }
}
