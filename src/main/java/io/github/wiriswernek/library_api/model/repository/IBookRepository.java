package io.github.wiriswernek.library_api.model.repository;

import io.github.wiriswernek.library_api.model.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookRepository extends JpaRepository<BookEntity, Long> {
    public Boolean existsByIsbn(String isbn);
}
