package com.FnxLucas.EventosBuffetAPI.Dto;

public class LoginResponseDto {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String nome;
    private String role;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String tokenType, String username, String nome, String role) {
        this.token = token;
        this.tokenType = (tokenType != null && !tokenType.isEmpty()) ? tokenType : "Bearer";
        this.username = username;
        this.nome = nome;
        this.role = role;
    }

    public LoginResponseDto(String token, String username, String nome, String role) {
        this(token, "Bearer", username, nome, role);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
