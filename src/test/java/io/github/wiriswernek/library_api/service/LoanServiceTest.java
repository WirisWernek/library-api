package io.github.wiriswernek.library_api.service;

import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.LoanRequest;
import io.github.wiriswernek.library_api.model.repository.ILoanRepository;
import io.github.wiriswernek.library_api.service.imp.LoanServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
    LoanService loanService;

    @MockBean
    ILoanRepository iLoanRepository;

    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImp(iLoanRepository, new ModelMapper());
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest() {
        BookEntity book = BookEntity.builder().id(1L).build();
        LoanEntity loanEntity = LoanEntity.builder().customer("Wiris").loanDate(LocalDate.now()).book(book).build();
        LoanEntity savedLoan = LoanEntity.builder().id(1L).customer("Wiris").loanDate(LocalDate.now()).book(book).build();

        when(iLoanRepository.existsByBookAndNotReturned(loanEntity.getBook())).thenReturn(false);
        when(iLoanRepository.save(loanEntity)).thenReturn(savedLoan);

        LoanEntity loan = loanService.save(loanEntity);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        Mockito.verify(iLoanRepository, Mockito.times(1)).save(Mockito.any(LoanEntity.class));
        Mockito.verify(iLoanRepository, Mockito.times(1)).existsByBookAndNotReturned(Mockito.any(BookEntity.class));

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar realizar o emprestimo de um livro já emprestado")
    public void loanedBookSaveTest() {
        BookEntity book = BookEntity.builder().id(1L).build();
        LoanEntity loanEntity = LoanEntity.builder().customer("Wiris").loanDate(LocalDate.now()).book(book).build();

        when(iLoanRepository.existsByBookAndNotReturned(loanEntity.getBook())).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> loanService.save(loanEntity));

        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.LIVRO_JA_EMPRESTADO.toString());
        Mockito.verify(iLoanRepository, Mockito.never()).save(Mockito.any(LoanEntity.class));
    }

    @Test
    @DisplayName("Deve retornar os dados de um empréstimo")
    public void getLoanByIdTest() {
        LoanEntity entity = LoanEntity.builder().id(1L).customer("Wiris").loanDate(LocalDate.now()).book(BookEntity.builder().id(1L).build()).build();

        when(iLoanRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        LoanEntity loan = loanService.getLoanById(entity.getId());

        assertThat(loan.getId()).isEqualTo(entity.getId());
        assertThat(loan.getLoanDate()).isEqualTo(entity.getLoanDate());
        assertThat(loan.getCustomer()).isEqualTo(entity.getCustomer());
        assertThat(loan.getBook().getId()).isEqualTo(entity.getBook().getId());
        Mockito.verify(iLoanRepository, Mockito.times(1)).findById(Mockito.any(Long.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar os dados de um empréstimo")
    public void getLoanNotExistsTest() {
        when(iLoanRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> loanService.getLoanById(1L));

        assertThat(throwable).isInstanceOf(BusinessExcetion.class).hasMessage(ErrosEnum.EMPRESTIMO_NAO_ENCONTRADO.toString());
        Mockito.verify(iLoanRepository, Mockito.times(1)).findById(Mockito.any(Long.class));
    }

    @Test
    @DisplayName("Deve atualizar um emprśtimo")
    public void updateLoanTest() {
        LoanEntity entity = LoanEntity.builder().id(1L).customer("Wiris").loanDate(LocalDate.now()).returned(true).book(BookEntity.builder().id(1L).build()).build();

        when(iLoanRepository.save(entity)).thenReturn(entity);

        var savedLoan = loanService.update(entity);

        assertThat(savedLoan.getId()).isEqualTo(entity.getId());
        assertThat(savedLoan.getReturned()).isEqualTo(entity.getReturned());
        assertThat(savedLoan.getReturned()).isTrue();
        assertThat(savedLoan.getLoanDate()).isEqualTo(entity.getLoanDate());
        assertThat(savedLoan.getCustomer()).isEqualTo(entity.getCustomer());
        assertThat(savedLoan.getBook().getId()).isEqualTo(entity.getBook().getId());
        Mockito.verify(iLoanRepository, Mockito.times(1)).save(Mockito.any(LoanEntity.class));
    }

    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void searchLoanTest() throws Exception {

        BookEntity book = BookEntity.builder().id(1L).isbn("850205709X").build();
        List<LoanEntity> listLoan = Arrays.asList(LoanEntity.builder().id(1L).customer("Wiris").loanDate(LocalDate.now()).book(book).build());

        PageRequest pageRequest = PageRequest.of(0, 10);
        LoanRequest filter = LoanRequest.builder().isbn("850205709X").customer("Wiris").build();

        Page<LoanEntity> page = new PageImpl(listLoan, PageRequest.of(0, 10), 1);

        Mockito.when(iLoanRepository.findByBookIsbnOrCustomer(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(PageRequest.class))).thenReturn(page);
        Page<LoanEntity> loans = loanService.search(filter, pageRequest);

        assertThat(loans.getTotalElements()).isEqualTo(1);
        assertThat(loans.getContent()).isEqualTo(listLoan);
        assertThat(loans.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(loans.getPageable().getPageSize()).isEqualTo(10);

    }


}
