package io.github.wiriswernek.library_api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.dto.BookDTO;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import io.github.wiriswernek.library_api.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService service;

    static String BOOK_API = "/api/books";

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void creatBookTest() throws Exception {

        BookRequest book = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BookEntity savedBook = new BookEntity((Long) 1L, "Primeiro Livro", "Meu Autor", "123456789");
        BDDMockito.given(service.save(Mockito.any(BookRequest.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(book.title()))
                .andExpect(jsonPath("author").value(book.author()))
                .andExpect(jsonPath("isbn").value(book.isbn()))
        ;
    }

    @Test
    @DisplayName("Deve lançar erro ao criar um livro com dados inválidos")
    public void creatInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(BookRequest.builder().build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Não deve cadastrar um livro com ISBN repetido")
    public void creatBookDuplicatedISBN() throws Exception {
        BookRequest book = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BDDMockito.given(service.save(Mockito.any(BookRequest.class))).willThrow(new BusinessExcetion(ErrosEnum.ISBN_DUPLICADO));

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.ISBN_DUPLICADO.toString())));
    }

    @Test
    @DisplayName("Deve buscar os detalhes de um livro a partir do ID")
    public void getBookDetailsTest() throws Exception {
        Long id = (Long) 1L;
        BookEntity bookEntity = new BookEntity(id, "Primeiro Livro", "Meu Autor", "123456789");
        BDDMockito.given(service.findById(id)).willReturn(bookEntity);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("%s/%s".formatted(BOOK_API, id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookEntity.getId()))
                .andExpect(jsonPath("title").value(bookEntity.getTitle()))
                .andExpect(jsonPath("author").value(bookEntity.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookEntity.getIsbn()))
        ;
    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar um livro pelo ID")
    public void bookNotFoundTest() throws Exception {
        Long id = (Long) 2L;
        BDDMockito.given(service.findById(id)).willThrow(new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("%s/%s".formatted(BOOK_API, id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.LIVRO_NAO_ENCONTRADO.toString())));
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        Long id = (Long) 1L;
        BDDMockito.given(service.delete(id)).willReturn(Boolean.TRUE);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("%s/%s".formatted(BOOK_API, id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar um livro para excluir")
    public void deleteInexistentBookTest() throws Exception {
        Long id = (Long) 1L;
        BDDMockito.given(service.delete(id)).willThrow(new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("%s/%s".formatted(BOOK_API, id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.LIVRO_NAO_ENCONTRADO.toString())));
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    public void updateBookTest() throws Exception {
        Long id = (Long) 1L;
        BookRequest book = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BookEntity bookEntity = BookEntity.builder().id(id).isbn(book.isbn()).title(book.title()).author(book.author()).build();
        BDDMockito.given(service.update(id, book)).willReturn(bookEntity);

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("%s/%s".formatted(BOOK_API, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
        ;
        assertThat(bookEntity.getId()).isEqualTo(id);
        assertThat(bookEntity.getAuthor()).isEqualTo(book.author());
        assertThat(bookEntity.getTitle()).isEqualTo(book.title());
        assertThat(bookEntity.getIsbn()).isEqualTo(book.isbn());
    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar um livro para atualizar")
    public void updateInexistentBookTest() throws Exception {
        Long id = (Long) 1L;
        BookRequest book = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BDDMockito.given(service.update(id, book)).willThrow(new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("%s/%s".formatted(BOOK_API, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.LIVRO_NAO_ENCONTRADO.toString())));
    }

    @Test
    @DisplayName("Deve lançar erro quando os dados para atualizar um livro forem inválidos")
    public void invalidDataUpdateBookTest() throws Exception {
        Long id = (Long) 1L;
        String json = new ObjectMapper().writeValueAsString(BookRequest.builder().build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("%s/%s".formatted(BOOK_API, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Não deve atualizar um livro com ISBN usado por outro livro")
    public void updateBookDuplicatedISBN() throws Exception {
        Long id = (Long) 1L;
        BookRequest book = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BDDMockito.given(service.update(id, book)).willThrow(new BusinessExcetion(ErrosEnum.ISBN_DUPLICADO));

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("%s/%s".formatted(BOOK_API, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.ISBN_DUPLICADO.toString())));
    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void searchBooksTest() throws Exception {
        Long id = (Long) 1L;
        BookRequest filter = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BookEntity bookEntity = BookEntity.builder().id(id).isbn(filter.isbn()).title(filter.title()).author(filter.author()).build();
        BDDMockito.given(service.search(Mockito.any(BookRequest.class), Mockito.any(Pageable.class))).willReturn(
                new PageImpl<BookEntity>(Arrays.asList(bookEntity), PageRequest.of(0, 100), 1));

        String json = new ObjectMapper().writeValueAsString(filter);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("%s/search?page=0&size=100".formatted(BOOK_API))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

    @Test
    @DisplayName("Deve buscar todos os livros")
    public void getAllBooksTest() throws Exception {
        Long id = (Long) 1L;
        BookRequest filter = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BookEntity bookEntity = BookEntity.builder().id(id).isbn(filter.isbn()).title(filter.title()).author(filter.author()).build();
        BDDMockito.given(service.findAll(Mockito.any(Pageable.class))).willReturn(
                new PageImpl<BookEntity>(Arrays.asList(bookEntity), PageRequest.of(0, 100), 1));

        String json = new ObjectMapper().writeValueAsString(filter);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("%s?page=0&size=100".formatted(BOOK_API))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

}
