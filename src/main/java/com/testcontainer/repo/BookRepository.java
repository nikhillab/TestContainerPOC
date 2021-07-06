package com.testcontainer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testcontainer.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}