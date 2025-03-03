package io.github.wiriswernek.library_api.api;

import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.LoanRequest;
import io.github.wiriswernek.library_api.service.BookService;
import io.github.wiriswernek.library_api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanRequest loan) throws Exception {
        BookEntity bookEntity = bookService.getBookByIsbn(loan.isbn());

        LoanEntity loanEntity = LoanEntity.builder().customer(loan.customer()).book(bookEntity).loanDate(LocalDate.now()).returned(false).build();

        loanEntity = loanService.save(loanEntity);

        return loanEntity.getId();
    }

}
