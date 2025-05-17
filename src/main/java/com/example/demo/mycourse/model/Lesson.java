package com.example.demo.mycourse.model;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import jakarta.persistence.*;

@Entity
@Table(name = "Lessons")

public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "Id")
    private Integer id;

    @Column(nullable = false, length = 450)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 100)
    private String duration;
    @Column(length = 100)
    private String orderLesson = "1000";
    @Column(length = 450)
    private String rowVersion;
    @ManyToOne
    @JoinColumn(name = "CourseId", nullable = false)
    private Course course;



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
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getOrderLesson() {
        return orderLesson;
    }

    public void setOrderLesson(String orderLesson) {
        this.orderLesson = orderLesson;
    }

    public String getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(String rowVersion) {
        this.rowVersion = rowVersion;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
