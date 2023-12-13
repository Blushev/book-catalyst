package com.example.bookcatalog.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class BookLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime borrowedDate;
    private LocalDateTime returnedDate;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id") // "book_id" - это имя внешнего ключа в таблице "book_loan"
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id", referencedColumnName = "id") // "reader_id" - это имя внешнего ключа в таблице "book_loan"
    private User reader;
    public BookLoan() {}

    public BookLoan(Long id, Book book, User reader, LocalDateTime borrowedDate, LocalDateTime returnedDate) {
        this.id = id;
        this.book = book;
        this.reader = reader;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDateTime borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }
}
