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
    Long id;

    @Column(name = "CUSTOMER")
    String customer;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "ID")
    BookEntity book;

    @Column(name = "LOAN_DATE")
    LocalDate loanDate;

    @Column(name = "RETURNED")
    Boolean returned;
        }
