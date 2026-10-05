package br.com.fiap3esr.autoescola3esr.instrucao;

import br.com.fiap3esr.autoescola3esr.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3esr.autoescola3esr.application.core.domain.Instrucao;
import br.com.fiap3esr.autoescola3esr.application.service.AgendaDeInstrucoes;
import br.com.fiap3esr.autoescola3esr.exception.type.ValidacaoException;
import br.com.fiap3esr.autoescola3esr.shared.vo.enumeration.MotivoCancelamento;
import br.com.fiap3esr.autoescola3esr.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InstrucaoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AgendaDeInstrucoes agenda;

    @Test
    @DisplayName("Expectativa: Retornar código 400 para dados inválidos.")
    @WithMockUser
    void agendarInstrucaoCenario1() throws Exception {
        mockMvc.perform(post("/instrucoes"))
                .andExpect(status().isBadRequest());
        verify(agenda, never()).agendar(any());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 201 ao agendar instrução com dados válidos.")
    @WithMockUser
    void agendarInstrucaoCenario2() throws Exception {
        LocalDateTime dataHora = DadosTeste.proximaSegundaAs10();
        String dataHoraFormatada = dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
        when(agenda.agendar(any()))
                .thenReturn(new DadosDetalhamentoAgendamento(DadosTeste.instrucao(1L, dataHora)));

        mockMvc.perform(post("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id_aluno": 1, "id_instrutor": 1, "data_hora": "%s" }
                                """.formatted(dataHoraFormatada)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/instrucoes/1"))
                .andExpect(jsonPath("$.nome_aluno").value("Aluno Teste"))
                .andExpect(jsonPath("$.data_hora").value(dataHoraFormatada))
                .andExpect(jsonPath("$.cancelada").value(false));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 quando uma regra de negócio do agendamento for violada.")
    @WithMockUser
    void agendarInstrucaoCenario3() throws Exception {
        String dataHora = DadosTeste.proximaSegundaAs10().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
        when(agenda.agendar(any()))
                .thenThrow(new ValidacaoException("Instrutor indisponível na data/hora escolhida!"));

        mockMvc.perform(post("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id_aluno": 1, "id_instrutor": 1, "data_hora": "%s" }
                                """.formatted(dataHora)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Instrutor indisponível na data/hora escolhida!"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao cancelar instrução sem informar o motivo.")
    @WithMockUser
    void cancelarInstrucaoCenario1() throws Exception {
        mockMvc.perform(post("/instrucoes/1/cancelamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("motivo"));
        verify(agenda, never()).cancelar(any(), any());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 ao cancelar instrução com motivo válido.")
    @WithMockUser
    void cancelarInstrucaoCenario2() throws Exception {
        Instrucao instrucao = DadosTeste.instrucao(1L, DadosTeste.proximaSegundaAs10());
        instrucao.cancelar(MotivoCancelamento.ALUNO_DESISTIU);
        when(agenda.cancelar(eq(1L), any())).thenReturn(new DadosDetalhamentoAgendamento(instrucao));

        mockMvc.perform(post("/instrucoes/1/cancelamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "motivo": "ALUNO_DESISTIU" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelada").value(true))
                .andExpect(jsonPath("$.motivo_cancelamento").value("ALUNO_DESISTIU"));
    }
}
