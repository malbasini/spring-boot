package com.example.demo.mycourse.service;

import com.example.demo.mycourse.repository.RoleRepository;
import com.example.demo.mycourse.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService{

    public static final String ROLE_ADMIN   = "ROLE_ADMIN";
    public static final String ROLE_TEACHER = "ROLE_EDITOR";

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public RoleServiceImpl(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }
    public void assignSingleRole(Integer userId, String roleName) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente inesistente"));
        var role = Optional.ofNullable(roleRepo.findByName(roleName))
                .orElseThrow(() -> new IllegalArgumentException("Ruolo inesistente: " + roleName));
        // blocco concorrenza su ADMIN
        if (ROLE_ADMIN.equals(roleName)) {
            userRepo.lockUsersByRoleName(ROLE_ADMIN); // crea un lock sulla “vista” admin
            var already = userRepo.findFirstByRoles_Name(ROLE_ADMIN);
            if (already.isPresent() && !already.get().getId().equals(userId)) {
                throw new IllegalStateException("Esiste già un amministratore: " + already.get().getUsername());
            }
        }

        // se vuoi che ogni utente abbia UN SOLO ruolo effettivo:
        try {
            user.getRoles().clear();
            user.getRoles().add(role);
        }
        catch( Exception e){}
        // se invece l'utente può avere più ruoli,
        // sostituisci le due righe sopra con:
        // user.getRoles().add(role);

        // flush automatico con @Transactional
    }
    public void removeRole(Integer userId, String roleName) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente inesistente"));
        if (ROLE_ADMIN.equals(roleName)) {
            // vieta di rimuovere l’unico ADMIN
            userRepo.lockUsersByRoleName(ROLE_ADMIN);
            long adminCount = userRepo.countByRoles_Name(ROLE_ADMIN);
            if (adminCount <= 1 && user.getRoles().stream().anyMatch(r -> ROLE_ADMIN.equals(r.getName()))) {
                throw new IllegalStateException("Non puoi rimuovere l'unico amministratore.");
            }
        }
        user.getRoles().removeIf(r -> r.getName().equals(roleName));
    }
}
