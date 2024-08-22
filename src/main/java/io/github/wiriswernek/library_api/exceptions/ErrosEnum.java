package io.github.wiriswernek.library_api.exceptions;

public enum ErrosEnum {
    ISBN_DUPLICADO("O ISBN informado já é utilizado em outro livro");

    private String descricao;

    ErrosEnum(String descricao) {
        this.descricao = descricao;
    }
}
