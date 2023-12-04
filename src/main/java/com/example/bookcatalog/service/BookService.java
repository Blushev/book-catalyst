package com.example.bookcatalog.service;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.BookLoan;
import com.example.bookcatalog.repository.BookLoanRepository;
import com.example.bookcatalog.repository.BookRepository;
import com.example.bookcatalog.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookLoanRepository bookLoanRepository;

    @Autowired
    public BookService(BookRepository bookRepository, BookLoanRepository bookLoanRepository) {
        this.bookRepository = bookRepository;
        this.bookLoanRepository = bookLoanRepository;
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

    public void deleteBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        bookRepository.delete(book);
    }

    public List<BookLoan> getBookHistoryByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));

        return bookLoanRepository.findByBookId(book.getId());
    }
}
