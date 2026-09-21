package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Dto.LoginRequestDto;
import com.FnxLucas.EventosBuffetAPI.Dto.LoginResponseDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioRegistroDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioResponseDto;
import com.FnxLucas.EventosBuffetAPI.Model.Usuario;
import com.FnxLucas.EventosBuffetAPI.Repository.UsuarioRepository;
import com.FnxLucas.EventosBuffetAPI.Security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPadrao;

    @BeforeEach
    void setUp() {
        usuarioPadrao = new Usuario(1L, "lucas", "hash_senha_123", "Lucas Silva", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Registrar Usuário - Sucesso")
    void registrarUsuario_Sucesso() {
        UsuarioRegistroDto dto = new UsuarioRegistroDto("lucas", "senha123", "Lucas Silva", "ROLE_ADMIN");

        when(usuarioRepository.existsByUsername("lucas")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_senha_123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPadrao);

        UsuarioResponseDto response = usuarioService.registrarUsuario(dto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("lucas", response.getUsername());
        assertEquals("Lucas Silva", response.getNome());
        assertEquals("ROLE_ADMIN", response.getRole());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar Usuário - Atribui ROLE_USER quando role não informada")
    void registrarUsuario_RoleDefault() {
        UsuarioRegistroDto dto = new UsuarioRegistroDto("maria", "senha123", "Maria", null);
        Usuario usuarioSalvo = new Usuario(2L, "maria", "hash_senha_123", "Maria", "ROLE_USER");

        when(usuarioRepository.existsByUsername("maria")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_senha_123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponseDto response = usuarioService.registrarUsuario(dto);

        assertNotNull(response);
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    @DisplayName("Registrar Usuário - Erro quando DTO é nulo")
    void registrarUsuario_Erro_DtoNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(null));
        assertEquals("Dados de cadastro não fornecidos", ex.getMessage());
    }

    @Test
    @DisplayName("Registrar Usuário - Erro quando Username é vazio")
    void registrarUsuario_Erro_UsernameVazio() {
        UsuarioRegistroDto dto = new UsuarioRegistroDto("", "senha123", "Nome", "ROLE_USER");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(dto));
        assertEquals("Username é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Registrar Usuário - Erro quando Senha tem menos de 6 caracteres")
    void registrarUsuario_Erro_SenhaCurta() {
        UsuarioRegistroDto dto = new UsuarioRegistroDto("user1", "123", "Nome", "ROLE_USER");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(dto));
        assertEquals("Senha deve ter no mínimo 6 caracteres", ex.getMessage());
    }

    @Test
    @DisplayName("Registrar Usuário - Erro quando Username já existe")
    void registrarUsuario_Erro_UsernameDuplicado() {
        UsuarioRegistroDto dto = new UsuarioRegistroDto("lucas", "senha123", "Lucas Silva", "ROLE_USER");
        when(usuarioRepository.existsByUsername("lucas")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(dto));
        assertEquals("Username já cadastrado: lucas", ex.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Autenticar - Sucesso com retorno de Token JWT")
    void autenticar_Sucesso() {
        LoginRequestDto dto = new LoginRequestDto("lucas", "senha123");

        when(usuarioRepository.findByUsername("lucas")).thenReturn(Optional.of(usuarioPadrao));
        when(passwordEncoder.matches("senha123", "hash_senha_123")).thenReturn(true);
        when(jwtTokenProvider.generateToken(usuarioPadrao)).thenReturn("mocked.jwt.token");

        LoginResponseDto response = usuarioService.autenticar(dto);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("lucas", response.getUsername());
        assertEquals("Lucas Silva", response.getNome());
        assertEquals("ROLE_ADMIN", response.getRole());
    }

    @Test
    @DisplayName("Autenticar - Erro quando usuário não existe")
    void autenticar_Erro_UsuarioNaoExiste() {
        LoginRequestDto dto = new LoginRequestDto("inexistente", "senha123");
        when(usuarioRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticar(dto));
        assertEquals("Credenciais inválidas: usuário não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Autenticar - Erro quando senha é incorreta")
    void autenticar_Erro_SenhaIncorreta() {
        LoginRequestDto dto = new LoginRequestDto("lucas", "senhaErrada");

        when(usuarioRepository.findByUsername("lucas")).thenReturn(Optional.of(usuarioPadrao));
        when(passwordEncoder.matches("senhaErrada", "hash_senha_123")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticar(dto));
        assertEquals("Credenciais inválidas: senha incorreta", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar Por Username - Sucesso")
    void buscarPorUsername_Sucesso() {
        when(usuarioRepository.findByUsername("lucas")).thenReturn(Optional.of(usuarioPadrao));

        UsuarioResponseDto response = usuarioService.buscarPorUsername("lucas");

        assertNotNull(response);
        assertEquals("lucas", response.getUsername());
        assertEquals("Lucas Silva", response.getNome());
    }

    @Test
    @DisplayName("Buscar Por Username - Não Encontrado")
    void buscarPorUsername_NaoEncontrado() {
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.buscarPorUsername("fantasma"));
        assertEquals("Usuário não encontrado: fantasma", ex.getMessage());
    }

    @Test
    @DisplayName("Bootstrap Admin - Cria usuário admin padrão quando não existe")
    void inicializarUsuarioAdminSeNecessario_CriaAdmin() {
        when(usuarioRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("admin_hash");

        usuarioService.inicializarUsuarioAdminSeNecessario();

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Bootstrap Admin - Não recria quando admin já existe")
    void inicializarUsuarioAdminSeNecessario_AdminJaExiste() {
        when(usuarioRepository.existsByUsername("admin")).thenReturn(true);

        usuarioService.inicializarUsuarioAdminSeNecessario();

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
