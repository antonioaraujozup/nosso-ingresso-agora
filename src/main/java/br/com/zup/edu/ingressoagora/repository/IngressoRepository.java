package br.com.zup.edu.ingressoagora.repository;

import br.com.zup.edu.ingressoagora.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngressoRepository extends JpaRepository<Ingresso,Long> {
}
