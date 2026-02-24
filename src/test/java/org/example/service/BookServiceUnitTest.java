package org.example.service;

import org.example.entity.Book;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//import static jdk.jfr.internal.jfc.model.Constraint.any;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void testFindBooks() {
        Book book1 = new Book(1L,
                "Spring Boot Basics",
                "John Doe",
                29.99,
                LocalDate.of(2024, 1, 1),
                "Programming");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Book> foundBook = bookService.findBookById(1L);

        assert foundBook.isPresent();
        assert foundBook.get().getTitle().equals("Spring Boot Basics");

    }

    @Test
    void test_Not_found() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Book> NoBook = bookService.findBookById(99L);

        assertFalse(NoBook.isPresent());
    }


    @Test
    void FindAllBooks() {
        // This test can be implemented similarly by mocking the findAll method of the repository
        Book book1 = new Book(1L,
                "Spring Boot Basics",
                "John Doe",
                29.99,
                LocalDate.of(2024, 1, 1),
                "Programming");
        Book book2 = new Book(2L,
                "Java Fundamentals",
                "Jane Smith",
                39.99,
                LocalDate.of(2023, 5, 15),
                "Programming");
        List<Book> bookList = List.of(book1, book2);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookList, pageable, bookList.size()));


        List<Book> foundBooks = bookService.findAllBooks(pageable).getContent();

        assert foundBooks.size() == 2;
        assert foundBooks.get(0).getTitle().equals("Spring Boot Basics");

    }

    @Test
    void testSaveBook() {
        Book book = new Book(null,
                "Spring Boot Basics",
                "John Doe",
                29.99,
                LocalDate.of(2024, 1, 1),
                "Programming");

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.saveBook(book);

        assert savedBook.getTitle().equals("Spring Boot Basics");
    }

    @Test
    void testUpdateBook() {
        Book existingBook = new Book(1L,
                "Spring Boot Basics",
                "John Doe",
                29.99,
                LocalDate.of(2024, 1, 1),
                "Programming");
        Book updatedBook = new Book(1L,
                "Spring Boot Advanced",
                "John Doe",
                39.99,
                LocalDate.of(2024, 1, 1),
                "Programming");

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(1L, updatedBook);

        assert result.getTitle().equals("Spring Boot Advanced");


    }

    @Test
    void testUpdateBook_NotFound() {
        Book updatedBook = new Book(99L,
                "Non-Existent Book",
                "Unknown Author",
                0.0,
                LocalDate.now(),
                "Unknown");

        when(bookRepository.existsById(99L)).thenReturn(false);

        Book result = bookService.updateBook(99L, updatedBook);

        assert result == null;
    }

    @Test
    void testDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        boolean result = bookService.deleteBook(1L);

        assert result;
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        boolean result = bookService.deleteBook(99L);

        assert !result;


    }
}
