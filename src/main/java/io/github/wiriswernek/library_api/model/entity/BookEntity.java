package io.github.wiriswernek.library_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BOOK")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "ISBN")
    private String isbn;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<LoanEntity> loans;

    public BookEntity(Long id, String title, String author, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;

    }
}
