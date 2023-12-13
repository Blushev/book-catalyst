package com.example.bookcatalog.controller;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.BookLoan;
import com.example.bookcatalog.model.Genre;
import com.example.bookcatalog.model.User;
import com.example.bookcatalog.repository.UserRepository;
import com.example.bookcatalog.service.BookLoanService;
import com.example.bookcatalog.service.BookService;
import com.example.bookcatalog.service.GenreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final BookLoanService bookLoanService;
    private final GenreService genreService;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    public BookController(BookService bookService, BookLoanService bookLoanService, GenreService genreService) {
        this.bookService = bookService;
        this.bookLoanService = bookLoanService;
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            // Получите жанры из объекта книги
            Set<Genre> genreSet = book.getGenres().stream()
                    .map(Genre::getGenreName) // предполагается, что у жанра есть метод getName()
                    .map(String::trim)
                    .map(genreService::getOrCreateGenreByName)
                    .collect(Collectors.toSet());

            book.setGenres(genreSet);

            // Остальной код сохранения книги
            Book createdBook = bookService.createBook(book);
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (EntityNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isBorrowed = bookLoanService.borrowBook(user, bookId);
            if (isBorrowed) {
                return ResponseEntity.ok("Book borrowed successfully");
            } else {
                return ResponseEntity.badRequest().body("Book is already borrowed or not found");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable Long bookId, Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isReturned = bookLoanService.returnBook(user, bookId);
            if (isReturned) {
                return "redirect:/books/list?returned=true";
            } else {
                return "redirect:/books/list?error=return";
            }
        } else {
            return "redirect:/books/list?error=user";
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<List<BookLoan>> getBookHistoryByBookId(@PathVariable Long id) {
        try {
            List<BookLoan> bookHistory = bookService.getBookHistoryByBookId(id);
            return ResponseEntity.ok(bookHistory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/details/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            return "books/bookDetails";
        } catch (EntityNotFoundException e) {
            return "redirect:/books";
        }
    }

    @GetMapping("/borrow/{id}")
    public String showBorrowBookForm(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            return "borrowbook";
        } catch (EntityNotFoundException e) {
            return "redirect:/books";
        }
    }

    @GetMapping("/return/{id}")
    public String showReturnBookForm(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            return "returnbook";
        } catch (EntityNotFoundException e) {
            return "redirect:/books";
        }
    }



    @GetMapping("/list")
    public String showAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();

        for (Book book : books) {
            book.setStatus("Выдана");
        }

        model.addAttribute("books", books);
        return "allbooks";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "addbook";
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editBook(@RequestBody Book book) {
        try {
            // Проверка наличия книги в базе данных перед редактированием
            if (book.getId() == null || !bookService.existsById(book.getId())) {
                logger.warn("Book not found for editing with ID: {}", book.getId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }

            // Ваш код для обновления книги
            bookService.updateBook(book);

            logger.info("Book updated successfully. Book ID: {}", book.getId());

            return ResponseEntity.ok().body(Map.of("message", "Book updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            return "editbook";
        } catch (EntityNotFoundException e) {
            logger.warn("Book not found for editing with ID: {}", id);
            return "redirect:/books";
        }
    }

    @GetMapping("/delete/{id}")
    public String showDeleteBookForm(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            return "deletebook";
        } catch (EntityNotFoundException e) {
            return "redirect:/books";
        }
    }

    @PostMapping("/delete")
    public String deleteBook(@RequestParam Long id) {
        try {
            bookService.deleteBookById(id);
            return "redirect:/books/list";
        } catch (EntityNotFoundException e) {
            return "redirect:/books";
        }
    }
}
