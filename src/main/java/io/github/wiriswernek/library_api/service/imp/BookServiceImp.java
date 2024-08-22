package io.github.wiriswernek.library_api.service.imp;

import io.github.wiriswernek.library_api.exceptions.BusinessExcetion;
import io.github.wiriswernek.library_api.exceptions.ErrosEnum;
import io.github.wiriswernek.library_api.model.entity.BookEntity;
import io.github.wiriswernek.library_api.model.record.BookRequest;
import io.github.wiriswernek.library_api.model.repository.IBookRepository;
import io.github.wiriswernek.library_api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImp implements BookService {

    private final IBookRepository bookRepository;

    private ModelMapper modelMapper;

    public BookServiceImp(IBookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookEntity save(BookRequest book) throws Exception{

        if (bookRepository.existsByIsbn(book.isbn())){
            throw new BusinessExcetion(ErrosEnum.ISBN_DUPLICADO);
        }

        return bookRepository.save(modelMapper.map(book, BookEntity.class));
    }
}
