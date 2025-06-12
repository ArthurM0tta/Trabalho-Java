package com.doceria.cardapio.config;

import com.doceria.cardapio.model.Doce;
import com.doceria.cardapio.repository.DoceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private DoceRepository doceRepository;

    @Override
    public void run(String... args) {
        if (doceRepository.count() == 0) {
            doceRepository.saveAll(List.of(
                new Doce("Brigadeiro", "Chocolate com granulado", 2.50, ""),
                new Doce("Beijinho", "Coco com açúcar", 2.00, ""),
                new Doce("Bolo de Cenoura", "Cobertura de chocolate", 5.00, "")
            ));
        }
    }
}
