package com.example.demo.model;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import jakarta.persistence.*;

@Entity
@Table(name = "Lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @Column(name = "title", nullable = false, length = 4500)
    private String title;
    @Column(name = "description", nullable = true, length = 45000)
    private String description;
    @Column(name = "duration", nullable = true, length =  100)
    private String duration;
    @Column(name = "row_version", nullable = true, length = 450)
    private String rowVersion;
    @Column(name = "order_lesson", nullable = true, length = 100)
    private String orderLesson;
    @Override
    public String toString() {
        if (getDuration() == null || getDuration().isEmpty()) {
            return "00:00:00"; // Valore predefinito
        }
        try {
            LocalTime time = LocalTime.parse(getDuration(), DateTimeFormatter.ofPattern("H:m:s"));
            return time.format(DateTimeFormatter.ofPattern("HH:mm:ss")); // Normalizza a HH:mm:ss
        } catch (DateTimeParseException e) {
            return getDuration(); // Restituisce il valore originale se non è valido
        }
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(String rowVersion) {
        this.rowVersion = rowVersion;
    }

    public String getOrderLesson() {
        return orderLesson;
    }

    public void setOrderLesson(String orderLesson) {
        this.orderLesson = orderLesson;
    }

}
