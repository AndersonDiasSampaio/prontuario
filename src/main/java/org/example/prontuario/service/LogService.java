package org.example.prontuario.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prontuario.model.Log;
import org.example.prontuario.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void registrarLog(String tipo, String acao, String descricao, String usuarioResponsavel, Object dadosAntigos, Object dadosNovos) {
        try {
            Log logEntry = new Log();
            logEntry.setTipo(tipo);
            logEntry.setAcao(acao);
            logEntry.setDataHora(LocalDateTime.now());
            logEntry.setDescricao(descricao);
            logEntry.setUsuarioResponsavel(usuarioResponsavel);
            
            if (dadosAntigos != null) {
                Map<String, Object> dadosAntigosMap = new HashMap<>();
                if (dadosAntigos instanceof org.example.prontuario.model.Prontuario) {
                    org.example.prontuario.model.Prontuario p = (org.example.prontuario.model.Prontuario) dadosAntigos;
                    dadosAntigosMap.put("id", p.getId());
                    dadosAntigosMap.put("data", p.getData());
                    dadosAntigosMap.put("diagnostico", p.getDiagnostico());
                    dadosAntigosMap.put("prescricao", p.getPrescricao());
                    dadosAntigosMap.put("observacoes", p.getObservacoes());
                    if (p.getPaciente() != null) {
                        dadosAntigosMap.put("pacienteId", p.getPaciente().getId());
                    }
                    if (p.getMedico() != null) {
                        dadosAntigosMap.put("medicoId", p.getMedico().getId());
                    }
                } else if (dadosAntigos instanceof org.example.prontuario.model.Paciente) {
                    org.example.prontuario.model.Paciente p = (org.example.prontuario.model.Paciente) dadosAntigos;
                    dadosAntigosMap.put("id", p.getId());
                    dadosAntigosMap.put("nome", p.getNome());
                    dadosAntigosMap.put("cpf", p.getCpf());
                    dadosAntigosMap.put("dataNascimento", p.getDataNascimento());
                    dadosAntigosMap.put("telefone", p.getTelefone());
                    dadosAntigosMap.put("endereco", p.getEndereco());
                    dadosAntigosMap.put("email", p.getEmail());
                    dadosAntigosMap.put("tipoSanguineo", p.getTipoSanguineo());
                    dadosAntigosMap.put("dataCriacao", p.getDataCriacao());
                }
                logEntry.setDadosAntigos(objectMapper.writeValueAsString(dadosAntigosMap));
            }
            
            if (dadosNovos != null) {
                Map<String, Object> dadosNovosMap = new HashMap<>();
                if (dadosNovos instanceof org.example.prontuario.model.Prontuario) {
                    org.example.prontuario.model.Prontuario p = (org.example.prontuario.model.Prontuario) dadosNovos;
                    dadosNovosMap.put("id", p.getId());
                    dadosNovosMap.put("data", p.getData());
                    dadosNovosMap.put("diagnostico", p.getDiagnostico());
                    dadosNovosMap.put("prescricao", p.getPrescricao());
                    dadosNovosMap.put("observacoes", p.getObservacoes());
                    if (p.getPaciente() != null) {
                        dadosNovosMap.put("pacienteId", p.getPaciente().getId());
                    }
                    if (p.getMedico() != null) {
                        dadosNovosMap.put("medicoId", p.getMedico().getId());
                    }
                } else if (dadosNovos instanceof org.example.prontuario.model.Paciente) {
                    org.example.prontuario.model.Paciente p = (org.example.prontuario.model.Paciente) dadosNovos;
                    dadosNovosMap.put("id", p.getId());
                    dadosNovosMap.put("nome", p.getNome());
                    dadosNovosMap.put("cpf", p.getCpf());
                    dadosNovosMap.put("dataNascimento", p.getDataNascimento());
                    dadosNovosMap.put("telefone", p.getTelefone());
                    dadosNovosMap.put("endereco", p.getEndereco());
                    dadosNovosMap.put("email", p.getEmail());
                    dadosNovosMap.put("tipoSanguineo", p.getTipoSanguineo());
                    dadosNovosMap.put("dataCriacao", p.getDataCriacao());
                }
                logEntry.setDadosNovos(objectMapper.writeValueAsString(dadosNovosMap));
            }
            
            logRepository.save(logEntry);
            log.info("Log registrado com sucesso: tipo={}, acao={}, usuario={}", tipo, acao, usuarioResponsavel);
        } catch (Exception e) {
            log.error("Erro ao registrar log: {}", e.getMessage(), e);
        }
    }

    public void registrarExclusaoUsuario(String usuarioResponsavel, Object dadosAntigos) {
        registrarLog("USUARIO", "EXCLUSAO", "Exclusão de usuário", usuarioResponsavel, dadosAntigos, null);
    }

    public void registrarEdicaoPaciente(String usuarioResponsavel, Object dadosAntigos, Object dadosNovos) {
        registrarLog("PACIENTE", "EDICAO", "Edição de paciente", usuarioResponsavel, dadosAntigos, dadosNovos);
    }

    public void registrarExclusaoPaciente(String usuarioResponsavel, Object dadosAntigos) {
        registrarLog("PACIENTE", "EXCLUSAO", "Exclusão de paciente", usuarioResponsavel, dadosAntigos, null);
    }

    @Transactional
    public void registrarExclusaoProntuario(String usuarioResponsavel, Object dadosAntigos) {
        try {
            log.info("Registrando exclusão de prontuário por: {}", usuarioResponsavel);
            registrarLog("PRONTUARIO", "EXCLUSAO", "Exclusão de prontuário", usuarioResponsavel, dadosAntigos, null);
            log.info("Log de exclusão de prontuário registrado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao registrar log de exclusão de prontuário: {}", e.getMessage(), e);
            // Não propaga o erro para não impedir a exclusão
        }
    }
} 