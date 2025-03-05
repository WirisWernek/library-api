package io.github.wiriswernek.library_api.service.imp;

import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.model.record.LoanRequest;
import io.github.wiriswernek.library_api.model.repository.ILoanRepository;
import io.github.wiriswernek.library_api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImp implements LoanService {

    private final ILoanRepository loanRepository;
    private final ModelMapper modelMapper;

    @Override
    public LoanEntity save(LoanEntity entity) {
        if (loanRepository.existsByBookAndNotReturned(entity.getBook())) {
            throw new BusinessExcetion(ErrosEnum.LIVRO_JA_EMPRESTADO);
        }
        return loanRepository.save(entity);
    }

    @Override
    public LoanEntity getLoanById(long id) {
        return loanRepository.findById(id).orElseThrow(() -> new BusinessExcetion(ErrosEnum.EMPRESTIMO_NAO_ENCONTRADO));
    }

    @Override
    public LoanEntity update(LoanEntity loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<LoanEntity> search(LoanRequest filter, Pageable page) {
        return loanRepository.findByBookIsbnOrCustomer(filter.isbn(), filter.customer(), page);
    }

    @Override
    public Page<LoanEntity> getLoansByBook(BookEntity book, Pageable page) {
        return loanRepository.findByBook(book, page);
    }
}
