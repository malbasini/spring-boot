package com.example.demo.mycourse.controller;
import com.example.demo.mycourse.service.CaptchaValidator;
import com.example.demo.mycourse.service.CourseService;
import com.example.demo.mycourse.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


import com.example.demo.mycourse.model.*;

@Controller
@RequestMapping("/lessons")
public class LessonController {

    private final CaptchaValidator captchaValidator;
    private final LessonService lessonService;
    private final CourseService courseService;

    public LessonController(CourseService courseService,
                            CaptchaValidator captchaValidator,
                            LessonService lessonService ) {

        this.courseService = courseService;
        this.captchaValidator = captchaValidator;
        this.lessonService = lessonService;
    }


    // GET /lessons/course/{courseId} -> visualizzazione delle lezioni di un corso
    @GetMapping("/course/{courseId}")
    public String listLessonsByCourse(@PathVariable("courseId") Integer courseId,
                                      Model model) {
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        Course course = courseService.findById(courseId);
        model.addAttribute("lessons", lessons);
        model.addAttribute("course", course);
        return "lessons/list";
    }
    // GET /lessons/new?courseId=... -> mostra form per creare una lezione
    @GetMapping("/new/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_EDITOR')")
    public String showCreateForm(@PathVariable("courseId") Integer courseId, Model
            model) {
        Lesson lesson = new Lesson();
        // Impostiamo il corso nella lesson
        Course course = courseService.findById(courseId);
        lesson.setCourse(course);
        model.addAttribute("lesson", lesson);
        model.addAttribute("courseId", courseId);
        return "lessons/create";
    }
    // POST /lessons -> creazione (salvataggio) della lezione
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_EDITOR')")
    public String createLesson(@ModelAttribute("lesson") Lesson lesson,
                               @RequestParam("courseId") Integer courseId,
                               @RequestParam("g-recaptcha-response") String captchaResponse,
                               Model model) {
        // Associa la lezione al corso
        Course course = courseService.findById(courseId);
        model.addAttribute("courseId", courseId);
        lesson.setCourse(course);
        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            model.addAttribute("error", "Captcha non valido. Riprova.");
            model.addAttribute("lesson", lesson);
            return "lessons/create";// Torna alla pagina del form
        }
        if(lesson.getTitle().isEmpty()|| lesson.getTitle() == null) {
            model.addAttribute("message", "Il Titolo è obbligatorio");
            return "lessons/create"; // JSP da mostrare
        }
        lesson.setDuration("00:00:00");
        lesson.setDescription("At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio.");
        try {
            lessonService.save(lesson);
        }
        catch (RuntimeException ex){
            model.addAttribute("message", ex.getMessage());
            return "lessons/create";
        }
        catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "lessons/create"; // JSP da mostrare
        }
        Lesson l = lessonService.findByTitleAndCourseId(lesson.getTitle(), courseId);
        model.addAttribute("lesson", l);
          return "redirect:/lessons/" + lesson.getId() + "/edit?message1=Inserimento lezione avvenuto con successo. Ora inserisci gli altri dati!";
    }
    // GET /lessons/{id}/edit -> mostra form di modifica
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('ROLE_EDITOR')")
    public String editLesson(@PathVariable("id") Integer id,
                             @RequestParam(name = "message1",required = false) String message1,
                             Model model) {
        Lesson lesson = lessonService.findById(id);
        if (!lesson.equals(null)) {
            model.addAttribute("lesson", lesson);
            model.addAttribute("message1", message1);
            // gestisci errore se non trovato
            return "lessons/edit"; // JSP da mostrare";

        }
        return "redirect:/lessons/edit";
    }
        // POST /lessons/{id} -> aggiornamento
    @PostMapping("/{id}/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_EDITOR')")
    public String updateLesson(@PathVariable("id") Integer id,
                               @PathVariable("courseId") Integer courseId,
                               @ModelAttribute("lesson") Lesson updatedLesson,
                               Model model) {
        Lesson existing = lessonService.findById(id);
        Course course = courseService.findById(courseId);
        model.addAttribute("courseId", courseId);
        updatedLesson.setCourse(course);
        String message = this.validazioni(updatedLesson);
        if(message != null){
            model.addAttribute("message", message);
            return "lessons/edit";
        }
        if (existing == null) {
            // gestisci errore se non trovato
            return "redirect:/courses";
        } else {
            // Copia i campi
            existing.setTitle(updatedLesson.getTitle());
            existing.setDescription(updatedLesson.getDescription());
            existing.setDuration(updatedLesson.getDuration());
            // Non dimenticare di reimpostare il course!
            existing.setCourse(updatedLesson.getCourse());
            lessonService.updateLesson(existing);
            return "redirect:/courses?message=Lezione aggiornata correttamente!";
        }
    }
        // POST /lessons/{id}/delete -> cancellazione
        @PostMapping("/{id}/delete")
        @PreAuthorize("hasAuthority('ROLE_EDITOR')")
        public String deleteLesson(@PathVariable("id") Integer id) {
            Lesson lesson = lessonService.findById(id);
            if (lesson != null) {
                lessonService.deleteLesson(id);
                return "redirect:/courses?message=Lezione cancellata correttamente!";
            }
            return "redirect:/courses";
        }
    @GetMapping("/{id}/detail")
    public String detailLesson(@PathVariable("id") Integer id,Model model) {
        Lesson lesson = lessonService.findById(id);
        if (!lesson.equals(null))
        {
            model.addAttribute("lesson", lesson);
            return "lessons/detail";
        }
        return "redirect:/courses";
    }

    public String validazioni(Lesson lesson){

        String cleanedText = lesson.getDescription().replaceAll("<[^>]*>", " ").trim();

        if((lesson.getTitle().isEmpty()) || (lesson.getTitle() == null)){
            return "Il titolo non può essere vuoto";
        }
        if((lesson.getDuration() == null) || (lesson.getDuration().isEmpty())|| lesson.getDuration().equals("00:00:00")){
            return "La durata deve essere valorizzata";
        }
        if((cleanedText == null) || (cleanedText.isEmpty())){
            return "La descrizione deve essere valorizzata";
        }


        return null;
    }
}
