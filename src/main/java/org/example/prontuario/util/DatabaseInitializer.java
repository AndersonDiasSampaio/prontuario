package org.example.prontuario.util;

import org.example.prontuario.model.Medico;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DatabaseInitializer {
    
    public static void initialize() {
        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            
            Transaction transaction = session.beginTransaction();
            
            // Criar médico de teste
            Medico medico = new Medico();
            medico.setNome("Dr. João Silva");
            medico.setCrm("12345");
            medico.setEspecialidade("Clínico Geral");
            medico.setEmail("joao.silva@email.com");
            medico.setSenha("123456");
            
            session.persist(medico);
            transaction.commit();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 