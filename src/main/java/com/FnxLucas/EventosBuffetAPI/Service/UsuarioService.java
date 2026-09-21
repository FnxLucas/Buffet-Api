package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Dto.LoginRequestDto;
import com.FnxLucas.EventosBuffetAPI.Dto.LoginResponseDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioRegistroDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioResponseDto;
import com.FnxLucas.EventosBuffetAPI.Model.Usuario;
import com.FnxLucas.EventosBuffetAPI.Repository.UsuarioRepository;
import com.FnxLucas.EventosBuffetAPI.Security.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public UsuarioService() {
    }

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UsuarioResponseDto registrarUsuario(UsuarioRegistroDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados de cadastro não fornecidos");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        if (dto.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

        String sanitizedUsername = dto.getUsername().trim().toLowerCase();
        if (usuarioRepository.existsByUsername(sanitizedUsername)) {
            throw new IllegalArgumentException("Username já cadastrado: " + sanitizedUsername);
        }

        String role = (dto.getRole() != null && !dto.getRole().trim().isEmpty())
                ? dto.getRole().trim().toUpperCase()
                : "ROLE_USER";

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        String nome = (dto.getNome() != null && !dto.getNome().trim().isEmpty())
                ? dto.getNome().trim()
                : sanitizedUsername;

        Usuario usuario = new Usuario(
                sanitizedUsername,
                passwordEncoder.encode(dto.getPassword().trim()),
                nome,
                role
        );

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDto(
                salvo.getId(),
                salvo.getUsername(),
                salvo.getNome(),
                salvo.getRole()
        );
    }

    public LoginResponseDto autenticar(LoginRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Credenciais não fornecidas");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        String sanitizedUsername = dto.getUsername().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByUsername(sanitizedUsername)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas: usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getPassword().trim(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas: senha incorreta");
        }

        String token = jwtTokenProvider.generateToken(usuario);

        return new LoginResponseDto(
                token,
                "Bearer",
                usuario.getUsername(),
                usuario.getNome(),
                usuario.getRole()
        );
    }

    public UsuarioResponseDto buscarPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não fornecido");
        }

        String sanitizedUsername = username.trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByUsername(sanitizedUsername)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + sanitizedUsername));

        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNome(),
                usuario.getRole()
        );
    }

    @PostConstruct
    @Transactional
    public void inicializarUsuarioAdminSeNecessario() {
        try {
            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario admin = new Usuario(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "Administrador do Sistema",
                        "ROLE_ADMIN"
                );
                usuarioRepository.save(admin);
            }
        } catch (Exception e) {
            // Safe fallback during specific bootstrap / test lifecycle phases
        }
    }
}
