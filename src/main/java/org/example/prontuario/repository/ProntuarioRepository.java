package org.example.prontuario.repository;

import org.example.prontuario.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {
} 