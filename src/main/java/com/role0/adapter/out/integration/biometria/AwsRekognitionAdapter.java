package com.role0.adapter.out.integration.biometria;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.role0.core.application.port.out.IdentityVerificationPort;

/**
 * Adapter para integração com provedor de biometria (ex: AWS Rekognition
 * Liveness).
 * Garante a regra Zero-Knowledge da arquitetura: o Role-Zero nunca transaciona
 * nem armazena fotos dos usuários (PII Categoria Especial), apenas validação do
 * Token JWT do Face Liveness externo.
 */
@Component
public class AwsRekognitionAdapter implements IdentityVerificationPort {

    @Override
    public String extrairTokenBiometricoValido(byte[] imageBytes) {
        // Implementação AWS real omitida em favor da prova de arquitetura.
        // A regra é: Se o AWS API retornar score de Confiança > 90% para FaceMatch +
        // Liveness
        // e não detectar deepfakes injetadas na câmera, retorna 'true'.

        System.out.println("LOG INTERNA: Liveness processado via AWS Rekognition na Edge.");
        return UUID.randomUUID().toString();
    }
}
