package org.example.service;

import jakarta.transaction.Transactional;
import org.example.entity.Book;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest
@Transactional
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testFindBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = bookService.findAllBooks(pageable).getContent();
        assert books.size() > 0;
    }

}
