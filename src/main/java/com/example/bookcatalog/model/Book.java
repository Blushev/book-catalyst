package com.example.bookcatalog.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private int availableCopies;

    public String status;

    @ManyToOne
    @JoinColumn(name = "book_loan") // предположим, что связь с BookLoan устанавливается через поле bookLoan
    private BookLoan bookLoan;

    @Column(name = "reader_id", insertable = false, updatable = false)
    private Long readerId;

    @ManyToMany
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    public Book() {}

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public void addGenre(Genre genre) {
        if (!genres.contains(genre)) {
            genres.add(genre);
            genre.addBook(this);
        }
    }
    public String getGenreNames() {
        if (genres != null) {
            return genres.stream().map(Genre::getGenreName).collect(Collectors.joining(", "));
        }
        return "";
    }
    public void removeGenre(Genre genre) {
        if (genres.contains(genre)) {
            genres.remove(genre);
            genre.removeBook(this);
        }
    }

    public void decreaseAvailableCopies() {
        availableCopies--;
    }

    public void increaseAvailableCopies() {
        availableCopies++;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BookLoan getBookLoan() {
        return bookLoan;
    }

    public void setBookLoan(BookLoan bookLoan) {
        this.bookLoan = bookLoan;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    @PreRemove
    private void preRemove() {
        if (bookLoan != null) {
            bookLoan.setBook(null);
        }
    }
}