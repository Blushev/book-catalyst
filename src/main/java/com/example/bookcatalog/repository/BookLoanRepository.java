package com.example.bookcatalog.repository;

import com.example.bookcatalog.model.BookLoan;
import com.example.bookcatalog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    BookLoan findByUserAndBookIdAndReturnedDateIsNull(User user, Long bookId);
    List<BookLoan> findByBookId(Long bookId);
}
