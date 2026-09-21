package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Dto.LoginRequestDto;
import com.FnxLucas.EventosBuffetAPI.Dto.LoginResponseDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioRegistroDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioResponseDto;
import com.FnxLucas.EventosBuffetAPI.Security.JwtTokenProvider;
import com.FnxLucas.EventosBuffetAPI.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthController() {
    }

    public AuthController(UsuarioService usuarioService, JwtTokenProvider jwtTokenProvider) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) LoginRequestDto loginRequest) {
        if (loginRequest == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Credenciais não fornecidas"));
        }
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username é obrigatório"));
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Senha é obrigatória"));
        }

        try {
            LoginResponseDto response = usuarioService.autenticar(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Credenciais inválidas")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", msg));
            }
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody(required = false) UsuarioRegistroDto registroDto) {
        if (registroDto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Dados de cadastro não fornecidos"));
        }

        try {
            UsuarioResponseDto response = usuarioService.registrarUsuario(registroDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuário não autenticado"));
        }

        String username = authentication.getName();
        try {
            UsuarioResponseDto usuario = usuarioService.buscarPorUsername(username);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Map<String, String> body) {

        String token = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (body != null && body.containsKey("token") && StringUtils.hasText(body.get("token"))) {
            token = body.get("token");
        }

        if (!StringUtils.hasText(token)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token não fornecido"));
        }

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido ou expirado"));
        }

        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UsuarioResponseDto usuario = usuarioService.buscarPorUsername(username);
            String novoToken = jwtTokenProvider.generateToken(usuario.getUsername(), usuario.getRole(), usuario.getNome());

            LoginResponseDto response = new LoginResponseDto(
                    novoToken,
                    "Bearer",
                    usuario.getUsername(),
                    usuario.getNome(),
                    usuario.getRole()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Não foi possível renovar o token: " + e.getMessage()));
        }
    }
}
