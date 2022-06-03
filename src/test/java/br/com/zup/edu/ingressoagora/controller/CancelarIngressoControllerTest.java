package br.com.zup.edu.ingressoagora.controller;

import br.com.zup.edu.ingressoagora.model.EstadoIngresso;
import br.com.zup.edu.ingressoagora.model.Evento;
import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.EventoRepository;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
class CancelarIngressoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private IngressoRepository ingressoRepository;

    private Evento evento;

    private Ingresso ingresso;

    void criarEvento(LocalDate data) {
        this.evento = new Evento("Show", data, new BigDecimal("500"));
        this.eventoRepository.save(this.evento);
    }

    void criarIngresso() {
        this.ingresso = new Ingresso(this.evento);
        this.ingressoRepository.save(this.ingresso);
    }

    @BeforeEach
    void setUp() {
        this.ingressoRepository.deleteAll();
        this.eventoRepository.deleteAll();
    }

    @Test
    @DisplayName("Não deve cancelar um ingresso não cadastrado")
    void naoDeveCancelarUmIngressoNaoCadastrado() throws Exception {

        // Cenário
        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", Integer.MAX_VALUE);

        // Ação e Corretude
        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isNotFound()
                )
                .andReturn()
                .getResolvedException();

        // Asserts
        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals("Este ingresso não existe.", ((ResponseStatusException) resolvedException).getReason());

    }

    @Test
    @DisplayName("Não deve cancelar um ingresso faltando menos de 1 dia para a data do evento")
    void naoDeveCancelarUmIngressoFaltandoMenosDe1DiaParaADataDoEvento() throws Exception {

        // Cenário
        criarEvento(LocalDate.now());
        criarIngresso();

        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", this.ingresso.getId());

        // Ação e Corretude
        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isUnprocessableEntity()
                )
                .andReturn()
                .getResolvedException();

        // Asserts
        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals("Não é possivel cancelar faltando menos de 1 dia para data do evento", ((ResponseStatusException) resolvedException).getReason());

    }

    @Test
    @DisplayName("Não deve cancelar um ingresso já consumido")
    void naoDeveCancelarUmIngressoJaConsumido() throws Exception {

        // Cenário
        criarEvento(LocalDate.now().plusDays(7));
        criarIngresso();
        this.ingresso.consumir();
        this.ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", this.ingresso.getId());

        // Ação e Corretude
        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isUnprocessableEntity()
                )
                .andReturn()
                .getResolvedException();

        // Asserts
        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals("Impossivel cancelar um Ingresso já consumido.", ((ResponseStatusException) resolvedException).getReason());

    }

    @Test
    @DisplayName("Deve cancelar um ingresso")
    void deveCancelarUmIngresso() throws Exception {

        // Cenário
        criarEvento(LocalDate.now().plusDays(7));
        criarIngresso();

        MockHttpServletRequestBuilder request = patch("/ingressos/{id}/cancelamento", this.ingresso.getId());

        // Ação e Corretude
        mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isNoContent()
                );

        Optional<Ingresso> possivelIngressoCancelado = ingressoRepository.findById(this.ingresso.getId());

        // Asserts
        assertTrue(possivelIngressoCancelado.isPresent());

        Ingresso ingressoCancelado = possivelIngressoCancelado.get();

        assertEquals(EstadoIngresso.CANCELADO, ingressoCancelado.getEstado());

    }

}