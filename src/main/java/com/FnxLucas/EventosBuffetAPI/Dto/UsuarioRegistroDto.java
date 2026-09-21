package com.FnxLucas.EventosBuffetAPI.Dto;

public class UsuarioRegistroDto {
    private String username;
    private String password;
    private String nome;
    private String role;

    public UsuarioRegistroDto() {
    }

    public UsuarioRegistroDto(String username, String password, String nome, String role) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
