package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.Register;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Optional<Register> user = userRepository.findByUsernameWithRoles(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Utente non trovato con username: " + username);
        }

        // Mappiamo i ruoli su SimpleGrantedAuthority
        return new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                user.get().isEnabled(),
                true,   // accountNonExpired
                true,   // credentialsNonExpired
                true,   // accountNonLocked
                user.get().getRoles().stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }
}
