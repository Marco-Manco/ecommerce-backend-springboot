package com.ecommerce.ecommerce.application.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    public RecursoNoEncontradoException(String recurso, Long id){
        super(String.format("%s con ID %d no encontrado", recurso, id));
    }
}
