package org.example.prontuario.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prontuario.dto.UsuarioDTO;
import org.example.prontuario.model.Medico;
import org.example.prontuario.service.MedicoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final MedicoService medicoService;

    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            
            if (!medico.isAdmin()) {
                log.error("Usuário não tem permissão para criar novos usuários");
                return "redirect:/dashboard2?erro=Você não tem permissão para criar novos usuários";
            }
            
            model.addAttribute("medico", medico);
            model.addAttribute("usuario", new UsuarioDTO());
            return "usuarios/form";
        } catch (Exception e) {
            log.error("Erro ao acessar página de novo usuário: {}", e.getMessage());
            return "redirect:/dashboard2?erro=Erro ao acessar página de novo usuário: " + e.getMessage();
        }
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute UsuarioDTO usuarioDTO, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            
            if (!medico.isAdmin()) {
                log.error("Usuário não tem permissão para criar novos usuários");
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para criar novos usuários");
                return "redirect:/dashboard2";
            }
            
            log.info("Tentando criar novo usuário: {}", usuarioDTO.getEmail());
            medicoService.criarUsuario(usuarioDTO);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário criado com sucesso!");
            return "redirect:/dashboard2";
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar usuário: " + e.getMessage());
            return "redirect:/usuarios/novo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            
            if (!medico.isAdmin()) {
                log.error("Usuário não tem permissão para editar usuários");
                return "redirect:/dashboard2?erro=Você não tem permissão para editar usuários";
            }
            
            Medico medicoParaEditar = medicoService.buscarPorId(id);
            if (medicoParaEditar == null) {
                return "redirect:/dashboard2?erro=Usuário não encontrado";
            }
            
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNome(medicoParaEditar.getNome());
            usuarioDTO.setEmail(medicoParaEditar.getEmail());
            usuarioDTO.setCrm(medicoParaEditar.getCrm());
            usuarioDTO.setEspecialidade(medicoParaEditar.getEspecialidade());
            usuarioDTO.setAdmin(medicoParaEditar.isAdmin());
            
            model.addAttribute("medico", medico);
            model.addAttribute("usuario", usuarioDTO);
            model.addAttribute("id", id);
            return "usuarios/form";
        } catch (Exception e) {
            log.error("Erro ao acessar página de edição de usuário: {}", e.getMessage());
            return "redirect:/dashboard2?erro=Erro ao acessar página de edição de usuário: " + e.getMessage();
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioDTO usuarioDTO, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            
            if (!medico.isAdmin()) {
                log.error("Usuário não tem permissão para editar usuários");
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para editar usuários");
                return "redirect:/dashboard2";
            }
            
            log.info("Tentando atualizar usuário: {}", usuarioDTO.getEmail());
            medicoService.atualizarUsuario(id, usuarioDTO);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário atualizado com sucesso!");
            return "redirect:/dashboard2";
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar usuário: " + e.getMessage());
            return "redirect:/usuarios/editar/" + id;
        }
    }

    @PostMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            
            if (!medico.isAdmin()) {
                log.error("Usuário não tem permissão para excluir usuários");
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para excluir usuários");
                return "redirect:/dashboard2";
            }
            
            log.info("Tentando excluir usuário com ID: {}", id);
            medicoService.excluirUsuario(id);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário excluído com sucesso!");
            return "redirect:/dashboard2";
        } catch (Exception e) {
            log.error("Erro ao excluir usuário: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir usuário: " + e.getMessage());
            return "redirect:/dashboard2";
        }
    }
} 