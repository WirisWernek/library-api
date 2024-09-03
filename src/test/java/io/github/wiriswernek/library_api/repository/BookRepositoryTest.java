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
    public void returnTrueWhenIsbnExists() {
        String isbn = "123";
        entityManager.persist(BookEntity.builder().isbn("123").title("As Aventuras de PI").author("Fulano").build());

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base de dados com o ISBN informado")
    public void returnFalseWhenIsbnDoesntExists() {
        String isbn = "123";

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base de dados com o mesmo ISBN informado")
    public void returnTrueWhenIsbnAndIdNotExists() {
        String isbn = "123";
        BookEntity book = BookEntity.builder().isbn("123").title("As Aventuras de PI").author("Fulano").build();
        entityManager.persist(book);

        boolean exists = bookRepository.existsByIsbnAndIdNot(isbn, book.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar true quando existir um livro na base de dados com o mesmo ISBN informado")
    public void returnFalseWhenIsbnAndIdNotDoesntExists() {
        String isbn = "123";
        BookEntity book1 = BookEntity.builder().isbn(isbn).title("As Aventuras de PI").author("Fulano").build();
        BookEntity book2 = BookEntity.builder().isbn("abcd").title("As Aventuras de PI").author("Fulano").build();
        entityManager.persist(book1);
        entityManager.persist(book2);
        boolean exists = bookRepository.existsByIsbnAndIdNot(isbn, book2.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve Salvar um livro")
    public void saveBookTest(){
        BookEntity book = BookEntity.builder().isbn("123").title("As Aventuras de PI").author("Fulano").build();
        BookEntity saved = bookRepository.save(book);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve Deletar um livro")
    public void deleteBookTest(){
        BookEntity book = BookEntity.builder().isbn("123").title("As Aventuras de PI").author("Fulano").build();
        entityManager.persist(book);
        Long id = book.getId();

        bookRepository.deleteById(id);

        BookEntity deleted = entityManager.find(BookEntity.class, id);
        assertThat(deleted).isNull();
    }
}
