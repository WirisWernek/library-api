package io.github.wiriswernek.library_api.service;


import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.LoanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanService {
    LoanEntity save(LoanEntity entity);
    LoanEntity getLoanById(long id);
    LoanEntity update(LoanEntity loan);
    Page<LoanEntity> search(LoanRequest filter, Pageable page);
    Page<LoanEntity> getLoansByBook(BookEntity book, Pageable page);

}
