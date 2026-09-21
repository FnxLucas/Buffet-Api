package com.FnxLucas.EventosBuffetAPI.Security;

import com.FnxLucas.EventosBuffetAPI.Model.Usuario;
import com.FnxLucas.EventosBuffetAPI.Repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("LoadUserByUsername - Sucesso")
    void loadUserByUsername_Sucesso() {
        Usuario usuario = new Usuario(1L, "admin", "encoded_password", "Administrador", "ROLE_ADMIN");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("LoadUserByUsername - Formata role sem prefixo ROLE_")
    void loadUserByUsername_AdicionaPrefixoRole() {
        Usuario usuario = new Usuario(2L, "user", "encoded_password", "Usuario Comum", "USER");
        when(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("LoadUserByUsername - Usuário não encontrado lança UsernameNotFoundException")
    void loadUserByUsername_NaoEncontrado() {
        when(usuarioRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("inexistente"));
    }
}
