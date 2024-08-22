package io.github.wiriswernek.library_api.service;


import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;

public interface BookService {
    BookEntity save(BookRequest book) throws Exception;
}
