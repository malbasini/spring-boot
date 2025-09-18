package com.example.demo.mycourse.controller;

import com.example.demo.mycourse.repository.UserRepository;
import com.example.demo.mycourse.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

// web/RolePageController.java
@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminRoleController {

    private final RoleService roleService;
    private final UserRepository userRepository;

    public AdminRoleController(RoleService roleService, UserRepository userRepository) {
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    @PostMapping("/assign")
    public String assign(@RequestParam String email, @RequestParam String role, RedirectAttributes ra) {
        String controls = controlli(email, role, ra);
        if(controls != null) return controls;
        int userId = userRepository.findByEmail(email).getId();
        try { roleService.assignSingleRole(userId, role);
            ra.addFlashAttribute("message","Ruolo assegnato.");
        } catch (Exception e) {
            ra.addFlashAttribute("message1", e.getMessage());
        }
        return "redirect:/admin/roles?role=" + UriUtils.encode(role, StandardCharsets.UTF_8);
    }

    @PostMapping("/revoke")
    public String revoke(@RequestParam String email, @RequestParam String role, RedirectAttributes ra) {
        String controls = controlli(email, role, ra);
        if(controls != null) return controls;
        int userId = userRepository.findByEmail(email).getId();
        try { roleService.removeRole(userId, role);
            ra.addFlashAttribute("message","Ruolo revocato.");
        } catch (Exception e) {
            ra.addFlashAttribute("message1", e.getMessage());
        }
        return "redirect:/admin/roles?role=" + UriUtils.encode(role, StandardCharsets.UTF_8);
    }
    private String controlli(String email, String role, RedirectAttributes ra)
    {
        if(email == null || role == null)
        {
            ra.addFlashAttribute("message1", "Email e/o ruolo non validi.");
            return "redirect:/admin/roles";
        }
        if (email.isEmpty() || role.isEmpty()) {
            ra.addFlashAttribute("message1", "Email e/o ruolo non validi.");
            return "redirect:/admin/roles";
        }
        return null;
    }
}
