package br.com.zup.edu.ingressoagora.repository;

import br.com.zup.edu.ingressoagora.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento,Long> {
}
