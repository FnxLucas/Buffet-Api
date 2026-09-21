package com.FnxLucas.EventosBuffetAPI.Dto;

public class UsuarioResponseDto {
    private Long id;
    private String username;
    private String nome;
    private String role;

    public UsuarioResponseDto() {
    }

    public UsuarioResponseDto(Long id, String username, String nome, String role) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
