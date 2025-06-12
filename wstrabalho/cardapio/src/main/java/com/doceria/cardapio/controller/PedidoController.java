package com.doceria.cardapio.controller;

import com.doceria.cardapio.model.Cliente;
import com.doceria.cardapio.model.Pedido;
import com.doceria.cardapio.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/pedidos")
    public String listarPedidos(HttpSession session, Model model) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoRepository.findByCliente(clienteLogado);
        model.addAttribute("pedidos", pedidos);
        return "lista-pedidos";  // Página para mostrar os pedidos
    }
}
