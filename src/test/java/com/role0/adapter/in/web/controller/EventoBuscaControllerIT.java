package com.role0.adapter.in.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.role0.adapter.out.external.weather.OpenWeatherClient;
import com.role0.config.security.JwtService;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.application.port.out.WeatherServicePort.PrevisaoClima;
import com.role0.core.domain.usuario.entity.Usuario;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventoBuscaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EventoRepositoryPort eventoRepository;

    @Autowired
    private UsuarioRepositoryPort usuarioRepository;

    @MockitoBean
    private OpenWeatherClient weatherClient;

    private UUID eventoId;
    private UUID hostId;
    private String userToken;
    private CoordenadaGeografica coords;

    @BeforeEach
    void setup() {
        hostId = UUID.randomUUID();
        Usuario host = new Usuario(hostId, "Host Test");
        usuarioRepository.salvar(host);

        coords = new CoordenadaGeografica(-23.5505, -46.6333);
        eventoId = UUID.randomUUID();
        Evento evento = new Evento(
                eventoId,
                hostId,
                "Role Teste",
                20,
                coords,
                LocalDateTime.now().plusDays(1)
        );
        evento.setEnderecoLegivel("Av. Paulista, 1000");
        
        eventoRepository.salvar(evento);

        UUID leitorId = UUID.randomUUID();
        userToken = jwtService.generateToken(leitorId);
    }

    @Test
    @DisplayName("Deve buscar evento detalhado e incluir previsão de tempo se disponível (HTTP 200)")
    void deveBuscarComClima_Http200() throws Exception {
        PrevisaoClima climaMock = new PrevisaoClima(25.0, "Céu limpo", "01d");
        Mockito.when(weatherClient.getPrevisaoPorCoordenadas(coords.getLatitude(), coords.getLongitude()))
               .thenReturn(Optional.of(climaMock));

        mockMvc.perform(get("/api/v1/events/" + eventoId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventoId.toString()))
                .andExpect(jsonPath("$.anfitriao.nomeDisplay").value("Host Test"))
                .andExpect(jsonPath("$.clima.descricao").value("Céu limpo"))
                .andExpect(jsonPath("$.clima.temperaturaAtual").value(25.0));
    }

    @Test
    @DisplayName("Deve buscar evento detalhado mesmo se previsão de tempo falhar/indisponivel (HTTP 200 via Fallback)")
    void deveBuscarSemClimaFallback_Http200() throws Exception {
        Mockito.when(weatherClient.getPrevisaoPorCoordenadas(coords.getLatitude(), coords.getLongitude()))
               .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/events/" + eventoId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventoId.toString()))
                .andExpect(jsonPath("$.clima").doesNotExist());
    }

    @Test
    @DisplayName("Deve retonar HTTP 404 para evento inexistente")
    void deveRetornarHttp404ParaEventoInexistente() throws Exception {
        UUID randomId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/events/" + randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
