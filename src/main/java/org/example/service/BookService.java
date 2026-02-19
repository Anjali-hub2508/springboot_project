package org.example.service;

import org.example.entity.Book;
import org.example.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Find all books
     */
    public Page<Book> findAllBooks(Pageable pageable) {

        return bookRepository.findAll(pageable);
    }

    /**
     * Find book by ID
     */
    public Optional<Book> findBookById(Long id) {

        return bookRepository.findById(id);
    }

    // Save single book
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Save multiple books
    public List<Book> saveBooks(List<Book> books) {
        return bookRepository.saveAll(books);
    }

    /**
     * Update an existing book
     */
    public Book updateBook(Long id, Book book) {
        if (bookRepository.existsById(id)) {
            book.setId(id);
            return bookRepository.save(book);
        }
        return null;
    }

    /**
     * Partially update an existing book (only update provided fields)
     */
    public Book partialUpdateBook(Long id, Book bookDetails) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            if (bookDetails.getTitle() != null) {
                book.setTitle(bookDetails.getTitle());
            }
            if (bookDetails.getAuthor() != null) {
                book.setAuthor(bookDetails.getAuthor());
            }
            if (bookDetails.getPrice() != null) {
                book.setPrice(bookDetails.getPrice());
            }
            if (bookDetails.getPublishedDate() != null) {
                book.setPublishedDate(bookDetails.getPublishedDate());
            }
            if (bookDetails.getGenre() != null) {
                book.setGenre(bookDetails.getGenre());
            }
            return bookRepository.save(book);
        }
        return null;
    }

    /**
     * Delete a book by ID
     */
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

