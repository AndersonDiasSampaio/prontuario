package org.example.prontuario.controller;

import lombok.RequiredArgsConstructor;
import org.example.prontuario.model.Medico;
import org.example.prontuario.service.MedicoService;
import org.example.prontuario.service.PacienteService;
import org.example.prontuario.service.ProntuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.web.csrf.CsrfToken;

import java.time.LocalDate;
import java.util.List;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
public class WebController {

    private final MedicoService medicoService;
    private final PacienteService pacienteService;
    private final ProntuarioService prontuarioService;

    @ModelAttribute("medico")
    public Medico getMedicoLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return medicoService.getCurrentMedico(auth.getName());
        }
        return null;
    }

    @ModelAttribute("_csrf")
    public CsrfToken getCsrfToken(CsrfToken token) {
        return token;
    }

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/login2")
    public String login2() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard2";
        }
        return "login2";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        String email = auth.getName();
        model.addAttribute("medico", medicoService.getCurrentMedico(email));
        model.addAttribute("totalPacientes", pacienteService.listarTodos().size());
        model.addAttribute("totalProntuarios", prontuarioService.listarTodos().size());
        model.addAttribute("atendimentosHoje", prontuarioService.listarTodos().stream()
                .filter(p -> p.getData().toLocalDate().equals(LocalDate.now()))
                .count());
        return "dashboard";
    }

    @GetMapping("/dashboard2")
    public String dashboard2(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login2";
        }
        String email = auth.getName();
        model.addAttribute("medico", medicoService.getCurrentMedico(email));
        model.addAttribute("medicos", medicoService.listarTodos());
        model.addAttribute("totalPacientes", pacienteService.listarTodos().size());
        model.addAttribute("totalProntuarios", prontuarioService.listarTodos().size());
        model.addAttribute("atendimentosHoje", prontuarioService.listarTodos().stream()
                .filter(p -> p.getData().toLocalDate().equals(LocalDate.now()))
                .count());
        return "dashboard2";
    }
} 