package config;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
public class JPAUtil {
    
    private static EntityManagerFactory emf;
    
    private JPAUtil() {}
    
    private static EntityManagerFactory getEMF() {
        if (emf == null)
            emf = Persistence.createEntityManagerFactory("universityPU");
        return emf;
    }
    
    public static EntityManager getEntityManager() {
        return getEMF().createEntityManager();
    }
    
    public static void closeEMF() {
        if (emf != null)
            emf.close();
    }
}