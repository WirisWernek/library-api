package io.github.wiriswernek.library_api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.dto.BookDTO;
import io.github.wiriswernek.library_api.model.dto.LoanDTO;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.service.BookService;
import io.github.wiriswernek.library_api.service.LoanService;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

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

        LoanDTO dto = LoanDTO.builder().isbn("850205709X").customer("Wiris").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BookEntity savedBook = new BookEntity((Long) 1L, "Primeiro Livro", "Meu Autor", "850205709X");
        LoanEntity savedLoan = new LoanEntity((Long) 1L, "Wiris", savedBook, LocalDate.now(), false);

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
        LoanDTO dto = LoanDTO.builder().isbn("850205709X").customer("Wiris").build();
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
        LoanDTO dto = LoanDTO.builder().isbn("850205709X").customer("Wiris").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BookEntity savedBook = new BookEntity((Long) 1L, "Primeiro Livro", "Meu Autor", "850205709X");
        LoanEntity savedLoan = new LoanEntity((Long) 1L, "Wiris", savedBook, LocalDate.now(), false);

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
}
