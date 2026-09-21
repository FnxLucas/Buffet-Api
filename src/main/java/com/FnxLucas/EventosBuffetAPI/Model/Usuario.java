package com.FnxLucas.EventosBuffetAPI.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String nome;

    @Column(nullable = false)
    private String role;

    public Usuario() {
    }

    public Usuario(Long id, String username, String password, String nome, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.role = role;
    }

    public Usuario(String username, String password, String nome, String role) {
        this.username = username;
        this.password = password;
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
