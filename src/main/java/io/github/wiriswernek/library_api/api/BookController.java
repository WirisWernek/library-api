package io.github.wiriswernek.library_api.api;

import io.github.wiriswernek.library_api.model.dto.BookDTO;
import io.github.wiriswernek.library_api.model.dto.LoanDTO;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import io.github.wiriswernek.library_api.service.BookService;
import io.github.wiriswernek.library_api.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookRequest book) throws Exception {
        var entity = bookService.save(book);
        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO getById(@PathVariable Long id) throws Exception {
        var entity = bookService.findById(id);
        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("/{id}/loans")
    @ResponseStatus(HttpStatus.OK)
    public Page<LoanDTO> getLoansByBook(@PathVariable Long id, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 10) Pageable page) throws Exception {
        var book = bookService.findById(id);
        Page<LoanEntity> loans = loanService.getLoansByBook(book, page);

        var listLoanDTO = loans.getContent().stream().map((loan) -> this.modelMapper.map(loan, LoanDTO.class)).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(listLoanDTO, page, loans.getTotalElements());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> getAll(@PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 10) Pageable page) throws Exception {
        var books = bookService.findAll(page);

        var listBookDTO = books.getContent().stream().map((book) -> this.modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
        return new PageImpl<BookDTO>(listBookDTO, page, books.getTotalElements());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws Exception {
        bookService.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody @Valid BookRequest book) throws Exception {
        bookService.update(id, book);
    }

    @PostMapping("search")
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> search(@RequestBody BookRequest filter, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 10) Pageable page) throws Exception {
        var books = bookService.search(filter, page);

        var listBookDTO = books.getContent().stream().map((book) -> this.modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
        return new PageImpl<BookDTO>(listBookDTO, page, books.getTotalElements());
    }
}
