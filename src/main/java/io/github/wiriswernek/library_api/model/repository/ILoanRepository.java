package io.github.wiriswernek.library_api.model.repository;

import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ILoanRepository extends JpaRepository<LoanEntity, Long> {

    @Query(value = "select case when count(l.id) > 0 then true else false end from LoanEntity l where l.book = :book and (l.returned is null or l.returned is false) ")
    Boolean existsByBookAndNotReturned(@Param("book") BookEntity book);

    @Query(value = "select l from LoanEntity as l join l.book as b where b.isbn = :isbn or l.customer = :customer ")
    Page<LoanEntity> findByBookIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable page);
}
