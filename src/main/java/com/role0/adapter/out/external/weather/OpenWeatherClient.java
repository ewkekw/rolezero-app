package com.role0.adapter.out.external.weather;

import com.role0.core.application.port.out.WeatherServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class OpenWeatherClient implements WeatherServicePort {

    private static final Logger log = LoggerFactory.getLogger(OpenWeatherClient.class);
    private final RestClient restClient;
    private final String apiKey;

    public OpenWeatherClient(RestClient.Builder restClientBuilder, 
                             @Value("${openweather.api.key:dummy}") String apiKey) {
        this.restClient = restClientBuilder.baseUrl("https://api.openweathermap.org/data/2.5").build();
        this.apiKey = apiKey;
    }

    /**
     * Busca a previsão do clima de até 8 blocos de 3 horas.
     * Resultado é cacheadol por coordenadas.
     */
    @Override
    @Cacheable(value = "weatherCache", key = "#lat + '_' + #lon")
    public Optional<PrevisaoClima> getPrevisaoPorCoordenadas(double lat, double lon) {
        log.info("Buscando clima na OpenWeather API para lat={}, lon={}", lat, lon);
        try {
            OpenWeatherForecastResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "pt_br")
                            .queryParam("cnt", 8)
                            .build())
                    .retrieve()
                    .body(OpenWeatherForecastResponse.class);

            if (response != null && response.list() != null && !response.list().isEmpty()) {
                // Pega a primeira previsão disponível como a principal
                var forecast = response.list().get(0);
                var weatherInfo = forecast.weather().get(0);
                return Optional.of(new PrevisaoClima(
                        forecast.main().temp(),
                        weatherInfo.description(),
                        weatherInfo.icon()
                ));
            }
        } catch (Exception e) {
            log.warn("Erro ao buscar previsão do clima: {}", e.getMessage());
        }
        return Optional.empty(); // Fallback tolerável segundo documentação
    }
    
    public record OpenWeatherForecastResponse(List<ForecastData> list) {}
    public record ForecastData(MainData main, java.util.List<WeatherData> weather) {}
    public record MainData(Double temp) {}
    public record WeatherData(String description, String icon) {}
}
