package com.FnxLucas.EventosBuffetAPI.Security;

import com.FnxLucas.EventosBuffetAPI.Dto.LoginRequestDto;
import com.FnxLucas.EventosBuffetAPI.Dto.UsuarioRegistroDto;
import com.FnxLucas.EventosBuffetAPI.Model.Usuario;
import com.FnxLucas.EventosBuffetAPI.Repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class SecurityIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String validAdminToken;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "Administrador do Sistema",
                    "ROLE_ADMIN"
            );
            usuarioRepository.save(admin);
        }
        validAdminToken = jwtTokenProvider.generateToken("admin", "ROLE_ADMIN", "Administrador do Sistema");
    }

    // ==========================================
    // 1. ROTAS PROTEGIDAS SEM TOKEN (401 UNAUTHORIZED)
    // ==========================================

    @Test
    @DisplayName("GET /clientes - Sem Token deve retornar 401 Unauthorized")
    void clientes_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("POST /clientes - Sem Token deve retornar 401 Unauthorized")
    void criarCliente_SemToken_Retorna401() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Teste Sem Token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /clientes/1 - Sem Token deve retornar 401 Unauthorized")
    void clientePorId_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("PUT /clientes/1 - Sem Token deve retornar 401 Unauthorized")
    void atualizarCliente_SemToken_Retorna401() throws Exception {
        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Teste Update Sem Token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("DELETE /clientes/1 - Sem Token deve retornar 401 Unauthorized")
    void deletarCliente_SemToken_Retorna401() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /eventos - Sem Token deve retornar 401 Unauthorized")
    void eventos_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/eventos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /eventos - Sem Token deve retornar 401 Unauthorized")
    void criarEvento_SemToken_Retorna401() throws Exception {
        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Evento Sem Token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /eventos/1 - Sem Token deve retornar 401 Unauthorized")
    void eventoPorId_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/eventos/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("PUT /eventos/1 - Sem Token deve retornar 401 Unauthorized")
    void atualizarEvento_SemToken_Retorna401() throws Exception {
        mockMvc.perform(put("/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Update Evento Sem Token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("DELETE /eventos/1 - Sem Token deve retornar 401 Unauthorized")
    void deletarEvento_SemToken_Retorna401() throws Exception {
        mockMvc.perform(delete("/eventos/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /orcamentos - Sem Token deve retornar 401 Unauthorized")
    void orcamentos_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/orcamentos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /orcamentos - Sem Token deve retornar 401 Unauthorized")
    void criarOrcamento_SemToken_Retorna401() throws Exception {
        mockMvc.perform(post("/orcamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacoes\":\"Sem Token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /orcamentos/1 - Sem Token deve retornar 401 Unauthorized")
    void orcamentoPorId_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/orcamentos/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /orcamentos/evento/1 - Sem Token deve retornar 401 Unauthorized")
    void orcamentosPorEvento_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/orcamentos/evento/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /orcamentos/1/aprovar - Sem Token deve retornar 401 Unauthorized")
    void aprovarOrcamento_SemToken_Retorna401() throws Exception {
        mockMvc.perform(post("/orcamentos/1/aprovar"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /orcamentos/1/recusar - Sem Token deve retornar 401 Unauthorized")
    void recusarOrcamento_SemToken_Retorna401() throws Exception {
        mockMvc.perform(post("/orcamentos/1/recusar"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /orcamentos/1/download-docx - Sem Token deve retornar 401 Unauthorized")
    void downloadDocx_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/orcamentos/1/download-docx"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /itens - Sem Token deve retornar 401 Unauthorized")
    void itens_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/itens"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /itens - Sem Token deve retornar 401 Unauthorized")
    void criarItem_SemToken_Retorna401() throws Exception {
        mockMvc.perform(post("/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeItem\":\"Item Sem Token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /auth/me - Sem Token deve retornar 401 Unauthorized")
    void me_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /dashboard/resumo - Sem Token deve retornar 401 Unauthorized")
    void dashboardResumo_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/dashboard/resumo"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /dashboard/agenda - Sem Token deve retornar 401 Unauthorized")
    void dashboardAgenda_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/dashboard/agenda"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    // ==========================================
    // 2. ROTAS PROTEGIDAS COM TOKEN INVÁLIDO OU ADULTERADO (401 UNAUTHORIZED)
    // ==========================================

    @Test
    @DisplayName("GET /clientes - Com Token Inválido deve retornar 401 Unauthorized")
    void clientes_TokenInvalido_Retorna401() throws Exception {
        mockMvc.perform(get("/clientes")
                        .header("Authorization", "Bearer token_invalido_falso_12345"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /eventos - Com Token Bearer Vazio deve retornar 401 Unauthorized")
    void eventos_BearerVazio_Retorna401() throws Exception {
        mockMvc.perform(get("/eventos")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /orcamentos - Com Header Inválido (sem Bearer) deve retornar 401 Unauthorized")
    void orcamentos_HeaderSemBearer_Retorna401() throws Exception {
        mockMvc.perform(get("/orcamentos")
                        .header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ="))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /auth/me - Com Token Adulterado deve retornar 401 Unauthorized")
    void me_TokenAdulterado_Retorna401() throws Exception {
        String tokenAdulterado = validAdminToken + "adulteracao";
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenAdulterado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    // ==========================================
    // 3. ROTAS PROTEGIDAS COM TOKEN VÁLIDO (200 OK)
    // ==========================================

    @Test
    @DisplayName("GET /clientes - Com Token Válido deve retornar 200 OK")
    void clientes_TokenValido_Retorna200() throws Exception {
        mockMvc.perform(get("/clientes")
                        .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /eventos - Com Token Válido deve retornar 200 OK")
    void eventos_TokenValido_Retorna200() throws Exception {
        mockMvc.perform(get("/eventos")
                        .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /orcamentos - Com Token Válido deve retornar 200 OK")
    void orcamentos_TokenValido_Retorna200() throws Exception {
        mockMvc.perform(get("/orcamentos")
                        .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /itens - Com Token Válido deve retornar 200 OK")
    void itens_TokenValido_Retorna200() throws Exception {
        mockMvc.perform(get("/itens")
                        .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /auth/me - Com Token Válido deve retornar 200 OK com dados do usuário")
    void me_TokenValido_Retorna200ComDados() throws Exception {
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("GET /dashboard/resumo - Com Token Válido deve retornar 200 OK")
    void dashboardResumo_TokenValido_Retorna200() throws Exception {
        mockMvc.perform(get("/dashboard/resumo")
                        .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /dashboard/agenda - Com Token Válido deve retornar 200 OK")
    void dashboardAgenda_TokenValido_Retorna200() throws Exception {
        mockMvc.perform(get("/dashboard/agenda")
                        .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // ==========================================
    // 4. FLUXO COMPLETO: REGISTRO -> LOGIN -> EXTRAÇÃO TOKEN -> USO EM ROTA PROTEGIDA -> REFRESH
    // ==========================================

    @Test
    @DisplayName("Fluxo Completo de Segurança E2E: Registro -> Login -> Rota Protegida -> Refresh Token")
    void fluxoCompleto_SegurancaE2E() throws Exception {
        String username = "usuario.teste.e2e";
        String password = "senhaForte123";
        String nomeCompleto = "Usuário Teste E2E";

        // Limpar usuário anterior se existir
        usuarioRepository.findByUsername(username).ifPresent(usuarioRepository::delete);

        // 1. Registro de novo usuário
        UsuarioRegistroDto registroDto = new UsuarioRegistroDto(username, password, nomeCompleto, "ROLE_USER");
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.nome").value(nomeCompleto))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        // 2. Login com credenciais válidas e obtenção do Token JWT
        LoginRequestDto loginDto = new LoginRequestDto(username, password);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value(username))
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = loginJson.get("token").asText();
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 3. Acesso à rota protegida /auth/me com o token gerado
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.nome").value(nomeCompleto));

        // 4. Acesso à rota protegida de negócio /clientes com o token gerado
        mockMvc.perform(get("/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 5. Renovação de Token (Refresh)
        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username))
                .andReturn();

        JsonNode refreshJson = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String refreshedToken = refreshJson.get("token").asText();
        assertNotNull(refreshedToken);

        // 6. Acesso à rota protegida com o novo token após o refresh
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + refreshedToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    // ==========================================
    // 5. LOGIN COM CREDENCIAIS INCORRETAS OU INVÁLIDAS
    // ==========================================

    @Test
    @DisplayName("POST /auth/login - Senha Incorreta deve retornar 401 Unauthorized")
    void login_SenhaIncorreta_Retorna401() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("admin", "senhaTotalmenteIncorreta");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciais inválidas: senha incorreta"));
    }

    @Test
    @DisplayName("POST /auth/login - Usuário Inexistente deve retornar 401 Unauthorized")
    void login_UsuarioInexistente_Retorna401() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("usuario_totalmente_inexistente_999", "qualquerSenha");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciais inválidas: usuário não encontrado"));
    }

    @Test
    @DisplayName("POST /auth/login - Body Vazio ou Campos Vazios deve retornar 400 Bad Request")
    void login_BodyInvalido_Retorna400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\", \"password\":\"123456\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\", \"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/registro - Username Duplicado deve retornar 400 Bad Request")
    void registro_UsernameDuplicado_Retorna400() throws Exception {
        UsuarioRegistroDto registroDto = new UsuarioRegistroDto("admin", "outraSenha123", "Admin Duplicado", "ROLE_ADMIN");
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username já cadastrado: admin"));
    }

    // ==========================================
    // 6. REQUISIÇÕES CORS PREFLIGHT (OPTIONS)
    // ==========================================

    @Test
    @DisplayName("OPTIONS /** - Preflight CORS permitido sem autenticação (200 OK)")
    void corsPreflight_PermitidoSemAutenticacao() throws Exception {
        mockMvc.perform(options("/clientes")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());

        mockMvc.perform(options("/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk());
    }
}
