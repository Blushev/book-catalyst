package com.example.bookcatalog.service;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.BookLoan;
import com.example.bookcatalog.model.User;
import com.example.bookcatalog.repository.BookLoanRepository;
import com.example.bookcatalog.repository.BookRepository;
import com.example.bookcatalog.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookLoanService {
    private final BookLoanRepository bookLoanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookLoanService(BookLoanRepository bookLoanRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.bookLoanRepository = bookLoanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<BookLoan> getBookHistoryByBookId(Long bookId) {
        return bookLoanRepository.findByBookId(bookId);
    }

    public boolean borrowBook(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() > 0) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
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
        userRepository.save(user);

        BookLoan bookLoan = bookLoanRepository.findByReaderAndBookIdAndReturnedDateIsNull(user, bookId);
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
