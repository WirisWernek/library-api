package io.github.wiriswernek.library_api.repository;

import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.repository.IBookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.print.Book;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    IBookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base de dados com o ISBN informado")
    public void returnTrueWhenIsbnExistis(){
        String isbn = "123";
        entityManager.persist(BookEntity.builder().isbn("123").title("As Aventuras de PI").author("Fulano").build());

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }
}
