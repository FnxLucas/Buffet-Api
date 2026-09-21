package com.FnxLucas.EventosBuffetAPI.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetailsService customUserDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = mock(JwtTokenProvider.class);
        customUserDetailsService = mock(CustomUserDetailsService.class);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("doFilterInternal - Token válido deve autenticar no SecurityContextHolder")
    void doFilterInternal_TokenValido_AutenticaUsuario() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token.valido.aqui");
        when(jwtTokenProvider.validateToken("token.valido.aqui")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("token.valido.aqui")).thenReturn("admin");

        UserDetails userDetails = new User("admin", "pass", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal - Header Authorization ausente não deve autenticar mas continuar a cadeia")
    void doFilterInternal_SemHeader_NaoAutentica() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    @DisplayName("doFilterInternal - Token inválido não deve autenticar mas continuar a cadeia")
    void doFilterInternal_TokenInvalido_NaoAutentica() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token.invalido");
        when(jwtTokenProvider.validateToken("token.invalido")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("getJwtFromRequest - Deve extrair token do header Bearer corretamente")
    void getJwtFromRequest_ExtraiToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer meu.jwt.token");

        String token = jwtAuthenticationFilter.getJwtFromRequest(request);

        assertEquals("meu.jwt.token", token);
    }

    @Test
    @DisplayName("getJwtFromRequest - Header sem Bearer retorna null")
    void getJwtFromRequest_SemBearerRetornaNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Basic 123456");

        String token = jwtAuthenticationFilter.getJwtFromRequest(request);

        assertNull(token);
    }
}
