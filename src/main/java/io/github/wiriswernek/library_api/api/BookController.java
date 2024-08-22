package io.github.wiriswernek.library_api.api;

import io.github.wiriswernek.library_api.exceptions.ApiErrors;
import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.model.dto.BookDTO;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import io.github.wiriswernek.library_api.service.BookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationsExceptions(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessExcetion.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrors handleBusinessExceptions(BusinessExcetion exception){
        return new ApiErrors(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrors handleGenericExceptions(Exception exception){
        return new ApiErrors(exception.getMessage());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookRequest book) throws Exception {
        var entity = bookService.save(book);
        return modelMapper.map(entity, BookDTO.class);
    }
}
