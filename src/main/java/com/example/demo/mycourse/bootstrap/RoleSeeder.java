package com.example.demo.mycourse.bootstrap;

import com.example.demo.mycourse.model.Role;
import com.example.demo.mycourse.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements ApplicationRunner {
    private final RoleRepository roleRepo;

    public RoleSeeder(RoleRepository roleRepo) { this.roleRepo = roleRepo; }

    @Override
    public void run(ApplicationArguments args) {
        // seed idempotente
        ensure("ROLE_ADMIN");
        ensure("ROLE_EDITOR");
        ensure("ROLE_STUDENT");
    }

    private void ensure(String name) {
        if (roleRepo.findByName(name) == null) {
            var r = new Role();
            r.setName(name);
            roleRepo.save(r);
        }
    }
}