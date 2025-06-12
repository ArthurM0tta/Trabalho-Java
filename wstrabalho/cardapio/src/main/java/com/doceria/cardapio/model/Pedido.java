package com.doceria.cardapio.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ElementCollection
    private List<String> itens;


    public Pedido() {}

    public Pedido(Cliente cliente, Double preco, List<String> itens) {
        this.cliente = cliente;
        this.itens = itens;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<String> getItens() { return itens; }
    public void setItens(List<String> itens) { this.itens = itens; }

}
