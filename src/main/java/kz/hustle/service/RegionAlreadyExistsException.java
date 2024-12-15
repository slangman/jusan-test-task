package kz.hustle.service;

public class RegionAlreadyExistsException extends RuntimeException {
    public RegionAlreadyExistsException(String message) {
        super(message);
    }
}
