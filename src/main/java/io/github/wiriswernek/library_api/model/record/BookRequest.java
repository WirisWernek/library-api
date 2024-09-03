package io.github.wiriswernek.library_api.model.record;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
public record BookRequest(@NotEmpty @Getter String title,@NotEmpty @Getter String author,@NotEmpty @Getter String isbn) {
}
