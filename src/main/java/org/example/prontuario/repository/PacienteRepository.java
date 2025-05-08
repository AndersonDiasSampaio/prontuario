package org.example.prontuario.repository;

import org.example.prontuario.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf);

    @Query("SELECT p FROM Paciente p ORDER BY p.dataCriacao DESC")
    List<Paciente> findTopByOrderByDataCriacaoDesc(@Param("quantidade") int quantidade);

    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR p.cpf LIKE CONCAT('%', :termo, '%') ORDER BY p.nome")
    List<Paciente> buscarPorNomeOuCpf(@Param("termo") String termo);
} 