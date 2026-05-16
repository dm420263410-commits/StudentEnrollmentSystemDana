package dao;

import config.JPAUtil;
import java.util.List;
import jakarta.persistence.EntityManager;import models.Course;

public class CourseDAO {
    
    
    public List<Integer> getAllCoursesIds() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT c.courseId FROM Course c", Integer.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
    

    public List<Course> findAll() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT c FROM Course c", Course.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
    
    public Course findById(Integer courseId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.find(Course.class, courseId);
        } finally {
            if (em != null) em.close();
        }
    }
}