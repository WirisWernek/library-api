package io.github.wiriswernek.library_api.model.record;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record BookRequest(@NotEmpty String title,@NotEmpty String author,@NotEmpty String isbn) {
}
