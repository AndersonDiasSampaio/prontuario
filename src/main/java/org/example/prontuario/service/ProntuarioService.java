package org.example.prontuario.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prontuario.model.Medico;
import org.example.prontuario.model.Prontuario;
import org.example.prontuario.repository.ProntuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final LogService logService;
    private final MedicoService medicoService;

    public List<Prontuario> listarTodos() {
        log.info("Buscando todos os prontuários");
        try {
            List<Prontuario> prontuarios = prontuarioRepository.findAll();
            log.info("Prontuários encontrados: {}", prontuarios.size());
            return prontuarios;
        } catch (Exception e) {
            log.error("Erro ao buscar prontuários: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Prontuario buscarPorId(Long id) {
        return prontuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));
    }

    public Prontuario salvar(Prontuario prontuario) {
        return prontuarioRepository.save(prontuario);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Iniciando exclusão do prontuário com ID: {}", id);
        try {
            Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Prontuário não encontrado com ID: {}", id);
                    return new RuntimeException("Prontuário não encontrado");
                });
            
            log.info("Prontuário encontrado: {}", prontuario);
            
            // Registra o log antes de excluir
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("Usuário autenticado: {}", auth.getName());
            
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            log.info("Médico encontrado: {}", medico.getEmail());
            
            logService.registrarExclusaoProntuario(medico.getEmail(), prontuario);
            log.info("Log de exclusão registrado");
            
            prontuarioRepository.delete(prontuario);
            log.info("Prontuário excluído com sucesso: ID {}", id);
        } catch (Exception e) {
            log.error("Erro ao excluir prontuário: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao excluir prontuário: " + e.getMessage());
        }
    }
} 