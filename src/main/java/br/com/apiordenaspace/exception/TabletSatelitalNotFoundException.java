package br.com.apiordenaspace.exception;

public class TabletSatelitalNotFoundException extends RuntimeException {

    public TabletSatelitalNotFoundException() {
        super("Tablet satelital nao encontrado.");
    }
}
