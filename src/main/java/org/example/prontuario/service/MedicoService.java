package org.example.prontuario.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prontuario.dto.UsuarioDTO;
import org.example.prontuario.model.Medico;
import org.example.prontuario.repository.MedicoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicoService implements UserDetailsService {

    private final MedicoRepository medicoRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Tentando carregar usuário com email: {}", email);
        return medicoRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com email: {}", email);
                    return new UsernameNotFoundException("Médico não encontrado com email: " + email);
                });
    }

    public Medico getCurrentMedico(String email) {
        log.info("Buscando médico atual com email: {}", email);
        return medicoRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Médico não encontrado com email: {}", email);
                    return new UsernameNotFoundException("Médico não encontrado com email: " + email);
                });
    }
    public Medico buscarPorId(Long id) {
        log.info("Buscando médico com ID: {}", id);
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + id));
    }

    @Transactional
    public void atualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        log.info("Atualizando usuário com ID: {}", id);
        Medico medico = buscarPorId(id);

        // Verifica se o email já está em uso por outro usuário
        if (!medico.getEmail().equals(usuarioDTO.getEmail()) &&
                medicoRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Verifica se o CRM já está em uso por outro usuário
        if (!medico.getCrm().equals(usuarioDTO.getCrm()) &&
                medicoRepository.findByCrm(usuarioDTO.getCrm()).isPresent()) {
            throw new RuntimeException("CRM já cadastrado");
        }

        medico.setNome(usuarioDTO.getNome());
        medico.setEmail(usuarioDTO.getEmail());
        medico.setCrm(usuarioDTO.getCrm());
        medico.setEspecialidade(usuarioDTO.getEspecialidade());
        medico.setAdmin(usuarioDTO.isAdmin());

        // Se a senha foi alterada, atualiza
        if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
            medico.setSenha(usuarioDTO.getSenha());
        }

        medicoRepository.save(medico);
        log.info("Usuário atualizado com sucesso: {}", usuarioDTO.getEmail());
    }


    @Transactional
    public void excluirUsuario(Long id) {
        log.info("Excluindo usuário com ID: {}", id);
        Medico medico = buscarPorId(id);

        // Não permite excluir o próprio usuário
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Medico medicoAtual = getCurrentMedico(auth.getName());
        if (medico.getId().equals(medicoAtual.getId())) {
            throw new RuntimeException("Não é possível excluir o próprio usuário");
        }

        medicoRepository.delete(medico);
        log.info("Usuário excluído com sucesso: {}", medico.getEmail());
    }
    @Transactional
    public void criarUsuario(UsuarioDTO usuarioDTO) {
        log.info("Criando novo usuário: {}", usuarioDTO.getEmail());
        
        if (medicoRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (medicoRepository.findByCrm(usuarioDTO.getCrm()).isPresent()) {
            throw new RuntimeException("CRM já cadastrado");
        }

        try {
            Medico medico = new Medico();
            medico.setNome(usuarioDTO.getNome());
            medico.setEmail(usuarioDTO.getEmail());
            medico.setSenha(usuarioDTO.getSenha());
            medico.setCrm(usuarioDTO.getCrm());
            medico.setEspecialidade(usuarioDTO.getEspecialidade());
            medico.setAdmin(usuarioDTO.isAdmin());

            medicoRepository.save(medico);
            log.info("Usuário criado com sucesso: {}", usuarioDTO.getEmail());
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }
    }

    public Medico save(Medico medico) {
        log.info("Salvando médico: {}", medico.getEmail());
        return medicoRepository.save(medico);
    }

    public void atualizarPerfil(Medico medico) {
        log.info("Atualizando perfil do médico: {}", medico.getEmail());
        Medico medicoExistente = getCurrentMedico(medico.getEmail());
        medicoExistente.setNome(medico.getNome());
        medicoExistente.setCrm(medico.getCrm());
        medicoExistente.setEspecialidade(medico.getEspecialidade());
        medicoRepository.save(medicoExistente);
    }

    public List<Medico> listarTodos() {
        log.info("Listando todos os médicos");
        return medicoRepository.findAll();
    }
} 