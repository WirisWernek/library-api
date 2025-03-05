package io.github.wiriswernek.library_api.service.imp;

import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import io.github.wiriswernek.library_api.model.repository.IBookRepository;
import io.github.wiriswernek.library_api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImp implements BookService {

    private final IBookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public BookEntity save(BookRequest book) throws Exception {

        if (bookRepository.existsByIsbn(book.isbn())) {
            throw new BusinessExcetion(ErrosEnum.ISBN_DUPLICADO);
        }

        BookEntity bookEntity = this.modelMapper.map(book, BookEntity.class);
        return bookRepository.save(bookEntity);
    }

    @Override
    public BookEntity findById(Long id) throws Exception {
        return bookRepository.findById(id).orElseThrow(() -> new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));
    }

    @Override
    public Page<BookEntity> findAll(Pageable page) {
        return bookRepository.findAll(page);
    }

    @Override
    public Boolean delete(Long id) throws Exception {
        bookRepository.findById(id).orElseThrow(() -> new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));
        bookRepository.deleteById(id);
        return Boolean.TRUE;
    }

    @Override
    public BookEntity update(Long id, BookRequest book) throws Exception {
        bookRepository.findById(id).orElseThrow(() -> new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));
        var exists = bookRepository.existsByIsbnAndIdNot(book.isbn(), id);
        if (exists) {
            throw new BusinessExcetion(ErrosEnum.ISBN_DUPLICADO);
        }

        var bookEntity = this.modelMapper.map(book, BookEntity.class);
        bookEntity.setId(id);
        return bookRepository.save(bookEntity);
    }

    @Override
    public Page<BookEntity> search(BookRequest filter, Pageable page) {

        BookEntity map = this.modelMapper.map(filter, BookEntity.class);
        Example<BookEntity> example = Example.of(map,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)

        );
        return bookRepository.findAll(example, page);
    }

    @Override
    public BookEntity getBookByIsbn(String isbn) throws Exception {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BusinessExcetion(ErrosEnum.LIVRO_NAO_ENCONTRADO));
    }
}
