package com.FnxLucas.EventosBuffetAPI.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class JwtAuthenticationEntryPointTest {

    @Test
    @DisplayName("commence - Deve responder 401 Unauthorized com JSON")
    void commence_Sucesso() throws Exception {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = new BadCredentialsException("Token inválido");

        when(request.getRequestURI()).thenReturn("/clientes");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        entryPoint.commence(request, response, exception);

        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonOutput = stringWriter.toString();
        assertTrue(jsonOutput.contains("401"));
        assertTrue(jsonOutput.contains("Unauthorized"));
        assertTrue(jsonOutput.contains("Token inválido"));
        assertTrue(jsonOutput.contains("/clientes"));
    }
}
