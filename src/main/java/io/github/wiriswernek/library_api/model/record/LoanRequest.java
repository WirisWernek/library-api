package io.github.wiriswernek.library_api.model.record;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Builder
public record LoanRequest(@NotEmpty String customer, @NotEmpty String isbn) {
}
