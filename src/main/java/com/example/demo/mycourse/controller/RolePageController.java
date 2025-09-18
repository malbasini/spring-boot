package com.example.demo.mycourse.controller;

import com.example.demo.mycourse.repository.RoleRepository;
import com.example.demo.mycourse.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RolePageController {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;

    public RolePageController(RoleRepository roleRepo, UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String page(@RequestParam(value = "role", required = false) String activeRole, Model model) {
        var roles = roleRepo.findAll(); // contiene ROLE_ADMIN, ROLE_EDITOR, ...
        model.addAttribute("roles", roles);

        if (activeRole == null && !roles.isEmpty()) {
            activeRole = "ROLE_EDITOR"; // default tab (docenti)
        }
        model.addAttribute("activeRole", activeRole);

        if ("ROLE_ADMIN".equals(activeRole)) {
            model.addAttribute("admins", userRepo.findAllByRoles_Name("ROLE_ADMIN"));
        } else if ("ROLE_EDITOR".equals(activeRole)) {
            model.addAttribute("users", userRepo.findAllByRoles_Name("ROLE_EDITOR"));
        }
        // message / message1 li gestiremo lato JS; possono anche restare vuoti
        return "admin/role"; // il tuo template
    }
}