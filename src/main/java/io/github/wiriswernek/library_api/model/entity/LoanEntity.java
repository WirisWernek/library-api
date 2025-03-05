package io.github.wiriswernek.library_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "LOAN")
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER")
    private String customer;

    @Column(name = "LOAN_DATE")
    private LocalDate loanDate;

    @Column(name = "RETURNED")
    private Boolean returned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_BOOK", referencedColumnName = "ID")
    private BookEntity book;
}
