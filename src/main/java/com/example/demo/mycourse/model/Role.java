package com.example.demo.mycourse.model;

import jakarta.persistence.Table;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;  // ad es. ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN ...

    @ManyToMany(mappedBy = "roles") // definito da "register_roles"
    private Set<User> users;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        return Objects.equals(name, ((Role) o).getName());
    }
    @Override public int hashCode() { return Objects.hash(name); }



}
