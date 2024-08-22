package io.github.wiriswernek.library_api.service;

import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import io.github.wiriswernek.library_api.model.repository.IBookRepository;
import io.github.wiriswernek.library_api.service.imp.BookServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    BookService bookService;

    @MockBean
    IBookRepository iBookRepository;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImp(iBookRepository, new ModelMapper());
    }

    @Test
    @DisplayName("Deve Salvar um livro")
    public void saveBookTest() throws Exception {
        //cenario
        BookRequest book = new BookRequest("As Aventuras de PI", "Fulano", "123");
        Mockito.when(iBookRepository.save(Mockito.any(BookEntity.class))).thenReturn(BookEntity.builder().id(1L).isbn(book.isbn()).title(book.title()).author(book.author()).build());

        // execucao
        BookEntity bookSaved = bookService.save(book);

        //verificacao
        assertThat(bookSaved.getId()).isNotNull();
        assertThat(bookSaved.getAuthor()).isEqualTo(book.author());
        assertThat(bookSaved.getTitle()).isEqualTo(book.title());
        assertThat(bookSaved.getIsbn()).isEqualTo(book.isbn());
    }

    @Test
    @DisplayName("Não deve cadastrar um livro com ISBN repetido")
    public void creatBookDuplicatedISBN() throws Exception {
        //cenario
        BookRequest book = new BookRequest("As Aventuras de PI", "Fulano", "123");
        Mockito.when(iBookRepository.existsByIsbn(book.isbn())).thenReturn(true);

        // execucao
        Throwable throwable = Assertions.catchThrowable(() -> bookService.save(book));


        //verificacao
        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.ISBN_DUPLICADO.toString());
        Mockito.verify(iBookRepository, Mockito.times(1)).existsByIsbn(book.isbn());
        Mockito.verify(iBookRepository, Mockito.never()).save(Mockito.any(BookEntity.class));


    }

}