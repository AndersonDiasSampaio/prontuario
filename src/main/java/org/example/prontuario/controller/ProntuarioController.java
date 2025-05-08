package org.example.prontuario.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prontuario.model.Medico;
import org.example.prontuario.model.Prontuario;
import org.example.prontuario.model.Paciente;
import org.example.prontuario.service.MedicoService;
import org.example.prontuario.service.ProntuarioService;
import org.example.prontuario.service.PacienteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/prontuarios")
@RequiredArgsConstructor
public class ProntuarioController {

    private final ProntuarioService prontuarioService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    @GetMapping
    public String listarProntuarios(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            List<Prontuario> prontuarios = prontuarioService.listarTodos();
            
            model.addAttribute("medico", medico);
            model.addAttribute("prontuarios", prontuarios);
            return "prontuarios/lista";
        } catch (Exception e) {
            log.error("Erro ao listar prontuários: {}", e.getMessage());
            model.addAttribute("erro", "Erro ao listar prontuários: " + e.getMessage());
            return "prontuarios/lista";
        }
    }

    @GetMapping("/novo")
    public String novoProntuario(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            
            Prontuario prontuario = new Prontuario();
            prontuario.setData(LocalDateTime.now());
            
            model.addAttribute("medico", medico);
            model.addAttribute("prontuario", prontuario);
            model.addAttribute("pacientes", pacienteService.listarUltimosCadastrados(3));
            return "prontuarios/form";
        } catch (Exception e) {
            log.error("Erro ao criar novo prontuário: {}", e.getMessage());
            return "redirect:/prontuarios?erro=Erro ao criar novo prontuário: " + e.getMessage();
        }
    }

    @PostMapping("/salvar")
    public String salvarProntuario(@ModelAttribute Prontuario prontuario, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            prontuario.setMedico(medico);
            
            // Se a data não foi definida, define como agora
            if (prontuario.getData() == null) {
                prontuario.setData(LocalDateTime.now());
            }
            
            prontuarioService.salvar(prontuario);
            redirectAttributes.addFlashAttribute("mensagem", "Prontuário salvo com sucesso!");
            return "redirect:/prontuarios";
        } catch (Exception e) {
            log.error("Erro ao salvar prontuário: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar prontuário: " + e.getMessage());
            return "redirect:/prontuarios/novo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarProntuario(@PathVariable Long id, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            Prontuario prontuario = prontuarioService.buscarPorId(id);
            
            if (prontuario == null) {
                return "redirect:/prontuarios?erro=Prontuário não encontrado";
            }
            
            model.addAttribute("medico", medico);
            model.addAttribute("prontuario", prontuario);
            model.addAttribute("pacientes", pacienteService.listarTodos());
            return "prontuarios/form";
        } catch (Exception e) {
            log.error("Erro ao editar prontuário: {}", e.getMessage());
            return "redirect:/prontuarios?erro=Erro ao editar prontuário: " + e.getMessage();
        }
    }

    @GetMapping("/visualizar/{id}")
    public String visualizarProntuario(@PathVariable Long id, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Medico medico = medicoService.getCurrentMedico(auth.getName());
            Prontuario prontuario = prontuarioService.buscarPorId(id);
            
            if (prontuario == null) {
                return "redirect:/prontuarios?erro=Prontuário não encontrado";
            }
            
            model.addAttribute("medico", medico);
            model.addAttribute("prontuario", prontuario);
            return "prontuarios/visualizar";
        } catch (Exception e) {
            log.error("Erro ao visualizar prontuário: {}", e.getMessage());
            return "redirect:/prontuarios?erro=Erro ao visualizar prontuário: " + e.getMessage();
        }
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        try {
            Prontuario prontuario = prontuarioService.buscarPorId(id);
            
            if (prontuario == null) {
                return ResponseEntity.notFound().build();
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Cabeçalho
            document.add(new Paragraph("PRONTUÁRIO ELETRÔNICO"));
            document.add(new Paragraph("\n"));

            // Informações do Paciente
            document.add(new Paragraph("                                      INFORMAÇÕES DO PACIENTE"));
            document.add(new Paragraph("Nome: " + prontuario.getPaciente().getNome()));
            document.add(new Paragraph("CPF: " + prontuario.getPaciente().getCpf()));
            document.add(new Paragraph("Data de Nascimento: " + prontuario.getPaciente().getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("Telefone: " + prontuario.getPaciente().getTelefone()));
            document.add(new Paragraph("\n"));

            // Informações do Atendimento
            document.add(new Paragraph("INFORMAÇÕES DO ATENDIMENTO"));
            document.add(new Paragraph("Data: " + prontuario.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Médico: " + prontuario.getMedico().getNome()));
            document.add(new Paragraph("\n"));

            // Diagnóstico
            document.add(new Paragraph("DIAGNÓSTICO"));
            document.add(new Paragraph(prontuario.getDiagnostico()));
            document.add(new Paragraph("\n"));

            // Prescrição
            document.add(new Paragraph("PRESCRIÇÃO"));
            document.add(new Paragraph(prontuario.getPrescricao()));
            document.add(new Paragraph("\n"));

            // Observações
            if (prontuario.getObservacoes() != null && !prontuario.getObservacoes().isEmpty()) {
                document.add(new Paragraph("OBSERVAÇÕES"));
                document.add(new Paragraph(prontuario.getObservacoes()));
            }

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "prontuario_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            log.error("Erro ao gerar PDF do prontuário: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirProntuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            prontuarioService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Prontuário excluído com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao excluir prontuário: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir prontuário: " + e.getMessage());
        }
        return "redirect:/prontuarios";
    }

    @GetMapping("/buscar-pacientes")
    @ResponseBody
    public List<Paciente> buscarPacientes(@RequestParam(required = false) String termo) {
        return pacienteService.buscarPorNomeOuCpf(termo);
    }
} 