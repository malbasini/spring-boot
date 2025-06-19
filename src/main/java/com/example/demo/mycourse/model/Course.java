package com.example.demo.mycourse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Courses")

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Title", nullable = false,length = 450,unique = true)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 450)
    private String imagePath;

    @Column(length = 450)
    private String author;

    @Column(length = 450)
    private String email;

    @Column(nullable = false, columnDefinition = "DECIMAL(18,1) DEFAULT 1.0")
    private BigDecimal rating;

    @Column(name = "FullPrice_Amount", precision = 18, scale = 2)
    private BigDecimal fullPriceAmount;

    @Column(name = "FullPrice_Currency", length = 45)
    private String fullPriceCurrency;

    @Column(name = "CurrentPrice_Amount", precision = 18, scale = 2)
    private BigDecimal currentPriceAmount;

    @Column(name = "CurrentPrice_Currency", length = 45)
    private String currentPriceCurrency;

    @Column(length = 45)
    private String rowVersion;

    @Column(length = 45)
    private String status;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons;


    // Relazione con l’utente (docente) che ha creato il corso
    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private User userOwner;  // se preferisci, chiama la variabile "teacher"


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public BigDecimal getFullPriceAmount() {
        return fullPriceAmount;
    }

    public void setFullPriceAmount(BigDecimal fullPriceAmount) {
        this.fullPriceAmount = fullPriceAmount;
    }

    public String getFullPriceCurrency() {
        return fullPriceCurrency;
    }

    public void setFullPriceCurrency(String fullPriceCurrency) {
        this.fullPriceCurrency = fullPriceCurrency;
    }

    public BigDecimal getCurrentPriceAmount() {
        return currentPriceAmount;
    }

    public void setCurrentPriceAmount(BigDecimal currentPriceAmount) {
        this.currentPriceAmount = currentPriceAmount;
    }

    public String getCurrentPriceCurrency() {
        return currentPriceCurrency;
    }

    public void setCurrentPriceCurrency(String currentPriceCurrency) {
        this.currentPriceCurrency = currentPriceCurrency;
    }

    public String getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(String rowVersion) {
        this.rowVersion = rowVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFullStars() {
        return getRating().intValue();
    }

    public boolean isHasHalfStar() {
        return getRating().subtract(new BigDecimal(getFullStars())).compareTo(new BigDecimal("0.5")) >= 0;
    }

    public int getEmptyStars() {
        return 5 - getFullStars() - (isHasHalfStar() ? 1 : 0);
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public User getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(User userOwner) {
        this.userOwner = userOwner;
    }
}
