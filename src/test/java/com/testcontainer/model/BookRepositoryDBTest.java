package com.testcontainer.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.testcontainer.repo.BookRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class BookRepositoryDBTest {

	@Autowired
	private BookRepository bookRepository;

	@SuppressWarnings("rawtypes")
	@Container
	public static PostgreSQLContainer postgreSQLContainer;

	static {
		postgreSQLContainer = new PostgreSQLContainer("postgres");

		postgreSQLContainer.start();
	}

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

	}

	@Test
	void test() {
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@Test
	void read_book() {
		long id = save();
		Optional<Book> book = bookRepository.findById(id);

		assertThat(book).isPresent();
	}

	@Test
	void save_and_read_book() {
		Book book = new Book();
		book.setTitle("My fancy title");

		bookRepository.save(book);

		List<Book> allBooks = bookRepository.findAll();

		assertThat(allBooks).isNotEmpty();
		assertThat(allBooks).hasSize(2);
		assertThat(allBooks.get(1).getTitle()).isEqualTo("My fancy title");
	}

	long save() {
		Book book = new Book();
		book.setTitle("My Java book");
		return bookRepository.save(book).getId();

	}
	
	@AfterAll
	public static void after(){
		postgreSQLContainer.stop();
	}

}