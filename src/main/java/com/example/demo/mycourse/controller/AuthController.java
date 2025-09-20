package com.example.demo.mycourse.controller;

import com.example.demo.mycourse.model.Role;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.RoleRepository;
import com.example.demo.mycourse.repository.UserRepository;
import com.example.demo.mycourse.service.CaptchaValidator;
import com.example.demo.mycourse.service.RoleService;
import com.example.demo.mycourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final CaptchaValidator captchaValidator;
    BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UserRepository userRepository;

    public AuthController(RoleService roleService,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          UserService userService,
                          CaptchaValidator captchaValidator,
                          BCryptPasswordEncoder passwordEncoder

                          ) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.captchaValidator = captchaValidator;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage() {
        return "redirect:/login?logout=true"; // restituisce la JSP login.jsp
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        User userForm = new User();
        model.addAttribute("userForm", userForm);
        return "security/login"; // restituisce la JSP login.jsp
    }

    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    public String doLogin(@ModelAttribute("userForm") User userForm,
                          Model model) {
        boolean isAuthenticated;
        User user = null;
        try {
            user = userService.login(userForm.getUsername());
        } catch (Exception e) {
            model.addAttribute("errore", "I dati inseriti non sono corretti");
            return "security/login"; // restituisce la JSP login.jsp
        }
        if (user.getUsername().equals(userForm.getUsername()) && passwordEncoder.matches(userForm.getPassword(), user.getPassword())) {
            isAuthenticated = true;
            model.addAttribute("username", userForm.getUsername());
            model.addAttribute("isAuthenticated", isAuthenticated);
            return "redirect:/";
        }
        model.addAttribute("errore", "I dati inseriti non sono corretti");
        return "security/login"; // restituisce la JSP login.jsp
    }


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterPage(Model model) {
        return "security/register"; // restituisce la JSP register.jsp
    }

    @RequestMapping(value = "/doRegister", method = RequestMethod.POST)
    public String doRegister(
            @RequestParam("username") String username,
            @RequestParam("fullname") String fullname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("roleId") String role,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            RedirectAttributes ra,
            Model model) {


        model.addAttribute("username", username);
        model.addAttribute("fullname", fullname);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("role", role);
        this.valorizzaCampi(model, username, fullname, email, password, role);
        //Controlli
        if (fullname.isEmpty()) {
            model.addAttribute("errore", "Valorizzare il fullname");
            return "security/register";
        }
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute("errore", "Valorizzare username, password ed email");
            return "security/register";
        }
        if (!role.equals("ROLE_STUDENT") && (!role.equals("ROLE_EDITOR") && !role.equals("ROLE_ADMIN"))) {
            model.addAttribute("errore", "Valorizzare il ruolo");
            return "security/register";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("errore", "Email non valida");
            return "security/register";
        }
        if (password.length() < 5) {
            model.addAttribute("errore", "Password non valida. La lunghezza deve essere di almeno cinque caratteri");
            return "security/register";
        }
        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            model.addAttribute("error", "Captcha non valido. Riprova.");
            this.valorizzaCampi(model, username, fullname, email, password, role);
            return "security/register";// Torna alla pagina del form
        }
        // Qui invochiamo il servizio che crea l'utente e assegna il ruolo
        try {
            User user = new User();
            user.setUsername(username);
            user.setFullname(fullname);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
            Role ruolo = roleRepository.findByName(role);
            user.setRoles(java.util.Collections.singleton(ruolo));
            User u = userService.registerNewUser(user);
            //AGGIUNGO LA RIGA ALLA TABELLA ROLE
            int userId = userRepository.findByEmail(email).getId();
            try {
                roleService.assignSingleRole(userId, role);
                model.addAttribute("message","Ruolo assegnato correttamente.");
                return "security/register";
            } catch (Exception e) {
                model.addAttribute("errore",e.getMessage());
                return "security/register";
            }
        } catch (Exception e) {
            model.addAttribute("errore",e.getMessage());
            return "security/register";
        }
    }
    private void valorizzaCampi(Model model, String username, String fullname, String email, String password, String role) {
        model.addAttribute("username", username);
        model.addAttribute("fullname", fullname);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("role", role);
    }
}