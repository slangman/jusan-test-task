package kz.hustle.service;

import java.io.IOException;

public class WeatherAPIRequestTimeoutException extends IOException {
    public WeatherAPIRequestTimeoutException(String message) {
        super(message);
    }
}
