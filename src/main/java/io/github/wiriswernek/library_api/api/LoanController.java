package io.github.wiriswernek.library_api.api;

import io.github.wiriswernek.library_api.model.dto.BookDTO;
import io.github.wiriswernek.library_api.model.dto.LoanDTO;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.LoanRequest;
import io.github.wiriswernek.library_api.model.record.ReturnedLoan;
import io.github.wiriswernek.library_api.service.BookService;
import io.github.wiriswernek.library_api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

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

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoan returnedLoan) throws Exception {
        var loan = loanService.getLoanById(id);
        loan.setReturned(returnedLoan.returned());
        loanService.update(loan);
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<LoanDTO> search(@RequestBody LoanRequest filter, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 10) Pageable page) throws Exception {

        Page<LoanEntity> loans = loanService.search(filter, page);

        var listLoanDTO = loans.getContent().stream().map((loan) -> this.modelMapper.map(loan, LoanDTO.class)).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(listLoanDTO, page, loans.getTotalElements());
    }

}
