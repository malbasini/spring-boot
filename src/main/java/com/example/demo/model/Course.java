package com.example.demo.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "Courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idUser", referencedColumnName = "id")
    private Register user;

    @Column(name = "Title", nullable = false, length = 450)
    private String title;
    @Column(name = "Description", nullable = true, length = 45000)
    private String description;
    @Column(name = "image_path", nullable = true, length = 450)
    private String imagePath;
    @Column(name = "Author", nullable = true, length = 450)
    private String author;
    @Column(name = "Email", nullable = true, length = 450)
    private String email;
    @Column(name = "Rating", nullable = true, scale = 18, precision = 1)
    private BigDecimal rating;
    @Column(name = "full_price_amount", nullable = true, scale = 18, precision = 2)
    private BigDecimal fullPriceAmount;
    @Column(name = "full_price_currency", nullable = true, length = 45)
    private String fullPriceCurrency;
    @Column(name = "current_price_amount", nullable = true, scale = 18, precision = 2)
    private BigDecimal currentPriceAmount;
    @Column(name = "current_price_currency", nullable = true, length = 45)
    private String currentPriceCurrency;
    @Column(name = "row_version", nullable = true, length = 45)
    private String rowVersion;
    @Column(name = "Status", nullable = true, length = 45)
    private String status;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Lesson> lessons;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Subscription> subscriptions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Register getUser() {
        return user;
    }

    public void setUser(Register user) {
        this.user = user;
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

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
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

}
