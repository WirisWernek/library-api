package io.github.wiriswernek.library_api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.LoanRequest;
import io.github.wiriswernek.library_api.model.record.ReturnedLoan;
import io.github.wiriswernek.library_api.service.BookService;
import io.github.wiriswernek.library_api.service.LoanService;
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

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    static String LOAN_API = "/api/loans";

    @Test
    @DisplayName("Deve realizar um empréstimo")
    public void createLoanTest() throws Exception {

        LoanRequest dto = LoanRequest.builder().isbn("850205709X").customer("Wiris").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BookEntity savedBook = new BookEntity((Long) 1L, "Primeiro Livro", "Meu Autor", "850205709X");
        LoanEntity savedLoan = new LoanEntity((Long) 1L, "Wiris", "wiriswernek@gmail.com", LocalDate.now(), false, savedBook);

        BDDMockito.given(bookService.getBookByIsbn("850205709X")).willReturn(savedBook);

        BDDMockito.given(loanService.save(Mockito.any(LoanEntity.class))).willReturn(savedLoan);

        var request = MockMvcRequestBuilders.post(LOAN_API).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request).andExpect(status().isCreated()).andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar realizar o emprestimo de um livro inexistente")
    public void invalidISBNTest() throws Exception {
        LoanRequest dto = LoanRequest.builder().isbn("850205709X").customer("Wiris").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("850205709X")).willThrow(new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));

        var request = MockMvcRequestBuilders.post(LOAN_API).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.LIVRO_NAO_ENCONTRADO.toString())));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar realizar o emprestimo de um livro já emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        LoanRequest dto = LoanRequest.builder().isbn("850205709X").customer("Wiris").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BookEntity savedBook = new BookEntity((Long) 1L, "Primeiro Livro", "Meu Autor", "850205709X");
        LoanEntity savedLoan = new LoanEntity((Long) 1L, "Wiris", "wiriswernek@gmail.com", LocalDate.now(), false, savedBook);

        BDDMockito.given(bookService.getBookByIsbn("850205709X")).willReturn(savedBook);

        BDDMockito.given(loanService.save(Mockito.any(LoanEntity.class))).willThrow(new BusinessExcetion(ErrosEnum.LIVRO_JA_EMPRESTADO));

        var request = MockMvcRequestBuilders.post(LOAN_API).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.LIVRO_JA_EMPRESTADO.toString())));
    }

    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception {
        ReturnedLoan dto = new ReturnedLoan(true);
        String json = new ObjectMapper().writeValueAsString(dto);
        Long id = 1L;

        LoanEntity loan = LoanEntity.builder().id(1L).customer("Wiris").loanDate(LocalDate.now()).build();

        BDDMockito.given(loanService.getLoanById(Mockito.any(Long.class))).willReturn(loan);

        BDDMockito.given(loanService.update(Mockito.any(LoanEntity.class))).willReturn(loan);

        var request = MockMvcRequestBuilders.patch(LOAN_API.concat("/").concat(String.valueOf(id))).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Deve Lançar erro ao tentar retornar um livro já devolvido")
    public void returnBookAsReturnedTest() throws Exception {
        ReturnedLoan dto = new ReturnedLoan(true);
        String json = new ObjectMapper().writeValueAsString(dto);
        Long id = 1L;

        BDDMockito.given(loanService.getLoanById(Mockito.any(Long.class))).willThrow(new BusinessExcetion(ErrosEnum.LIVRO_JA_DEVOLVIDO));

        var request = MockMvcRequestBuilders.patch(LOAN_API.concat("/").concat(String.valueOf(id))).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.LIVRO_JA_DEVOLVIDO.toString())));

    }

    @Test
    @DisplayName("Deve Lançar erro ao não encontrar um empréstimo")
    public void loanNotFounTest() throws Exception {
        ReturnedLoan dto = new ReturnedLoan(true);
        String json = new ObjectMapper().writeValueAsString(dto);
        Long id = 1L;

        BDDMockito.given(loanService.getLoanById(Mockito.any(Long.class))).willThrow(new BusinessExcetion(ErrosEnum.EMPRESTIMO_NAO_ENCONTRADO));

        var request = MockMvcRequestBuilders.patch(LOAN_API.concat("/").concat(String.valueOf(id))).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors", contains(ErrosEnum.EMPRESTIMO_NAO_ENCONTRADO.toString())));

    }

    @Test
    @DisplayName("Deve filtrar os emprestimos")
    public void searchLoanTest() throws Exception {
        BookEntity book = BookEntity.builder().id(1L).isbn("850205709X").build();

        LoanEntity loan = LoanEntity.builder().id(1L).customer("Wiris").loanDate(LocalDate.now()).book(book).build();

        LoanRequest filter = LoanRequest.builder().isbn("850205709X").customer("Wiris").build();


        BDDMockito.given(loanService.search(Mockito.any(LoanRequest.class), Mockito.any(Pageable.class))).willReturn(
                new PageImpl<LoanEntity>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        String json = new ObjectMapper().writeValueAsString(filter);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("%s/search?page=0&size=100".formatted(LOAN_API))
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
