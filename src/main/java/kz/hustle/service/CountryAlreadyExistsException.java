package kz.hustle.service;

public class CountryAlreadyExistsException extends RuntimeException{
    public CountryAlreadyExistsException(String message) {
        super(message);
    }
}
