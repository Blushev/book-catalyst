package com.example.bookcatalog.controller;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.BookLoan;
import com.example.bookcatalog.model.User;
import com.example.bookcatalog.service.BookLoanService;
import com.example.bookcatalog.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final BookLoanService bookLoanService;

    @Autowired
    public BookController(BookService bookService, BookLoanService bookLoanService) {
        this.bookService = bookService;
        this.bookLoanService = bookLoanService;
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (EntityNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Book> updateBook(@RequestBody Book book) {
        Book updatedBook = bookService.updateBook(book.getId(), book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBookById(id);
            return ResponseEntity.ok("Book with ID " + id + " has been deleted.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, @AuthenticationPrincipal User user) {
        boolean isBorrowed = bookLoanService.borrowBook(user, bookId);
        if (isBorrowed) {
            return ResponseEntity.ok("Book borrowed successfully");
        } else {
            return ResponseEntity.badRequest().body("Book is already borrowed or not found");
        }
    }

    @PostMapping("/{bookId}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId, @AuthenticationPrincipal User user) {
        boolean isReturned = bookLoanService.returnBook(user, bookId);
        if (isReturned) {
            return ResponseEntity.ok("Book returned successfully");
        } else {
            return ResponseEntity.badRequest().body("Book was not borrowed by the user or not found");
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<BookLoan>> getBookHistoryByBookId(@PathVariable Long id) {
        try {
            List<BookLoan> bookHistory = bookService.getBookHistoryByBookId(id);
            return ResponseEntity.ok(bookHistory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
