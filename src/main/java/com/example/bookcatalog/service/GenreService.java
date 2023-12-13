package com.example.bookcatalog.service;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.Genre;
import com.example.bookcatalog.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public List<Book> getBooksByGenreId(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id));

        return genre.getBooks().stream().toList();
    }

    public List<Book> getBooksByGenreName(String genreName) {
        List<Genre> genres = genreRepository.findByGenreName(genreName);

        if (genres.isEmpty()) {
            throw new EntityNotFoundException("Genre not found with name: " + genreName);
        }

        // Создаем список для хранения книг
        List<Book> books = new ArrayList<>();

        // Перебираем все найденные жанры и добавляем их книги в общий список
        for (Genre genre : genres) {
            books.addAll(genre.getBooks());
        }

        return books;
    }



    public Genre getOrCreateGenreByName(String genreName) {
        List<Genre> existingGenres = genreRepository.findByGenreName(genreName);

        if (!existingGenres.isEmpty()) {
            return existingGenres.get(0);
        } else {
            Genre newGenre = new Genre();
            newGenre.setGenreName(genreName);
            return genreRepository.save(newGenre);
        }
    }


    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }


    public void deleteGenreById(Long id) {
        genreRepository.deleteById(id);
    }


}
