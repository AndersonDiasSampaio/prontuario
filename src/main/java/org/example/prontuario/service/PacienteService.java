package org.example.prontuario.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prontuario.model.Medico;
import org.example.prontuario.model.Paciente;
import org.example.prontuario.repository.PacienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final LogService logService;
    private final MedicoService medicoService;

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    @Transactional
    public void salvar(Paciente paciente) {
        log.info("Salvando paciente: {}", paciente.getNome());
        try {
            if (paciente.getId() != null) {
                // Se for uma edição, busca o paciente antigo para o log
                Paciente pacienteAntigo = buscarPorId(paciente.getId());
                pacienteRepository.save(paciente);
                
                // Registra o log de edição
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Medico medico = medicoService.getCurrentMedico(auth.getName());
                logService.registrarEdicaoPaciente(medico.getEmail(), pacienteAntigo, paciente);
            } else {
                pacienteRepository.save(paciente);
            }
        } catch (Exception e) {
            log.error("Erro ao salvar paciente: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar paciente: " + e.getMessage());
        }
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo paciente com ID: {}", id);
        try {
            Paciente paciente = buscarPorId(id);
            
            // Registra o log antes de excluir
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            logService.registrarExclusaoPaciente(medico.getEmail(), paciente);
            
            pacienteRepository.delete(paciente);
            log.info("Paciente excluído com sucesso: {}", paciente.getNome());
        } catch (Exception e) {
            log.error("Erro ao excluir paciente: {}", e.getMessage());
            throw new RuntimeException("Erro ao excluir paciente: " + e.getMessage());
        }
    }

    private void validarPaciente(Paciente paciente) {
        if (paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do paciente é obrigatório");
        }

        if (paciente.getCpf() == null || !paciente.getCpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("CPF inválido");
        }

        if (paciente.getDataNascimento() == null || paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }

        if (paciente.getTelefone() == null || !paciente.getTelefone().matches("\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}")) {
            throw new IllegalArgumentException("Telefone inválido");
        }

        if (paciente.getEndereco() == null || paciente.getEndereco().trim().length() < 5) {
            throw new IllegalArgumentException("Endereço inválido");
        }

        // Verifica se já existe outro paciente com o mesmo CPF
        if (paciente.getId() == null) {
            if (pacienteRepository.findByCpf(paciente.getCpf()).isPresent()) {
                throw new IllegalArgumentException("Já existe um paciente cadastrado com este CPF");
            }
        } else {
            pacienteRepository.findByCpf(paciente.getCpf())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(paciente.getId())) {
                        throw new IllegalArgumentException("Já existe um paciente cadastrado com este CPF");
                    }
                });
        }
    }

    public List<Paciente> listarUltimosCadastrados(int quantidade) {
        return pacienteRepository.findTopByOrderByDataCriacaoDesc(quantidade)
                .stream()
                .limit(quantidade)
                .toList();
    }

    public List<Paciente> buscarPorNomeOuCpf(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarUltimosCadastrados(3);
        }
        return pacienteRepository.buscarPorNomeOuCpf(termo.trim());
    }
} 