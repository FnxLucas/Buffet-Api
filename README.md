# Buffet Organiza API

Uma API RESTful desenvolvida em Spring Boot para o gerenciamento de eventos e buffets. Este sistema permite o controle completo de clientes, eventos, orçamentos e itens, além de fornecer um painel comercial e geração automatizada de propostas em formato DOCX.

## 🚀 Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot** (Web, Data JPA, Security)
- **Spring Security & JWT** (Autenticação e Autorização)
- **Apache POI** (Geração de documentos DOCX)
- **Banco de Dados** (PostgreSQL)
- **JUnit 5 & MockMvc** (Testes Unitários e de Integração)
- **Maven** (Gerenciamento de dependências)

## 🎯 Principais Funcionalidades

- **Gestão de Clientes e Eventos:** Cadastro, atualização, listagem e exclusão de clientes e eventos com validações de regras de negócio (CPF/CNPJ).
- **Módulo de Orçamentos e Itens:** Criação de propostas orçamentárias, controle de itens, cálculos automáticos (subtotal, margem, desconto, total) e fluxos de aprovação/recusa.
- **Geração de Propostas (DOCX):** Geração dinâmica de propostas comerciais em formato Word (.docx) mesclando dados do cliente, evento e itens do orçamento.
- **Autenticação e Segurança:** Proteção de endpoints utilizando Spring Security e JWT. Endpoints de login, registro, refresh token e validação de sessão.
- **Painel Comercial (Dashboard):** Rotas específicas para geração de métricas de negócios, como taxa de conversão, faturamento total, ticket médio e agenda cronológica de eventos.

## 🛠️ Instalação e Execução

### Pré-requisitos
- Java 17 ou superior
- Maven

### Passos para executar localmente

1. Clone este repositório:
   ```bash
   git clone https://github.com/seu-usuario/EventosBuffetAPI.git
   ```
2. Acesse a pasta do projeto:
   ```bash
   cd EventosBuffetAPI
   ```
3. Compile e instale as dependências usando o Maven:
   ```bash
   ./mvnw clean install
   ```
4. Inicie a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
A API estará rodando por padrão na porta `8080`.

## 📚 Endpoints da API (Resumo)

### Autenticação (`/auth`)
- `POST /auth/login` - Autenticar usuário e gerar token JWT
- `POST /auth/registro` - Registrar novo usuário
- `POST /auth/refresh` - Atualizar token expirado
- `GET /auth/me` - Obter dados do usuário logado

### Dashboard (`/dashboard`)
- `GET /dashboard/resumo` - Resumo financeiro (faturamento, conversão, ticket médio)
- `GET /dashboard/agenda` - Agenda de eventos com filtros de período e status

### Clientes e Eventos
- `GET /clientes`, `POST /clientes`, `PUT /clientes/{id}`, `DELETE /clientes/{id}`
- `GET /eventos`, `POST /eventos`, `PUT /eventos/{id}`, `DELETE /eventos/{id}`

### Orçamentos e Itens
- `GET /orcamentos`, `POST /orcamentos`, `GET /orcamentos/evento/{id}`
- `POST /orcamentos/{id}/aprovar`, `POST /orcamentos/{id}/recusar`
- `GET /orcamentos/{id}/download-docx` - Baixar proposta em DOCX
- CRUD completo de Itens em `/itens`

## 🧪 Testes Automatizados

O projeto conta com uma cobertura completa de testes (mais de 150 testes unitários e de integração), garantindo a estabilidade e funcionamento de todos os módulos.

## 📄 Licença

Este projeto está sob a licença [MIT](https://opensource.org/licenses/MIT).
