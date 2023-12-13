package com.example.bookcatalog.controller;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.Genre;
import com.example.bookcatalog.service.GenreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/genres")
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

    @GetMapping
    public String showGenres(Model model) {
        List<Genre> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);
        model.addAttribute("newGenre", new Genre()); // Для формы добавления нового жанра
        return "genres";
    }

    @PostMapping("/add")
    public String addGenre(@ModelAttribute Genre newGenre) {
        genreService.createGenre(newGenre);
        return "redirect:/genres";
    }

    @GetMapping("/{id}/delete")
    public String deleteGenre(@PathVariable Long id) {
        genreService.deleteGenreById(id);
        return "redirect:/genres";
    }

    @GetMapping("/{genreName}/books-info")
    public String showGenreBooks(@PathVariable String genreName, Model model) {
        try {
            List<Book> books = genreService.getBooksByGenreName(genreName);
            model.addAttribute("books", books);
            model.addAttribute("genreName", genreName); // Добавляем genreName в модель
            return "genrebooks";
        } catch (EntityNotFoundException e) {
            return "redirect:/genres";
        }
    }
}
