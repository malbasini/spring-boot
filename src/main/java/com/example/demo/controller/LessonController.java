package com.example.demo.controller;
import com.example.demo.service.CaptchaValidator;
import com.example.demo.service.CoursesService;
import com.example.demo.service.CoursesService;
import com.example.demo.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.*;

@Controller
@RequestMapping("/lessons")
public class LessonController {
    @Autowired
    private CaptchaValidator captchaValidator;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private CoursesService courseService;
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
            model.addAttribute("error", "Il Titolo è obbligatorio");
            return "lessons/create"; // JSP da mostrare
        }
        Lesson l = lessonService.findByTitleAndCourseId(lesson.getTitle(),courseId);
        if(l != null){
            model.addAttribute("error", "Il Titolo è già esistente");
            return "lessons/create"; // JS
        }
        try {
            Lesson lesson1 = valorizzaDefault(lesson);
            lesson1.setCourse(course);
            lessonService.save(lesson1);
        }
        catch (RuntimeException ex){
            model.addAttribute("message", ex.getMessage());
            return "lessons/create";
        }
        catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "lessons/create"; // JSP da mostrare
        }
        return "lessons/edit";
    }

    private Lesson valorizzaDefault(Lesson lesson) {
        lesson.setDuration("00:00:00");
        lesson.setDescription("");
        lesson.setOrderLesson("1000");
        lesson.setRowVersion(String.valueOf(Date.valueOf(LocalDate.now().toString())));
        return lesson;
    }

    // GET /lessons/{id}/edit -> mostra form di modifica
    @GetMapping("/{id}/edit")
    public String editLesson(@PathVariable("id") Integer id, Model model) {
        Lesson lesson = lessonService.findById(id);
        if (!lesson.equals(null)) {
            model.addAttribute("lesson", lesson);
            // gestisci errore se non trovato
            return "/lessons/edit"; // JSP da mostrare";

        }
        return "redirect:/lessons/edit";
    }
        // POST /lessons/{id} -> aggiornamento
    @PostMapping("/{id}/{courseId}")
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
            lessonService.save(existing);
            return "redirect:/courses";
        }
    }
        // POST /lessons/{id}/delete -> cancellazione
        @PostMapping("/{id}/delete")
        public String deleteLesson(@PathVariable("id") Integer id) {
            Lesson lesson = lessonService.findById(id);
            if (!lesson.equals(null)) {
                lessonService.deleteById(id);
                return "redirect:/courses";
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
