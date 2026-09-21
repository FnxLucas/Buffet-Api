package com.FnxLucas.EventosBuffetAPI.Security;

import com.FnxLucas.EventosBuffetAPI.Model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "TestSecretKeyBuffetOrganizaSecurityLongKey256Bits123456789";
    private final long expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    @DisplayName("Geração e Validação de Token JWT - Sucesso")
    void gerarEValidarToken_Sucesso() {
        Usuario usuario = new Usuario(1L, "admin", "senha", "Administrador", "ROLE_ADMIN");

        String token = jwtTokenProvider.generateToken(usuario);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("admin", jwtTokenProvider.getUsernameFromToken(token));
        assertEquals("ROLE_ADMIN", jwtTokenProvider.getRoleFromToken(token));
        assertEquals("Administrador", jwtTokenProvider.getNomeFromToken(token));
    }

    @Test
    @DisplayName("Validação de Token - Token inválido deve retornar false")
    void validarToken_Invalido() {
        assertFalse(jwtTokenProvider.validateToken("token.invalido.falso"));
        assertFalse(jwtTokenProvider.validateToken(null));
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    @DisplayName("Validação de Token - Token expirado deve retornar false")
    void validarToken_Expirado() {
        // JwtTokenProvider with expiration in negative time (already expired)
        JwtTokenProvider expiredProvider = new JwtTokenProvider(secret, -1000);
        String expiredToken = expiredProvider.generateToken("user", "ROLE_USER", "Usuario");

        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }
}
