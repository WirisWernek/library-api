package io.github.wiriswernek.library_api.model.repository;

import io.github.wiriswernek.library_api.model.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IBookRepository extends JpaRepository<BookEntity, Long> {
    public Boolean existsByIsbn(String isbn);

    @Query(value = "SELECT EXISTS (SELECT b FROM BookEntity b WHERE b.id <> :id AND b.isbn = :isbn )")
    public Boolean existsByIsbnAndIdNot(@Param("isbn") String isbn, @Param("id") Long id);
}
