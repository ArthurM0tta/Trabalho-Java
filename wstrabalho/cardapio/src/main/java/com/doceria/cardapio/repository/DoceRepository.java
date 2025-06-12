package com.doceria.cardapio.repository;

import com.doceria.cardapio.model.Doce;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoceRepository extends JpaRepository<Doce, Long> {
    Optional<Doce> findByNome(String nome);
}