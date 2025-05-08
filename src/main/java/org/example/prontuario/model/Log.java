package org.example.prontuario.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "logs")
public class Log {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tipo; // "USUARIO", "PACIENTE", "PRONTUARIO"
    
    @Column(nullable = false)
    private String acao; // "EXCLUSAO", "EDICAO"
    
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Column(nullable = false)
    private String descricao;
    
    @Column(nullable = false)
    private String usuarioResponsavel;
    
    @Column
    private String dadosAntigos;
    
    @Column
    private String dadosNovos;
} 