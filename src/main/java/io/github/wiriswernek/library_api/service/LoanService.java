package io.github.wiriswernek.library_api.service;


import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;

public interface LoanService {
    LoanEntity save(LoanEntity entity);
}
