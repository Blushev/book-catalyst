package com.example.bookcatalog.service;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.BookLoan;
import com.example.bookcatalog.model.User;
import com.example.bookcatalog.repository.BookLoanRepository;
import com.example.bookcatalog.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookLoanService {
    private final BookLoanRepository bookLoanRepository;
    private final BookRepository bookRepository;

    public BookLoanService(BookLoanRepository bookLoanRepository, BookRepository bookRepository) {
        this.bookLoanRepository = bookLoanRepository;
        this.bookRepository = bookRepository;
    }

    public boolean borrowBook(User user, Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null && book.getAvailableCopies() > 0) {
            book.decreaseAvailableCopies();
            bookRepository.save(book);

            BookLoan bookLoan = new BookLoan();
            bookLoan.setReader(user);
            bookLoan.setBook(book);
            bookLoan.setBorrowedDate(LocalDateTime.now());
            bookLoanRepository.save(bookLoan);
            return true;
        }
        return false;
    }

    public boolean returnBook(User user, Long bookId) {
        BookLoan bookLoan = bookLoanRepository.findByUserAndBookIdAndReturnedDateIsNull(user, bookId);
        if (bookLoan != null) {
            bookLoan.setReturnedDate(LocalDateTime.now());
            bookLoanRepository.save(bookLoan);

            Book book = bookLoan.getBook();
            book.increaseAvailableCopies();
            bookRepository.save(book);
            return true;
        }
        return false;
    }
}
