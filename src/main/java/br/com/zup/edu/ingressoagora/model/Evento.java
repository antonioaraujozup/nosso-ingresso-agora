package br.com.zup.edu.ingressoagora.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private LocalDate data;

    @OneToMany(mappedBy = "evento")
    private List<Ingresso> ingressos= new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal preco;

    public Evento(String titulo, LocalDate data, BigDecimal preco) {
        this.titulo = titulo;
        this.data = data;
        this.preco = preco;
    }

    public Evento() {
    }

    public Long getId() {
        return id;
    }
}
