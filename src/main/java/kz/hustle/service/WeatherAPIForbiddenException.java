package kz.hustle.service;

import java.io.IOException;

public class WeatherAPIForbiddenException extends IOException {
    public WeatherAPIForbiddenException(String message) {
        super(message);
    }
}
