package com.example.bookcatalog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Book> books;

    public Genre() {
        this.books = new HashSet<>();
    }

    // Добавляем конструктор с одним аргументом
    public Genre(String genreName) {
        this.genreName = genreName;
        this.books = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String name) {
        this.genreName = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
            book.addGenre(this);
        }
    }

    public void removeBook(Book book) {
        if (books.contains(book)) {
            books.remove(book);
            book.removeGenre(this);
        }
    }
}