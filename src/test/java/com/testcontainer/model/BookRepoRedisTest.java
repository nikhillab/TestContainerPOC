package com.testcontainer.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("rawtypes")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class BookRepoRedisTest {

	@Autowired
	private RedisTemplate<String, Book> redisTemplate;

	@Container
	public static GenericContainer redis;

	static {
		redis = new GenericContainer("redis").withExposedPorts(6379);

		redis.start();
	}

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", () -> redis.getContainerIpAddress());
		registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
	}

	@Test
	void test() {
		assertThat(redis.isRunning()).isTrue();
	}

	@Test
	void read_book() {
		save();
		Book book = redisTemplate.opsForValue().get("Book1");

		assertThat(book.getTitle()).isEqualTo("My Java book");
	}

	void save() {
		Book book = new Book();
		book.setId(1);
		book.setTitle("My Java book");

		Book book2 = new Book();
		book2.setId(2);
		book2.setTitle("My Python book");

		redisTemplate.opsForValue().set("Book1", book);
		redisTemplate.opsForValue().set("Book2", book2);

	}

	@AfterAll
	public static void after() {
		redis.stop();
	}
}
