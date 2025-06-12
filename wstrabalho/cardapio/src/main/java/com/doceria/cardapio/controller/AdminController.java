package com.doceria.cardapio.controller;

import com.doceria.cardapio.model.Admin;
import com.doceria.cardapio.model.Doce;
import com.doceria.cardapio.model.Pedido;
import com.doceria.cardapio.repository.AdminRepository;
import com.doceria.cardapio.repository.DoceRepository;
import com.doceria.cardapio.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoceRepository doceRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Admin adminLogado = (Admin) session.getAttribute("adminLogado");
        if (adminLogado == null) {
            return "redirect:/login";
        }
        model.addAttribute("admin", adminLogado);
        return "admin/dashboard";
    }

    // Listar doces
    @GetMapping("/doces")
    public String listarDoces(Model model, HttpSession session) {
        if (session.getAttribute("adminLogado") == null) return "redirect:/login";
        model.addAttribute("doces", doceRepository.findAll());
        return "admin/lista-doces";
    }

    // Formulário para novo doce
    @GetMapping("/doces/novo")
    public String novoDoceForm(Model model, HttpSession session) {
        if (session.getAttribute("adminLogado") == null) return "redirect:/login";
        model.addAttribute("doce", new Doce());
        return "admin/form-doce";
    }

    // Salvar doce
    @PostMapping("/doces/salvar")
    public String salvarDoce(@ModelAttribute Doce doce, HttpSession session) {
        if (session.getAttribute("adminLogado") == null) return "redirect:/login";
        doceRepository.save(doce);
        return "redirect:/admin/doces";
    }

    // Editar doce
    @GetMapping("/doces/editar/{id}")
    public String editarDoce(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminLogado") == null) return "redirect:/login";
        Optional<Doce> doce = doceRepository.findById(id);
        doce.ifPresent(d -> model.addAttribute("doce", d));
        return "admin/form-doce";
    }

    // Excluir doce
    @GetMapping("/doces/excluir/{id}")
    public String excluirDoce(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminLogado") == null) return "redirect:/login";
        doceRepository.deleteById(id);
        return "redirect:/admin/doces";
    }

    // Visualizar todos os pedidos
    @GetMapping("/pedidos")
    public String listarTodosPedidos(Model model, HttpSession session) {
        if (session.getAttribute("adminLogado") == null) return "redirect:/login";

        List<Pedido> pedidos = pedidoRepository.findAll();
        model.addAttribute("pedidos", pedidos);

        return "admin/lista-pedidos";
    }


    // Redirecionar login de admin
    @PostMapping("/login-admin")
    public String loginAdmin(@RequestParam String email,
                             @RequestParam String senha,
                             HttpSession session,
                             Model model) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            if (admin.getSenha().equals(senha)) {
                session.setAttribute("adminLogado", admin);
                return "redirect:/admin/dashboard";
            }
        }
        model.addAttribute("erro", "Login inválido.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminLogado");
        return "redirect:/login";
    }
}