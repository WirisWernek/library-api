package io.github.wiriswernek.library_api.exceptions;

public class BusinessExcetion extends RuntimeException {
    public BusinessExcetion(String message) {
        super(message);
    }

    public BusinessExcetion(ErrosEnum erro) {
        super(erro.toString());
    }
}
