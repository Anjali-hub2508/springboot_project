package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.entity.Book;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * GET /books - List all books using pagination
     */
    @Operation(summary = "Get all books", description = "Returns a paginated list of books")
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Book> books = bookService.findAllBooks(pageable);
        return ResponseEntity.ok(books.getContent());
    }




    /**
     * GET /books/{id} - Find book by ID
     */

    @Operation(summary = "Get specific books", description = "Returns a paginated list of books")
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.findBookById(id);
        return book.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /books - Save a new book
     */
    @PostMapping("/single")
    @Operation(summary = "Add a book", description = "Returns a paginated list of books")
    public ResponseEntity<Book> createSingleBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Add all books", description = "Returns a paginated list of books")
    public ResponseEntity<List<Book>> createMultipleBooks(@RequestBody List<Book> books) {
        List<Book> savedBooks = bookService.saveBooks(books);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooks);
    }

    /**
     * PUT /books/{id} - Update an existing book
     */

    @Operation(summary = "Update a specific book", description = "Returns a paginated list of books")
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book updatedBook = bookService.updateBook(id, book);
        if (updatedBook != null) {
            return ResponseEntity.ok(updatedBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /books/{id} - Partially update an existing book
     */
    @Operation(summary = "Update a specific book", description = "Returns a paginated list of books")
    @PatchMapping("/{id}")
    public ResponseEntity<Book> partialUpdateBook(@PathVariable Long id, @RequestBody Book book) {
        Book updatedBook = bookService.partialUpdateBook(id, book);
        if (updatedBook != null) {
            return ResponseEntity.ok(updatedBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /books/{id} - Delete a book
     */
    @Operation(summary = "Delete a specific book", description = "Returns a paginated list of books")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

