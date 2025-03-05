package io.github.wiriswernek.library_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDTO {
    private Long id;
    private String customer;
    private Boolean returned;
    private LocalDate loanDate;
    private BookDTO book;
}
