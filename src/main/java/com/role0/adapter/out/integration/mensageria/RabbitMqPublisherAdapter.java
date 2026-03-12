package com.role0.adapter.out.integration.mensageria;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.role0.core.application.port.out.MessageBrokerEventPort;

@Component
public class RabbitMqPublisherAdapter implements MessageBrokerEventPort {

    // private final RabbitTemplate rabbitTemplate; Omitido por hora

    /**
     * Skill: Backend/Arquitetura
     * Publica uma mensagem num Exchange configurado com o plugin x-delayed-message.
     * Depois do delay de horas, ela cairá na Fila final de exclusão do rolezinho
     * (EDA Pipeline).
     */
    @Override
    public void agendarEncerramentoDeEvento(UUID eventoId, long horasDelay) {
        // long delayInMillis = horasDelay * 3600000L;
        // String routingKey = "evento.expiracao";

        // rabbitTemplate.convertAndSend("delayed-exchange", routingKey,
        // eventoId.toString(), message -> {
        // message.getMessageProperties().setDelayLong(delayInMillis);
        // return message;
        // });

        System.out.println("EDA PIPELINE: Publicou evento " + eventoId + " com delay de " + horasDelay
                + "h para exclusão na madrugada.");
    }
}
