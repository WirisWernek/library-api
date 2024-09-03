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
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        Mockito.when(iBookRepository.save(Mockito.any(BookEntity.class))).thenReturn(BookEntity.builder().id((Long) 1L).isbn(book.isbn()).title(book.title()).author(book.author()).build());

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
        Mockito.when(iBookRepository.existsByIsbn(book.isbn())).thenReturn(Boolean.TRUE);

        // execucao
        Throwable throwable = Assertions.catchThrowable(() -> bookService.save(book));

        //verificacao
        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.ISBN_DUPLICADO.toString());
        Mockito.verify(iBookRepository, Mockito.times(1)).existsByIsbn(book.isbn());
        Mockito.verify(iBookRepository, Mockito.never()).save(Mockito.any(BookEntity.class));

    }

    @Test
    @DisplayName("Deve buscar os detalhes de um livro a partir do ID")
    public void getBookDetailsTest() throws Exception {
        Long id = (Long) 1L;
        BookEntity book = BookEntity.builder().id(id).isbn("123").title("As Aventuras de PI").author("Fulano").build();
        Mockito.when(iBookRepository.findById(id)).thenReturn(Optional.ofNullable(book));

        BookEntity bookSaved = bookService.findById(id);

        assertThat(bookSaved.getId()).isEqualTo(id);
        assertThat(bookSaved.getAuthor()).isEqualTo("Fulano");
        assertThat(bookSaved.getTitle()).isEqualTo("As Aventuras de PI");
        assertThat(bookSaved.getIsbn()).isEqualTo("123");

    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar um livro pelo ID")
    public void bookNotFoundTest() throws Exception {
        //cenario
        Long id = (Long) 1L;
        Mockito.when(iBookRepository.findById(id)).thenReturn(Optional.empty());

        // execucao
        Throwable throwable = Assertions.catchThrowable(() -> bookService.findById(id));

        //verificacao
        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.LIVRO_NAO_ENCONTRADO.toString());
        Mockito.verify(iBookRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        Long id = (Long) 1L;
        BookEntity book = BookEntity.builder().id(id).isbn("123").title("As Aventuras de PI").author("Fulano").build();
        Mockito.when(iBookRepository.findById(id)).thenReturn(Optional.of(book));

        Boolean result = bookService.delete(id);

        assertThat(result).isTrue();
        Mockito.verify(iBookRepository, Mockito.times(1)).findById(id);
        Mockito.verify(iBookRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar um livro para excluir")
    public void deleteInexistentBookTest() throws Exception {
        Long id = (Long) 1L;
        Mockito.when(iBookRepository.findById(id)).thenReturn(Optional.empty());

        // execucao
        Throwable throwable = Assertions.catchThrowable(() -> bookService.delete(id));

        //verificacao
        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.LIVRO_NAO_ENCONTRADO.toString());
        Mockito.verify(iBookRepository, Mockito.times(1)).findById(id);
        Mockito.verify(iBookRepository, Mockito.never()).deleteById(id);

    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    public void updateBookTest() throws Exception {
        Long id = (Long) 1L;
        BookEntity bookEntity = BookEntity.builder().id(id).isbn("123").title("As Aventuras de PI").author("Fulano").build();
        BookRequest bookRequest = new BookRequest("As Aventuras de PI V2", "Fulano 2", "123");

        BookEntity book = BookEntity.builder().id(id).isbn(bookRequest.isbn()).title(bookRequest.title()).author(bookRequest.author()).build();
        Mockito.when(iBookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        Mockito.when(iBookRepository.existsByIsbnAndIdNot("123", id)).thenReturn(Boolean.FALSE);
        Mockito.when(iBookRepository.save(Mockito.any(BookEntity.class))).thenReturn(book);

        BookEntity result = bookService.update(id, bookRequest);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
        assertThat(result.getIsbn()).isEqualTo(book.getIsbn());

        Mockito.verify(iBookRepository, Mockito.times(1)).findById(id);
        Mockito.verify(iBookRepository, Mockito.times(1)).existsByIsbnAndIdNot("123", id);
        Mockito.verify(iBookRepository, Mockito.times(1)).save(Mockito.any(BookEntity.class));

    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar um livro para atualizar")
    public void updateInexistentBookTest() throws Exception {
        Long id = (Long) 1L;
        Mockito.when(iBookRepository.findById(id)).thenReturn(Optional.empty());
        BookRequest bookRequest = new BookRequest("As Aventuras de PI", "Fulano", "123");

        // execucao
        Throwable throwable = Assertions.catchThrowable(() -> bookService.update(id, bookRequest));

        //verificacao
        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.LIVRO_NAO_ENCONTRADO.toString());
        Mockito.verify(iBookRepository, Mockito.times(1)).findById(id);
        Mockito.verify(iBookRepository, Mockito.never()).existsByIsbnAndIdNot("123", id);
        Mockito.verify(iBookRepository, Mockito.never()).save(Mockito.any(BookEntity.class));

    }

    @Test
    @DisplayName("Não deve atualizar um livro com ISBN usado por outro livro")
    public void updateBookDuplicatedISBN() throws Exception {
        Long id = (Long) 1L;
        BookEntity bookEntity = BookEntity.builder().id(id).isbn("123").title("As Aventuras de PI").author("Fulano").build();
        BookRequest bookRequest = new BookRequest("As Aventuras de PI", "Fulano", "123");
        Mockito.when(iBookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        Mockito.when(iBookRepository.existsByIsbnAndIdNot("123", id)).thenReturn(Boolean.TRUE);

        Throwable throwable = Assertions.catchThrowable(() -> bookService.update(id, bookRequest));

        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.ISBN_DUPLICADO.toString());
        Mockito.verify(iBookRepository, Mockito.times(1)).findById(id);
        Mockito.verify(iBookRepository, Mockito.times(1)).existsByIsbnAndIdNot("123", id);
        Mockito.verify(iBookRepository, Mockito.never()).save(Mockito.any(BookEntity.class));
    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void searchBooksTest() throws Exception {
        Long id = (Long) 1L;
        List<BookEntity> listBook = Arrays.asList(BookEntity.builder().id(id).isbn("123").title("As Aventuras de PI").author("Fulano").build());

        PageRequest pageRequest = PageRequest.of(0, 10);
        BookRequest filter = new BookRequest("As Aventuras de PI", "Fulano", "123");

        Page<BookEntity> page = new PageImpl(listBook, PageRequest.of(0, 10), 1);

        Mockito.when(iBookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
        Page<BookEntity> books = bookService.search(filter, pageRequest );

        assertThat(books.getTotalElements()).isEqualTo(1);
        assertThat(books.getContent()).isEqualTo(listBook);
        assertThat(books.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(books.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Deve buscar todos os livros")
    public void getAllBooksTest() throws Exception {
        Long id = (Long) 1L;
        List<BookEntity> listBook = Arrays.asList(BookEntity.builder().id(id).isbn("123").title("As Aventuras de PI").author("Fulano").build());

        PageRequest pageRequest = PageRequest.of(0, 10);
        BookRequest filter = new BookRequest("As Aventuras de PI", "Fulano", "123");

        Page<BookEntity> page = new PageImpl(listBook, PageRequest.of(0, 10), 1);

        Mockito.when(iBookRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(page);
        Page<BookEntity> books = bookService.findAll( pageRequest );

        assertThat(books.getTotalElements()).isEqualTo(1);
        assertThat(books.getContent()).isEqualTo(listBook);
        assertThat(books.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(books.getPageable().getPageSize()).isEqualTo(10);

    }

}