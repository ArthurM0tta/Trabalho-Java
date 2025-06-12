package com.doceria.cardapio.repository;

import com.doceria.cardapio.model.Cliente;
import com.doceria.cardapio.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(Cliente cliente);
}
