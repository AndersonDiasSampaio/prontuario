package org.example.prontuario.controller;

import lombok.RequiredArgsConstructor;
import org.example.prontuario.model.Medico;
import org.example.prontuario.service.MedicoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Medico medico = medicoService.getCurrentMedico(auth.getName());
        model.addAttribute("medico", medico);
        return "medicos/perfil";
    }

    @PostMapping("/perfil")
    public String atualizarPerfil(@ModelAttribute Medico medico, RedirectAttributes redirectAttributes) {
        try {
            medicoService.atualizarPerfil(medico);
            redirectAttributes.addFlashAttribute("mensagem", "Perfil atualizado com sucesso!");
            return "redirect:/medicos/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar perfil: " + e.getMessage());
            return "redirect:/medicos/perfil";
        }
    }

    @PostMapping("/atualizar-senha")
    public String atualizarSenha(@RequestParam String senhaAtual,
                                @RequestParam String novaSenha,
                                @RequestParam String confirmarSenha,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Medico medico = medicoService.getCurrentMedico(auth.getName());

        if (!medico.getPassword().equals(senhaAtual)) {
            redirectAttributes.addFlashAttribute("error", "Senha atual incorreta");
            return "redirect:/medicos/perfil";
        }

        if (!novaSenha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("error", "As senhas não coincidem");
            return "redirect:/medicos/perfil";
        }

        medico.setSenha(novaSenha);
        medicoService.save(medico);
        redirectAttributes.addFlashAttribute("success", "Senha atualizada com sucesso");
        return "redirect:/medicos/perfil";
    }
} 