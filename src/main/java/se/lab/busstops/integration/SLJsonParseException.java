package se.lab.busstops.integration;

public class SLJsonParseException extends RuntimeException {

    public SLJsonParseException(Throwable throwable) {
        super("Failed to parse SL Json", throwable);
    }
}
