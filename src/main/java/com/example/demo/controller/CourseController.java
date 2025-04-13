package com.example.demo.controller;
import com.example.demo.model.Course;
import com.example.demo.model.Lesson;
import com.example.demo.model.Register;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CaptchaValidator;
import com.example.demo.service.CoursesService;
import com.example.demo.service.EmailService;
import com.example.demo.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import com.example.demo.model.Subscription;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CaptchaValidator captchaValidator;
    @Autowired
    private CoursesService courseService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EmailService emailService;

    @Value("${upload.path:uploads}")
    private String uploadDir;
    // GET /courses -> listing di tutti i corsi con supporto a paginazione, ricerca e ordinamento
    @GetMapping
    public String listCourses(
            @RequestParam(defaultValue = "0") int page, // Pagina corrente
            @RequestParam(defaultValue = "10") int size, // Elementi per pagina
            @RequestParam(defaultValue = "") String title, // Filtro per titolo
            @RequestParam(defaultValue = "title") String sortBy, // Campo di ordinamento
            @RequestParam(defaultValue = "asc") String sortDirection,
            Principal principal,
            Model model) {

        String loggedUsername = principal.getName();
        Register user = userRepository.findByUsername(loggedUsername);
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_TEACHER")))
        {
            model.addAttribute("isTeacher",true);
        }
        // Ottenere i corsi con paginazione, ricerca e ordinamento
        Page<Course> courses = courseService.findCourses(page, size, title, sortBy, sortDirection);

        // Passare i dati al modello per JSP
        model.addAttribute("courses", courses.getContent()); // Lista dei corsi
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("titleFilter", title);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);

        return "courses/list"; // Nome della vista JSP
    }

    // GET /courses/new -> mostra form per creare un nuovo corso
    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/create"; // JSP da mostrare
    }
    // POST /courses -> salva un nuovo corso
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("course") Course course,
                         BindingResult bindingResult,
                         @RequestParam("g-recaptcha-response") String captchaResponse,
                         Model model,
                         Principal principal) {

        String username = principal.getName();  // lo username loggato
        Register user = courseService.findByUsername(username);
        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            model.addAttribute("error", "Captcha non valido. Riprova.");
            model.addAttribute("course", course);
            return "courses/create";// Torna alla pagina del form
        }
        if(course.getTitle()==null || course.getTitle().isEmpty()){
            model.addAttribute("error", "Il titolo è obbligatorio");
            return "courses/create";
        }
        if (bindingResult.hasErrors()) {
            return "courses/create"; // JSP da mostrare
        }
        try {
            course.setUser(user);
            Course corso = valorizzaDefault(course);
            Course c = courseService.save(corso);
            course.setId(c.getId());
        }
        catch (Exception ex){
            model.addAttribute("message", "Il titolo è già esistente");
            return "courses/create";
        }
        model.addAttribute("message1", "Inserimento corso avvenuto con successo. Ora inserisci gli altri dati");
        return "redirect:/courses/" + course.getId() + "/edit"; // JSP da mostrare

    }

    private Course valorizzaDefault(Course course) {
        course.setAuthor("");
        course.setDescription("");
        course.setEmail("noreply@example.com");
        course.setCurrentPriceCurrency("EUR");
        course.setImagePath("default.png");
        course.setFullPriceCurrency("EUR");
        course.setRating(BigDecimal.valueOf(1));
        course.setStatus("Draft");
        course.setRowVersion(String.valueOf(Date.valueOf(LocalDate.now().toString())));
        return course;
    }

    @GetMapping(value = "/course/{id}/detail")
    public String courseDetail(@PathVariable Integer id,
                               @RequestParam(name = "message", required = false) String message,
                               @RequestParam(name = "message1", required = false) String message1,
                               Model model,
                               Principal principal) {
        Course course = courseService.getCourseByIdWithLessons(id);
        if (course == null) {
            return "redirect:/courses"; // Gestione caso corso non trovato
        }
        boolean isTeacher = false;
        boolean isStudent = false;
        // L'utente loggato
        String loggedUsername = principal.getName();
        Register user = userRepository.findByUsername(loggedUsername);
        if(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"))) {
            course.setUser(user);// es: "mariorossi"
            isStudent = true;
            model.addAttribute("isStudent",isStudent);
            Subscription subscription = subscriptionRepository.findByCourse_Id(course.getId());
            if(subscription != null) {
                model.addAttribute("subscription", true);
            }
            else {
                model.addAttribute("subscription", false);
            }
        }
        if(course.getUser()==null)
            return "security/access-denied";
        if(course.getUser().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_TEACHER"))){
            isTeacher = true;
            model.addAttribute("isTeacher",isTeacher);
        }
        // Verifico se il proprietario del corso è lo stesso che ha fatto login
        boolean isOwner = (course.getUser().getUsername().equals(loggedUsername) && isTeacher);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);
        // Calcola la durata totale
        Duration totalDuration = calculateTotalDuration(course.getLessons());
        model.addAttribute("course", course);
        model.addAttribute("totalDuration", formatDuration(totalDuration));
        //ricavo se il provider dei pagamenti è paypal o stripe
        return "courses/detail"; // JSP da mostrare

    }
    // GET /courses/{id}/edit -> mostra form di modifica
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public String editCourse(@PathVariable("id") Integer id,
                             @RequestParam(name = "message", required = false) String message,
                             @RequestParam(name = "message1", required = false) String message1,
                             Model model,
                             Principal principal) {
        Course course = courseService.findById(id);
        if (course == null) {
            // gestisci errore se non trovato
            return "redirect:/courses";
        }
        if(course.getUser() == null) {
            return "security/access-denied";
        }
        else
        {
            boolean isTeacher = false;
            boolean isStudent = false;
            // L'utente loggato
            String loggedUsername = principal.getName();
            Register user = userRepository.findByUsername(loggedUsername);
            if(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"))) {
                course.setUser(user);// es: "mariorossi"
                isStudent = true;
                model.addAttribute("isStudent",isStudent);
                Subscription subscription = subscriptionRepository.findByCourse_Id(course.getId());
                if(subscription != null) {
                    model.addAttribute("subscription", true);
                }
            }
            if(course.getUser().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_TEACHER"))){
                isTeacher = true;
                model.addAttribute("isTeacher",isTeacher);
            }
            // Verifico se il proprietario del corso è lo stesso che ha fatto login
            boolean isOwner = (course.getUser().getUsername().equals(loggedUsername) && isTeacher);
            model.addAttribute("isOwner", isOwner);
            model.addAttribute("course", course);
            model.addAttribute("iduser",course.getUser().getId());
            model.addAttribute("message", message);
            model.addAttribute("descrizione", course.getDescription().trim());
            model.addAttribute("message1", message1);
            return "/courses/edit"; // JSP da mostrare
        }
    }
    // POST /courses/{id} -> aggiorna un corso esistente
    @PostMapping("/{id}/{iduser}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public String updateCourse(@PathVariable("id") Integer id, @PathVariable("iduser") Integer idUser,
                               @ModelAttribute("course") @Valid Course updatedCourse,
                               BindingResult bindingResult, Model model, Principal principal) {
        // L'utente loggato
        String loggedUsername = principal.getName(); // es: "mariorossi"
        if (idUser != null)
        {
            Register user = userRepository.findByUsername(loggedUsername);
            // Verifico se il proprietario del corso è lo stesso che ha fatto login
            if (!user.getUsername().equals(loggedUsername)) {
                    // se non sei il proprietario, redirect o errore
                return "security/access-denied";
            }
            else {
                updatedCourse.setUser(user);
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("course", updatedCourse);
            return "redirect:/courses/" + updatedCourse.getId() + "/edit"; // JSP da mostrare
        }
        String imagePath = courseService.findById(id).getImagePath();
         // Assicuriamoci che l'ID coincida
        updatedCourse.setId(id);
        updatedCourse.setCurrentPriceCurrency("EUR");
        updatedCourse.setFullPriceCurrency("EUR");
        updatedCourse.setImagePath(imagePath);
        updatedCourse.setRating(BigDecimal.valueOf(1));
        updatedCourse.setStatus("Draft");
        updatedCourse.setRowVersion(String.valueOf(Date.valueOf(LocalDate.now().toString())));
        updatedCourse.setDescription(updatedCourse.getDescription().trim());
        try{
            courseService.update(updatedCourse);
            String message = validazioni(updatedCourse,model);
            if(message != null) {
                model.addAttribute("message", message);
                return "redirect:/courses/" + updatedCourse.getId() + "/edit" + "?message=" + message;
            }
            model.addAttribute("message", "Course updated successfully");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/courses/" + updatedCourse.getId() + "/edit"; // JSP da mostrare
        }
        return "redirect:/courses";
}
    // POST /courses/{id}/delete -> cancella un corso
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public String deleteCourse(@PathVariable("id") Integer id,Principal principal,Model model) {
        Course course = courseService.findById(id);
        String loggedUsername = principal.getName(); // es: "mariorossi"
        Subscription subscription = subscriptionRepository.findByCourse_Id(id);
        if(subscription != null) {
            return "redirect:/courses/course/" + course.getId() + "/detail?" + "message=" + "Corso acquistato, impossibile eliminarlo";
        }
        // Verifico se il proprietario del corso è lo stesso che ha fatto login
        boolean isOwner = course.getUser().getUsername().equals(loggedUsername);
        Register user = courseService.findByUsername(course.getUser().getUsername());
        if (!isOwner) {
            // se non sei il proprietario, redirect o errore
            return "redirect:security/access-denied";
        }
        courseService.delete(id);
        return "redirect:/courses";
    }
    @GetMapping("/{idCourse}/vote")
    public String vote(@PathVariable("idCourse") Integer idCourse,Principal principal, Model model) {
        Course course = courseService.findById(idCourse);
        String loggedUsername = principal.getName(); // es: "mariorossi"
        Subscription subscription = subscriptionRepository.findByCourse_Id(idCourse);
        if(subscription != null) {
            model.addAttribute("courses",course);
            model.addAttribute("vote",subscription.getVote());
            return "courses/vote";
        }
        return "redirect:/courses/course/" + course.getId() + "/detail";
    }
    @PostMapping("/{idCourse}/vote")
    public String voteCourse(@PathVariable("idCourse") Integer idCourse, @RequestParam("vote") Integer vote, Principal principal, Model model) {
        Course course = courseService.findById(idCourse);
        String loggedUsername = principal.getName(); // es: "mariorossi"
        Subscription subscription = subscriptionRepository.findByCourse_Id(idCourse);
        if(subscription != null) {
            try {
                subscription.setVote(vote);
                subscription.setCourse(course);
                subscriptionService.save(subscription);
                return "redirect:/courses/course/" + course.getId() + "/detail?message1=" + "Grazie per aver espresso la tua opinione sul corso";
            }
            catch (Exception e) {
                model.addAttribute("message", e.getMessage());
                return "redirect:/courses/course/" + course.getId() + "/detail";
            }
        }
        return "redirect:/courses/course/" + course.getId() + "/detail";
    }
    @GetMapping("/{courseId}/question")
    public String getQuestion(@PathVariable("courseId") Integer courseId,Principal principal, Model model) {
        Course course = courseService.findById(courseId);
        String loggedUsername = principal.getName(); // es: "mariorossi"
        Subscription subscription = subscriptionRepository.findByCourse_Id(courseId);
        if(subscription != null) {
            model.addAttribute("courses",course);
            return "courses/question";
        }
        return "redirect:/courses/course/" + course.getId() + "/detail";
    }
    @PostMapping("/{courseId}/description")
    public String postQuestion(@PathVariable("courseId") Integer courseId,
                               @RequestParam("description") String question,
                               Principal principal,
                               Model model) {
        Course course = courseService.findById(courseId);
        Course c = courseService.getEmailByCourseIdAndAuthor(courseId,course.getAuthor());
        String email = c.getEmail();
        if(question.isEmpty()) {
            model.addAttribute("message","Domanda obbligatoria");
            model.addAttribute("courses",course);
            return "courses/question";
        }
        String loggedUsername = principal.getName(); // es: "mariorossi"
        Register user = courseService.findByUsername(loggedUsername);
        Subscription subscription = subscriptionRepository.findByCourse_Id(courseId);
        if(subscription != null) {
            try {
                emailService.sendSimpleEmail(
                         email ,
                        "Lo studente " + user.getFullname() + " ti ha inviato una domanda",
                        question.trim()
                );
            }
            catch (Exception e) {
                model.addAttribute("message","Errore invio email " + e.getMessage());
                return "redirect:/courses/course/" + course.getId() + "/detail";
            }
            return "redirect:/courses/course/" + course.getId() + "/detail?message1=" + "Grazie per aver inviato la tua domanda";
        }
        return "redirect:/courses/course/" + course.getId() + "/detail";
    }
    // Calcola la durata totale di una lista di lezioni

    private Duration calculateTotalDuration(List<Lesson> lessons) {
        return lessons.stream()
                .map(lesson -> Duration.between(LocalTime.MIN, LocalTime.parse(lesson.getDuration())))
                .reduce(Duration.ZERO, Duration::plus);
    }
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    @PostMapping("/{id}/uploadImage")
    public String uploadImage(@PathVariable("id") Integer id,
                              @RequestParam("imageFile") MultipartFile file,
                              Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "File non valido.");
            return "redirect:/courses/" + id + "/edit"; // Torna alla vista
        }
        Course course = courseService.findById(id);
        String message = validazioni(course,model);
        if(message != null) {
            model.addAttribute("message", message);
            return "redirect:/courses/" + id + "/edit";
        }
        try {
            // Creare la directory di upload se non esiste
            String uploadPath = new File(uploadDir).getAbsolutePath();
            File uploadDirectory = new File(uploadPath);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // Salva il file
            String fileName = file.getOriginalFilename();
            String filePath = uploadPath + File.separator + fileName;
            file.transferTo(new File(filePath));
            //salvo il percorso dell'immagine nel database
            Course c = courseService.findById(id);
            if (c == null) {
                // gestisci errore se non trovato
                return "redirect:/courses/" + id + "/edit";  // Torna alla vista
            }
            course.setImagePath("/uploads/" + fileName);
            courseService.update(course);
            // Successo
            model.addAttribute("success", "Immagine caricata con successo: " + fileName);
            model.addAttribute("filePath", "/uploads/" + fileName); // Percorso per visualizzare il file
            return "redirect:/courses"; // Torna alla vista

        } catch (IOException e) {
            model.addAttribute("error", "Errore durante il caricamento del file: " + e.getMessage());
            return "redirect:/courses/" + id + "/edit";  //
        }
    }
    public String validazioni(Course course, Model model)
    {
        String cleanedText = "";
        if(course.getDescription()!=null || !course.getDescription().isEmpty()) {
            cleanedText = course.getDescription().replaceAll("<[^>]*>", " ").trim();
        }
        else
        {
            model.addAttribute("message", "Descrizione obbligatoria");
            return "Descrizione obbligatoria";
        }
        if(course.getTitle()==null || course.getTitle().isEmpty()){
            model.addAttribute("message", "Titolo obbligatorio");
            return "Titolo obbligatorio";
        }
        if(course.getAuthor()==null || course.getAuthor().isEmpty()){
            model.addAttribute("message", "Autore obbligatorio");
            return "Autore obbligatorio";
        }
        if(course.getEmail()==null || course.getEmail().isEmpty()){
            model.addAttribute("message", "Email obbligatoria");
            return "Email obbligatoria";
        }
        if(course.getFullPriceAmount()==null || (course.getFullPriceAmount().compareTo(BigDecimal.ZERO) < 0)||(course.getFullPriceAmount().equals(BigDecimal.ZERO))){
            model.addAttribute("message", "Prezzo intero obbligatorio e deve essere maggiore di zero");
            return "Prezzo intero obbligatorio e deve essere maggiore di zero";
        }
        if(course.getCurrentPriceAmount()==null || (course.getCurrentPriceAmount().compareTo(BigDecimal.ZERO) < 0)||(course.getCurrentPriceAmount().equals(BigDecimal.ZERO))){
            model.addAttribute("message", "Prezzo corrente obbligatorio e deve essere maggiore di zero");
            return "Prezzo corrente obbligatorio e deve essere maggiore di zero";
        }
        if((course.getCurrentPriceAmount().compareTo(course.getFullPriceAmount())) > 0) {
           model.addAttribute("message", "prezzo scontato maggiore del prezzo intero");
           return "prezzo scontato maggiore del prezzo intero";
        }
        if(cleanedText.isEmpty()||cleanedText.equals(null)) {
            model.addAttribute("message", "Descrizione obbligatoria");
            return "Descrizione obbligatoria";
        }
        return null;
    }
}
