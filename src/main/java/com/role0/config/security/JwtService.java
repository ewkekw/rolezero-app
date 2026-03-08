package com.role0.config.security;

import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Serviço JWT Stubbed isolando nimbus-jose-jwt.
 * Garante ciclo de vida do Authorization Bearer Tokens do Spring Security.
 */
@Service
public class JwtService {

    public String generateToken(UUID usuarioId) {
        // Implementação Omitida por mock: Jwts.builder().setSubject(usuarioId)...
        return "ey.ROLEZERO.MOCKED.TOKEN." + usuarioId.toString();
    }

    public boolean isTokenValid(String token) {
        return token != null && token.startsWith("ey.");
    }

    public String extractUserId(String token) {
        // Decriptaria a Claim subject
        return token.replace("ey.ROLEZERO.MOCKED.TOKEN.", "");
    }
}
