package io.github.wiriswernek.library_api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;

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
    @Autowired
    private View error;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void creatBookTest() throws Exception {

        BookRequest book = new BookRequest("Primeiro Livro", "Meu Autor", "123456789");
        BookEntity savedBook = new BookEntity(1L,"Primeiro Livro", "Meu Autor", "123456789");
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


}
