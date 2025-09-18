package com.example.demo.mycourse.controller;

import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.model.Lesson;
import com.example.demo.mycourse.model.Subscription;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.LessonRepository;
import com.example.demo.mycourse.repository.SubscriptionRepository;
import com.example.demo.mycourse.repository.UserRepository;
import com.example.demo.mycourse.service.CaptchaValidator;
import com.example.demo.mycourse.service.CourseService;
import com.example.demo.mycourse.service.EmailService;
import com.example.demo.mycourse.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
// import… (tutti i tuoi import esistenti)

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CaptchaValidator captchaValidator;
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;
    private final LessonRepository lessonRepository;

    @Value("${upload.path:uploads}")
    private String uploadDir;

    public CourseController(CaptchaValidator captchaValidator,
                            CourseService courseService,
                            UserRepository userRepository,
                            SubscriptionService subscriptionService,
                            SubscriptionRepository subscriptionRepository,
                            EmailService emailService,
                            LessonRepository lessonRepository) {
        this.captchaValidator = captchaValidator;
        this.courseService = courseService;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
        this.lessonRepository = lessonRepository;
    }

    // ================== LIST ==================
    @GetMapping
    public String listCourses(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "") String title,
                              @RequestParam(defaultValue = "title") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDirection,
                              @RequestParam(name = "message", required = false) String message,
                              Authentication auth,
                              Model model) {

        boolean isAdmin = hasAuthority(auth, "ROLE_ADMIN");
        boolean isTeacher = hasAuthority(auth, "ROLE_EDITOR");

        Page<Course> courses = courseService.findCourses(page, size, title, sortBy, sortDirection);

        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("titleFilter", title);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("message", message);

        // flag per JSP
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isAdmin", isAdmin);
        return "courses/list";
    }

    // ================== CREATE ==================
    @GetMapping("/new")
    @PreAuthorize("hasAnyAuthority('ROLE_EDITOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/create";
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EDITOR')")
    public String create(@Valid @ModelAttribute("course") Course course,
                         BindingResult bindingResult,
                         @RequestParam("g-recaptcha-response") String captchaResponse,
                         Model model,
                         Authentication authentication) {

        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            model.addAttribute("error", "Captcha non valido. Riprova.");
            model.addAttribute("course", course);
            return "courses/create";
        }
        if (course.getTitle() == null || course.getTitle().isEmpty()) {
            model.addAttribute("message", "Il titolo è obbligatorio");
            return "courses/create";
        }
        if (bindingResult.hasErrors()) return "courses/create";

        // Proprietario = utente loggato (docente/admin)
        String username = authentication.getName();
        User user = courseService.findByUsername(username);

        course.setUserOwner(user);
        course.setCurrentPriceCurrency("EUR");
        course.setFullPriceCurrency("EUR");
        course.setRating(BigDecimal.valueOf(1.0));
        course.setImagePath("default.png");
        course.setAuthor(user.getFullname());
        course.setCurrentPriceAmount(BigDecimal.valueOf(0.0));
        course.setFullPriceAmount(BigDecimal.valueOf(0.0));
        course.setDescription(defaultIfBlank(course.getDescription(),
                "Descrizione mancante"));

        try {
            Course saved = courseService.saveCourse(course);
            return "redirect:/courses/" + saved.getId()
                    + "/edit?message1=" + UriUtils.encode("Inserimento corso avvenuto con successo. Ora inserisci gli altri dati!", StandardCharsets.UTF_8);
        } catch (Exception ex) {
            model.addAttribute("message", "Il titolo è già esistente");
            return "courses/create";
        }
    }

    // ================== DETAIL ==================
    @GetMapping("/course/{id}/detail")
    public String courseDetail(@PathVariable Integer id,
                               @RequestParam(name = "message", required = false) String message,
                               @RequestParam(name = "message1", required = false) String message1,
                               Authentication auth,
                               Model model) {

        Course course = courseService.getCourseByIdWithLessons(id);
        String loggedUsername = auth.getName();
        if (course == null) return "redirect:/courses";
        if (course.getUserOwner() == null) return "security/access-denied";

        boolean isAdmin = hasAuthority(auth, "ROLE_ADMIN");
        boolean isStudent = hasAuthority(auth, "ROLE_STUDENT");
        String ownerUsername = course.getUserOwner().getUsername();
        // È ancora docente?
        boolean ownerIsStillEditor = userRepository.existsByUsernameAndRoles_Name(ownerUsername, "ROLE_EDITOR");
        boolean canEdit = (course.getUserOwner().getUsername().equals(loggedUsername)) && (ownerIsStillEditor);
        boolean subscription = false;

        if (isStudent) {
            Subscription s = subscriptionRepository.findByCourse_Id(course.getId());
            subscription = (s != null);
        }

        model.addAttribute("canEdit", canEdit);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("subscription", subscription);
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);

        Duration totalDuration = calculateTotalDuration(course.getLessons());
        model.addAttribute("course", course);
        model.addAttribute("totalDuration", formatDuration(totalDuration));
        return "courses/detail";
    }

    // ================== EDIT ==================
    @GetMapping("/{id}/edit")
    @PreAuthorize("(@permission.isOwnerOfCourse(#id, authentication) " + " and @permission.userHasRole(authentication.name, 'ROLE_EDITOR'))")
    public String editCourse(@PathVariable("id") Integer id,
                             @RequestParam(name = "message", required = false) String message,
                             @RequestParam(name = "message1", required = false) String message1,
                             Authentication auth,
                             Model model) {
        String loggedUsername = auth.getName();
        Course course = courseService.findById(id);
        if (course == null) return "redirect:/courses";
        if (course.getUserOwner() == null) return "security/access-denied";

        boolean isAdmin = hasAuthority(auth, "ROLE_ADMIN");
        boolean isStudent = hasAuthority(auth, "ROLE_STUDENT");
        boolean subscription = false;

        if (isStudent) {
            Subscription s = subscriptionRepository.findByCourse_Id(course.getId());
            if (s != null) {
                subscription = true;
                // La pagina JSP usa i flag per mostrare messaggi/sezioni
                model.addAttribute("subscription", subscription);
                model.addAttribute("isStudent", true);
                model.addAttribute("course", course);
                model.addAttribute("iduser", course.getUserOwner().getId());
                model.addAttribute("message", message);
                model.addAttribute("message1", message1);
                // (In realtà questo branch non dovrebbe attivarsi perché lo studente non passa il @PreAuthorize)
                return "courses/edit";
            }
        }
        String ownerUsername = course.getUserOwner().getUsername();
        // È ancora docente?
        boolean ownerIsStillEditor = userRepository.existsByUsernameAndRoles_Name(ownerUsername, "ROLE_EDITOR");
        boolean canEdit = (course.getUserOwner().getUsername().equals(loggedUsername)) && (ownerIsStillEditor);
        model.addAttribute("canEdit", canEdit);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("subscription", subscription);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("course", course);
        model.addAttribute("iduser", course.getUserOwner().getId());
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);
        return "courses/edit";
    }

    // ================== UPDATE ==================
    @PostMapping("/{id}/{iduser}")
    @PreAuthorize("(@permission.isOwnerOfCourse(#id, authentication) " + " and @permission.userHasRole(authentication.name, 'ROLE_EDITOR'))")
    public String updateCourse(@PathVariable("id") Integer id,
                               @PathVariable("iduser") Integer idUser,
                               @ModelAttribute("course") @Valid Course updatedCourse,
                               BindingResult bindingResult,
                               Authentication auth,
                               Model model,
                               @RequestParam(name = "message", required = false) String message,
                               @RequestParam(name = "message1", required = false) String message1) {

        Course persisted = courseService.findById(id);
        if (persisted == null) return "redirect:/courses";

        // Manteniamo owner e campi non editabili lato form
        updatedCourse.setId(id);
        updatedCourse.setUserOwner(persisted.getUserOwner());
        updatedCourse.setLessons(lessonRepository.findByCourseId(id));
        updatedCourse.setImagePath(persisted.getImagePath());
        updatedCourse.setRating(persisted.getRating());
        updatedCourse.setCurrentPriceCurrency("EUR");
        updatedCourse.setFullPriceCurrency("EUR");

        if (bindingResult.hasErrors()) {
            model.addAttribute("course", updatedCourse);
            return "redirect:/courses/" + id + "/edit?message=" + UriUtils.encode("Errore nel binding!", StandardCharsets.UTF_8);
        }

        try {
            String m = validazioni(updatedCourse, model);
            if (m != null) {
                return "redirect:/courses/" + id + "/edit?message=" + UriUtils.encode("Errore di validazione! " + m, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return "redirect:/courses/" + id + "/edit?message=" + UriUtils.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

        courseService.updateCourse(updatedCourse);
        return "redirect:/courses?message=" + UriUtils.encode("Corso aggiornato con successo!", StandardCharsets.UTF_8);
    }

    // ================== DELETE (SOLO ADMIN, se non acquistato) ==================
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteCourse(@PathVariable("id") Integer id) {
        Course course = courseService.findById(id);
        if (course == null) return "redirect:/courses";

        Subscription subscription = subscriptionRepository.findByCourse_Id(id);
        if (subscription != null) {
            return "redirect:/courses/course/" + id + "/detail?message1="
                    + UriUtils.encode("Corso acquistato, impossibile eliminarlo!", StandardCharsets.UTF_8);
        }

        courseService.deleteCourse(id);
        return "redirect:/courses?message=" + UriUtils.encode("Corso eliminato con successo!", StandardCharsets.UTF_8);
    }

    // ================== VOTE / QUESTIONS (logica invariata) ==================
    @GetMapping("/{idCourse}/vote")
    public String vote(@PathVariable("idCourse") Integer idCourse, Authentication auth, Model model) {
        Course course = courseService.findById(idCourse);
        Subscription subscription = subscriptionRepository.findByCourse_Id(idCourse);
        if (subscription != null) {
            model.addAttribute("courses", course);
            model.addAttribute("vote", subscription.getVote());
            return "courses/vote";
        }
        return "redirect:/courses/course/" + course.getId() + "/detail";
    }

    @PostMapping("/{idCourse}/vote")
    public String voteCourse(@PathVariable("idCourse") Integer idCourse,
                             @RequestParam("vote") Integer vote) {
        Course course = courseService.findById(idCourse);
        Subscription subscription = subscriptionRepository.findByCourse_Id(idCourse);
        if (subscription != null) {
            try {
                subscriptionService.subscriptionVote(subscription.getId(), vote);
                return "redirect:/courses/course/" + course.getId() + "/detail?message="
                        + UriUtils.encode("Grazie per aver espresso la tua opinione sul corso!", StandardCharsets.UTF_8);
            } catch (Exception e) {
                return "redirect:/courses/course/" + course.getId() + "/detail?message="
                        + UriUtils.encode("Errore durante l'operazione " + e.getMessage() + "!", StandardCharsets.UTF_8);
            }
        }
        return "redirect:/courses/course/" + course.getId() + "/detail";
    }

    @GetMapping("/{courseId}/question")
    public String getQuestion(@PathVariable("courseId") Integer courseId, Model model) {
        Course course = courseService.findById(courseId);
        Subscription subscription = subscriptionRepository.findByCourse_Id(courseId);
        if (subscription != null) {
            model.addAttribute("courses", course);
            return "courses/question";
        }
        return "redirect:/courses/course/" + course.getId() + "/detail";
    }

    @PostMapping("/{courseId}/sendquestion")
    public String postQuestion(@RequestParam("g-recaptcha-response") String captchaResponse,
                               @PathVariable("courseId") Integer courseId,
                               @RequestParam("question") String question) {
        Course course = courseService.findById(courseId);
        String email = courseService.getEmailByCourseIdAndAuthor(courseId, course.getAuthor());

        if (question == null || question.isBlank()) {
            return "redirect:/courses/" + courseId + "/question?error="
                    + UriUtils.encode("Domanda obbligatoria", StandardCharsets.UTF_8);
        }

        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            return "redirect:/courses/" + courseId + "/question?error="
                    + UriUtils.encode("Captcha non valido. Riprova.", StandardCharsets.UTF_8);
        }

        try {
            // mittente = studente loggato (già gestito altrove)
            // Invia email
            emailService.sendSimpleEmail(email, "Nuova domanda sul corso", question.trim());
            return "redirect:/courses/course/" + course.getId() + "/detail?message="
                    + UriUtils.encode("Domanda inviata con successo!", StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "redirect:/courses/course/" + course.getId() + "/detail?message="
                    + UriUtils.encode("Errore invio email " + e.getMessage() + "!", StandardCharsets.UTF_8);
        }
    }

    // ================== UPLOAD IMAGE (owner o admin) ==================
    @PostMapping("/{id}/uploadImage")
    @PreAuthorize("(@permission.isOwnerOfCourse(#id, authentication) " + " and @permission.userHasRole(authentication.name, 'ROLE_EDITOR'))")
    public String uploadImage(@PathVariable("id") Integer id,
                              @RequestParam("imageFile") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/courses/" + id + "/edit?message="
                    + UriUtils.encode("File non valido.", StandardCharsets.UTF_8);
        }
        Course course = courseService.findById(id);
        String message = validazioni(course, null);
        if (message != null) {
            return "redirect:/courses/" + id + "/edit?message="
                    + UriUtils.encode(message, StandardCharsets.UTF_8);
        }
        try {
            String uploadPath = new File(uploadDir).getAbsolutePath();
            File uploadDirectory = new File(uploadPath);
            if (!uploadDirectory.exists()) uploadDirectory.mkdirs();

            String fileName = file.getOriginalFilename();
            String filePath = uploadPath + File.separator + fileName;
            file.transferTo(new File(filePath));

            courseService.updateImagePath("/uploads/" + fileName, course.getId());
            return "redirect:/courses?message="
                    + UriUtils.encode("Immagine caricata con successo! " + fileName, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "redirect:/courses/" + id + "/edit?message="
                    + UriUtils.encode("Errore durante il caricamento del file: " + e.getMessage(), StandardCharsets.UTF_8);
        }
    }
    // ================== UTIL ==================
    private boolean hasAuthority(Authentication auth, String authority) {
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(authority));
    }

    private boolean ownerIsAuthenticated(Course course, Authentication auth) {
        return auth != null &&
                course.getUserOwner() != null &&
                course.getUserOwner().getUsername().equals(auth.getName());
    }

    private String defaultIfBlank(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }

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

    // La tua validazioni(...) rimane invariata (puoi tenerla com’è)
    public String validazioni(Course course, Model model)
    {
        if(course.getDescription()==null)
        {
            model.addAttribute("message", "Salvare i dati prima di fare l'upload");
            return "Salvare i dati prima di fare l'upload";
        }
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
        if(course.getFullPriceAmount()==null || (course.getFullPriceAmount().compareTo(BigDecimal.ZERO) < 0)){
            model.addAttribute("message", "Prezzo intero obbligatorio e deve essere maggiore di zero");
            return "Prezzo intero obbligatorio e deve essere maggiore di zero";
        }
        if(course.getCurrentPriceAmount()==null || (course.getCurrentPriceAmount().compareTo(BigDecimal.ZERO) < 0)){
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
