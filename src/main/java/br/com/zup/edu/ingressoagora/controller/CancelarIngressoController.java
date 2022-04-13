package br.com.zup.edu.ingressoagora.controller;

import br.com.zup.edu.ingressoagora.model.EstadoIngresso;
import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@RestController
public class CancelarIngressoController {

    private final IngressoRepository repository;

    public CancelarIngressoController(IngressoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @PatchMapping("/ingressos/{id}/cancelamento")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        Ingresso ingresso = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (ingresso.estadoDiferenteDeNaoConsumido() || ingresso.foraDoPrazoParaCancelamento()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        ingresso.setEstado(EstadoIngresso.CANCELADO);

        repository.save(ingresso);

        return ResponseEntity.noContent().build();
    }
}
