package com.example.bookcatalog.service;

import com.example.bookcatalog.controller.BookController;
import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.BookLoan;
import com.example.bookcatalog.model.Genre;
import com.example.bookcatalog.model.User;
import com.example.bookcatalog.repository.BookLoanRepository;
import com.example.bookcatalog.repository.BookRepository;
import com.example.bookcatalog.repository.GenreRepository;
import com.example.bookcatalog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;


@Service
@AllArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookLoanRepository bookLoanRepository;
    private final GenreRepository genreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private final Logger logger = LoggerFactory.getLogger(BookController.class);


    public void addUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Autowired
    public BookService(BookRepository bookRepository, BookLoanRepository bookLoanRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.bookLoanRepository = bookLoanRepository;
        this.genreRepository = genreRepository;
    }

    public Book createBook(Book book) throws EntityNotFoundException{
        return bookRepository.save(book);
    }
    public Book updateBook(Long bookId, Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            Book existingBook = optionalBook.get();

            existingBook.setName(updatedBook.getName());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setGenres(updatedBook.getGenres());

            return bookRepository.save(existingBook);
        } else {
            throw new EntityNotFoundException("Book not found with id: " + bookId);
        }
    }



    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public void deleteBookById(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            List<BookLoan> bookLoans = bookLoanRepository.findByBook(book);
            bookLoanRepository.deleteAll(bookLoans);

            bookRepository.delete(book);
        }
    }

    public List<BookLoan> getBookHistoryByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));

        return bookLoanRepository.findByBookId(book.getId());
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    public boolean existsById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.isPresent();
    }

    public void updateBook(Book updatedBook) {
        Optional<Book> existingBookOptional = bookRepository.findById(updatedBook.getId());

        if (existingBookOptional.isPresent()) {
            Book existingBook = existingBookOptional.get();
            existingBook.setName(updatedBook.getName());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setDescription(updatedBook.getDescription());
            existingBook.setAvailableCopies(updatedBook.getAvailableCopies());

            Set<Genre> genres = updatedBook.getGenres();
            if (genres != null && !genres.isEmpty()) {
                for (Genre genre : genres) {
                    if (genre.getId() == null) {
                        // Если у жанра нет ID, это значит, что он не сохранен в базе данных
                        genreRepository.save(genre);
                    }
                }
            }


            existingBook.setGenres(updatedBook.getGenres());


            Book savedBook = bookRepository.save(existingBook);
            logger.info("Book updated successfully: {}", savedBook);

            // Save the updated book back to the database
            bookRepository.save(existingBook);
        }
        else {
            logger.warn("Book not found for update with id: {}", updatedBook.getId());
        }
    }
}
