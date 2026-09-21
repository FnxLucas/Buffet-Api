package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Dto.LoginRequestDto;
import com.FnxLucas.EventosBuffetAPI.Dto.LoginResponseDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioRegistroDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioResponseDto;
import com.FnxLucas.EventosBuffetAPI.Security.JwtTokenProvider;
import com.FnxLucas.EventosBuffetAPI.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /auth/login - Sucesso (200 OK)")
    void login_Sucesso() {
        LoginRequestDto request = new LoginRequestDto("admin", "admin123");
        LoginResponseDto mockResponse = new LoginResponseDto("mock.jwt.token", "Bearer", "admin", "Administrador", "ROLE_ADMIN");

        when(usuarioService.autenticar(any(LoginRequestDto.class))).thenReturn(mockResponse);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponseDto);
        LoginResponseDto body = (LoginResponseDto) response.getBody();
        assertEquals("mock.jwt.token", body.getToken());
        assertEquals("admin", body.getUsername());
    }

    @Test
    @DisplayName("POST /auth/login - Credenciais Inválidas (401 Unauthorized)")
    void login_CredenciaisInvalidas() {
        LoginRequestDto request = new LoginRequestDto("admin", "senhaErrada");
        when(usuarioService.autenticar(any(LoginRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Credenciais inválidas: senha incorreta"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /auth/login - Body Nulo (400 Bad Request)")
    void login_BodyNulo() {
        ResponseEntity<?> response = authController.login(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /auth/login - Username ou Senha Vazios (400 Bad Request)")
    void login_CamposVazios() {
        ResponseEntity<?> response1 = authController.login(new LoginRequestDto("", "123456"));
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());

        ResponseEntity<?> response2 = authController.login(new LoginRequestDto("admin", ""));
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    @DisplayName("POST /auth/registro - Sucesso (201 Created)")
    void registro_Sucesso() {
        UsuarioRegistroDto request = new UsuarioRegistroDto("novo.usuario", "123456", "Novo Usuário", "ROLE_USER");
        UsuarioResponseDto mockResponse = new UsuarioResponseDto(2L, "novo.usuario", "Novo Usuário", "ROLE_USER");

        when(usuarioService.registrarUsuario(any(UsuarioRegistroDto.class))).thenReturn(mockResponse);

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof UsuarioResponseDto);
        UsuarioResponseDto body = (UsuarioResponseDto) response.getBody();
        assertEquals("novo.usuario", body.getUsername());
    }

    @Test
    @DisplayName("POST /auth/registro - Body Nulo (400 Bad Request)")
    void registro_BodyNulo() {
        ResponseEntity<?> response = authController.registrar(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /auth/registro - Username Duplicado (400 Bad Request)")
    void registro_UsernameDuplicado() {
        UsuarioRegistroDto request = new UsuarioRegistroDto("admin", "123456", "Admin", "ROLE_ADMIN");
        when(usuarioService.registrarUsuario(any(UsuarioRegistroDto.class)))
                .thenThrow(new IllegalArgumentException("Username já cadastrado: admin"));

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /auth/me - Usuário Autenticado (200 OK)")
    void me_UsuarioAutenticado() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        UsuarioResponseDto usuarioMock = new UsuarioResponseDto(1L, "admin", "Administrador", "ROLE_ADMIN");
        when(usuarioService.buscarPorUsername("admin")).thenReturn(usuarioMock);

        ResponseEntity<?> response = authController.me(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UsuarioResponseDto);
        UsuarioResponseDto body = (UsuarioResponseDto) response.getBody();
        assertEquals("admin", body.getUsername());
    }

    @Test
    @DisplayName("GET /auth/me - Sem Autenticação (401 Unauthorized)")
    void me_NaoAutenticado() {
        ResponseEntity<?> response = authController.me(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /auth/me - Usuário Não Encontrado (404 Not Found)")
    void me_UsuarioNaoEncontrado() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "usuario_removido",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(usuarioService.buscarPorUsername("usuario_removido"))
                .thenThrow(new IllegalArgumentException("Usuário não encontrado: usuario_removido"));

        ResponseEntity<?> response = authController.me(auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /auth/refresh - Sucesso com Header Authorization (200 OK)")
    void refresh_SucessoHeader() {
        String tokenAntigo = "token.antigo.valido";
        String tokenNovo = "token.novo.gerado";

        when(jwtTokenProvider.validateToken(tokenAntigo)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(tokenAntigo)).thenReturn("admin");

        UsuarioResponseDto usuario = new UsuarioResponseDto(1L, "admin", "Administrador", "ROLE_ADMIN");
        when(usuarioService.buscarPorUsername("admin")).thenReturn(usuario);
        when(jwtTokenProvider.generateToken("admin", "ROLE_ADMIN", "Administrador")).thenReturn(tokenNovo);

        ResponseEntity<?> response = authController.refresh("Bearer " + tokenAntigo, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponseDto);
        LoginResponseDto body = (LoginResponseDto) response.getBody();
        assertEquals(tokenNovo, body.getToken());
        assertEquals("admin", body.getUsername());
    }

    @Test
    @DisplayName("POST /auth/refresh - Sucesso com Body JSON (200 OK)")
    void refresh_SucessoBody() {
        String tokenAntigo = "token.antigo.valido";
        String tokenNovo = "token.novo.gerado";

        when(jwtTokenProvider.validateToken(tokenAntigo)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(tokenAntigo)).thenReturn("admin");

        UsuarioResponseDto usuario = new UsuarioResponseDto(1L, "admin", "Administrador", "ROLE_ADMIN");
        when(usuarioService.buscarPorUsername("admin")).thenReturn(usuario);
        when(jwtTokenProvider.generateToken("admin", "ROLE_ADMIN", "Administrador")).thenReturn(tokenNovo);

        ResponseEntity<?> response = authController.refresh(null, Map.of("token", tokenAntigo));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponseDto);
        LoginResponseDto body = (LoginResponseDto) response.getBody();
        assertEquals(tokenNovo, body.getToken());
    }

    @Test
    @DisplayName("POST /auth/refresh - Token Não Fornecido (400 Bad Request)")
    void refresh_TokenNaoFornecido() {
        ResponseEntity<?> response = authController.refresh(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /auth/refresh - Token Inválido ou Expirado (401 Unauthorized)")
    void refresh_TokenInvalido() {
        when(jwtTokenProvider.validateToken("token.expirado")).thenReturn(false);

        ResponseEntity<?> response = authController.refresh("Bearer token.expirado", null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
