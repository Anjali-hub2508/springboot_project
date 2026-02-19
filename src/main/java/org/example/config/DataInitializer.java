package org.example.config;

import org.example.entity.Book;
import org.example.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(BookRepository bookRepository) {
        return args -> {
            // Check if data already exists
            if (bookRepository.count() == 0) {
                // Insert sample books
                Book book1 = new Book(null, "To Kill a Mockingbird", "Harper Lee", 12.99, LocalDate.of(1960, 7, 11), "Fiction");
                Book book2 = new Book(null, "1984", "George Orwell", 13.99, LocalDate.of(1949, 6, 8), "Dystopian");
                Book book3 = new Book(null, "The Great Gatsby", "F. Scott Fitzgerald", 11.99, LocalDate.of(1925, 4, 10), "Fiction");
                Book book4 = new Book(null, "Pride and Prejudice", "Jane Austen", 10.99, LocalDate.of(1813, 1, 28), "Romance");
                Book book5 = new Book(null, "The Catcher in the Rye", "J.D. Salinger", 14.99, LocalDate.of(1951, 7, 16), "Fiction");

                bookRepository.save(book1);
                bookRepository.save(book2);
                bookRepository.save(book3);
                bookRepository.save(book4);
                bookRepository.save(book5);

                System.out.println("Sample books have been inserted successfully!");
            }
        };
    }
}

