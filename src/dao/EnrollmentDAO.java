package dao;

import config.JPAUtil;
import java.util.List;
import jakarta.persistence.EntityManager;import models.Enrollment;
import models.Student;
import models.Course;

public class EnrollmentDAO {
    
    public List<Enrollment> findAll() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT e FROM Enrollment e", Enrollment.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
    
    public boolean isDuplicate(Integer studentId, Integer courseId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            Long count = em.createQuery(
                "SELECT COUNT(e) FROM Enrollment e WHERE e.student.studentId = :sid AND e.course.courseId = :cid",
                Long.class)
                .setParameter("sid", studentId)
                .setParameter("cid", courseId)
                .getSingleResult();
            return count > 0;
        } finally {
            if (em != null) em.close();
        }
    }
    
    
    public boolean insertOne(Enrollment e) {
        if (isDuplicate(e.getStudent().getStudentId(), e.getCourse().getCourseId())) {
            return false;
        }
        
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.persist(e);  // NEW STATE -> MANAGED STATE
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive())
                em.getTransaction().rollback();
            return false;
        } finally {
            if (em != null) em.close();  // MANAGED -> DETACHED
        }
    }
    
   
    public boolean updateOne(Enrollment e) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.merge(e);  // DETACHED STATE -> MANAGED STATE
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive())
                em.getTransaction().rollback();
            return false;
        } finally {
            if (em != null) em.close();  // MANAGED -> DETACHED
        }
    }
    
    
    public boolean deleteOne(Enrollment e) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            Enrollment managedEnrollment = em.merge(e);  // DETACHED -> MANAGED
            em.remove(managedEnrollment);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive())
                em.getTransaction().rollback();
            return false;
        } finally {
            if (em != null) em.close();
        }
    }
}