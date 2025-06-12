package com.doceria.cardapio.controller;

import com.doceria.cardapio.model.Admin;
import com.doceria.cardapio.model.Cliente;
import com.doceria.cardapio.model.Doce;
import com.doceria.cardapio.model.Pedido;
import com.doceria.cardapio.repository.AdminRepository;
import com.doceria.cardapio.repository.ClienteRepository;
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
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DoceRepository doceRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/")
    public String paginaInicial() {
        return "index";
    }

    @GetMapping("/login")
    public String paginaLogin() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String paginaCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarCliente(@RequestParam String nome,
                                   @RequestParam String cpf,
                                   @RequestParam String telefone,
                                   @RequestParam String email,
                                   @RequestParam String senha) {
        Cliente cliente = new Cliente(nome, cpf, telefone, email, senha);
        clienteRepository.save(cliente);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpSession session,
                        Model model) {

        // Primeiro tenta logar como admin
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            if (admin.getSenha().equals(senha)) {
                session.setAttribute("adminLogado", admin);
                return "redirect:/admin/dashboard";
            }
        }

        // Se não for admin, tenta logar como cliente
        Optional<Cliente> optionalCliente = clienteRepository.findByEmail(email);
        if (optionalCliente.isPresent()) {
            Cliente cliente = optionalCliente.get();
            if (cliente.getSenha().equals(senha)) {
                session.setAttribute("clienteLogado", cliente);
                return "redirect:/pedido";
            }
        }

        model.addAttribute("erro", "Login inválido.");
        return "login";
    }

    @GetMapping("/pedido")
    public String paginaPedido(Model model, HttpSession session) {
        if (session.getAttribute("clienteLogado") == null) {
            return "redirect:/";
        }

        List<Doce> doces = doceRepository.findAll();
        model.addAttribute("doces", doces);
        return "pedido";
    }

    @PostMapping("/pedido")
    public String finalizarPedido(@RequestParam(required = false) List<String> itens,
                                  @RequestParam(required = false) List<String> tamanhos,
                                  Model model, HttpSession session) {
        if (session.getAttribute("clienteLogado") == null) {
            return "redirect:/";
        }

        if (itens == null || itens.isEmpty()) {
            model.addAttribute("mensagem", "Você não selecionou nenhum doce.");
            return "confirmacao";
        }

        List<String> pedidoCompleto = new java.util.ArrayList<>();
        double precoTotal = 0.0;

        for (int i = 0; i < itens.size(); i++) {
            String nomeDoce = itens.get(i);
            String tamanho = (tamanhos != null && tamanhos.size() > i) ? tamanhos.get(i) : "Padrão";

            // Busca o doce pelo nome
            Optional<Doce> doceOpt = doceRepository.findByNome(nomeDoce);
            if (doceOpt.isPresent()) {
                Doce doce = doceOpt.get();
                precoTotal += doce.getPreco(); // soma o preço

                // Salva a descrição completa do pedido
                pedidoCompleto.add(nomeDoce + " - Tamanho: " + tamanho);
            } else {
                // Se não encontrar, pode adicionar um aviso
                pedidoCompleto.add(nomeDoce + " - Tamanho: " + tamanho + " (Preço não encontrado)");
            }
        }

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        Pedido pedido = new Pedido(clienteLogado, precoTotal, pedidoCompleto);
        pedidoRepository.save(pedido);

        return "redirect:/lista-pedidos";
    }

    @GetMapping("/lista-pedidos")
    public String mostrarListaPedidos(Model model, HttpSession session) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        if (clienteLogado == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidosDoCliente = pedidoRepository.findByCliente(clienteLogado);
        model.addAttribute("pedidos", pedidosDoCliente);

        return "lista-pedidos";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Limpa toda a sessão, incluindo cliente e admin
        return "redirect:/login";
    }
}