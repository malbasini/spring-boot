package com.example.demo.mycourse.controller;

import com.example.demo.mycourse.model.Admin;
import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.model.Role;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.AdminRepository;
import com.example.demo.mycourse.repository.CourseRepository;
import com.example.demo.mycourse.repository.RoleRepository;
import com.example.demo.mycourse.repository.UserRepository;
import com.example.demo.mycourse.service.AdminService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping
public class AdminController {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/role/{role}")
    public String showRoleAssignPage(@PathVariable(value = "role",required = false) String inRole,
                                     @RequestParam(value = "message",required = false) String message,
                                     @RequestParam(value = "message1",required = false) String message1,
                                     Model model) {
        List<Role> roles = roleRepository.findAll() ;
        roles.removeIf(role -> role.getName().equals("ROLE_STUDENT"));
        // Gestisce il ruolo attivo selezionato (default EDIT se nullo)
        Role activeRole = inRole != null ? roleRepository.findByName(inRole) : roleRepository.findByName("ROLE_EDITOR");
        model.addAttribute("activeRole", activeRole.getName());
        if(activeRole.getName().equals("ROLE_ADMIN")){
            List<Admin> admin = adminRepository.findAll();
            admin = admin.stream().filter(z -> z.getRole().equals("ROLE_ADMIN")).toList();
            model.addAttribute("admins", admin);
        }
        if(activeRole.getName().equals("ROLE_EDITOR")) {
            List<Admin> users = adminRepository.findAll();
            users = users.stream().filter(z -> z.getRole().equals("ROLE_EDITOR")).toList();
            model.addAttribute("users", users);
        }
        model.addAttribute("roles", roles);
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);
        return "admin/role";
    }

    private void popolaTabella() {
        List<User> users = userRepository.findAll();
        for(User user : users){
            Admin admin = new Admin();
            admin.setFullname(user.getFullname());
            admin.setEmail(user.getEmail());
            admin.setUserId(user.getId());
            admin.setRole("ROLE_EDITOR");
            admin.setRevoke(0);
            Admin savedAdmin = adminRepository.save(admin);
        }
    }

    @PostMapping("/assign")
    public String roleAssign(@RequestParam("email") String email,
                             @RequestParam("role") String role,
                             Model model,
                             Authentication authentication ){

        String message = Controlli(email,role);
        if(!message.isEmpty()){
            return "redirect:/role/" + role + "?message1= " + message ;
        }
        User user = userRepository.findByEmail(email);
        if(user == null){
            return "redirect:/role/" + role + "?message1=L'email non esiste!" ;
        }
        Admin admin = new Admin();
        admin.setFullname(user.getFullname());
        admin.setEmail(email);
        admin.setUserId(user.getId());
        admin.setRole(role);
        admin.setRevoke(0);
        //VERIFICO CHE CI SIA UN SOLO AMMINISTRATORE
        if(role.equals("ROLE_ADMIN")){
            Admin a = adminRepository.findByRole("ROLE_ADMIN");
            if(a != null){
                return "redirect:/role/" + role + "?message1=Ci puo essere un solo amministratore!" ;
            }
            Admin ad = adminRepository.findByEmailAndRole(email,"ROLE_EDITOR");
            if(ad != null) {
                return "redirect:/role/" + role + "?message1=L'amministratore non puo essere anche editore!";
            }
        }
        //L'AMMINISTRATORE NON PUO' AVERE DUE RUOLI
        if(role.equals("ROLE_EDITOR")){
            Admin a = adminRepository.findByEmailAndRole(email,"ROLE_ADMIN");
            if(a != null){
                return "redirect:/role/" + role + "?message1=L'amministratore non puo essere anche editore!" ;
            }
        }
        Role r = roleRepository.findByName(role);
        //AGGIORNO IL RUOLO NELLA TABELLA REGISTER
        if (!user.getRoles().contains(r)) {
            user.getRoles().add(r);
            userRepository.save(user);
        }
        Admin savedAdmin = adminRepository.save(admin);
        return "redirect:/role/" + role + "?message=Ruolo assegnato correttamente" ;

    }

    private String Controlli(String email, String role) {

        String message = "";
        if(email.isEmpty()){
            message= "Valorizzare la mail!";
        }
        if(role.isEmpty()){
            message= "Valorizzare il ruolo!";
        }
        //VERIFICO CHE IL RUOLO NON SIA STATO GIA' ASSEGNATO E CHE LA MAIL ESISTA
        if(!email.isEmpty() && !role.isEmpty()){
            Admin admin = adminRepository.findByEmailAndRole(email,role);
            if(admin != null){
                if(admin.getRole().equals(role) && admin.getRevoke() == 0){
                    message= "Ruolo gia assegnato correttamente!";
                }
            }
        }
        return message;
    }

    @PostMapping("/revoke")
    public String roleRevoke(@RequestParam("role") String role,
                             @RequestParam("email") String email,
                             Model model,
                             Authentication authentication ){

        String message = "";
        if(email.isEmpty()){
            message= "Valorizzare la mail!";
        }
        if(role.isEmpty()){
            message= "Valorizzare il ruolo!";
        }
        if(!message.isEmpty()){
            return "redirect:/role/" + role + "?message1= " + message ;
        }
        String username = authentication.getName();  // lo username loggato
        User user = userRepository.findByUsername(username);
        Admin admin = null;
        if(!role.isEmpty() && !email.isEmpty()){
            admin = adminRepository.findByEmailAndRole(email,role);
        }
        //AGGIORNO IL RUOLO NELLA TABELLA REGISTER
        Role r = roleRepository.findByName(role);
        if (user.getRoles().contains(r)) {
            user.getRoles().remove(r);
            userRepository.save(user);
        }
        adminRepository.delete(admin);
        return "redirect:/role/" + role + "?message=Ruolo revocato correttamente";

    }
}
