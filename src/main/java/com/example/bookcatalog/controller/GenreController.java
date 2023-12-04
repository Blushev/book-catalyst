package com.example.bookcatalog.controller;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.Genre;
import com.example.bookcatalog.service.GenreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return new ResponseEntity<>(createdGenre, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<Book>> getBooksByGenreId(@PathVariable Long id) {
        try {
            List<Book> books = genreService.getBooksByGenreId(id);
            return ResponseEntity.ok(books);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
