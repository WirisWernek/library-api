package io.github.wiriswernek.library_api.exceptions;

public enum ErrosEnum {
    ISBN_DUPLICADO("O ISBN informado já é utilizado em outro livro"),
    LIVRO_NAO_ENCONTRADO("Livro não encontrado"),
    LIVRO_JA_EMPRESTADO("O Livro já está emprestado");

    private String descricao;

    ErrosEnum(String descricao) {
        this.descricao = descricao;
    }
}
