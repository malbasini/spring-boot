package com.example.demo.mycourse.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")  // nome tabella nel DB
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullname;

    @Column(name="revoked", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer revoke;

    @Column(name="user_id", nullable = false)
    private Integer userId;

    @Column(name="role", nullable = false)
    private String role;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRevoke() {
        return revoke;
    }

    public void setRevoke(Integer revoke) {
        this.revoke = revoke;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}