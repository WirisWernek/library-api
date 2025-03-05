package io.github.wiriswernek.library_api.repository;

import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.LoanRequest;
import io.github.wiriswernek.library_api.model.repository.IBookRepository;
import io.github.wiriswernek.library_api.model.repository.ILoanRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.AssertTrueValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LoanRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ILoanRepository loanRepository;


    @Test
    @DisplayName("Deve verificar se existe emprestimo não devolvido para um livro")
    public void existsByBookAndNotReturnedTest(){
        var book = BookEntity.builder().isbn("123").title("As Aventuras de PI").author("Fulano").build();
        entityManager.persist(book);

        var loan = LoanEntity.builder().customer("Wiris").loanDate(LocalDate.now()).book(book).build();
        entityManager.persist(loan);

        var retorno = loanRepository.existsByBookAndNotReturned(book);
        assertThat(retorno).isTrue();
    }

    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void searchLoanTest() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        LoanRequest filter = LoanRequest.builder().isbn("850205709X").customer("MACARENA").build();

        var book = BookEntity.builder().isbn("850205709X").title("As Aventuras de PI").author("Fulano").build();
        entityManager.persist(book);

        var loan = LoanEntity.builder().customer("MACARENA").loanDate(LocalDate.now()).book(book).build();
        entityManager.persist(loan);

        var result = loanRepository.findByBookIsbnOrCustomer(filter.isbn(), filter.customer(), pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter emprestimos cuja data emprestimo for menor ou igual a tres dias e ainda não foi retornado")
    public void findByLoanDateLessThanAndNotReturnedTest(){

        var book = BookEntity.builder().isbn("850205709X").title("As Aventuras de PI").author("Fulano").build();
        entityManager.persist(book);

        var loan = LoanEntity.builder().customer("MACARENA").customerEmail("macarena@gmail.com").loanDate(LocalDate.now().minusDays(5)).book(book).build();
        entityManager.persist(loan);

        var result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).contains(loan);

    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver empréstimos atrasados")
    public void notFindByLoanDateLessThanAndNotReturnedTest(){

        var book = BookEntity.builder().isbn("850205709X").title("As Aventuras de PI").author("Fulano").build();
        entityManager.persist(book);

        var loan = LoanEntity.builder().customer("MACARENA").customerEmail("macarena@gmail.com").loanDate(LocalDate.now()).book(book).build();
        entityManager.persist(loan);

        var result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();

    }
}
