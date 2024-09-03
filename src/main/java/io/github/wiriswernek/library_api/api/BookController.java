package io.github.wiriswernek.library_api.api;

import io.github.wiriswernek.library_api.exceptions.ApiErrors;
import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.model.dto.BookDTO;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import io.github.wiriswernek.library_api.service.BookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationsExceptions(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessExcetion.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrors handleBusinessExceptions(BusinessExcetion exception) {
        return new ApiErrors(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrors handleGenericExceptions(Exception exception) {
        return new ApiErrors(exception.getMessage());
    }

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
