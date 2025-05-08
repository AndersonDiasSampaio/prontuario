package org.example.prontuario.controller;

import lombok.RequiredArgsConstructor;
import org.example.prontuario.model.Medico;
import org.example.prontuario.model.Paciente;
import org.example.prontuario.service.MedicoService;
import org.example.prontuario.service.PacienteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    @GetMapping
    public String listarPacientes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Medico medico = medicoService.getCurrentMedico(auth.getName());
        model.addAttribute("medico", medico);
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "pacientes/lista";
    }

    @GetMapping("/novo")
    public String novoPaciente(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Medico medico = medicoService.getCurrentMedico(auth.getName());
        model.addAttribute("medico", medico);
        model.addAttribute("paciente", new Paciente());
        return "pacientes/form";
    }

    @PostMapping("/salvar")
    public String salvarPaciente(@ModelAttribute Paciente paciente, RedirectAttributes redirectAttributes) {
        try {
            pacienteService.salvar(paciente);
            redirectAttributes.addFlashAttribute("mensagem", "Paciente salvo com sucesso!");
            return "redirect:/pacientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar paciente: " + e.getMessage());
            return "redirect:/pacientes/novo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarPaciente(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Medico medico = medicoService.getCurrentMedico(auth.getName());
        model.addAttribute("medico", medico);
        model.addAttribute("paciente", pacienteService.buscarPorId(id));
        return "pacientes/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluirPaciente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pacienteService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Paciente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir paciente: " + e.getMessage());
        }
        return "redirect:/pacientes";
    }
} 