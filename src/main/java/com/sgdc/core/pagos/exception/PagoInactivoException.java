package com.sgdc.core.pagos.exception;

public class PagoInactivoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PagoInactivoException(String message) {
        super(message);
    }

    public PagoInactivoException(String message, Throwable cause) {
        super(message, cause);
    }

    public PagoInactivoException(Throwable cause) {
        super(cause);
    }
}
