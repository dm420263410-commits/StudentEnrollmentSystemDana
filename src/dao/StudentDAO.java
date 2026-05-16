package dao;

import config.JPAUtil;
import java.util.List;
import jakarta.persistence.EntityManager;import models.Student;

public class StudentDAO {
    

    public List<Integer> getAllStudentsIds() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT s.studentId FROM Student s", Integer.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
    
  
    public List<Student> findAll() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
    
    public Student findById(Integer studentId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.find(Student.class, studentId);
        } finally {
            if (em != null) em.close();
        }
    }
}