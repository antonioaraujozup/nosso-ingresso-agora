package br.com.zup.edu.ingressoagora.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import static br.com.zup.edu.ingressoagora.model.EstadoIngresso.NAOCONSUMIDO;

@Entity
public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoIngresso estado=NAOCONSUMIDO;


    @Column(nullable = false)
    private LocalDateTime compradoEm=LocalDateTime.now();


    @ManyToOne(optional = false)
    private Evento evento;


    public Ingresso(EstadoIngresso estado) {
        this.estado = estado;
    }

    /**
     * @deprecated construtor para uso exclusivo do Hibernate
     */
    @Deprecated
    public Ingresso() {
    }

    public Boolean estadoDiferenteDeNaoConsumido() {
        return this.estado != NAOCONSUMIDO;
    }

    public Boolean foraDoPrazoParaCancelamento() {
        LocalDate hoje = LocalDate.now();

        int diasAteOEvento = Period.between(hoje, this.evento.getData()).getDays();

        return diasAteOEvento < 1;
    }

    public Long getId() {
        return id;
    }

    public void setEstado(EstadoIngresso estado) {
        this.estado = estado;
    }
}
