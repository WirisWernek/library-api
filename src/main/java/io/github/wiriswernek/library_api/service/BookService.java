package io.github.wiriswernek.library_api.service;


import io.github.wiriswernek.library_api.model.dto.BookDTO;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookEntity save(BookRequest book) throws Exception;

    BookEntity findById(Long id) throws Exception;

    Page<BookEntity> findAll(Pageable page);

    Boolean delete(Long id) throws Exception;

    BookEntity update(Long id, BookRequest book) throws Exception;

    Page<BookEntity> search(BookRequest filter, Pageable page);
}
